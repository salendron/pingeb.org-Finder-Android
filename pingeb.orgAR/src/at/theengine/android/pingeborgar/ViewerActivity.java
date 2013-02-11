package at.theengine.android.pingeborgar;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import at.theengine.android.pingeborgar.animation.AnimationFactory;
import at.theengine.android.pingeborgar.mapping.TagMapMarker;
import at.theengine.android.pingeborgar.sensors.DeviceOrientation;
import at.theengine.android.pingeborgar.sensors.OnDeviceOrientationListener;

public class ViewerActivity extends MapActivity {
	
	private static final String TAG = "pingebAR-ViewerActivity";
	
	//device orientation
	private DeviceOrientation mOrientation;
	private OnDeviceOrientationListener mOrientationListener;
	
	private Context mContext;
	private Activity mActivity;
	
	private JSONArray mTags;
	private Location mLocation;
	
	//views
	private MapView mvTags;
	private LinearLayout llMap;
	private LinearLayout llAR;
	private MyLocationOverlay mMyLocationOverlay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewer);
		
		Log.d(TAG,"OnCreate");
		
		mContext = this;
		mActivity = this;
		
		initViews();
		initOrientationListener();
		
		try {
			mTags = new JSONArray(getIntent().getExtras().getString("tags"));
			double lat = getIntent().getExtras().getDouble("lat");
			double lng = getIntent().getExtras().getDouble("lng");
			mLocation = new Location("my location");
			mLocation.setLatitude(lat);
			mLocation.setLongitude(lng);
		} catch (JSONException e) {
			Toast.makeText(mContext, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			this.finish();
		}
		redrawTags();
	}
	
	private void initViews(){
		Log.d(TAG,"Initiliazing views...");
		
		mvTags = (MapView) findViewById(R.id.mvTags);
		llMap = (LinearLayout) findViewById(R.id.llMap);
		llAR = (LinearLayout) findViewById(R.id.llAR);
		
		llAR.setVisibility(View.GONE);
		
		mMyLocationOverlay = new MyLocationOverlay(this,mvTags); 
		mvTags.getOverlays().add(mMyLocationOverlay); 
	}
	
	private void initOrientationListener(){
		Log.d(TAG,"Initiliazing OrientationListener...");
		
		mOrientationListener = new OnDeviceOrientationListener(){

			@Override
			public void onOrientationChanged(DEVICE_ORIENTATION orientation) {
				if(orientation == DEVICE_ORIENTATION.UP){
					llMap.setVisibility(View.GONE);
					llAR.setVisibility(View.VISIBLE);
				} else {
					llMap.setVisibility(View.VISIBLE);
					llAR.setVisibility(View.GONE);
				}
			}
			
		};
		
		mOrientation = new DeviceOrientation(mOrientationListener, mActivity, mContext);
	}
	
	private void redrawTags(){
		mvTags.getController().setZoom(17);
		drawTagsOnMap();
	}
	
	private void drawTagsOnMap(){
		List<Overlay> mapOverlays = mvTags.getOverlays();
		mapOverlays.clear();
		Drawable drawable = this.getResources().getDrawable(R.drawable.ic_launcher);
		TagMapMarker itemizedoverlay = new TagMapMarker(drawable, this);
		
		GeoPoint point;
		Location tagLocation;
		OverlayItem overlayitem;
		double distance;
		JSONObject tag;
		for(int i = 0; i < mTags.length(); i++){
			try {
				tag = mTags.getJSONObject(i);
				
				tagLocation = new Location("tag");
				tagLocation.setLatitude(tag.getDouble("lat"));
				tagLocation.setLongitude(tag.getDouble("lon"));
				distance = mLocation.distanceTo(tagLocation);
				
				point = new GeoPoint((int)(tag.getDouble("lat") * 1e6),
	                    			(int)(tag.getDouble("lon") * 1e6));
				overlayitem = new OverlayItem(
						point, 
						tag.getString("name"), 
						"Entfernung: " + 
						String.valueOf(((int) Math.round(distance))) + 
						"m");
				itemizedoverlay.addOverlay(overlayitem);
			} catch(JSONException ex){
				Log.e(TAG, ex.getMessage());
			}
		}
		
		mapOverlays.add(itemizedoverlay);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG,"OnResume");
		mOrientation.subscribe();
		
		//mark me
		mMyLocationOverlay.enableMyLocation(); 
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG,"OnPause");
		mOrientation.unsubscribe();
		
		//me
		mMyLocationOverlay.disableMyLocation();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
