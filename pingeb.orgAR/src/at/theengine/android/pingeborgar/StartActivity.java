package at.theengine.android.pingeborgar;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import at.theengine.android.pingeborgar.dataobjects.City;
import at.theengine.android.pingeborgar.sensors.DeviceLocation;
import at.theengine.android.pingeborgar.sensors.OnDeviceLocationListener;
import at.theengine.android.pingeborgar.animation.AnimationFactory;

public class StartActivity extends Activity {

	private static final String TAG = "pingebAR-StartActivity";
	
	private Context mContext;
	private Activity mActivity;
	
	//location
	private DeviceLocation mDeviceLocation;
	private OnDeviceLocationListener mLocationListener;
	private City mCity;
	
	//views
	private ImageView imgLaunch;
	private LinearLayout llLocating;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		Log.d(TAG,"OnCreate");
		
		mContext = this;
		mActivity = this;
		
		initViews();
		initLocationListener();
	}
	
	private void initViews(){
		Log.d(TAG,"Initiliazing views...");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		imgLaunch = (ImageView) findViewById(R.id.imgLaunch);
		llLocating = (LinearLayout) findViewById(R.id.llLocating);
		
		llLocating.setVisibility(View.GONE);
	}
	
	private void initLocationListener(){
		Log.d(TAG,"Initiliazing LocationListener...");
		mLocationListener = new OnDeviceLocationListener(){

			@Override
			public void onCityFound(City city) {
				mCity = city;
				Toast.makeText(mContext, 
						mCity.getName(), 
						Toast.LENGTH_LONG).show();
				
				Intent viewer = new Intent();
				viewer.setClass(mContext, ViewerActivity.class);
				startActivity(viewer);
			}

			@Override
			public void onNoCityFound() {
				mCity = null;
				Toast.makeText(mContext, 
						getResources().getString(R.string.msg_no_city), 
						Toast.LENGTH_LONG).show();
				mActivity.finish();
			}

			@Override
			public void onError(Exception ex) {
				Toast.makeText(mContext, 
						ex.getMessage(), 
						Toast.LENGTH_LONG).show();
				mActivity.finish();
			}
			
		};
		
		mDeviceLocation = new DeviceLocation(mContext, this, mLocationListener);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG,"OnResume");
		AnimationFactory.doFadeAnimationWithDelay(2000, imgLaunch, llLocating, mContext);
		mDeviceLocation.subscribe();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG,"OnPause");
		AnimationFactory.doFadeAnimationWithDelay(0, llLocating, imgLaunch, mContext);
		mDeviceLocation.unsubscribe();
	}

}
