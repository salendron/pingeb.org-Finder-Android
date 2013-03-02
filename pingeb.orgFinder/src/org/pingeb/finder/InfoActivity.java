package org.pingeb.finder;

import org.pingeb.finder.animation.AnimationFactory;

import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InfoActivity extends Activity {

	private LinearLayout llInfo;
	private TextView tvInfoUrl;
	private TextView tvInfoText1;
	private TextView tvInfoText2;
	private TextView tvInfoText3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		initViews();
	}
	
	private void initViews(){
		llInfo = (LinearLayout) findViewById(R.id.llInfo);
		tvInfoUrl = (TextView) findViewById(R.id.tvInfoUrl);
		tvInfoText1 = (TextView) findViewById(R.id.tvInfoText1);
		tvInfoText2 = (TextView) findViewById(R.id.tvInfoText2);
		tvInfoText3 = (TextView) findViewById(R.id.tvInfoText3);
		
		llInfo.startAnimation(AnimationFactory.getBackgroundInitAnimation());
		
		Linkify.addLinks(tvInfoUrl, Linkify.ALL);
		Linkify.addLinks(tvInfoText1, Linkify.ALL);
		Linkify.addLinks(tvInfoText2, Linkify.ALL);
		Linkify.addLinks(tvInfoText3, Linkify.ALL);
	}

}
