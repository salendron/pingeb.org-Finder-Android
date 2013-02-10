package at.theengine.android.pingeborgar.dataobjects;

public class City {

	private String mName;
	private String mUrl;
	
	public City(String name, String url) {
		super();
		this.mName = name;
		this.mUrl = url;
	}

	public String getName() {
		return mName;
	}

	public String getUrl() {
		return mUrl;
	}
	
	
	
}
