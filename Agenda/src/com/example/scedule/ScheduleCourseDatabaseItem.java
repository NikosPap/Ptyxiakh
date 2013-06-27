package com.example.scedule;

public class ScheduleCourseDatabaseItem {
    
    //private variables
    String _day;
    String _hour;
    String _name;
    String _class;
     

    // constructor
    public ScheduleCourseDatabaseItem(String day, String hour, String name, String cl){
        this._day = day;
        this._hour = hour;
        this._name = name;
        this._class = cl;
    }
     

    // getting DAY
    public String getDay(){
        return this._day;
    }
     
    // setting day
    public void setDay(String d){
        this._day = d;
    }
     
    // getting name
    public String getName(){
        return this._name;
    }
     
    // setting name
    public void setName(String name){
        this._name = name;
    }
     
    // getting hour
    public String getHour(){
        return this._hour;
    }
     
    // setting hour
    public void setHour(String h){
        this._hour = h;
    }
    
    public String get_class() {
		return _class;
	}
	public void set_class(String _class) {
		this._class = _class;
	}
}
