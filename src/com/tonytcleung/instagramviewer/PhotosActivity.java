package com.tonytcleung.instagramviewer;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;


public class PhotosActivity extends Activity {

	public static final String CLIENT_ID		= "bc4e71c7e67e4b0e968f7d1e36a57342";
	private ArrayList<InstagramPhoto> photos;
	private InstagramPhotoAdapter photosAdapter;		
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);	
        
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
    	String popularURL						= "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;
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
    					if (photoJSON.getJSONObject("caption") != null) {
    						photo.userName		= photoJSON.getJSONObject("user").getString("username");
    					}
    					if (photoJSON.getJSONObject("caption") != null) {
    						photo.caption		= photoJSON.getJSONObject("caption").getString("text");
    					}
    					if (photoJSON.getJSONObject("images").getJSONObject("standard_resolution") != null) {
    						photo.imageURL		= photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
    						photo.imageHeight	= photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
    					}
    					if (photoJSON.getJSONObject("likes") != null) {
    						photo.likesCount	= photoJSON.getJSONObject("likes").getInt("count");
    						photos.add(photo);
    					}
    				}
    				// draw the list
    				photosAdapter.notifyDataSetChanged();	
    			}catch (JSONException e) {
    				// fires if things fail
    				e.printStackTrace();
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
