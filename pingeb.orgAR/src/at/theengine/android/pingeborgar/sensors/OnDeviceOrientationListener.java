package at.theengine.android.pingeborgar.sensors;

public abstract class OnDeviceOrientationListener {

	public enum DEVICE_ORIENTATION {
		UP,
		DOWN
	}
	
	public abstract void onOrientationChanged(DEVICE_ORIENTATION orientation);
	
}
