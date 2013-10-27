package com.example.scedule;

import java.util.List;

import com.example.agenda.R;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class SelectCourseAdapter_Listener extends ArrayAdapter<ScheduleCourseItem> {
	private LayoutInflater inflater;
	private List<ScheduleCourseItem> clist;
	private OnMultiChoiceClickListener SelectListener;
	

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
	
	public SelectCourseAdapter_Listener(Context context, List<ScheduleCourseItem> CourseList) {
		super(context, R.layout.course_row, R.id.rowTextView, CourseList);
		// Cache the LayoutInflate to avoid asking for a new one each time.
		clist = CourseList;
		inflater = LayoutInflater.from(context);
		SelectListener = new OnMultiChoiceClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int pos, boolean arg2) {
				CheckBox checkBox = clist.get(pos).getCBView();
				if(!checkBox.isChecked())
					checkBox.setChecked(true);
				else
					checkBox.setChecked(false);
			}

		};
	}

	public OnMultiChoiceClickListener getSelectListener() {
		return SelectListener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Course to display
		ScheduleCourseItem course = (ScheduleCourseItem) this.getItem(position);

		// The child views in each row.
		CheckBox checkBox;
		TextView textView;

		// Create a new row view
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.course_row, null);

			// Find the child views.
			textView = (TextView) convertView.findViewById(R.id.rowTextView);
			textView.setTextColor(Color.BLACK);
			checkBox = (CheckBox) convertView.findViewById(R.id.CheckBox01);
			checkBox.setClickable(false);
			
			
			// Optimization: Tag the row with it's child views, so we don't
			// have to
			// call findViewById() later when we reuse the row.
			convertView.setTag(new SelectViewHolder(textView, checkBox));
			// If CheckBox is toggled, update the course it is tagged with.
			if(course.isChecked()){
				checkBox.setChecked(true);
			}
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
		clist.get(position).setCBView(checkBox);
		return convertView;
	}

}
