package at.theengine.android.pingeborgar.animation;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

public class AnimationFactory {

	public static Animation getButtonClickAnimation(){	
		Animation anim;
		
		anim = new AlphaAnimation(0, 1);
		
		anim.setDuration(500);
		anim.setRepeatMode(0);
		anim.setFillAfter(true);
		anim.setRepeatCount(0);
		
		return anim;
	}
	
	public static void doFadeAnimation(View outView, View inView, Context c){	
		final View iv = inView;
		final View ov = outView;
		
		final Animation fadeInAnim = AnimationUtils.makeInAnimation(c, false);
		
		fadeInAnim.setDuration(500);
		fadeInAnim.setRepeatMode(0);
		fadeInAnim.setFillAfter(false);
		fadeInAnim.setRepeatCount(0);	
		
		final Animation fadeOutAnim = AnimationUtils.makeOutAnimation(c, true);
		fadeOutAnim.setDuration(500);
		fadeOutAnim.setRepeatMode(0);
		fadeOutAnim.setFillAfter(false);
		fadeOutAnim.setRepeatCount(0);	
		
		fadeOutAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) { }
			
			@Override
			public void onAnimationRepeat(Animation animation) { }
			
			@Override
			public void onAnimationEnd(Animation animation) {
				ov.setVisibility(View.GONE);
				
				if(iv != null){
					iv.setVisibility(View.VISIBLE);
					iv.startAnimation(fadeInAnim);
				}
			}
		});
		
		if(ov != null){
			ov.startAnimation(fadeOutAnim);
		} else {
			iv.startAnimation(fadeInAnim);
		}
	}	
	
	public static void doFadeAnimationWithDelay(int delay, View outView, View inView, Context c){
		final int d = delay;
		final View i = inView;
		final View o = outView;
		final Context context = c;
		
		(new AsyncTask(){

			@Override
			protected void onPostExecute(Object result) {
				super.onPostExecute(result);
				doFadeAnimation(o, i, context);
			}
			
			@Override
			protected Object doInBackground(Object... params) {
				try {
					Thread.sleep(d);
				} catch (InterruptedException e) { }
				return null;
			}
			
		}).execute();
	}
}
