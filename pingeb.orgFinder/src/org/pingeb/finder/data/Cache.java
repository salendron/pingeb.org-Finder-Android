package org.pingeb.finder.data;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Cache {

	private static final String TAG = "pingeb-Cache";
	
	private static final String DATABASE_NAME = "pingeb-finder-cache";
	private static final int DATABASE_VERSION = 3;
	
	private SQLiteDatabase database;
	private CacheDatabase dbHelper;
	
	private String[] mSystemColumns = { 
			CacheDatabase.SYSTEM_ID,
			CacheDatabase.SYSTEM_NAME,
			CacheDatabase.SYSTEM_URL,
			CacheDatabase.SYSTEM_DOWNLOADS,
			CacheDatabase.SYSTEM_DOWNLOADS_TODAY,
			CacheDatabase.SYSTEM_PC_QR,
			CacheDatabase.SYSTEM_PC_NFC
			};
	
	private String[] mTagColumns = { 
			CacheDatabase.TAG_ID,
			CacheDatabase.TAG_PINGEB_ID,
			CacheDatabase.TAG_NAME,
			CacheDatabase.TAG_SYSTEM,
			CacheDatabase.TAG_CLICKS,
			CacheDatabase.TAG_LAT,
			CacheDatabase.TAG_LNG,
			CacheDatabase.TAG_GEOFENCE_RADIUS,
			CacheDatabase.TAG_GEOFENCE_ENABLED,
			CacheDatabase.TAG_CURRENT_CONTENT_ID
			};

	public Cache(Context context) {
		dbHelper = new CacheDatabase(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.d(TAG, "Cache initialized!");
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
		Log.d(TAG, "Cache Database opened!");
	}

	public void close() {
		dbHelper.close();
		Log.d(TAG, "Cache Database closed!");
	}
	
	public void loadSystem(System system) {
		Log.d(TAG,"Loading System " + system.getName() + " - " + system.getUrl() + "...");
		Cursor cursor;
		
		//check if System exists
		cursor = database.query(
	    		CacheDatabase.TABLE_SYSTEM,
	    		mSystemColumns, 
	    		CacheDatabase.SYSTEM_NAME + " = '" + system.getName() + "' AND " + CacheDatabase.SYSTEM_URL + " = '" + system.getUrl() + "'", 
	    		null,
	    		null, 
	    		null, 
	    		null);
		
		if(cursor.isAfterLast()){
			Log.d(TAG,"Inserting new System " + system.getName() + " - " + system.getUrl() + "...");
			
			ContentValues values = new ContentValues();
		    values.put(CacheDatabase.SYSTEM_NAME, system.getName());
		    values.put(CacheDatabase.SYSTEM_URL, system.getUrl());
		    values.put(CacheDatabase.SYSTEM_DOWNLOADS, 0);
		    values.put(CacheDatabase.SYSTEM_DOWNLOADS_TODAY, 0);
		    values.put(CacheDatabase.SYSTEM_PC_QR, 0);
		    values.put(CacheDatabase.SYSTEM_PC_NFC, 0);
		    
		    long insertId = database.insert(CacheDatabase.TABLE_SYSTEM, null,values);
		    
		    cursor = database.query(
		    		CacheDatabase.TABLE_SYSTEM,
		    		mSystemColumns, 
		    		CacheDatabase.SYSTEM_ID + " = " + insertId, 
		    		null,
		    		null, 
		    		null, 
		    		null);
		}
		
	    cursor.moveToFirst();
	    system.setSystemValues(
	    		cursor.getInt(0),
	    		system.getUrl(), 
	    		system.getName(), 
	    		cursor.getInt(3), 
	    		cursor.getInt(4), 
	    		cursor.getFloat(5), 
	    		cursor.getFloat(6));
	    cursor.close();
	}
	
	public void updateSystem(System system){
		Log.d(TAG,"Updating System " + system.getName() + " - " + system.getUrl() + "...");
		ContentValues values = new ContentValues();
	    values.put(CacheDatabase.SYSTEM_DOWNLOADS, 0);
	    values.put(CacheDatabase.SYSTEM_DOWNLOADS_TODAY, 0);
	    values.put(CacheDatabase.SYSTEM_PC_QR, 0);
	    values.put(CacheDatabase.SYSTEM_PC_NFC, 0);
		database.update(
				CacheDatabase.TABLE_SYSTEM, 
				values, 
				CacheDatabase.SYSTEM_ID + " = " + system.getId(), 
				null);
	}
	
	public List<Tag> getTagsOfSystem(System system) {
		Log.d(TAG,"Loading Tags for System " + system.getName() + " - " + system.getUrl() + "...");
		List<Tag> tags = new ArrayList<Tag>();

	    Cursor cursor = database.query(
	    		CacheDatabase.TABLE_TAG,
	    		mTagColumns, 
	    		CacheDatabase.TAG_SYSTEM + " = " + system.getId(), 
	    		null, 
	    		null, 
	    		null, 
	    		null);

	    cursor.moveToFirst();
	    
	    while (!cursor.isAfterLast()) {
	      Tag tag = new Tag(cursor.getInt(0), 
	    		  cursor.getInt(1), 
	    		  system, 
	    		  cursor.getString(2), 
	    		  cursor.getInt(4), 
	    		  new LatLng(cursor.getDouble(5), cursor.getDouble(6)),
	    		  cursor.getInt(7), 
	    		  cursor.getString(8), 
	    		  cursor.getString(9));
	      tags.add(tag);
	      cursor.moveToNext();
	    }
	    
	    cursor.close();
	    return tags;
	}
	
	public void insertTag(Tag tag) {
		Log.d(TAG,"Inserting Tag " + tag.getId() + " - " + tag.getName() + " - System " + tag.getSystem().getName() + " - " + tag.getSystem().getUrl() + "...");
	    ContentValues values = new ContentValues();
	    values.put(CacheDatabase.TAG_PINGEB_ID, tag.getPingebId());
	    values.put(CacheDatabase.TAG_NAME, tag.getName());
	    values.put(CacheDatabase.TAG_SYSTEM, tag.getSystem().getId());
	    values.put(CacheDatabase.TAG_CLICKS, tag.getClicks());
	    values.put(CacheDatabase.TAG_LAT, tag.getLatlng().latitude);
	    values.put(CacheDatabase.TAG_LNG, tag.getLatlng().longitude);
	    values.put(CacheDatabase.TAG_GEOFENCE_RADIUS, tag.getGeofenceRadius());
	    values.put(CacheDatabase.TAG_GEOFENCE_ENABLED, tag.getGeofenceEnabledString());
	    values.put(CacheDatabase.TAG_CURRENT_CONTENT_ID, tag.getCurrentContentId());
	    
	    database.insert(CacheDatabase.TABLE_TAG, null,values);
	}
	
	public void removeTagCacheForSystem(System system){
		Log.d(TAG,"Removing Tags for System " + system.getName() + " - " + system.getUrl() + "...");
		database.delete(CacheDatabase.TABLE_TAG, 
				CacheDatabase.TAG_SYSTEM + " = " + system.getId(),
		        null);
	}
	
}
