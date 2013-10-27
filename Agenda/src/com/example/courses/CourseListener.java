package com.example.courses;

import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


@SuppressLint("NewApi")
// This class is responsibly for displaying the infos for each clicked course
public class CourseListener implements OnItemClickListener{
	// List item's reference
	List<CourseItem> listItems;
	
	// Calling activity reference
	Activity activity;
	
	
	public CourseListener(List<CourseItem> aListItems, Activity anActivity) {
		listItems = aListItems;
		activity  = anActivity;
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		ScrollView scrollv = new ScrollView(activity);
		RelativeLayout rel = new RelativeLayout(activity);
		TextView info_text;
		TextView grade_text;
		EditText grade_input = null;
		View sep;

		grade_text = new TextView(activity);
		grade_text.setId(View.generateViewId());
		
		// Displaying grade if it has been set
		if(listItems.get(pos).isChecked()){
			grade_text.setTextSize(18);
			grade_text.setText('\n'+"Grade:");
        	grade_input = new EditText(activity);
        	grade_input.setTextSize(18);
        	//grade_input.setBackgroundColor(Color.LTGRAY);
        	if(listItems.get(pos).getGrade()!=-1)
        		grade_input.setText(String.valueOf(listItems.get(pos).getGrade()));
        	RelativeLayout.LayoutParams rlp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        	rlp1.addRule(RelativeLayout.ALIGN_BOTTOM, grade_text.getId());
        	//rlp1.addRule(RelativeLayout.BELOW,sep.getId());
        	rlp1.addRule(RelativeLayout.RIGHT_OF,grade_text.getId());
        	//rlp1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        	//rlp1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        	grade_input.setLayoutParams(rlp1);
        	rel.addView(grade_input);
        }
		
		// Else notify user that he need to check the course in list
		// and set grade
        else{
        	grade_text.setTextSize(14);
        	grade_text.setText('\n'+"If you want to set grade, check the course in the list first"+ '\n');
        	grade_text.setTextColor(Color.RED);
        }
		rel.addView(grade_text);

		// Set a separator to view
		sep = new View(activity);
		sep.setId(View.generateViewId());
		//sep.setBackgroundResource(android.R.attr.listDividerAlertDialog);
		RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,1);
		rl.addRule(RelativeLayout.BELOW,grade_text.getId());
		rl.addRule(RelativeLayout.CENTER_HORIZONTAL);
	    rl.setMargins(0, 5, 0, 0);
		sep.setLayoutParams(rl);
		sep.setBackgroundColor(Color.DKGRAY);
		rel.addView(sep);
		
		// Set the text description for the course
		info_text = new TextView(activity);
		info_text.setTextSize(16);
		RelativeLayout.LayoutParams rlp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlp2.addRule(RelativeLayout.BELOW,sep.getId());
		info_text.setLayoutParams(rlp2);
		info_text.setText('\n'+listItems.get(pos).getDesc()+'\n'+'\n');
		//ms.setPadding(4, 2, 4, 2);
		info_text.setId(View.generateViewId());
		rel.addView(info_text);
		
        scrollv.addView(rel);
		
		AlertDialog alert = new AlertDialog.Builder(activity)
    	.setTitle(listItems.get(pos).getName())
    	.setView(scrollv)
    	.setNeutralButton(android.R.string.ok,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d, int which) {
                //Do nothing here. We override the onclick
            }
        })
    	.create();
		
		// Set listener for the ok button. We must check the grade that user has set...
    	alert.setOnShowListener(new DialogListener(listItems.get(pos),activity,grade_input,alert));
    	alert.show();
	}
}
