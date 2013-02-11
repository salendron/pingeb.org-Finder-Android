package at.theengine.android.pingeborgar.sensors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

public class DeviceLocation implements LocationListener {

	private static final String TAG = "pingebAR-DeviceLocation";
	
	private LocationManager mLocationManager;
	private Activity mActivity;
	private String mProvider;
	private OnDeviceLocationListener mListener;
	
	public DeviceLocation(Activity activity, OnDeviceLocationListener listener){
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
	    	
	    	mListener.onLocationFound(location.getLatitude(), location.getLongitude());
	    } else {
	    	Log.d(TAG, "No last known location found. Requesting location updates!");
	    }
	    
	    mLocationManager.requestLocationUpdates(mProvider, 400, 1, this);
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
		mListener.onLocationFound(lat, lng);
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
