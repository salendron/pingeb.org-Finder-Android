package org.pingeb.finder.net;

import java.io.DataInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.pingeb.finder.data.Cache;
import org.pingeb.finder.data.Tag;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class TagLoader {

	public static double RADIUS_VIEWER = 0.01;
	public static double RADIUS_MAP = 2;
	
	private static final String TAG = "pingeb-TagLoader";
	
	private static TagLoaderCallback mCallback;
	
	private static boolean mLoading = false;
	
	private static boolean mLoaded = false;
	
	private static org.pingeb.finder.data.System[] mSystems = {
		new org.pingeb.finder.data.System("http://pingeb.org", "Klagenfurt"),
		new org.pingeb.finder.data.System("http://graz.pingeb.org", "Graz"),
		new org.pingeb.finder.data.System("http://villach.pingeb.org", "Villach")
	};
	
	/*private static org.pingeb.finder.data.System[] mSystems = {
		new org.pingeb.finder.data.System("http://pingeb.org", "Klagenfurt"),
		new org.pingeb.finder.data.System("http://graz.pingeb.org", "Graz"),
		new org.pingeb.finder.data.System("http://vienna.pingeb.org", "Wien"),
		new org.pingeb.finder.data.System("http://villach.pingeb.org", "Villach")
	};*/
	
	public static ArrayList<Tag> mTags = new ArrayList<Tag>();
	
	public static void registerCallback(TagLoaderCallback callback){
		mCallback = callback;
	}
	
	public static void clearCallback(){
		mCallback = null;
	}
	
	public static void loadCache(Context context){
		final Context CTX = context;
		
		if(mLoading){
			return;
		}
		
		AsyncTask task = new AsyncTask() {

			private Exception mEx;
			
			@Override
			protected void onPostExecute(Object result) {
				if(mEx != null){
					Log.e(TAG, "Error while initialing Cache! " + mEx.getMessage());
					if(mCallback != null){
						mCallback.onCacheError(mEx);
					}
				} else {
					Log.d(TAG, "Cache initialized!");
					if(mCallback != null){
						mCallback.onCacheInitialized();
					}
				}
			}
			
			@Override
			protected Object doInBackground(Object... arg0) {
				try {
					mLoading = true;
					refreshCache(CTX);
				} catch(Exception e){
					mEx = e;
				} finally {
					mLoading = false;
				}
				
				return null;
			}
		};
		
		task.execute();
	}
	
	private static void refreshCache(Context context){
		mTags = new ArrayList<Tag>();
		Cache c = new Cache(context);
		try {
			c.open();
			for(int i = 0; i < mSystems.length; i++){
				c.loadSystem(mSystems[i]);
				mTags.addAll(c.getTagsOfSystem(mSystems[i]));
			}
		} finally {
			c.close();
		}
	}
	
	public static ArrayList<Tag> getTags(){
		return mTags;
	}
	
	public static org.pingeb.finder.data.System[] getSystems(){
		return mSystems;
	}
	
	public static void syncWithOnlineSystems(Context context){
		final Context CTX = context;
		
		if(mLoading){
			return;
		}
		
		if(mLoaded){
			Log.i(TAG,"Do not refresh. Data allready synched!");
			return;
		}
		
		AsyncTask task = new AsyncTask() {

			private ArrayList<Exception> mErrors = new ArrayList<Exception>();
			
			@Override
			protected void onPostExecute(Object result) {
				if(mErrors.size()  > 0){
					if(mCallback != null){
						mCallback.onError(mErrors);
					}
				} else {
					if(mCallback != null){
						mCallback.onTagsLoaded(mTags);
					}
				}
			}
			
			@Override
			protected Object doInBackground(Object... arg0) {
				Cache c = new Cache(CTX);
				ArrayList<Tag> systemTags;
				
				mLoading = true;
				
				boolean first = false;
				if(mTags.size() == 0){
					first = true;
				}
				
				c.open();
				for(int i = 0; i < mSystems.length; i++){
					try {
						//sync system
						syncSystem(mSystems[i]);
						c.updateSystem(mSystems[i]);
						
						//sync tags of system
						systemTags = syncTagsForSystem(mSystems[i]);
						
						if(first){
							mTags.addAll(systemTags);
							mCallback.onTagsLoaded(mTags);
						} else {
							c.removeTagCacheForSystem(mSystems[i]);
							for(int j = 0; j < systemTags.size(); j++){
								c.insertTag(systemTags.get(j));
							}
						}
						
					} catch(Exception e){
						Log.w(TAG, "Sync-Error for System" + mSystems[i].getName() + "! " + e.getMessage());
						Exception ex = new Exception("System " + mSystems[i].getName() + " konnte nicht synchronisiert werden!", e);
						//mErrors.add(ex);
					}
				}
				
				if(!first){
					refreshCache(CTX);
				} else {
					for(int j = 0; j < mTags.size(); j++){
						try {
							c.insertTag(mTags.get(j));
						} catch(Exception ex) { }
					}
				}
				
				mLoading = false;
				mLoaded = true;
				c.close();
				
				return null;
			}
		};
		
		task.execute();
	}
	
	private static void syncSystem(org.pingeb.finder.data.System system){
		try {
			URL url1;
			URLConnection urlConnection;
			DataInputStream inStream;
			
			url1 = new URL(system.getUrl() + "/api/systemStatistics");
			urlConnection = url1.openConnection();
			((HttpURLConnection)urlConnection).setRequestMethod("GET");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(false);
			urlConnection.setUseCaches(false);
	
			inStream = new DataInputStream(urlConnection.getInputStream());
	
			JSONObject jsonSystem = new JSONObject(inStream.readLine());
			
			system.setAvailable(true);
			system.setDownloads(jsonSystem.getInt("downloads"));
			system.setDownloadsToday(jsonSystem.getInt("downloadsToday"));
			system.setPercentageQr(jsonSystem.getInt("percentageQr"));
			system.setPercentageNfc(jsonSystem.getInt("percentageNfc"));
			
			inStream.close();
		} catch(Exception ex) {
			system.setAvailable(false);
			Log.e(TAG, "Failed to sync System! " + ex.getMessage());
		}
	}
	
	private static ArrayList<Tag> syncTagsForSystem(org.pingeb.finder.data.System system) throws Exception{
		try {
			ArrayList<Tag> tags = new ArrayList<Tag>();
			
			URL url1;
			URLConnection urlConnection;
			DataInputStream inStream;
			
			url1 = new URL(system.getUrl() + "/api/tags");
			urlConnection = url1.openConnection();
			((HttpURLConnection)urlConnection).setRequestMethod("GET");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(false);
			urlConnection.setUseCaches(false);
	
			inStream = new DataInputStream(urlConnection.getInputStream());
	
			JSONArray jsonTags = new JSONArray(inStream.readLine());
			Tag tag;
			JSONObject jsonTag;
			for(int i = 0; i < jsonTags.length(); i++){
				jsonTag = jsonTags.getJSONObject(i);
				
				//try to get new fields
				int geofenceRadius = 0;
				String geofenceEnabled = "0";
				String currentContentId = "";
				try {
					geofenceRadius = jsonTag.getInt("geofence_radius");
					geofenceEnabled = jsonTag.getString("geofence_enabled");
					currentContentId = jsonTag.getString("current_content_id");
				} catch(Exception ex) { 
					Log.i(TAG, "Old System detected!");
				}
				
				tag = new Tag(-1, 
						jsonTag.getInt("id"), 
						system, 
						jsonTag.getString("name"), 
						jsonTag.getInt("clicks"), 
						new LatLng(jsonTag.getDouble("lat"), jsonTag.getDouble("lon")),
						geofenceRadius,
						geofenceEnabled,
						currentContentId);
				
				tags.add(tag);
			}
			
			system.setAvailable(true);
			inStream.close();
			
			return tags;
		} catch(Exception ex) {
			system.setAvailable(false);
			throw ex;
		}
	}
}


