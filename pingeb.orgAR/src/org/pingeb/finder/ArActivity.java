package org.pingeb.finder;

import java.io.IOException;
import java.util.ArrayList;

import org.pingeb.finder.ar.AbstractArViewer;
import org.pingeb.finder.data.System;
import org.pingeb.finder.data.Tag;
import org.pingeb.finder.net.TagLoader;
import org.pingeb.finder.net.TagLoaderCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.nfc.tech.TagTechnology;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.metaio.sdk.SensorsComponentAndroid;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.IRadar;
import com.metaio.sdk.jni.LLACoordinate;
import com.metaio.tools.io.AssetsManager;

public class ArActivity extends AbstractArViewer implements SensorsComponentAndroid.Callback 
{
	private static final String TAG = "pingeb-ArActivity";
	
	private IGeometry[] mGeometries;
	private IRadar mRadar;
	private Context mContext;
	
	private TagLoaderCallback mLoaderCallback;
	
	private LLACoordinate mLocation;
	
	private String mRadarImg;
	private String mPingImg;
	private String mDotImg;
	
	private ArrayList<Tag> mMarkersToShow;
	
	private Activity mActivity;
	
	private Location mTagLocation;
	
	private int mOrientation;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		try {
			AssetsManager.extractAllAssets(this, false);
		} catch (IOException e) {
			Log.e(TAG, "loadTags:" + e.getMessage());
		}
		
		mDotImg = AssetsManager.getAssetPath("yellow.png");
		mPingImg = AssetsManager.getAssetPath("pin60.png");
		mRadarImg = AssetsManager.getAssetPath("radar.png");
		
		mContext = this;
		mActivity = this;
		
		boolean result = metaioSDK.setTrackingConfiguration("GPS");  
		
		metaioSDK.setLLAObjectRenderingLimits(1, 7000);
		//metaioSDK.setLLAObjectRenderingLimits(0, 0);
		
		mSensors.start(mSensors.SENSOR_ALL);
		
		mLocation = mSensors.getLocation();
		
		initLoaderCallback();
		
