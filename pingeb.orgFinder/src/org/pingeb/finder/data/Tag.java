package org.pingeb.finder.data;

import com.google.android.gms.maps.model.LatLng;

public class Tag {

	private int mId;
	private int mPingebID;
	private System mSystem;
	private String mName;
	private int mClicks;
	private LatLng mLatlng;
	private int mGeofenceRadius;
	private boolean mGeofenceEnabled;
	private String mCurretnContentId;
	
	public Tag(int id, int pingebId, System system, String name, int clicks, LatLng latlng,
			int geofenceRadius, String geofenceEnabled, String currentContentId) {
		super();
		this.mId = id;
		this.mPingebID = pingebId;
		this.mSystem = system;
		this.mName = name;
		this.mClicks = clicks;
		this.mLatlng = latlng;
		this.mGeofenceRadius = geofenceRadius;
		this.mGeofenceEnabled = (geofenceEnabled.equals("1"));
		this.mCurretnContentId = currentContentId;
	}
	
	public int getId() {
		return mId;
	}
	public void setId(int id) {
		this.mId = id;
	}
	public System getSystem() {
		return mSystem;
	}
	public void setSystem(System system) {
		this.mSystem = system;
	}
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		this.mName = name;
	}
	public int getClicks() {
		return mClicks;
	}
	public void setClicks(int clicks) {
		this.mClicks = clicks;
	}
	public LatLng getLatlng() {
		return mLatlng;
	}
	public void setLatlng(LatLng latlng) {
		this.mLatlng = latlng;
	}
	
	public int getPingebId() {
		return mPingebID;
	}
	
	public int getGeofenceRadius(){
		return mGeofenceRadius;
	}
	
	public boolean getGeofenceEnabled(){
		return mGeofenceEnabled;
	}
	
	public String getGeofenceEnabledString(){
		if(mGeofenceEnabled){
			return "1";
		} else {
			return "0";
		}
	}
	
	public String getCurrentContentId(){
		return mCurretnContentId;
	}
}
