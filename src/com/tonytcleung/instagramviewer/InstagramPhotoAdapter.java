package com.tonytcleung.instagramviewer;

import java.util.List;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class InstagramPhotoAdapter extends ArrayAdapter<InstagramPhoto> {
	// view cache
	private static class ViewHolder {
		ImageView imgPhoto;
		TextView tvCaption;
	}
	
	public InstagramPhotoAdapter(Context context, List<InstagramPhoto> photos) {
		super(context, android.R.layout.simple_list_item_1, photos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// take the data source
		// get data item
		InstagramPhoto photo	= getItem(position);
		
    	// use view holder pattern
    	ViewHolder viewHolder;
        // Check if an existing view is being reused, 
    	// if it is new, inflate the view and set viewHolder as tag
        if (convertView == null) {
        	viewHolder				= new ViewHolder();
        	convertView 			= LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);
            viewHolder.imgPhoto		= (ImageView) convertView.findViewById(R.id.imgPhoto);
            viewHolder.tvCaption	= (TextView) convertView.findViewById(R.id.tvCaption); 
            convertView.setTag(viewHolder);
        }
        // retrieve view holder from tag
        else {
        	viewHolder			= (ViewHolder) convertView.getTag();
        }
			
        // Populate the data into the template view using the data object
        viewHolder.tvCaption.setText(photo.caption);
        viewHolder.imgPhoto.getLayoutParams().height	= photo.imageHeight;
        // reset the image from the recycled view
        viewHolder.imgPhoto.setImageResource(0);
        // ask for the photo to be added to the imageview based on the photo url
        // background: send network request from the url, download image bytes, convert into bitmamp, resizing the image, insert bitmap into the imageview
        Picasso.with(getContext()).load(photo.imageURL).into(viewHolder.imgPhoto);
        
        // Return the completed view to render on screen
        return convertView;
	}

}