		mOrientation = getResources().getConfiguration().orientation;
	}
	
	private void initLoaderCallback(){
		mLoaderCallback = new TagLoaderCallback() {
			
			@Override
			public void onTagsLoaded(ArrayList<Tag> tags) {				
				Log.d(TAG,"onTagsLoaded: Loaded " + String.valueOf(tags.size()) + " Tags!");
				
				if(mRadar != null){
					drawTags();
				}
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
				Toast.makeText(mContext, "Fehler beim Synchronisieren der Tags! - " + sb.toString(), Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onCacheInitialized() {				
				Log.i(TAG,"onError: " + "onCacheInitialized: Loaded " + 
						String.valueOf(TagLoader.getSystems().length) + " Systems and " + 
						String.valueOf(TagLoader.getTags().size()) + " Tags!");
				
				if(mRadar != null){
					drawTags();
				}
				
				TagLoader.syncWithOnlineSystems(mContext);
			}
			
			@Override
			public void onCacheError(Exception ex) {
				Log.e(TAG,"onError: " + "onCacheError: " + ex.getMessage());
				Toast.makeText(mContext, "Fehler: " + ex.getMessage(), Toast.LENGTH_LONG).show();
			}
		};
	}
	
	@Override
	protected void onPause() 
	{
		super.onPause();
		if (mSensors != null)
		{
			mSensors.registerCallback(null);
		}
		
		if(mOrientation == getResources().getConfiguration().orientation){
			metaioSDK.delete();
			this.finish();
		}
		
		TagLoader.clearCallback();
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		if (mSensors != null)
		{
			mSensors.registerCallback(this);
		}
		
		TagLoader.registerCallback(mLoaderCallback);
	}

	@Override
	public void onLocationSensorChanged(LLACoordinate location)
	{
		mLocation = location;
		
		if(mRadar != null){
			drawTags();
		}
	}

	@Override
	protected int getGUILayout() 
	{
		return R.layout.activity_ar;
	}

	@Override
	protected IMetaioSDKCallback getMetaioSDKCallbackHandler() 
	{
		return null;
	}

	@Override
	protected void loadTags() 
	{
		mRadar = metaioSDK.createRadar();
		
		mRadar.setBackgroundTexture(mRadarImg);
		mRadar.setObjectsDefaultTexture(mDotImg);
		mRadar.setRelativeToScreen(IGeometry.ANCHOR_TL);
		
		drawTags();
		
		if(TagLoader.getTags().size() == 0){
			Toast.makeText(mContext, 
					"Synchronisiere Tags zum ersten Mal. Das kann je nach Netzwerkverbindung etwas dauern...", 
					Toast.LENGTH_LONG).show();
		}
	}
	
	private void drawTags(){
		try{
			mMarkersToShow = new ArrayList<Tag>();
			Location myLocation = new Location("NONE");
			if(mLocation != null){
				myLocation.setLatitude(mLocation.getLatitude());
				myLocation.setLongitude(mLocation.getLongitude());
			} else {
				myLocation.setLatitude(0);
				myLocation.setLongitude(0);
			}
			
			mRadar.removeAll();
			
			Location tagLocation;
			for(int i = 0; i < TagLoader.getTags().size(); i++){
				tagLocation = new Location("NONE");
				tagLocation.setLatitude(TagLoader.getTags().get(i).getLatlng().latitude);
				tagLocation.setLongitude(TagLoader.getTags().get(i).getLatlng().longitude);
				
				if(myLocation.distanceTo(tagLocation) <= 300){
					mMarkersToShow.add(TagLoader.getTags().get(i));
				}
			}
			
			if(mMarkersToShow.size() == 0){
				mMarkersToShow.add(new Tag(-1, -1, new System("", "None"), "Keine Tags in der N�he...", 0, new LatLng(0, 0), 0, "0", ""));
			}
			
			mGeometries = new IGeometry[mMarkersToShow.size()];
			
			for(int i = 0; i < mMarkersToShow.size(); i++){
				mGeometries[i] = metaioSDK.loadImageBillboard(mPingImg);
				mGeometries[i].setScale(4);
				mGeometries[i].setName(mMarkersToShow.get(i).getName());
				
				mGeometries[i].setTranslationLLA(new LLACoordinate(
						TagLoader.getTags().get(i).getLatlng().latitude, 
						TagLoader.getTags().get(i).getLatlng().longitude, 
						0, 
						1));
				
				mRadar.add(mGeometries[i]);
			} 
		} catch(Exception ex) {
			Log.e(TAG,ex.getMessage());
		}
	}
	
	@Override
	protected void onGeometryTouched(final IGeometry geometry) 
	{		
		AlertDialog.Builder builder;
		AlertDialog alertDialog;
		
		LayoutInflater inflater = (LayoutInflater)
		        mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.marker_popup,
		        (ViewGroup) findViewById(R.id.llPopup));
		
		mTagLocation = null;
		for(int i = 0; i < mMarkersToShow.size(); i++){
			if(geometry.getName().equals(mMarkersToShow.get(i).getName())){
				mTagLocation = new Location("NONE");
				mTagLocation.setLatitude(mMarkersToShow.get(i).getLatlng().latitude);
				mTagLocation.setLongitude(mMarkersToShow.get(i).getLatlng().longitude);
			}
		}
		
		if(mTagLocation == null){
			Toast.makeText(mContext, "Geometry not found", Toast.LENGTH_SHORT).show();
			return;
		}
		
		String distance;
		
		Location myLocation;
		if(mLocation != null){
			myLocation = new Location("NONE");
			myLocation.setLatitude(mLocation.getLatitude());
			myLocation.setLongitude(mLocation.getLongitude());
			
			int meters = Math.round(myLocation.distanceTo(mTagLocation));
			distance = "Entfernung: " + String.valueOf(meters) + "m";
		} else {
			distance = "??m";
		}
		
		TextView tvTagName = (TextView)layout.findViewById(R.id.tvTagName);
		tvTagName.setText(geometry.getName());
		
		TextView tvTagInfo = (TextView)layout.findViewById(R.id.tvTagInfo);
		tvTagInfo.setText(distance);
		
		ImageButton btnNavigate = (ImageButton)layout.findViewById(R.id.btnNavigate);
		btnNavigate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent =new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(
						Uri.parse(
								"google.navigation:ll=" + 
								String.valueOf(mTagLocation.getLatitude()) + 
								"," + String.valueOf(mTagLocation.getLongitude())
								)
						);
				mActivity.startActivity(intent);
			}
		});
		
		layout.setPadding(5, 5, 10, 5);
		layout.setBackgroundColor(mActivity.getResources().getColor(android.R.color.white));
		
		builder = new AlertDialog.Builder(mContext);
		builder.setView(layout);
		alertDialog = builder.create();
		
		alertDialog.show();
	}

	@Override
	public void onGravitySensorChanged(float[] gravity) {
	}

	@Override
	public void onHeadingSensorChanged(float[] orientation) {
	}

}
