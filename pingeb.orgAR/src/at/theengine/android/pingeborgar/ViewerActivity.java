package at.theengine.android.pingeborgar;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import at.theengine.android.pingeborgar.animation.AnimationFactory;
import at.theengine.android.pingeborgar.sensors.DeviceOrientation;
import at.theengine.android.pingeborgar.sensors.OnDeviceOrientationListener;

public class ViewerActivity extends MapActivity {
	
	private static final String TAG = "pingebAR-ViewerActivity";
	
	//device orientation
	private DeviceOrientation mOrientation;
	private OnDeviceOrientationListener mOrientationListener;
	
	private Context mContext;
	private Activity mActivity;
	
	//views
	private MapView mvTags;
	private LinearLayout llMap;
	private LinearLayout llAR;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewer);
		
		Log.d(TAG,"OnCreate");
		
		mContext = this;
		mActivity = this;
		
		initViews();
		initOrientationListener();
	}
	
	private void initViews(){
		Log.d(TAG,"Initiliazing views...");
		
		mvTags = (MapView) findViewById(R.id.mvTags);
		llMap = (LinearLayout) findViewById(R.id.llMap);
		llAR = (LinearLayout) findViewById(R.id.llAR);
		
		llAR.setVisibility(View.GONE);
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
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG,"OnResume");
		mOrientation.subscribe();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG,"OnPause");
		mOrientation.unsubscribe();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
