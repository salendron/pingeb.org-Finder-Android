package org.pingeb.finder;

import org.pingeb.finder.animation.AnimationFactory;
import org.pingeb.finder.net.TagLoader;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class BlogActivity extends Activity {

	private Button btnBlogKlagenfurt;
	private Button btnBlogGraz;
	private Button btnBlogWien;
	private Button btnBlogVillach;
	private WebView wvBlog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_blog);
		
		initViews();
		addButtonClickEvents();
	}
	
	private void initViews(){
		btnBlogKlagenfurt = (Button) findViewById(R.id.btnBlogKlagenfurt);
		btnBlogGraz = (Button) findViewById(R.id.btnBlogGraz);
		btnBlogWien = (Button) findViewById(R.id.btnBlogWien);
		btnBlogVillach = (Button) findViewById(R.id.btnBlogVillach);
		
		wvBlog = (WebView) findViewById(R.id.wvBlog);
		browse(TagLoader.getSystems()[0].getUrl());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		btnBlogKlagenfurt.startAnimation(AnimationFactory.getButtonInitAnimation());
		btnBlogGraz.startAnimation(AnimationFactory.getButtonInitAnimation());
		btnBlogWien.startAnimation(AnimationFactory.getButtonInitAnimation());
		btnBlogVillach.startAnimation(AnimationFactory.getButtonInitAnimation());
		
		btnBlogGraz.setVisibility(View.GONE);
		btnBlogWien.setVisibility(View.GONE);
		btnBlogVillach.setVisibility(View.GONE);
	}
	
	private void addButtonClickEvents(){
		btnBlogKlagenfurt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnBlogKlagenfurt.startAnimation(AnimationFactory.getButtonClickAnimation());
				browse(TagLoader.getSystems()[0].getUrl());
			}
		});

		btnBlogGraz.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnBlogGraz.startAnimation(AnimationFactory.getButtonClickAnimation());
				browse(TagLoader.getSystems()[1].getUrl());
			}
		});

		btnBlogWien.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnBlogWien.startAnimation(AnimationFactory.getButtonClickAnimation());
				browse(TagLoader.getSystems()[2].getUrl());
			}
		});

		btnBlogVillach.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnBlogVillach.startAnimation(AnimationFactory.getButtonClickAnimation());
				browse(TagLoader.getSystems()[3].getUrl());
			}
		});
	}
	
	private void browse(String url){
		wvBlog.getSettings().setJavaScriptEnabled(true);

		 final Activity activity = this;
		 wvBlog.setWebChromeClient(new WebChromeClient() {
		   public void onProgressChanged(WebView view, int progress) {
		     activity.setProgress(progress * 1000);
		   }
		 });
		 wvBlog.setWebViewClient(new WebViewClient() {
		   public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		     Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
		   }
		 });

		 wvBlog.loadUrl(url);
	}

}
