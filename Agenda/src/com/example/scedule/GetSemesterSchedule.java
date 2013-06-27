package com.example.scedule;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class GetSemesterSchedule extends AsyncTask<String, Void, Course >{
	ScheduleCourseDatabaseHandler db;
	Context context;
	int error = 0;

	public GetSemesterSchedule(ScheduleCourseDatabaseHandler db, Context con) {
		super();
		this.db = db;
		this.context = con;
	}

	@Override
	protected Course doInBackground(String... html) {
		String prog = "";
		Course c = new Course();
		
		// Debug the task thread name
        Log.d("GetSchedule", Thread.currentThread().getName());
        
		try {
			String[] Days = {"Δευτέρα", "Τρίτη", "Τετάρτη", "Πέμπτη", "Παρασκευή"};
			HashMap<String, String> Prog = new HashMap<String, String>();
			Document doc = Jsoup.connect(html[0]).get();
			
    //"http://cgi.di.uoa.gr/~schedule/timetables/12-13/timetable_PPS_spring1213.html").get();
			for (int j = 0; j < 5; j++) { // 5 meres
				for (int i = 1; i < 10; i++) { // 9 ai8ouses
					for (Element table : doc.select("table[summary=" + Days[j] + "]")) {
						for (Element row : table.select("tr")) {
							Elements tds = row.select("td");
							if (tds.size() > 1) {
								prog += tds.get(0).text() + " : " + tds.get(i).text() + "\n";
							}
						}
					}
				}
				Prog.put(Days[j], prog);
				prog = "";
			}
			String data1="";
			String[] lines;
			String res[],res1[],res2[];
			String hour;
			String course;
			String text;
    
			Iterator<String> iter = Prog.keySet().iterator();

			while (iter.hasNext()) {
				String key = (String) iter.next();
				String val = (String) Prog.get(key);
				lines = val.split(System.getProperty("line.separator"));
				for(int j=0;j<lines.length;j++){
					text = lines[j];
    
					if(findWholeWord(text,"Αίθουσα")==-1){
						res = text.split(":");
						res1 = res[1].split("-");
						hour = res[0]+ "-" +res1[1];
						course = res[3];
						if(course.length()!=2){
//System.out.println(key+ "----" + data1.trim() + "----" + hour + "----" + course.trim() );
							//c.setData(key, data1.trim(), hour, course.trim());
							db.addSCourse(new ScheduleCourseDatabaseItem(key, hour, course.trim(), data1.trim()));
						}
					}
					else if(findWholeWord(text,"Αίθουσα")!=-1){
						res = text.split("/");
						res2 = res[1].split(":");
						data1 = res2[1] + ",";
					}
				}
			}
db.printAll();
			//for(int i=0; i<c.getData().size();i++){
			//	System.out.println(c.getData().get(i).getDay() + "----"+c.getData().get(i).getHour() + "----"+c.getData().get(i).getClass()+ "-----"+c.getData().get(i).getName());
			//}
		}catch(HttpStatusException ex1){			//Semester schedule has not published yet
			Log.e("GetSchedule1", ex1.getMessage());
			error = 1;
			this.publishProgress();
		}
		catch(MalformedURLException ex2){
			Log.e("GetSchedule2", ex2.getMessage());
		}
		catch (Exception e) {
			Log.e("GetSchedule", e.getMessage()+ " " + e.getCause());
			error = 2;
			this.publishProgress();
		}
		return c;
	}
	

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
		if(error == 1)
			Toast.makeText(context, "Semester schedule is not yet published.", Toast.LENGTH_LONG).show();
		else if(error == 2)
			Toast.makeText(context, "Internet Connection Problem. Please check your internet connection.", Toast.LENGTH_LONG).show();
		error = 0;
	}

	private int findWholeWord(final String text, final String searchString) {
	    return (" " + text + " ").indexOf(" " + searchString + " ");
	}
	
}
