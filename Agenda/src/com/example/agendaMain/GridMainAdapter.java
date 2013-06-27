package com.example.agendaMain;

import com.example.agenda.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridMainAdapter extends BaseAdapter {
    private Context mContext;

    public GridMainAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View gridView;

		if (convertView == null) {

			gridView = new View(mContext);

			// get layout from mobile.xml
			gridView = inflater.inflate(R.layout.activity_agenda_main_grid2, null);

			// set value into textview
			TextView textView = (TextView) gridView.findViewById(R.id.grid_item_label);
			textView.setText(imageValues[position]);

			// set image based on selected text
			ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);
			imageView.setImageResource(mThumbIds[position]);
		} 
		else {
			gridView = (View) convertView;
		}

		return gridView;
	}

    // create a new ImageView for each item referenced by the Adapter
    /*
     public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }
    */

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.book, R.drawable.callendar, R.drawable.profile, R.drawable.rss
    };
    
    //values of images
    private String[] imageValues = {
    		"Courses", "Schedule", "Profile", "News"
    };
}
