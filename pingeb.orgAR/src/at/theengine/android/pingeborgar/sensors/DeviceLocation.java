package at.theengine.android.pingeborgar.sensors;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import at.theengine.android.pingeborgar.dataobjects.City;

public class DeviceLocation implements LocationListener {

	private static final String TAG = "pingebAR-DeviceLocation";
	
	private LocationManager mLocationManager;
	private Context mContext;
	private Activity mActivity;
	private String mProvider;
	private OnDeviceLocationListener mListener;
	
	public DeviceLocation(Context context, Activity activity, OnDeviceLocationListener listener){
		this.mContext = context;
		this.mActivity = activity;
		this.mListener = listener;
	}
	
	public void subscribe(){
		Log.d(TAG, "Activity " + mActivity.getLocalClassName() + " subscribed for location updates!");
		
		if(mLocationManager == null){
			mLocationManager = 
					(LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
			
			Log.d(TAG, "Location Manger initialized!");
		}
		
		boolean enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		if (!enabled) {
		  Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		  mActivity.startActivity(intent);
		  return;
		} 
		
		Criteria criteria = new Criteria();
	    mProvider = mLocationManager.getBestProvider(criteria, true);
	    Location location = mLocationManager.getLastKnownLocation(mProvider);
	    
	    if(location != null){
	    	Log.d(TAG, "Returning last known location!");
	    	
	    	doGeocode(location.getLatitude(), location.getLongitude());
	    } else {
	    	Log.d(TAG, "No last known location found. Requesting location updates!");
	    	
	    	mLocationManager.requestLocationUpdates(mProvider, 400, 1, this);
	    }
	}
	
	public void unsubscribe(){
		mLocationManager.removeUpdates(this);
		Log.d(TAG, "Activity " + mActivity.getLocalClassName() + " unsubscribed for location updates!");
	}
	
	@Override
	public void onLocationChanged(Location location) {		
		double lat = location.getLatitude();
		double lng = location.getLongitude();
		
		Log.d(TAG, "Got new Location: LAT(" + String.valueOf(lat) + ") LNG(" + String.valueOf(lng) + ")");
		doGeocode(lat, lng);
	}
	
	private void doGeocode(double lat, double lng){	
		Log.d(TAG, "Geocoding: LAT(" + String.valueOf(lat) + ") LNG(" + String.valueOf(lng) + ")");
		Geocoder geocoder = new Geocoder(mContext, Locale.ENGLISH);
	    
    	List<Address> addresses;
		try {
			addresses = geocoder.getFromLocation(lat, lng, 1);
 
	    	if(addresses != null) {
	    		String city = addresses.get(0).getSubAdminArea();
	    		
	    		if(city.toUpperCase().startsWith("KLAGENFURT")){
	    			Log.i(TAG, "Geocoder returned: KLAGENFURT");
	    			mListener.onCityFound(new City("Klagenfurt am Wörthersee", "http://pingeb.org"));
	    		} else if(city.toUpperCase().startsWith("GRAZ")){
	    			Log.i(TAG, "Geocoder returned: GRAZ");
	    			mListener.onCityFound(new City("Graz", "http://graz.pingeb.org"));
	    		} else if(city.toUpperCase().startsWith("WIEN")){
	    			Log.i(TAG, "Geocoder returned: WIEN");
	    			mListener.onCityFound(new City("Wien", "http://vienna.pingeb.org"));
	    		} else if(city.toUpperCase().startsWith("VILLACH")){
	    			Log.i(TAG, "Geocoder returned: VILLACH");
	    			mListener.onCityFound(new City("Villach", "http://villach.pingeb.org"));
	    		} else {
	    			Log.i(TAG, "Geocoder returned a city without pingeb.org system! " + city);
	    			mListener.onNoCityFound();
	    		}
	    	} else{
	    		Log.w(TAG, "Geocoder returned no address!");
	    		mListener.onError(new Exception("No Address found!"));
	    	}
		} catch (IOException e) {
			Log.e(TAG, "IOException during geocoding!");
			mListener.onError(e);
		}
		
		Log.d(TAG, "Removing location updates!");
		mLocationManager.removeUpdates(this);
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d(TAG, "Provider disabled! Opening Settings!");
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		mActivity.startActivity(intent);
	}

	@Override
	public void onProviderEnabled(String provider) { }

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) { }

}
