package org.pingeb.finder;

import java.util.ArrayList;
import java.util.Random;

import org.pingeb.finder.animation.AnimationFactory;
import org.pingeb.finder.data.Tag;
import org.pingeb.finder.net.TagLoader;
import org.pingeb.finder.net.TagLoaderCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LaunchActivity extends Activity {

	private static final String TAG = "pingeb-LaunchActivity";
	
	private ImageView mImgLandLaunch;
	private ProgressBar pbLaunchLoading;
	private ImageButton mBtnStartAr;
	private ImageButton mBtnStartMap;
	private ImageButton mBtnAbout;
	private ImageButton mBtnStartBlog;
	
	private Context mContext;
	
	private Random randomGenerator;
	
	private TagLoaderCallback mLoaderCallback;
	private boolean initialized = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		
		mContext = this;
		randomGenerator = new Random();
		
		initViews();
		addButtonClickEvents();
		initLoaderCallback();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(!initialized){
			pbLaunchLoading.setVisibility(View.VISIBLE);
			TagLoader.registerCallback(mLoaderCallback);
			TagLoader.loadCache(mContext);
		}
		
		mBtnStartAr.startAnimation(AnimationFactory.getButtonInitAnimation());
		mBtnStartMap.startAnimation(AnimationFactory.getButtonInitAnimation());
		mBtnStartBlog.startAnimation(AnimationFactory.getButtonInitAnimation());
		
		if(getResources().getConfiguration().orientation ==
				Configuration.ORIENTATION_LANDSCAPE){
			mBtnAbout.startAnimation(AnimationFactory.getButtonInitAnimation());
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		TagLoader.clearCallback();
	}
	
	private void initLoaderCallback(){
		mLoaderCallback = new TagLoaderCallback() {
			
			@Override
			public void onTagsLoaded(ArrayList<Tag> tags) {
				Log.d(TAG,"onTagsLoaded: Loaded " + String.valueOf(tags.size()) + " Tags!");
				initialized = true;
				pbLaunchLoading.setVisibility(View.GONE);
				//Toast.makeText(mContext, "onTagsLoaded: Loaded " + String.valueOf(tags.size()) + " Tags!", Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onError(ArrayList<Exception> errors) {
				StringBuilder sb = new StringBuilder();
				for(int i = 0; i < errors.size(); i++){
					sb.append(errors.get(i).getMessage());
					sb.append(" | ");
				}
				Log.e(TAG,"onError: " + sb.toString());
				//Toast.makeText(mContext, "Fehler beim Synchronisieren der Tags! - " + sb.toString(), Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onCacheInitialized() {
				Log.i(TAG,"onError: " + "onCacheInitialized: Loaded " + 
						String.valueOf(TagLoader.getSystems().length) + " Systems and " + 
						String.valueOf(TagLoader.getTags().size()) + " Tags!");
				
				/*Toast.makeText(mContext, "onCacheInitialized: Loaded " + 
						String.valueOf(TagLoader.getSystems().length) + " Systems and " + 
						String.valueOf(TagLoader.getTags().size()) + " Tags!"
						, Toast.LENGTH_SHORT).show(); */
				
				TagLoader.syncWithOnlineSystems(mContext);
			}
			
			@Override
			public void onCacheError(Exception ex) {
				Log.e(TAG,"onError: " + "onCacheError: " + ex.getMessage());
				//Toast.makeText(mContext, "Fehler: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
			}
		};
	}
	
	private void initViews(){
		pbLaunchLoading = (ProgressBar) findViewById(R.id.pbLaunchLoading);
		mBtnStartAr = (ImageButton) findViewById(R.id.btnStartAr);
		mBtnStartMap = (ImageButton) findViewById(R.id.btnStartMap);
		mBtnStartBlog = (ImageButton) findViewById(R.id.btnStartBlog);
		mBtnAbout = (ImageButton) findViewById(R.id.btnAbout);
		
		pbLaunchLoading.setVisibility(View.GONE);
		
		if(getResources().getConfiguration().orientation ==
				Configuration.ORIENTATION_LANDSCAPE){
			mImgLandLaunch = (ImageView) findViewById(R.id.imgLandLaunch);
			
			int[] images = new int[4];
			images[0] = R.drawable.launchimg1;
			images[1] = R.drawable.launchimg2;
			images[2] = R.drawable.launchimg3;
			images[3] = R.drawable.launchimg4;
			
			int index = randomGenerator.nextInt(images.length);			
			mImgLandLaunch.setImageResource(images[index]);
		}
	}
	
	private void addButtonClickEvents(){
		mBtnAbout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mBtnAbout.startAnimation(AnimationFactory.getButtonClickAnimation());
				
				Intent info = new Intent();
				info.setClass(mContext, InfoActivity.class);
				startActivity(info);
			}
		});
		
		mBtnStartAr.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mBtnStartAr.startAnimation(AnimationFactory.getButtonClickAnimation());
				
				Intent ar = new Intent();
				ar.setClass(mContext, ArActivity.class);
				startActivity(ar);
			}
		});

		mBtnStartMap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mBtnStartMap.startAnimation(AnimationFactory.getButtonClickAnimation());
				
				Intent map = new Intent();
				map.setClass(mContext, MapViewerActivity.class);
				startActivity(map);
			}
		});
		
		mBtnStartBlog.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mBtnStartBlog.startAnimation(AnimationFactory.getButtonClickAnimation());
				
				Intent blog = new Intent();
				blog.setClass(mContext, BlogActivity.class);
				startActivity(blog);
			}
		});
	}

}
