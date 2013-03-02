package org.pingeb.finder.net;

import java.util.ArrayList;

import org.pingeb.finder.data.Tag;

public abstract class TagLoaderCallback{
		
	public abstract void onTagsLoaded(ArrayList<Tag> tags);
	
	public abstract void onError(ArrayList<Exception> errors);
	
	public abstract void onCacheInitialized();
	
	public abstract void onCacheError(Exception ex);
		
}