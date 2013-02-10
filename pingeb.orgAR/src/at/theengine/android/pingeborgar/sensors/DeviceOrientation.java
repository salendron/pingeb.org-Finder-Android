package at.theengine.android.pingeborgar.sensors;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsStatus.Listener;
import android.util.Log;
import android.widget.Toast;
import at.theengine.android.pingeborgar.sensors.OnDeviceOrientationListener.DEVICE_ORIENTATION;

public class DeviceOrientation implements SensorEventListener {

	private static final String TAG = "pingebAR-DeviceOrientation";
	
	private OnDeviceOrientationListener mListener;
	private Context mContext;
	private Activity mActivity;
	private SensorManager mSensorManager = null;
	
	public DeviceOrientation(OnDeviceOrientationListener listener,
			Activity activity, Context context){
		this.mListener = listener;
		this.mContext = context;
		this.mActivity = activity;
		
		mSensorManager = (SensorManager) mActivity.getSystemService(Activity.SENSOR_SERVICE);
	}
	
	public void subscribe(){		
		mSensorManager.registerListener(this, 
				mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), 
				SensorManager.SENSOR_DELAY_UI);
	}
	
	public void unsubscribe(){		
		mSensorManager.unregisterListener(this, 
				mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) { }

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		float azimuth = Math.round(event.values[0]);
	    float pitch = Math.round(event.values[1]);
	    float roll = Math.round(event.values[2]);
		
		if (pitch == 0 && azimuth == 0) {
			mListener.onOrientationChanged(DEVICE_ORIENTATION.DOWN);
		} else {
			mListener.onOrientationChanged(DEVICE_ORIENTATION.UP);
		}
	}
	
}