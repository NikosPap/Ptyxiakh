package com.example.scedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agenda.R;
import com.example.agendaMain.AgendMainActivity;
import com.example.news.ITCutiesReaderAppActivity;


class Course {
    private ArrayList<ScheduleCourseDatabaseItem> data;

    public Course(){
    	this.data = new ArrayList<ScheduleCourseDatabaseItem>();
    }
    
    /**
     * @return the data
     */
    public ArrayList<ScheduleCourseDatabaseItem> getData() {
        return data;
    }
}

    
    

@SuppressLint("NewApi")
public class Schedule extends ExpandableListActivity {
	ScheduleCourseDatabaseHandler db;
	String Semester;
	SharedPreferences sharedPref;
	SharedPreferences.Editor prefEditor;
	Set<String> set;
	SimpleExpandableListAdapter expListAdapter;
	List<ArrayList<HashMap<String, String>>> resultC;
	
	// Sort courses per start hour
	private class SortCoursesHour implements Comparator<HashMap<String, String>>{

		@Override
		public int compare(HashMap<String, String> lh,HashMap<String, String> rh) {
			String lhs = lh.get("Course");
			String rhs = rh.get("Course");
			int c1 = lhs.indexOf('-');
	    	int c2 = rhs.indexOf('-');
	    	String lhsN = lhs.substring(0, c1);
	    	String rhsN = rhs.substring(0,c2);
	        return Integer.valueOf(lhsN) - Integer.valueOf(rhsN);
		}

	}
	
    public void onCreate(Bundle savedInstanceState) {
        try{
             super.onCreate(savedInstanceState);
             
             //set back button
             ActionBar bar = getActionBar();
             bar.setDisplayHomeAsUpEnabled(true);
             TextView bv = new TextView(Schedule.this);
             bv.setTextSize(18);
             bar.setCustomView(bv);
             bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.bd1));
             
             setContentView(R.layout.activity_schedule_courses);
             db = new ScheduleCourseDatabaseHandler(this);
            
    		String preFile = ITCutiesReaderAppActivity.PREFS_NAME;
    		//Retrieve shared preferences
            sharedPref = getSharedPreferences(preFile, 0);
            
            //What is the current semester
    		Semester = sharedPref.getString("Semester", null);
    		
    		//A variable in order to check if this activity has launched for first time
    		Boolean FirstTimeLaunched = sharedPref.getBoolean("FirstTime", true);
    		
    		if(FirstTimeLaunched){
    			set = new HashSet<String>();
    			SharedPreferences.Editor prefEditor = sharedPref.edit();
        		prefEditor.putStringSet("CoursesChecked", set);
        		prefEditor.putBoolean("FirstTime", false);
        		prefEditor.commit();
    		}

    		String html="";
    		html = findTimetableHTML();
    		if(html != null){			//Get new Semester Schedule if it is needed
    			GetSemesterSchedule getTask = new GetSemesterSchedule(db,this.getApplicationContext());
        		getTask.execute(html);
        		
        		//Remove from schedule courses of previous semester
            	set = sharedPref.getStringSet("CoursesChecked", null);
            	if(!set.isEmpty()){
            		set.clear();
            		SharedPreferences.Editor prefEditor = sharedPref.edit();
            		prefEditor.putStringSet("CoursesChecked", set);
            		prefEditor.commit();
            	}
    		
        		//Debug the thread name
        		Log.d("GetSchedule", Thread.currentThread().getName());

    		}
    		
