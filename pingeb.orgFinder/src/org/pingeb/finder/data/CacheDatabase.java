package org.pingeb.finder.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CacheDatabase extends SQLiteOpenHelper {

	private static final String TAG = "pingeb-CacheDatabase";
	
	public static final String TABLE_SYSTEM = "SYSTEM";
	public static final String SYSTEM_ID = "_id";
	public static final String SYSTEM_NAME = "SYSTEM_NAME";
	public static final String SYSTEM_URL = "SYSTEM_URL";
	public static final String SYSTEM_DOWNLOADS = "SYSTEM_DOWNLOADS";
	public static final String SYSTEM_DOWNLOADS_TODAY = "SYSTEM_DOWNLOADS_TODAY";
	public static final String SYSTEM_PC_QR = "SYSTEM_PC_QR";
	public static final String SYSTEM_PC_NFC = "SYSTEM_PC_NFC";
	
	public static final String TABLE_TAG = "TAG";
	public static final String TAG_ID = "_id";
	public static final String TAG_PINGEB_ID = "TAG_PINGEB_ID";
	public static final String TAG_NAME = "TAG_NAME";
	public static final String TAG_SYSTEM = "TAG_SYSTEM";
	public static final String TAG_CLICKS = "TAG_CLICKS";
	public static final String TAG_LAT = "TAG_LAT";
	public static final String TAG_LNG = "TAG_LNG";
	public static final String TAG_GEOFENCE_RADIUS = "TAG_GEOFENCE_RADIUS";
	public static final String TAG_GEOFENCE_ENABLED = "TAG_GEOFENCE_ENABLED";
	public static final String TAG_CURRENT_CONTENT_ID = "TAG_CURRENT_CONTENT_ID";

	private static final String DATABASE_TABLE_SYSTEM = "create table "
			+ TABLE_SYSTEM + "(" 
			+ SYSTEM_ID + " integer primary key autoincrement, " 
			+ SYSTEM_NAME + " text not null, "
			+ SYSTEM_URL + " text not null, "
			+ SYSTEM_DOWNLOADS + " integer not null, "
			+ SYSTEM_DOWNLOADS_TODAY + " integer not null, "
			+ SYSTEM_PC_QR + " float not null, "
			+ SYSTEM_PC_NFC + " float not null "
			+ ");";
	
	private static final String DATABASE_TABLE_TAG = "create table "
			+ TABLE_TAG + "(" 
			+ TAG_ID + " integer primary key autoincrement, " 
			+ TAG_SYSTEM + " int not null, "
			+ TAG_PINGEB_ID + " integer not null, "
			+ TAG_NAME + " text not null, "
			+ TAG_CLICKS + " integer not null, "
			+ TAG_LAT + " double not null, "
			+ TAG_LNG + " double not null, "
			+ TAG_GEOFENCE_RADIUS + " integer not null, "
			+ TAG_GEOFENCE_ENABLED + " text not null, "
			+ TAG_CURRENT_CONTENT_ID + " text not null "
			+ ");";

	public CacheDatabase(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	  
	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.d(TAG, "Initializing Cache DB...");
		database.execSQL(DATABASE_TABLE_SYSTEM);
		database.execSQL(DATABASE_TABLE_TAG);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(newVersion > oldVersion){
			Log.d(TAG, "Upgrading Cache DB...");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_SYSTEM);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAG);
			onCreate(db);
		}
	}

} 