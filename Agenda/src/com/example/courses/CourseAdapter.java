package com.example.courses;

import java.util.List;

import com.example.agenda.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class CourseAdapter extends ArrayAdapter<CourseItem>{
	private LayoutInflater inflater;
	private List<CourseItem> clist;
	CoursesDataBaseHelper DBHelper;
	

	private static class SelectViewHolder {
		private CheckBox checkBox;
		private TextView textView;

		public SelectViewHolder(TextView textView, CheckBox checkBox) {
			this.checkBox = checkBox;
			this.textView = textView;
		}

		public CheckBox getCheckBox() {
			return checkBox;
		}


		public TextView getTextView() {
			return textView;
		}

	}
	
	public CourseAdapter(Context context, List<CourseItem> CourseList, CoursesDataBaseHelper DB) {
		super(context, R.layout.course_row, R.id.rowTextView, CourseList);
		// Cache the LayoutInflate to avoid asking for a new one each time.
		clist = CourseList;
		inflater = LayoutInflater.from(context);
		DBHelper = DB;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Planet to display
		CourseItem course = (CourseItem) this.getItem(position);

		// The child views in each row.
		CheckBox checkBox;
		TextView textView;

		// Create a new row view
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.course_row, null);

			// Find the child views.
			textView = (TextView) convertView.findViewById(R.id.rowTextView);
			checkBox = (CheckBox) convertView.findViewById(R.id.CheckBox01);
			
			// Optimization: Tag the row with it's child views, so we don't
			// have to
			// call findViewById() later when we reuse the row.
			convertView.setTag(new SelectViewHolder(textView, checkBox));
			// If CheckBox is toggled, update the course it is tagged with.
			if(course.isChecked()){
				checkBox.setChecked(true);
			}
			checkBox.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					CourseItem course = (CourseItem) cb.getTag();
					course.setChanged(true);
					if(!course.isChecked()){
						//for(int i=clist.indexOf(course);i>0;i--){
						//	clist.remove(i);
						//	clist.add(i, clist.get(i-1));
						//}
						//clist.remove(0);
						int pos=0;
						while(clist.get(pos).isChecked())
							pos++;
						clist.remove(course);
						clist.add(pos, course);
						course.setChecked(cb.isChecked());
						notifyDataSetChanged();
					}
					else{
						int pos = clist.indexOf(course);
						course.setChecked(cb.isChecked());
						clist.remove(course);
						while(clist.get(pos).isChecked())
								pos++;
						clist.add(pos, course);
						course.setGrade(-1.0);
						notifyDataSetChanged();
					}
				}
			});
		}
		// Reuse existing row view
		else {
			// Because we use a ViewHolder, we avoid having to call
			// findViewById().
			SelectViewHolder viewHolder = (SelectViewHolder) convertView.getTag();
			checkBox = viewHolder.getCheckBox();
			textView = viewHolder.getTextView();
		}

		// Tag the CheckBox with the Course it is displaying, so that we can
		// access the course in onClick() when the CheckBox is toggled.
		checkBox.setTag(course);
		checkBox.setChecked(course.isChecked());
		textView.setText(course.getName());
		return convertView;
	}
}
