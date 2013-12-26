package com.example.courses;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

// This class is responsibly for validating the typed grade, when user click ok
public class DialogListener implements OnShowListener{
	CourseItem course;
	Activity activity;
	EditText grade;
	AlertDialog alert;
	
	public DialogListener(CourseItem c, Activity anActivity, EditText gr, AlertDialog al){
		course = c;
		activity = anActivity;
		grade = gr;
		alert = al;
	}
	

	@Override
	public void onShow(DialogInterface dialog) {

        Button b = alert.getButton(AlertDialog.BUTTON_NEUTRAL);
        b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String regex = "10.0||10||[0-9](\\.[0-9])?";
				if(grade != null){
					String gr = grade.getText().toString();
					if(gr.length()!=0){
						Pattern pattern = Pattern.compile(regex);
						Matcher matcher = pattern.matcher(gr);
						if(matcher.matches()){
							Double grade_double = Double.valueOf(gr);
							if(grade_double < 5.0){
								TextView tx = new TextView(activity);
								tx.setTextSize(16);
								tx.setText("The grade is not promotable. You better insert grade when you have pass the course");
						
								AlertDialog.Builder builder = new AlertDialog.Builder(activity);
								builder
								.setTitle("Not promotable grade..")
								.setView(tx)
								.setNeutralButton(android.R.string.ok,null)
								.show();
				    	
								grade.setText("");
							}else{
								course.setGrade(grade_double);
								course.setChanged(true);
								alert.dismiss();
							}
						}
						else{
							TextView tx = new TextView(activity);
							tx.setTextSize(16);
							tx.setText("Please set an appropriate grade");
					
							AlertDialog.Builder builder = new AlertDialog.Builder(activity);
							builder
							.setTitle("Grade error..")
							.setView(tx)
							.setNeutralButton(android.R.string.ok,null)
							.show();
			    	
							grade.setText("");
						}
					}
					else
						alert.dismiss();
				}
				else
					alert.dismiss();
			}
        });
	}
}
