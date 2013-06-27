package com.example.agendaMain;

import com.example.agenda.R;
//import com.example.courses.Courses;
import com.example.courses.Courses;
import com.example.news.ITCutiesReaderAppActivity;
import com.example.profile.Profile;
import com.example.scedule.Schedule;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;


public class AgendMainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBar bar = getActionBar();
        TextView bv = new TextView(AgendMainActivity.this);
        bv.setTextSize(18);
        bar.setCustomView(bv);
        bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.bd1));
		
		//setContentView(R.layout.activity_agenda_main);
		setContentView(R.layout.activity_agenda_main_grid);
		GridView gridview = (GridView) findViewById(R.id.gridview);
	    gridview.setAdapter(new GridMainAdapter(this));

	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            if(position == 0)
	            	showCourses(v);
	            else if(position == 1)
	            	showSchedule(v);
	            else if(position == 2)
	            	showProfile(v);
	            else
	            	showNews(v);
	        }
	    });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.agend_main, menu);

		return true;
	}
	
	/*Called when the user clicks the "Courses" button 
	public void showCourses(View view){
		Intent intent = new Intent(this, Courses.class);
		startActivity(intent);
		
	}
	*/
	/*Called when the user clicks the "Courses" button */
	public void showCourses(View view){
		//Intent intent = new Intent(this, Courses.class);
		Intent intent = new Intent(this, Courses.class);
		startActivity(intent);
	}
	
	
	/*Called when the user clicks the "Schedule" button */
	public void showSchedule(View view){
		Intent intent = new Intent(this, Schedule.class);
		startActivity(intent);
	}
	
	/*Called when the user clicks the "News Feed" button */
	public void showNews(View view){
		Intent intent = new Intent(this, ITCutiesReaderAppActivity.class);
		startActivity(intent);
	}
	
	/*Called when the user clicks the "Profile" button */
	public void showProfile(View view){
		Intent intent = new Intent(this, Profile.class);
		startActivity(intent);
	}

}
