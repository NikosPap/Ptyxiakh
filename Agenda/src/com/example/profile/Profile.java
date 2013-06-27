package com.example.profile;

import java.util.ArrayList;
import java.util.Collections;

import com.example.agenda.R;
import com.example.agendaMain.AgendMainActivity;
import com.example.courses.CourseItem;
import com.example.courses.CoursesDataBaseHelper;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Profile extends Activity {
	CoursesDataBaseHelper db_helper;
	SQLiteDatabase db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//set back button
        ActionBar bar = getActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        TextView bv = new TextView(Profile.this);
        bv.setTextSize(18);
        bar.setCustomView(bv);
        bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.bd1));
        
		setContentView(R.layout.activity_display_profile);
		
		db_helper = new CoursesDataBaseHelper(this);
		
		double average;
		int remain_courses,semester_courses;
		String direction;
		average = calculateAverage();
		remain_courses = remainCourses();
		semester_courses = semesterCourses();
		direction = calculateDirection();
	}
	
	private double calculateAverage(){
		ArrayList<Double> gradesK = new ArrayList<Double>();
		ArrayList<Double> gradesTh = new ArrayList<Double>();
		ArrayList<Double> gradesYs = new ArrayList<Double>();
		ArrayList<Double> gradesEp = new ArrayList<Double>();
		ArrayList<Double> gradesRest = new ArrayList<Double>();
		Double praktiki=0.0,ptyx_1=0.0,ptyx_2=0.0;
		
		db_helper.openDataBase();
		db = db_helper.getReadableDatabase();
		String select = "SELECT Grade, Code FROM Subjects WHERE Code LIKE 'ส%' AND Grade!=-1";
		String select2 = "SELECT Grade, ThP, YS, EP FROM Subjects WHERE Code NOT LIKE 'K%' AND Code NOT LIKE 'ระ%' AND Grade!=-1";
		
		Cursor cursor = db.rawQuery(select, null);
		if(cursor.moveToFirst()){
			do{
				if(cursor.getString(1) == "ส26")
					praktiki = cursor.getDouble(0);
				else if(cursor.getString(1) == "ส27")
					ptyx_1 = cursor.getDouble(0);
				else if(cursor.getString(1) == "ส28")
					ptyx_2 = cursor.getDouble(0);
				else
					gradesK.add(cursor.getDouble(0));
			}while(cursor.moveToNext());
		}
		cursor.close();
		
		cursor = db.rawQuery(select2, null);
		if(cursor.moveToFirst()){
			do{
				Double grade = cursor.getDouble(0);
				if(cursor.getDouble(1) == 1)
					gradesTh.add(grade);
				else if(cursor.getDouble(2) == 1)
					gradesYs.add(grade);
				else if(cursor.getDouble(3) ==1)
					gradesEp.add(grade);
				else
					gradesRest.add(grade);
			}while(cursor.moveToNext());
		}
		cursor.close();
		Collections.sort(gradesTh);
		Collections.sort(gradesYs);
		Collections.sort(gradesEp);
		Collections.sort(gradesRest);
		
		return 0.0;
	}
	
	private int remainCourses(){
		return 1;
	}
	
	private int semesterCourses(){
		return 1;
	}

	private String calculateDirection(){
		return "Fuck";
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_profile, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem menuItem){   
        startActivity(new Intent(this,AgendMainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        return true;
    }
	//loalalal

}
