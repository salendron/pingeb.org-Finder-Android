package at.theengine.android.pingeborgar.sensors;

import at.theengine.android.pingeborgar.dataobjects.City;

public abstract class OnDeviceLocationListener {

	public abstract void onCityFound(City city);
	
	public abstract void onNoCityFound();
	
	public abstract void onError(Exception ex);
	
}
