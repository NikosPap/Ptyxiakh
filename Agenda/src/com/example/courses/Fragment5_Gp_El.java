package com.example.courses;

import java.util.ArrayList;

import com.example.agenda.R;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

@SuppressLint("NewApi")
//Class responsibly for displaying courses 'Γενικής Παιδείας | Ελεύθερα'
public class Fragment5_Gp_El extends ListFragment{
	CoursesDataBaseHelper myDbHelper;
	String TABLE_NAME = "Subjects";
	SQLiteDatabase db;
	ArrayList<CourseItem> subjects;
	ListView lv;
	
	@SuppressLint("NewApi")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		String select = "SELECT Name, Grade FROM Subjects WHERE  (Code LIKE 'ΓΠ%' OR Code LIKE 'ΕΛ%') AND Selected=1";
		String select2 = "SELECT Name FROM Subjects WHERE  (Code LIKE 'ΓΠ%' OR Code LIKE 'ΕΛ%') AND Selected=0";
		
		subjects = new ArrayList<CourseItem>();
		
		// Read courses from database
		myDbHelper = new CoursesDataBaseHelper(this.getActivity());
		db = myDbHelper.getReadableDatabase();
		
		// Creates the list of subjects. On top we have the checked courses and then the unchecked
		Cursor cursor = db.rawQuery(select, null);
		if(cursor.moveToFirst()){
			do{
				subjects.add(new CourseItem(cursor.getString(0),true,"No description available",cursor.getDouble(1)));
			}while(cursor.moveToNext());
		}
		cursor.close();
		
		
		Cursor cursor2 = db.rawQuery(select2, null);
		if(cursor2.moveToFirst()){
			do{
				subjects.add(new CourseItem(cursor2.getString(0),false,"No description available"));
			}while(cursor2.moveToNext());
		}
		cursor2.close();
		
		db.close();
		myDbHelper.close();
		
		// Set adapter for this listfragment
		CourseAdapter mAdapter = new CourseAdapter(this.getActivity(),subjects,myDbHelper);
		setListAdapter(mAdapter);
		
		
		//View view = inflater.inflate(R.layout.courses_k, container, false);
		//ListView itcItems = (ListView) view.findViewById(android.R.id.list);
		//itcItems.setOnItemClickListener(new CourseListListener(subjects,this.getActivity()));
		if (container == null) {
            return null;
        }
		return (LinearLayout)inflater.inflate(R.layout.courses_k, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    lv = getListView();
	    lv.setOnItemClickListener(new CourseListener(subjects,getActivity()));
	}
	
	public void onDestroyView (){
		super.onDestroyView();

		//Store users selection in database
		db = myDbHelper.getReadableDatabase();
		for(int i = 0; i<subjects.size(); i++){
			CourseItem s = subjects.get(i);
			
			if(s.isChanged()){
				int sel=0;
				
				if(s.isChecked())
					sel=1;
				s.setChanged(false);
				String update = "UPDATE Subjects SET Selected=" + sel + ", Grade="+ s.getGrade() +" WHERE Name=" + "\"" + s.getName() +"\"" ;
				db.execSQL(update);
			}
		}
		db.close();
		myDbHelper.close();
	}
}

