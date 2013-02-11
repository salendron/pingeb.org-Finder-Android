package at.theengine.android.pingeborgar.sensors;
public abstract class OnDeviceLocationListener {

	public abstract void onLocationFound(double lat, double lng);
	
	public abstract void onError(Exception ex);
	
}
