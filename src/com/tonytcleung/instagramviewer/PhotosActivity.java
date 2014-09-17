package com.tonytcleung.instagramviewer;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;


public class PhotosActivity extends Activity {
	private static final String TAG = "MyActivity";
	private static final String CLIENT_ID				= "bc4e71c7e67e4b0e968f7d1e36a57342";
	private static final String INSTAGRAM_POPULAR_URL	= "https://api.instagram.com/v1/media/popular?client_id=";
	
	private ArrayList<InstagramPhoto> photos;
	private InstagramPhotoAdapter photosAdapter;		
    private SwipeRefreshLayout swipeContainer;

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);	
        
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
            	// call populate photos
            	fetchPopularPhotos();
            } 
        });
        // Configure the refreshing colors        
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        
        fetchPopularPhotos();
    }
    
    private void fetchPopularPhotos() {
    	// initialize array
    	photos 									= new ArrayList<InstagramPhoto>();
    	// add to adapter
    	photosAdapter							= new InstagramPhotoAdapter(this, photos);
    	// populate list view
    	ListView lvPhotos						= (ListView) findViewById(R.id.lvPhotos);
    	lvPhotos.setAdapter(photosAdapter);
    	
    	// setup popular url endpoint
    	String popularURL						= INSTAGRAM_POPULAR_URL + CLIENT_ID;
    	// create network client
    	AsyncHttpClient client					= new AsyncHttpClient();
    	// trigger network request
    	client.get(popularURL, new JsonHttpResponseHandler() {
    		// define the success and failure callbacks
        	// handle successful response
    		@Override
    		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
    			// fired once the successful response is back
    			// response is == popular photos json
    			JSONArray photosJSON			= null;
    			
    			try {
    				photosJSON					= response.getJSONArray("data");
    				photos.clear();
    				// loop through each item and parse the json object
    				for (int i = 0; i < photosJSON.length(); i++) {
    					JSONObject photoJSON	= photosJSON.getJSONObject(i);
    					InstagramPhoto photo	= new InstagramPhoto();
    					
    					// only parse if the given objects exists
    					if (photoJSON.optJSONObject("user") != null) {
    						photo.userName		= photoJSON.getJSONObject("user").getString("username");
    						photo.userImageURL	= photoJSON.getJSONObject("user").getString("profile_picture");	
    						Log.v(TAG, photo.userName + ": " + photo.userImageURL);
    					}
    					if (photoJSON.optJSONObject("caption") != null) {
    						photo.caption		= photoJSON.getJSONObject("caption").getString("text");
    					}
    					if (photoJSON.optJSONObject("images") != null && photoJSON.getJSONObject("images").optJSONObject("standard_resolution") != null) {
    						photo.imageURL		= photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
    						photo.imageHeight	= photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
    					}
    					if (photoJSON.optJSONObject("likes") != null) {
    						photo.likesCount	= photoJSON.getJSONObject("likes").getInt("count");
    						photos.add(photo);
    					}
    				}
    				// update swipe container
    				swipeContainer.setRefreshing(false);
    				// draw the list
    				photosAdapter.notifyDataSetChanged();	
    			}catch (JSONException e) {
    				// fires if things fail
    				e.printStackTrace();
    				swipeContainer.setRefreshing(false);
    			}
     		}
    		
    		@Override
    		public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
    			// TODO Auto-generated method stub
    			super.onFailure(statusCode, headers, throwable, errorResponse);
    		}
    	});
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
