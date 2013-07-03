package com.example.profile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import com.example.agenda.R;
import com.example.agendaMain.AgendMainActivity;
import com.example.courses.CoursesDataBaseHelper;
import com.example.news.ITCutiesReaderAppActivity;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

public class Profile extends Activity {
	CoursesDataBaseHelper db_courseHelper;
	SQLiteDatabase dbC;
	Set<String> set;
	double average = 0.0;
	int remain_courses = 48;
	int semester_courses = 0;
	String direction;
	int direC = 0;
	Context ctx = this;
	
	//kor=remain courses kormou -- bas=remain basic courses -- epil=remain choice courses -- remain general courses
	int kor = 27, bas = 5, epil = 10, gen = 6;
	
	//th,ys,ep true if user has pass one basic course from each direction
	boolean th = false, ys = false, ep = false;
	
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
        
		setContentView(R.layout.activity_agenda_profile);
		
		db_courseHelper = new CoursesDataBaseHelper(this);
        try { 
        	db_courseHelper.createDataBase();
        } catch (IOException ioe) {
        	throw new Error("Unable to create database");
        }
		
        
        //Calculate and display AVERAGE and REMAIN COURSES
		calculateAverage_RemainCourses();		
		TextView txa = (TextView)this.findViewById(R.id.prof_average);
		txa.setText(Html.fromHtml("<big><b>Average</b></big> <br> <small>"+average+"</small>"));
		
		TextView txr = (TextView) this.findViewById(R.id.prof_remainCourses);
		txr.setText(Html.fromHtml("<big><b>Remain Courses</b></big> <br> <small>"+remain_courses+"</small>"));
		txr.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	TextView ms = new TextView(ctx);
		    	String basika="";
		    	int bas2 = bas;
		    	
