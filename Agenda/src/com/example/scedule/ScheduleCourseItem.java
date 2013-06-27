package com.example.scedule;

import android.widget.CheckBox;

public class ScheduleCourseItem {
	private String name = "";
	private boolean checked = false;
	private CheckBox checkBox;
	
	public CheckBox getCBView() {
		return checkBox;
	}

	public void setCBView(CheckBox checkBox) {
		this.checkBox = checkBox;
	}

	public ScheduleCourseItem(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	

}
