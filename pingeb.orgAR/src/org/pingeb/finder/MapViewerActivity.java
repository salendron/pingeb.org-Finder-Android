package org.pingeb.finder;

import java.util.ArrayList;

import org.pingeb.finder.data.Tag;
import org.pingeb.finder.net.TagLoader;
import org.pingeb.finder.net.TagLoaderCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

public class MapViewerActivity extends FragmentActivity implements LocationListener, LocationSource, OnCameraChangeListener {
	
	private static final String TAG = "pingeb-MapViewerActivity";
	
	private Context mContext;
	private Activity mActivity;
	private GoogleMap map;
	private OnLocationChangedListener mListener;
	private LocationManager mLocationManager;
	private Location mLocation;

	private TagLoaderCallback mLoaderCallback;
	private Marker mSelectedMarker = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_viewer);
		
		mContext = this;
		mActivity = this;
		
		
		
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setLocationSource(this);
		
		map.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
				mSelectedMarker = marker;
				return false;
			}
		});
		
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			
			@Override
			public void onInfoWindowClick(Marker marker) {
				Intent intent =new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(
						Uri.parse(
								"google.navigation:ll=" + 
								String.valueOf(marker.getPosition().latitude) + 
								"," + String.valueOf(marker.getPosition().longitude)
								)
						);
				mActivity.startActivity(intent);
			}
		});

        map.setInfoWindowAdapter(new InfoWindowAdapter() {
 
            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }
 
            @Override
            public View getInfoContents(Marker arg0) {
 
                View v = getLayoutInflater().inflate(R.layout.marker_popup, null);
 
                TextView tvLat = (TextView) v.findViewById(R.id.tvTagName);
 
                TextView tvLng = (TextView) v.findViewById(R.id.tvTagInfo);

                tvLat.setText(arg0.getTitle());

                tvLng.setText(arg0.getSnippet());
                
                return v;
            }
        });
		
		setUpLocationManager();
		
		initLoaderCallback();
		
		redrawMarkers(map.getCameraPosition().zoom);
	}
	
	private void initLoaderCallback(){
		mLoaderCallback = new TagLoaderCallback() {
			
			@Override
			public void onTagsLoaded(ArrayList<Tag> tags) {				
				Log.d(TAG,"onTagsLoaded: Loaded " + String.valueOf(tags.size()) + " Tags!");
				redrawMarkers(map.getCameraPosition().zoom);
				//Toast.makeText(mContext, "onTagsLoaded: Loaded " + String.valueOf(tags.size()) + " Tags!", Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onError(ArrayList<Exception> errors) {
				StringBuilder sb = new StringBuilder();
				for(int i = 0; i < errors.size(); i++){
					sb.append(errors.get(i).getMessage());
					sb.append(" | ");
				}
				Log.e(TAG,"onError: " + sb.toString());
				//Toast.makeText(mContext, "Fehler beim Synchronisieren der Tags! - " + sb.toString(), Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onCacheInitialized() {				
				Log.i(TAG,"onError: " + "onCacheInitialized: Loaded " + 
						String.valueOf(TagLoader.getSystems().length) + " Systems and " + 
						String.valueOf(TagLoader.getTags().size()) + " Tags!");
				
				/*Toast.makeText(mContext, "onCacheInitialized: Loaded " + 
						String.valueOf(TagLoader.getSystems().length) + " Systems and " + 
						String.valueOf(TagLoader.getTags().size()) + " Tags!"
						, Toast.LENGTH_SHORT).show(); */
				
				TagLoader.syncWithOnlineSystems(mContext);
			}
			
			@Override
			public void onCacheError(Exception ex) {
				Log.e(TAG,"onError: " + "onCacheError: " + ex.getMessage());
				//Toast.makeText(mContext, "Fehler: " + ex.getMessage(), Toast.LENGTH_LONG).show();
			}
		};
	}
	
	private void redrawMarkers(float zoom){
		if(TagLoader.getTags().size() == 0){
			Toast.makeText(mContext, 
					"Synchronisiere Tags zum ersten Mal. Das kann je nach Netzwerkverbindung etwas dauern...", 
					Toast.LENGTH_LONG).show();
		}
		
		if(mSelectedMarker != null){
			mSelectedMarker = null;
			return;
		}
		
		map.clear();
		
		VisibleRegion region = map.getProjection().getVisibleRegion();	
		
		ArrayList<Tag> markersToShow = new ArrayList<Tag>();
		
		for(int i = 0; i < TagLoader.getTags().size(); i++){
			if(region.latLngBounds.contains(TagLoader.getTags().get(i).getLatlng())){
				markersToShow.add(TagLoader.getTags().get(i));
			}
		}
		
		if(zoom > 11 && markersToShow.size() < 200){
			Location tagLocation;
			String distance = "";
			for(int i = 0; i < markersToShow.size(); i++){
				
				if(mLocation != null){
					tagLocation = new Location("NONE");
					tagLocation.setLatitude(markersToShow.get(i).getLatlng().latitude);
					tagLocation.setLongitude(markersToShow.get(i).getLatlng().longitude);
					
					int meters = Math.round(mLocation.distanceTo(tagLocation));
					distance = "Entfernung: " + String.valueOf(meters) + "m";
				} else {
					distance = "??m";
				}
				
				map.addMarker(new MarkerOptions()
		        .position(markersToShow.get(i).getLatlng())
		        .title(markersToShow.get(i).getName())
		        .snippet(
		        		distance + " - " +
		        		String.valueOf(markersToShow.get(i).getClicks()) + 
		        		" Downloads"
		        		)
		        .icon(BitmapDescriptorFactory
		            .fromResource(R.drawable.pin60)));
				
				if(markersToShow.get(i).getGeofenceEnabled()){
					map.addGroundOverlay(new GroundOverlayOptions().
				            image(getMapCircleBitmap(markersToShow.get(i).getGeofenceRadius() * 100)).
				            position(markersToShow.get(i).getLatlng(),markersToShow.get(i).getGeofenceRadius()*2,markersToShow.get(i).getGeofenceRadius()*2).
				            transparency(0.75f));
				}
			}
		} else {
			String system = null;
			double lat = 0;
			double lon = 0;
			int count = 0;
			for(int i = 0; i < markersToShow.size(); i++){
				if(system == null){
					system = markersToShow.get(i).getSystem().getName();
					Log.d(TAG,system);
				}
				
				if(system.equals(markersToShow.get(i).getSystem().getName())){
					lat += markersToShow.get(i).getLatlng().latitude; 
					lon += markersToShow.get(i).getLatlng().longitude; 
					count++;
				} else {
					lat = lat / count;
					lon = lon / count;
					
					map.addMarker(new MarkerOptions()
			        .position(new LatLng(lat, lon))
			        .title("pingeb.org " + system)
			        .snippet("Zomme n�her um die einzelnen Tags zu sehen!")
			        .icon(BitmapDescriptorFactory
			            .fromResource(R.drawable.ic_launcher)));
					
					system = markersToShow.get(i).getSystem().getName();
					Log.d(TAG,system);
					lat = markersToShow.get(i).getLatlng().latitude; 
					lon = markersToShow.get(i).getLatlng().longitude; 
					count = 1;
				}				
			}
			
			lat = lat / count;
			lon = lon / count;
			
			map.addMarker(new MarkerOptions()
	        .position(new LatLng(lat, lon))
	        .title("pingeb.org " + system)
	        .snippet("Zomme näher heran um die einzelnen Tags zu sehen!")
	        .icon(BitmapDescriptorFactory
	            .fromResource(R.drawable.ic_launcher)));
		}
	}
	
	private BitmapDescriptor getMapCircleBitmap(int d){
	    Bitmap bm = Bitmap.createBitmap(d, d, Config.ARGB_8888);
	    Canvas c = new Canvas(bm);
	    Paint p = new Paint();
	    p.setColor(getResources().getColor(R.color.pingebYellow));
	    c.drawCircle(d/2, d/2, d/2, p);

	    return BitmapDescriptorFactory.fromBitmap(bm);
	}
	
	private void setUpLocationManager(){
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		if(mLocationManager != null)
	    {
	        boolean gpsIsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);	        
	        boolean networkIsEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	    	
	    	if(gpsIsEnabled) {
	    		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10F, this);
	    		initLocation();
	    	} else if(networkIsEnabled) {
	    		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10F, this);
	    		initLocation();
	    	} else {
	    		Toast.makeText(mContext, getResources().getString(R.string.msg_no_gps), Toast.LENGTH_LONG).show();
            }
	    } else {
	    	Toast.makeText(mContext, getResources().getString(R.string.msg_no_location_manager), Toast.LENGTH_LONG).show();
	    	this.finish();
	    }
	}
	
	private void initLocation(){
		mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(mLocation == null || mLocation.getAccuracy() < 0.1){
			 mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10F, this);
			 mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLocation.getLatitude(), mLocation.getLongitude())));
        map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		
		if(mLocationManager != null)
		{
			mLocationManager.removeUpdates(this);
		}
		
		TagLoader.clearCallback();
		map.setOnCameraChangeListener(null);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		TagLoader.registerCallback(mLoaderCallback);
		map.setOnCameraChangeListener(this);
		
		if(mLocationManager != null)
		{
			map.setMyLocationEnabled(true);
		}
	}
	

	@Override
	public void activate(OnLocationChangedListener listener) 
	{
		mListener = listener;
	}
	
	@Override
	public void deactivate() 
	{
		mListener = null;
	}

	@Override
	public void onLocationChanged(Location location) {
		if( mListener != null )
	    {
	        mListener.onLocationChanged( location );
	        mLocation = location;
	    }
	}

	@Override
	public void onProviderDisabled(String provider) { }

	@Override
	public void onProviderEnabled(String provider) { }

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) { }

	@Override
	public void onCameraChange(CameraPosition position) {
		redrawMarkers(map.getCameraPosition().zoom);
	}

}