		    	if(bas != 0){
		    		if(!th || !ys || !ep){
		    			basika = "( ";
		    			if(!th){
		    				basika += "1 από Θεωρητική Πληροφορική";
		    				bas2--;
		    			}
		    			if(!ys){
		    				if(!th)
		    					basika += " - ";
		    				basika += "1 από Υπολογιστικών Συστημάτων και εφαρμογών";
		    				bas2--;
		    			}
		    			if(!ep){
		    				if(!th || !ys)
		    					basika += " - ";
		    				basika += "1 από Επικοινωνιών και Επεξεργασία σήματος";
		    				bas2--;
		    			}
		    			if(bas2 != 0){
		    				if(!th || !ys || !ep)
		    					basika += " - ";
		    				basika += String.valueOf(bas2) + " από όποιαδήποτε κατεύθυνση";
		    			}
		    			basika += (")");	
		    		}
		    		else{
		    			basika += ("(από όποιαδήποτε κατεύθυνση)");
		    		}
		    	}
		    	ms.setText("Κορμου: " + kor + '\n'+ '\n' + "Βασικά Κατεύθυνσης: " + bas + " " + basika + '\n' + '\n' + "Επιλογής: " + epil + '\n' + '\n' + "Γενικής Παιδείας: " + gen );
		    	ms.setTextSize(16);
		    	AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		    	builder
		    	.setTitle("Remain Courses")
		    	.setView(ms)
		    	.setNeutralButton("OK", null)
		    	.show();
		    }
		    });
		
		
		//Calculate and display CURRENT SEMESTER COURSES
		semesterCourses();
		TextView txs = (TextView) this.findViewById(R.id.prof_semesterCourses);
		txs.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	String text = "";
		    	TextView ms = new TextView(ctx);
		    	ScrollView scroll = new ScrollView(ctx);
		    	
		    	if(set != null){
		    		if(!set.isEmpty()){
		    			Iterator<String> setIter = set.iterator();
		    			for(int i=0; i<set.size();i++){
		    				text += "--" + setIter.next() + '\n' + '\n';
		    			}
		    		}
		    		else
		    			text += "No course has been selected for this semester";
		    	}
		    	else
		    		text += "No course has been selected for this semester";
		    	
		    	ms.setText(text);
		    	scroll.addView(ms);
		    	AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		    	builder
		    	.setTitle("Current Semester Courses")
		    	.setView(scroll)
		    	.setNeutralButton("OK", null)
		    	.show();
		    }
		});
		txs.setText(Html.fromHtml("<big><b>Current Semester Courses</b></big> <br> <small>"+semester_courses+"</small>"));
		
		
		//Calculate and display PROPOSED DIRECTION
		calculateDirection();
		String p="";
		if(!direction.equals(" - ")){
			if(direC<=5)
				p = "<font color=\"red\">(Χαμηλή πρόταση)</font>";
			else if(direC<9)
				p = "<font color=\"blue\">(Μέτρια πρόταση)</font>";
			else
				p = "<font color=\"green\">(Υψηλή πρόταση)</font>";
		}
		TextView txd = (TextView) this.findViewById(R.id.prof_direction);
		txd.setText(Html.fromHtml("<big><b>Proposed direction</b></big> <br> <small>"+direction+p+"</small>"));
	}
	
	private void calculateAverage_RemainCourses(){
		ArrayList<Double> gradesK = new ArrayList<Double>();
		ArrayList<Double> gradesTh = new ArrayList<Double>();
		ArrayList<Double> gradesYs = new ArrayList<Double>();
		ArrayList<Double> gradesEp = new ArrayList<Double>();
		ArrayList<Double> gradesRest = new ArrayList<Double>();
		ArrayList<Double> igrades = new ArrayList<Double>();
		double k20=0.0,k23=0.0;
		

		dbC = db_courseHelper.getReadableDatabase();
		String select = "SELECT Grade, Code FROM Subjects WHERE (Code LIKE 'Κ__' OR Code LIKE 'Κ__α' OR Code LIKE 'Κ__β')  AND Grade!=-1";
		String select2 = "SELECT Grade, ThP, YS, EP FROM Subjects WHERE Code NOT LIKE 'Κ%' AND Code NOT LIKE '____ε' AND Code NOT LIKE 'ΓΠ%' AND Grade!=-1";
		String select3 = "SELECT COUNT(*) FROM Subjects WHERE Code LIKE 'ΓΠ%' AND Grade!=-1";
		
		Cursor cursor = dbC.rawQuery(select, null);
		if(cursor.moveToFirst()){
			do{
				String code = cursor.getString(1);
				Double grade = cursor.getDouble(0);
Log.i("PROFILE",code);
				if(code.equals("Κ26") || code.equals("Κ27") || code.equals("Κ28"))
					igrades.add(grade);
				else if(code.equals("Κ20α") || code.equals("Κ20β")){
					if(k20 == 0.0)
						k20 = grade;
					else{
						if(grade > k20){
							if(code.equals("Κ20α"))
								gradesEp.add(k20);
							else
								gradesTh.add(k20);
							k20 = grade;
						}
						else{
							if(code.equals("Κ20β"))
								gradesEp.add(grade);
							else
								gradesTh.add(grade);
						}
							
					}					
				}
				else if(code.equals("Κ23α") || code.equals("Κ23β")){
					if(cursor.getDouble(0)>k23)
						k23 = cursor.getDouble(0);
				}
				else
					gradesK.add(cursor.getDouble(0));
			}while(cursor.moveToNext());
		}
		cursor.close();
		
		cursor = dbC.rawQuery(select2, null);
		if(cursor.moveToFirst()){
			do{
				Double grade = cursor.getDouble(0);
Log.i("PROFILE",String.valueOf(grade));
				if(cursor.getDouble(1) == 1){
					gradesTh.add(grade);
					th = true;
				}
				else if(cursor.getDouble(2) == 1){
					gradesYs.add(grade);
					ys = true;
				}
				else if(cursor.getDouble(3) ==1){
					gradesEp.add(grade);
					ep = true;
				}
				else
					gradesRest.add(grade);
			}while(cursor.moveToNext());
		}
		cursor.close();
		
		cursor = dbC.rawQuery(select3, null);
		cursor.moveToFirst();
		gen -= cursor.getInt(0);
		remain_courses -= cursor.getInt(0);
		cursor.close();
		
		dbC.close();
		db_courseHelper.close();
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
		
		double weight_grade = 0.0, syntelestes = 0.0;
		
		//We need at least one basic course from each direction (3 basic courses)
		if(!gradesTh.isEmpty()){
Log.i("PROFILE TH","Theoritiki "+String.valueOf(gradesTh.get(0)));
			weight_grade += 2*gradesTh.get(0);
			gradesTh.remove(0);
			syntelestes += 2;
			bas --;
			remain_courses --;
		}
		if(!gradesYs.isEmpty()){
Log.i("PROFILE YS","Ypologistika "+String.valueOf(gradesYs.get(0)));
			weight_grade += 2*gradesYs.get(0);
			gradesYs.remove(0);
			syntelestes += 2;
			bas --;
			remain_courses --;
		}
		if(!gradesEp.isEmpty()){
Log.i("PROFILE EP", "Epeksergasia "+String.valueOf(gradesEp.get(0)));
			weight_grade += 2*gradesEp.get(0);
			gradesEp.remove(0);
			syntelestes += 2;
			bas --;
			remain_courses --;
		}
		
		//And at least 2 basic course from whichever direction
		gradesTh.addAll(gradesYs);
		gradesTh.addAll(gradesEp);
		Collections.sort(gradesTh);
		Collections.reverse(gradesTh);
		
		for(int i=0; i<2; i++){
			if(!gradesTh.isEmpty()){
				weight_grade += 2*gradesTh.get(0);
				syntelestes += 2;
				gradesTh.remove(0);
				bas --;
				remain_courses --;
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
				epil --;
				remain_courses --;
			}
			else
				break;
		}
		
		//Ptyxiakh 1,2 h Praktiki
		for(int i=0; i<2; i++){
			if(!igrades.isEmpty()){
Log.i("PROFILE PTYX",String.valueOf(igrades.get(0)));
				weight_grade += 3.0*igrades.get(0);
				syntelestes += 3.0;
				igrades.remove(0);
				kor --;
				remain_courses --;
			}
			else
				break;
		}
		
		//Finally 25 courses kormou
		for(int i=0; i<gradesK.size();i++){
Log.i("PROFILE K value",String.valueOf(gradesK.get(i)));
			weight_grade += 2.0*gradesK.get(i);
			syntelestes += 2.0;
			kor --;
			remain_courses --;
		}
		if(k20!=0.0){
			weight_grade += 2.0*k20;
			syntelestes += 2.0;
			kor --;
			remain_courses --;
		}
		if(k23!=0.0){
			weight_grade += 2.0*k23;
			syntelestes += 2.0;
			kor --;
			remain_courses --;
		}
Log.i("PROFILE WEIGHT GRADE+SYNTEL",String.valueOf(weight_grade)+"  "+String.valueOf(syntelestes));
		if(weight_grade!=0)
			average = (double)weight_grade/(double)syntelestes;
		DecimalFormat df = new DecimalFormat("#.##");
		String average2 = df.format(average);
System.out.println("Profile Average is " + average2);		
		average = Double.valueOf(average2);
	}
	
	private void semesterCourses(){
		SharedPreferences sharedPref;
		String preFile = ITCutiesReaderAppActivity.PREFS_NAME;
		
		//Retrieve shared preferences
        sharedPref = getSharedPreferences(preFile, 0);
        
		//Retrieve semester courses
		set = sharedPref.getStringSet("CoursesChecked", null);
		
		if(set == null)
			semester_courses = 0;
		else
			semester_courses = set.size();
	}

	private void calculateDirection(){
		double gradeTh = 0.0, gradeYs = 0.0, gradeEp = 0.0;
		double wTh = 0.0, wYs = 0.0, wEp = 0.0;
		double synTh = 0.0, synYs = 0.0, synEp = 0.0;
		int ThC = 0, YsC = 0, EpC = 0;
		
		dbC = db_courseHelper.getReadableDatabase();
		String select = "SELECT Grade, ThP FROM Subjects WHERE Code NOT LIKE '___ε' AND Code NOT LIKE '____ε' AND (ThP=1 OR ThP=2) AND Grade!=-1";
		String select2 = "SELECT Grade, YS FROM Subjects WHERE Code NOT LIKE '___ε' AND Code NOT LIKE '____ε' AND (YS=1 OR YS=2) AND Grade!=-1";
		String select3 = "SELECT Grade, EP FROM Subjects WHERE Code NOT LIKE '___ε' AND Code NOT LIKE '____ε' AND (EP=1 OR EP=2) AND Grade!=-1";
		
		Cursor cursor = dbC.rawQuery(select, null);
		if(cursor.moveToFirst()){
			do{
				ThC++;
				wTh += ((2/cursor.getInt(1))*cursor.getDouble(0));
				synTh += (2/cursor.getInt(1));
			}while(cursor.moveToNext());
		}
		cursor.close();
		if(wTh != 0.0)
			gradeTh = wTh/synTh;
		
		cursor = dbC.rawQuery(select2, null);
		if(cursor.moveToFirst()){
			do{
				YsC++;
				wYs += ((2/cursor.getInt(1))*cursor.getDouble(0));
				synYs += (2/cursor.getInt(1));
			}while(cursor.moveToNext());
		}
		cursor.close();
		if(wYs != 0.0)
			gradeYs = wYs/synYs;
		
		cursor = dbC.rawQuery(select3, null);
		if(cursor.moveToFirst()){
			do{
				EpC++;
				wEp += ((2/cursor.getInt(1))*cursor.getDouble(0));
				synEp += (2/cursor.getInt(1));
			}while(cursor.moveToNext());
		}
		cursor.close();
		if(wEp != 0.0)
			gradeEp = wEp/synEp;
		
		dbC.close();
		if(gradeTh==0.0 && gradeYs==0.0 && gradeEp==0.0)
			direction = " - ";
		else if(gradeTh>gradeYs && gradeTh>gradeEp){
			direction = "Θεωρητική Πληροφορική";
			direC = ThC;
		}
		else if(gradeYs>gradeTh && gradeYs>gradeEp){
			direction = "Υπολογιστικών Συστημάτων και Εφαρμογών";
			direC = YsC;
		}
		else if(gradeEp>gradeTh && gradeEp>gradeYs){
			direction = "Επικοινωνιών και Επεξεργασίας Σήματος";
			direC = EpC;
		}
		else if(gradeTh==gradeYs && gradeTh==gradeEp ){
			if(ThC>=YsC && ThC>=EpC){
				direction = "Θεωρητική Πληροφορική";
				direC = ThC;
			}
			else if(YsC>=ThC && YsC>=EpC){
				direction = "Υπολογιστικών Συστημάτων και Εφαρμογών";
				direC = YsC;
			}
			else{
				direction = "Επικοινωνιών και Επεξεργασίας Σήματος";
				direC = EpC;
			}
		}
		else if(gradeTh == gradeYs){
			if(ThC>=YsC){
				direction = "Θεωρητική Πληροφορική";
				direC = ThC;
			}
			else{
				direction = "Υπολογιστικών Συστημάτων και Εφαρμογών";
				direC = YsC;
			}
		}
		else if(gradeTh == gradeEp){
			if(ThC>=EpC){
				direction = "Θεωρητική Πληροφορική";
				direC = ThC;
			}
			else{
				direction = "Επικοινωνιών και Επεξεργασίας Σήματος";
				direC = EpC;
			}
		}
		else if(gradeYs==gradeEp){
			if(YsC>=EpC){
				direction = "Υπολογιστικών Συστημάτων και Εφαρμογών";
				direC = YsC;
			}
			else{
				direction = "Επικοινωνιών και Επεξεργασίας Σήματος";
				direC = EpC;
			}
		}		
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
