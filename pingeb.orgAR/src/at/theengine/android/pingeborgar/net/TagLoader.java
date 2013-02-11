package at.theengine.android.pingeborgar.net;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import at.theengine.android.pingeborgar.dataobjects.City;

public class TagLoader {

	public static double RADIUS_VIEWER = 0.01;
	public static double RADIUS_MAP = 2;
	
	private static final String TAG = "pingebAR-TagLoader";
	
	private static Context mContext;
	
	private static City mCity;
	
	public static void loadTags(Context context, double lat, double lng, double radius, TagLoaderCallback callback){
		final double LAT1 = lat - radius;
		final double LNG1 = lng - radius;
		final double LAT2 = lat + radius;
		final double LNG2 = lng + radius;
		final TagLoaderCallback CALLBACK = callback;
		mContext = context;
		
		Log.d(TAG, "Loading: LAT(" + String.valueOf(lat) + ") LNG(" + String.valueOf(lng) + ")");
		
		try {
			mCity = doGeocode(lat, lng);
		} catch (Exception ex) {
			callback.onError("Geocoding failed!", ex);
		}
		
		if(mCity == null){
			callback.onNoPingeborg();
			return;
		}
		
		AsyncTask task = new AsyncTask() {

			private Exception mEx;
			private JSONArray mTags;
			
			@Override
			protected void onPostExecute(Object result) {
				if(mEx != null){
					CALLBACK.onError(mEx.getMessage(), mEx);
				} else {
					CALLBACK.onTagsLoaded(mTags);
				}
			}
			
			@Override
			protected Object doInBackground(Object... arg0) {
				try {
					URL url1;
					URLConnection urlConnection;
					DataInputStream inStream;

					// Create connection
					url1 = new URL(mCity.getUrl() + "/api/tags?box=" + LAT2 + "," + LNG1 + "," + LAT1 + "," + LNG2);
					urlConnection = url1.openConnection();
					((HttpURLConnection)urlConnection).setRequestMethod("GET");
					urlConnection.setDoInput(true);
					urlConnection.setDoOutput(false);
					urlConnection.setUseCaches(false);

					inStream = new DataInputStream(urlConnection.getInputStream());

					mTags = new JSONArray(inStream.readLine());
					
					Log.d(TAG, "Got " + mTags.length() + " tags!");
				} catch(Exception e){
					Log.d(TAG, "Error while loading tags! " + e.getMessage());
					mEx = e;
				}
				return null;
			}
		};
		
		task.execute();
	}

	private static City doGeocode(double lat, double lng) throws Exception{	
		Log.d(TAG, "Geocoding: LAT(" + String.valueOf(lat) + ") LNG(" + String.valueOf(lng) + ")");
		Geocoder geocoder = new Geocoder(mContext, Locale.ENGLISH);
	    
    	List<Address> addresses;
		try {
			addresses = geocoder.getFromLocation(lat, lng, 1);
 
	    	if(addresses != null) {
	    		String city = addresses.get(0).getSubAdminArea();
	    		
	    		if(city.toUpperCase().startsWith("KLAGENFURT")){
	    			Log.i(TAG, "Geocoder returned: KLAGENFURT");
	    			return new City("Klagenfurt am Wörthersee", "http://pingeb.org");
	    		} else if(city.toUpperCase().startsWith("GRAZ")){
	    			Log.i(TAG, "Geocoder returned: GRAZ");
	    			return new City("Graz", "http://graz.pingeb.org");
	    		} else if(city.toUpperCase().startsWith("WIEN")){
	    			Log.i(TAG, "Geocoder returned: WIEN");
	    			return new City("Wien", "http://vienna.pingeb.org");
	    		} else if(city.toUpperCase().startsWith("VILLACH")){
	    			Log.i(TAG, "Geocoder returned: VILLACH");
	    			return new City("Villach", "http://villach.pingeb.org");
	    		} else {
	    			Log.i(TAG, "Geocoder returned a city without pingeb.org system! " + city);
	    			return null;
	    		}
	    	} else{
	    		Log.w(TAG, "Geocoder returned no address!");
	    		throw new Exception("No Address found!");
	    	}
		} catch (IOException e) {
			Log.e(TAG, "IOException during geocoding!");
			throw e;
		}
	}
}