    		resultC = createChildList();
            expListAdapter =
           		 new SimpleExpandableListAdapter(
           				 this,
           				 createGroupList(),              // Creating group List.
           				 R.layout.group_row,             // Group item layout XML.
           				 new String[] { "Days" },  // the key of group item.
           				 new int[] { R.id.row_name },    // ID of each group item.-Data under the key goes into this TextView.
           				 resultC,              // childData describes second-level entries.
           				 R.layout.child_row,             // Layout for sub-level entries(second level).
           				 new String[] {"Course"},      // Keys in childData maps to display.
           				 new int[] { R.id.grp_child}     // Data under the keys above go into these TextViews.
           		);
           setListAdapter( expListAdapter );       // setting the adapter in the list.
        }catch(Exception e){
            System.out.println("Errrr +++ " + e.getMessage());
        }
    }
 
    /* Creating the Hashmap for the row */
    private List<HashMap<String, String>> createGroupList() {
    	String Days[] = {"Monday","Tuesday","Wednesday","Thursday","Friday"};
        ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
        for( int i = 0 ; i < 5 ; ++i ) {
        	HashMap<String, String> m = new HashMap<String, String>();
        	m.put( "Days",Days[i] ); 
        	result.add( m );
        }
        return (List<HashMap<String, String>>)result;
    }

    
    /* creating the HashMap for the children */
    private List<ArrayList<HashMap<String, String>>> createChildList() {
    	ArrayList<ArrayList<HashMap<String, String>>> result = new ArrayList<ArrayList<HashMap<String, String>>>();
    	
    	set = sharedPref.getStringSet("CoursesChecked", null);
    	
    	if(set.isEmpty()){
    		for( int i = 0 ; i < 5 ; ++i ) { 
        		ArrayList<HashMap<String, String>> secList = new ArrayList<HashMap<String, String>>();
                HashMap<String, String> child = new HashMap<String, String>();
                child.put( "Course", "Add course to schedule.." );
                secList.add( child );
                result.add( secList );
            }	
    	}
    	else{
    		ArrayList<HashMap<String, String>> MonL = new ArrayList<HashMap<String, String>>();
    		ArrayList<HashMap<String, String>> TueL = new ArrayList<HashMap<String, String>>();
    		ArrayList<HashMap<String, String>> WedL = new ArrayList<HashMap<String, String>>();
    		ArrayList<HashMap<String, String>> ThuL = new ArrayList<HashMap<String, String>>();
    		ArrayList<HashMap<String, String>> FriL = new ArrayList<HashMap<String, String>>();
    	
    		Iterator<String> it = set.iterator();
    	
    		while(it.hasNext()) {
    			String les = it.next();
    			String query = "SELECT " + db.getKeyDay() + ", " + db.getKeyHour() + ", " + db.getKeyClass() + " FROM " + db.getTableScourses()+ " WHERE " + db.getKeyName() + "=" + "\"" + les + "\"";
    			//db.printAll();
    			
    			Cursor c = db.execQuery(query);
    			//Log.i("SCHEDULE_CREATE_LIST",les + "\n" + query + "\n" + c.getCount());
    			if (c.moveToFirst()) {
    				do {
    					HashMap<String, String> courseMap = new HashMap<String, String>();
    					ArrayList<HashMap<String, String>> array = null;
    					if("Δευτέρα".equals(c.getString(0)) )
    						array = MonL;
    					else if("Τρίτη".equals(c.getString(0)))
    						array = TueL;
    					else if("Τετάρτη".equals(c.getString(0)))
    						array = WedL;
    					else if("Πέμπτη".equals(c.getString(0)))
    						array = ThuL;
    					else if("Παρασκευή".equals(c.getString(0)))
    						array = FriL;
    					courseMap.put("Course", c.getString(1) + ": " +les + " - " + c.getString(2));
    					array.add(courseMap);
    				} while (c.moveToNext());
    			}
    			c.close();
    		}
    		if(MonL.isEmpty()){
    			HashMap<String, String> courseMap = new HashMap<String, String>();
    			courseMap.put("Course","No course has been added for this day");
    			MonL.add(courseMap);
    		}
    		else
    			Collections.sort(MonL, new SortCoursesHour() );
    		if(TueL.isEmpty()){
    			HashMap<String, String> courseMap = new HashMap<String, String>();
    			courseMap.put("Course","No course has been added for this day");
    			TueL.add(courseMap);
    		}
    		else
    			Collections.sort(TueL, new SortCoursesHour() );
    		if(WedL.isEmpty()){
    			HashMap<String, String> courseMap = new HashMap<String, String>();
    			courseMap.put("Course","No course has been added for this day");
    			WedL.add(courseMap);
    		}
    		else
    			Collections.sort(WedL, new SortCoursesHour() );
    		if(ThuL.isEmpty()){
    			HashMap<String, String> courseMap = new HashMap<String, String>();
    			courseMap.put("Course","No course has been added for this day");
    			ThuL.add(courseMap);
    		}
    		else
    			Collections.sort(ThuL, new SortCoursesHour() );
    		if(FriL.isEmpty()){
    			HashMap<String, String> courseMap = new HashMap<String, String>();
    			courseMap.put("Course","No course has been added for this day");
    			FriL.add(courseMap);
    		}
    		else
    			Collections.sort(FriL, new SortCoursesHour() );
    			
    		result.add(MonL);
    		result.add(TueL);
    		result.add(ThuL);
    		result.add(WedL);
    		result.add(FriL);
    	}

        return result;
    }
    
    
    /* This function is called on each child click */
    public boolean onChildClick( ExpandableListView parent, View v, int groupPosition,int childPosition,long id) {
    	TextView tx = (TextView) v.findViewById(R.id.grp_child);
    	int[] location = {0,0};
    	tx.getLocationOnScreen(location);
    	Toast toa = Toast.makeText(this, tx.getText(), Toast.LENGTH_LONG);
    	toa.setGravity(Gravity.TOP, 0, location[1]);
    	toa.show();
        return true;
    }
 
    /* This function is called on expansion of the group */
    public void  onGroupExpand  (int groupPosition) {
        try{
             System.out.println("Group exapanding Listener => groupPosition = " + groupPosition);
        }catch(Exception e){
            System.out.println(" groupPosition Errrr +++ " + e.getMessage());
        }
    }
	
	
	private String findTimetableHTML(){
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int ye = year%1000;
		String html;
		int InitYear=ye;
		//System.out.println(month+ "  " + year + "  Semester:" + Semester);
		
		if(month>=10 || month<=3){		//Winter Semester
			if(Semester == null || Semester.equals("Spring") || db.emptyDB() ){
				Semester = "Winter";
				prefEditor = sharedPref.edit();
				prefEditor.putString("Semester", Semester);
				prefEditor.commit();
			}
			else
				return null;
			if(month<=3)
				InitYear--;
			else
				ye++;
			html="http://cgi.di.uoa.gr/~schedule/timetables/"+InitYear+"-"+ye+"/timetable_PPS_"+"winter"+InitYear+ye+".html";
		}
		else{						//Spring Semester
			if(Semester == null || Semester.equals("Winter") || db.emptyDB() ){
				Semester = "Spring";
				prefEditor = sharedPref.edit();
				prefEditor.putString("Semester", Semester);
				prefEditor.commit();
			}
			else
				return null;
			InitYear--;
			html="http://cgi.di.uoa.gr/~schedule/timetables/"+InitYear+"-"+ye+"/timetable_PPS_"+"spring"+InitYear+ye+".html";
		}
		return html;
	}


	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.option_scedule,menu);
		return true;
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add:
			add();
			break;
		case R.id.alarm:
			alarm();
			break;
		default:
			startActivity(new Intent(this,AgendMainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
	                | Intent.FLAG_ACTIVITY_SINGLE_TOP));
			
			return (true);
		}

		return (super.onOptionsItemSelected(item));
	}
	
	private void addToAdapter(String CName){
    	String query = "SELECT " + db.getKeyDay() + ", " + db.getKeyHour() + ", " + db.getKeyClass() + " FROM " + db.getTableScourses()+ " WHERE " + db.getKeyName() + "=" + "\"" + CName + "\"";
    	Cursor c = db.execQuery(query);
    	HashMap<String, String> emptyMap = new HashMap<String, String>();
    	HashMap<String, String> emptyMap2 = new HashMap<String, String>();
    	
    	emptyMap.put("Course", "No course has been added for this day");
    	emptyMap2.put("Course", "Add course to schedule.." );
    	if (c.moveToFirst()) {
    		do {
    			HashMap<String, String> courseMap = new HashMap<String, String>();
    			ArrayList<HashMap<String, String>> array = null;
    			
    			courseMap.put("Course", c.getString(1) + ": " + CName + " - " + c.getString(2));
        		if("Δευτέρα".equals(c.getString(0)) )
    				array = resultC.get(0);
    			else if("Τρίτη".equals(c.getString(0))){			
    				array = resultC.get(1);
    			}
    			else if("Τετάρτη".equals(c.getString(0))){
    				array = resultC.get(2);
    			}
    			else if("Πέμπτη".equals(c.getString(0)))
    				array = resultC.get(3);
    			else if("Παρασκευή".equals(c.getString(0)))
    				array = resultC.get(4);
        		
        		if(array.contains(emptyMap))
        			array.remove(emptyMap);
        		else if(array.contains(emptyMap2))
        			array.remove(emptyMap2);
        		array.add(courseMap);
    		}while(c.moveToNext());
    		c.close();
    	}
    }
    
    private void removeFromAdapter(String CName){
    	String query = "SELECT " + db.getKeyDay() + ", " + db.getKeyHour() + ", " + db.getKeyClass() + " FROM " + db.getTableScourses()+ " WHERE " + db.getKeyName() + "=" + "\"" + CName + "\"";
    	Cursor c = db.execQuery(query);
    	if (c.moveToFirst()) {
    		do {
    			HashMap<String, String> courseMap = new HashMap<String, String>();
    			ArrayList<HashMap<String, String>> array = null;
    			
    			courseMap.put("Course", c.getString(1) + ": " + CName + " - " + c.getString(2));
        		if("Δευτέρα".equals(c.getString(0)) )
    				array = resultC.get(0);
    			else if("Τρίτη".equals(c.getString(0)))
    				array = resultC.get(1);
    			else if("Τετάρτη".equals(c.getString(0)))
    				array = resultC.get(2);
    			else if("Πέμπτη".equals(c.getString(0)))
    				array = resultC.get(3);
    			else if("Παρασκευή".equals(c.getString(0)))
    				array = resultC.get(4);
        		array.remove(courseMap);
        		if(array.isEmpty()){
        			HashMap<String, String> cMap = new HashMap<String, String>();
        			cMap.put("Course", "No course has been added for this day");
        			array.add(cMap);
        		}
    		}while(c.moveToNext());
    		c.close();
    	}
    }
    
    private void alarm(){
    	//Retrieve semester courses
    	//Set<String> set = sharedPref.getStringSet("CoursesChecked", null);
    }
	
	private void add() {
		ArrayList<ScheduleCourseItem> subjects = db.selectSubjects();
		final CharSequence[] subs = new CharSequence[subjects.size()];
		
		for(int i=0; i<subjects.size();i++){
			subs[i]= subjects.get(i).getName();
		}
		
		//Retrieve selected courses
		set = sharedPref.getStringSet("CoursesChecked", null);
		
		boolean[] selected = new boolean[subjects.size()];		//Get the already checked courses
		for(int i=0; i<subjects.size();i++){
			if(set.contains(subs[i].toString()))
				selected[i] = true;
			else
				selected[i] = false;
		}

		//SelectCourseAdapter_Listener adapter = new SelectCourseAdapter_Listener(this.getApplication(),subjects); 
		new AlertDialog.Builder(this).setTitle("Add subject to schedule")
						.setMultiChoiceItems(subs, selected, new DialogInterface.OnMultiChoiceClickListener() {
			                		@Override
			                		public void onClick(DialogInterface dialog, int which,boolean isChecked) {			            
			                			if(isChecked){
		                					set.add(subs[which].toString());
		                					addToAdapter(subs[which].toString());
			                			}
			                			else if(set.contains(subs[which].toString())){
			                				set.remove(subs[which].toString());
			                				removeFromAdapter(subs[which].toString());
			                			}
			                		}
								}
						)
					   //.setAdapter(adapter,null)
					   .setNeutralButton("OK", new DialogInterface.OnClickListener() {
				            @Override
				            public void onClick(DialogInterface d, int which) {
				            	SharedPreferences.Editor prefEditor = sharedPref.edit();
				        		prefEditor.putStringSet("CoursesChecked", set);
				        		prefEditor.commit();
				        		for(int i=0;i<5;i++)
				        			Collections.sort(resultC.get(i),new SortCoursesHour());
				        		expListAdapter.notifyDataSetChanged();
				            }
				        })
					   .create()
					   .show();
	}	
}
