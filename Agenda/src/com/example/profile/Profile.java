package com.example.profile;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import com.example.agenda.R;
import com.example.agendaMain.AgendMainActivity;
import com.example.courses.CoursesDataBaseHelper;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.util.Log;
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
        
		setContentView(R.layout.profile);
		
		db_helper = new CoursesDataBaseHelper(this);
		
		double average;
		int remain_courses,semester_courses;
		String direction;
		average = calculateAverage();
		remain_courses = remainCourses();
		semester_courses = semesterCourses();
		direction = calculateDirection();
		
		remain_courses = 7;
		semester_courses = 10;
		direction = "��������� �����������";
		TextView txa = (TextView)this.findViewById(R.id.prof_average);
		txa.setText(Html.fromHtml("<big><b>Average</b></big> <br> <small>"+average+"</small>"));
		
		TextView txr = (TextView) this.findViewById(R.id.prof_remainCourses);
		txr.setText(Html.fromHtml("<big><b>Remain Courses</b></big> <br> <small>"+remain_courses+"</small>"));
		
		TextView txs = (TextView) this.findViewById(R.id.prof_semesterCourses);
		txs.setText(Html.fromHtml("<big><b>Current Semester Courses</b></big> <br> <small>"+semester_courses+"</small>"));
		
		TextView txd = (TextView) this.findViewById(R.id.prof_direction);
		txd.setText(Html.fromHtml("<big><b>Proposed direction</b></big> <br> <small>"+direction+"</small>"));
	}
	
	private double calculateAverage(){
		ArrayList<Double> gradesK = new ArrayList<Double>();
		ArrayList<Double> gradesTh = new ArrayList<Double>();
		ArrayList<Double> gradesYs = new ArrayList<Double>();
		ArrayList<Double> gradesEp = new ArrayList<Double>();
		ArrayList<Double> gradesRest = new ArrayList<Double>();
		ArrayList<Double> igrades = new ArrayList<Double>();
		double k20=0.0,k23=0.0;
		double average = 0.0;
		

		db = db_helper.getReadableDatabase();
		String select = "SELECT Grade, Code FROM Subjects WHERE (Code LIKE '�__' OR Code LIKE '�__�' OR Code LIKE '�__�')  AND Grade!=-1";
		String select2 = "SELECT Grade, ThP, YS, EP FROM Subjects WHERE Code NOT LIKE '�%' AND Code NOT LIKE '____�' AND Code NOT LIKE '��%' AND Grade!=-1";
		
		Cursor cursor = db.rawQuery(select, null);
		if(cursor.moveToFirst()){
			do{
				String code = cursor.getString(1);
				Double grade = cursor.getDouble(0);
Log.i("PROFILE",code);
				if(code.equals("�26") || code.equals("�27") || code.equals("�28"))
					igrades.add(grade);
				else if(code.equals("�20�") || code.equals("�20�")){
					if(k20 == 0.0)
						k20 = grade;
					else{
						if(grade > k20){
							if(code.equals("�20�"))
								gradesEp.add(k20);
							else
								gradesTh.add(k20);
							k20 = grade;
						}
						else{
							if(code.equals("�20�"))
								gradesEp.add(grade);
							else
								gradesTh.add(grade);
						}
							
					}					
				}
				else if(code.equals("�23�") || code.equals("�23�")){
					if(cursor.getDouble(0)>k23)
						k23 = cursor.getDouble(0);
				}
				else
					gradesK.add(cursor.getDouble(0));
			}while(cursor.moveToNext());
		}
		cursor.close();
		
		cursor = db.rawQuery(select2, null);
		if(cursor.moveToFirst()){
			do{
				Double grade = cursor.getDouble(0);
Log.i("PROFILE",String.valueOf(grade));
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
		db.close();
		db_helper.close();
		Collections.sort(gradesTh);
		Collections.sort(gradesYs);
		Collections.sort(gradesEp);
		Collections.sort(gradesRest);
		Collections.sort(igrades);
		Collections.reverse(gradesTh);
		Collections.reverse(gradesYs);
		Collections.reverse(gradesEp);
		Collections.reverse(gradesRest);
		Collections.reverse(igrades);
		
		int basicCoursesNum = 5;
		double weight_grade = 0.0, syntelestes = 0.0;
		
		//We need at least one basic course from each direction (3 basic courses)
		if(!gradesTh.isEmpty()){
Log.i("PROFILE TH","Theoritiki "+String.valueOf(gradesTh.get(0)));
			basicCoursesNum--;
			weight_grade += 2*gradesTh.get(0);
			gradesTh.remove(0);
			syntelestes += 2;
		}
		if(!gradesYs.isEmpty()){
Log.i("PROFILE YS","Ypologistika "+String.valueOf(gradesYs.get(0)));
			basicCoursesNum--;
			weight_grade += 2*gradesYs.get(0);
			gradesYs.remove(0);
			syntelestes += 2;
		}
		if(!gradesEp.isEmpty()){
Log.i("PROFILE EP", "Epeksergasia "+String.valueOf(gradesEp.get(0)));
			basicCoursesNum--;
			weight_grade += 2*gradesEp.get(0);
			gradesEp.remove(0);
			syntelestes += 2;
		}
		
		//And at least 2 basic course from whichever direction
		gradesTh.addAll(gradesYs);
		gradesTh.addAll(gradesEp);
		Collections.sort(gradesTh);
		Collections.reverse(gradesTh);
		
		for(int i=0; i<basicCoursesNum; i++){
			if(!gradesTh.isEmpty()){
				weight_grade += 2*gradesTh.get(0);
				syntelestes += 2;
				gradesTh.remove(0);
			}
			else
				break;
		}
		
		//We need 10 courses basic, epiloghs or eleuthera
		gradesRest.addAll(gradesTh);
		Collections.sort(gradesRest);
		Collections.reverse(gradesRest);
		
		for(int i=0; i<10; i++){
			if(!gradesRest.isEmpty()){
				weight_grade += 1.5*gradesRest.get(0);
				syntelestes += 1.5;
				gradesRest.remove(0);
			}
			else
				break;
		}
		
		for(int i=0; i<2; i++){
			if(!igrades.isEmpty()){
Log.i("PROFILE PTYX",String.valueOf(igrades.get(0)));
				weight_grade += 3.0*igrades.get(0);
				syntelestes += 3.0;
				igrades.remove(0);
			}
			else
				break;
		}
		
		//Finally 25 courses kormou
		for(int i=0; i<gradesK.size();i++){
Log.i("PROFILE K value",String.valueOf(gradesK.get(i)));
			weight_grade += 2.0*gradesK.get(i);
			syntelestes += 2.0;
		}
		if(k20!=0.0){
			weight_grade += 2.0*k20;
			syntelestes += 2.0;
		}
		if(k23!=0.0){
			weight_grade += 2.0*k23;
			syntelestes += 2.0;
		}
Log.i("PROFILE WEIGHT GRADE+SYNTEL",String.valueOf(weight_grade)+"  "+String.valueOf(syntelestes));
		if(weight_grade!=0)
			average = (double)weight_grade/(double)syntelestes;
		DecimalFormat df = new DecimalFormat("#.##");
		String average2 = df.format(average);
System.out.println("Profile Average is " + average2);		
		return Double.valueOf(average2);
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
}
