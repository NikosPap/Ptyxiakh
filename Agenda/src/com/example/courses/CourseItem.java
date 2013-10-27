package com.example.courses;

// This class hold all the useful informations about a course
public class CourseItem {
	private String name = "";
	private String desc = "";
	private Double grade = -1.0;
	private boolean checked = false;	//boolean variable for status of checkbox
	private boolean changed = false;	//boolean variable for status of grade

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public CourseItem() {
	}

	public CourseItem(String name) {
		this.name = name;
	}

	public CourseItem(String name, boolean checked, String desc, Double gr) {
		this.name = name;
		this.checked = checked;
		this.desc = desc;
		this.grade = gr;
	}
	
	public CourseItem(String name, boolean checked, String desc) {
		this.name = name;
		this.checked = checked;
		this.desc = desc;
	}

	public Double getGrade() {
		return grade;
	}

	public void setGrade(Double grade) {
		this.grade = grade;
	}

	public String getDesc(){
		return this.desc;
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

	public String toString() {
		return name;
	}

	public void toggleChecked() {
		checked = !checked;
	}
}
