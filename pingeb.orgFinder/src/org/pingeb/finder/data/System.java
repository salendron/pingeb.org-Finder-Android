package org.pingeb.finder.data;

public class System {
	
	private boolean mAvailable;
	private int mId;
	private String mUrl;
	private String mName;
	private int mDownloads;
	private int mDownloadsToday;
	private float mPercentageQr;
	private float mPercentageNfc;

	public System(String mUrl, String mName) {
		super();
		this.mUrl = mUrl;
		this.mName = mName;
	}
	
	public void setSystemValues(int id, String mUrl, String mName, int downloads, int downloadsToday, float pcQr, float pcNfc) {
		this.mId = id;
		this.mUrl = mUrl;
		this.mName = mName;
		this.mDownloads = downloads;
		this.mDownloadsToday = downloadsToday;
		this.mPercentageQr = pcQr;
		this.mPercentageNfc = pcNfc;
	}

	public boolean isAvailable() {
		return mAvailable;
	}

	public String getUrl() {
		return mUrl;
	}

	public String getName() {
		return mName;
	}

	public int getDownloads() {
		return mDownloads;
	}

	public int getDownloadsToday() {
		return mDownloadsToday;
	}

	public float getPercentageQr() {
		return mPercentageQr;
	}

	public float getPercentageNfc() {
		return mPercentageNfc;
	}
	
	public int getId(){
		return mId;
	}

	public void setAvailable(boolean mAvailable) {
		this.mAvailable = mAvailable;
	}

	public void setUrl(String mUrl) {
		this.mUrl = mUrl;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public void setDownloads(int mDownloads) {
		this.mDownloads = mDownloads;
	}

	public void setDownloadsToday(int mDownloadsToday) {
		this.mDownloadsToday = mDownloadsToday;
	}

	public void setPercentageQr(float mPercentageQr) {
		this.mPercentageQr = mPercentageQr;
	}

	public void setPercentageNfc(float mPercentageNfc) {
		this.mPercentageNfc = mPercentageNfc;
	}
	
	
}
