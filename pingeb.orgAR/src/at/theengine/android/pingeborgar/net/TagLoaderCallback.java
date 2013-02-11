package at.theengine.android.pingeborgar.net;

import org.json.JSONArray;

public abstract class TagLoaderCallback{
		
	public abstract void onTagsLoaded(JSONArray tags);
		
	public abstract void onNoPingeborg();
	
	public abstract void onError(String msg, Exception ex);
		
}