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


/**
 * @author mwho
 *
 */
public class Fragment3_Ys extends ListFragment {
	CoursesDataBaseHelper myDbHelper;
	String TABLE_NAME = "Subjects";
	SQLiteDatabase db;
	ArrayList<CourseItem> subjects;
	ListView lv;
	
	@SuppressLint("NewApi")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		String select = "SELECT Ys, Name, Description, Grade FROM Subjects WHERE  Code LIKE 'ус%' AND Selected=1 ORDER BY Ys";
		String select2 = "SELECT Ys, Name, Description FROM Subjects WHERE  Code LIKE 'ус%' AND Selected=0 ORDER BY Ys";
		
		subjects = new ArrayList<CourseItem>();
		
		myDbHelper = new CoursesDataBaseHelper(this.getActivity());
		//db = myDbHelper.openDatabase();
		db = myDbHelper.getReadableDatabase();
		
		
		Cursor cursor = db.rawQuery(select, null);
		if(cursor.moveToFirst()){
			do{
				String name = cursor.getString(1);
				if(cursor.getInt(0)==1)
					name += " (B)";
				else
					name += " (E)";
				subjects.add(new CourseItem(name,true,cursor.getString(2),cursor.getDouble(3)));
			}while(cursor.moveToNext());
		}
		cursor.close();
		
		
		Cursor cursor2 = db.rawQuery(select2, null);
		if(cursor2.moveToFirst()){
			do{
				String name = cursor2.getString(1);
				if(cursor2.getInt(0)==1)
					name += " (B)";
				else
					name += " (E)";
				subjects.add(new CourseItem(name,false,cursor2.getString(2)));
			}while(cursor2.moveToNext());
		}
		cursor2.close();
		
		db.close();
		myDbHelper.close();
		CourseAdapter mAdapter = new CourseAdapter(this.getActivity(),subjects,myDbHelper);
		setListAdapter(mAdapter);
		
		
		//View view = inflater.inflate(R.layout.courses_ys, container, false);
		//ListView itcItems = (ListView) view.findViewById(android.R.id.list);
		//itcItems.setOnItemClickListener(new CourseListListener(subjects,this.getActivity()));
		if (container == null) {
            return null;
        }
		return (LinearLayout)inflater.inflate(R.layout.courses_ys, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    lv = getListView();
	    lv.setOnItemClickListener(new CourseListener(subjects,getActivity()));
	}
	
	public void onDestroyView (){
		super.onDestroyView();

		//Store users selection
		db = myDbHelper.getReadableDatabase();
		//db = myDbHelper.openDatabase();
		for(int i = 0; i<subjects.size(); i++){
			CourseItem s = subjects.get(i);
			
			if(s.isChanged()){
				String[] name = s.getName().split(" \\(");
				int sel=0;
				
				if(s.isChecked())
					sel=1;
				s.setChanged(false);
				String update = "UPDATE Subjects SET Selected=" + sel + ", Grade="+ s.getGrade() + " WHERE Name=" + "\"" + name[0] +"\"" ;
				db.execSQL(update);
			}
		}
		db.close();
		myDbHelper.close();
	}
}
