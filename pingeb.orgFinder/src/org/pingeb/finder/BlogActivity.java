package org.pingeb.finder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.pingeb.finder.animation.AnimationFactory;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import at.theengine.android.simple_rss2_android.SimpleRss2Parser;
import at.theengine.android.simple_rss2_android.SimpleRss2ParserCallback;
import at.theengine.android.simple_rss2_android.RSSItem;

public class BlogActivity extends Activity {

	private static final String TAG = "pingeb-BlogActivity";
	
	private LinearLayout llLoading;
	private ListView lvFeedItems;
	
	private List<RSSItem> allItems;
	private int feedCount;
	
	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_blog);
		
		mContext = this;
		
		feedCount = 0;
		allItems = new ArrayList<RSSItem>();
		
		initViews();
		loadRSS();
	}
	
	private void initViews(){		
		lvFeedItems = (ListView) findViewById(R.id.lvFeedItems);
		llLoading = (LinearLayout) findViewById(R.id.llLoading);
		lvFeedItems.setVisibility(View.GONE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	private void initRSSLoaderCallback(){
		/*mCallback = new RSSLoaderCallback() {
			
			@Override
			public void onRSSLoaded() {
				Log.d(TAG,"onRSSLoaded: Loaded " + String.valueOf(RSSLoader.getItems().size()) + " RSS Items!");
				
				for(int i = 0; i < RSSLoader.getItems().size(); i++){
					Log.d(TAG, RSSLoader.getItems().get(i).getTimestamp() + " - " + RSSLoader.getItems().get(i).getTitle());
				}
				
				lvFeedItems.setAdapter(new RSSListAdapter(mContext,R.layout.rss_list_item, RSSLoader.getItems()));
			}
			
			@Override
			public void onError(Exception ex) {
				Log.e(TAG,"onError (RSS): " + ex.getMessage());
			}
		};*/
	}
	
	private void loadRSS(){
		SimpleRss2Parser parserKlu = new SimpleRss2Parser("http://pingeb.org/feed", 
			    new SimpleRss2ParserCallback() {
			        @Override
			        public void onFeedParsed(List<RSSItem> items) {
			            for(int i = 0; i < items.size(); i++){
			                items.get(i).setContent("Klagenfurt");
			                
			                try {
			                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss z",Locale.US);
			                    Date date = sdf.parse(items.get(i).getDate());

			                    items.get(i).setDate(String.valueOf(date.getTime()));
							} catch (ParseException e) {
								items.get(i).setDate("Datum konnte nicht ausgelesen werden!");
							}
			            }
			            
			            displayFeed(items);
			        }
			        @Override
			        public void onError(Exception ex) {
			            Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_SHORT).show();
			        }
			    }
			);
			parserKlu.parseAsync();
			
			SimpleRss2Parser parserGraz = new SimpleRss2Parser("http://graz.pingeb.org/feed", 
				    new SimpleRss2ParserCallback() {
				        @Override
				        public void onFeedParsed(List<RSSItem> items) {
				            for(int i = 0; i < items.size(); i++){
				                items.get(i).setContent("Graz");
				                
				                try {
				                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss z",Locale.US);
				                    Date date = sdf.parse(items.get(i).getDate());

				                    items.get(i).setDate(String.valueOf(date.getTime()));
								} catch (ParseException e) {
									items.get(i).setDate("Datum konnte nicht ausgelesen werden!");
								}
				            }
				            
				            displayFeed(items);
				        }
				        @Override
				        public void onError(Exception ex) {
				            Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_SHORT).show();
				        }
				    }
				);
			parserGraz.parseAsync();
			
			SimpleRss2Parser parserVl = new SimpleRss2Parser("http://villach.pingeb.org/feed", 
				    new SimpleRss2ParserCallback() {
				        @Override
				        public void onFeedParsed(List<RSSItem> items) {
				            for(int i = 0; i < items.size(); i++){
				                items.get(i).setContent("Villach");
				                
				                try {
				                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss z",Locale.US);
				                    Date date = sdf.parse(items.get(i).getDate());

				                    items.get(i).setDate(String.valueOf(date.getTime()));
								} catch (ParseException e) {
									items.get(i).setDate("Datum konnte nicht ausgelesen werden!");
								}
				            }
				            
				            displayFeed(items);
				        }
				        @Override
				        public void onError(Exception ex) {
				            Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_SHORT).show();
				            
				            displayFeed(null);
				        }
				    }
				);
			parserVl.parseAsync();
	}
	
	private void displayFeed(List<RSSItem> items){
		if(items != null){
			allItems.addAll(items);
		}
		
		feedCount++;
		
		if(feedCount == 3){
			Collections.sort(allItems);
			lvFeedItems.setAdapter(new RSSListAdapter(mContext,R.layout.rss_list_item, allItems));
			AnimationFactory.doFadeAnimation(llLoading, lvFeedItems, mContext);
		}
	}
	
	
	
	private class RSSListAdapter extends ArrayAdapter<RSSItem> {

        private ArrayList<RSSItem> items;
        private Context ctx;
        private int layout;

        public RSSListAdapter(Context context, int layout, List<RSSItem> items) {
                super(context, layout, items);
                this.items = (ArrayList<RSSItem>) items;
                this.ctx = context;
                this.layout = layout;
        }
        
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(layout, null);
                }
                
                RSSItem o = items.get(position);
                if (o != null) {
                	TextView tvSystem = ((TextView) v.findViewById(R.id.tvSystem));
                	TextView tvTitle = ((TextView) v.findViewById(R.id.tvTitle));
                	TextView tvDescription = ((TextView) v.findViewById(R.id.tvDescription));
                	TextView tvLnk = ((TextView) v.findViewById(R.id.tvLnk));
                	
                	if (tvSystem != null) {
                		String date = o.getDate();
                		
                		try{
                			date = new Date(Long.parseLong(o.getDate())).toLocaleString();
                		} catch (Exception ex) {}
                		
                		tvSystem.setText(o.getContent() + " - " + date);
                    }
                	
                	if (tvTitle != null) {
                		tvTitle.setText(o.getTitle());
                    }
                	
                	if (tvDescription != null) {
                    	tvDescription.setText(o.getDescription());
                    }
                	
                	if (tvLnk != null) {
                    	tvLnk.setText("weiterlesen: " + o.getLink());
                    	Linkify.addLinks(tvLnk, Linkify.ALL);
                    }
                }
                
                return v;
        }
    }

}
