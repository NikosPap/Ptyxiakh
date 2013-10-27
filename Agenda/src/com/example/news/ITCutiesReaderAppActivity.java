package com.example.news;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.agenda.R;
import com.example.agendaMain.AgendMainActivity;


@SuppressLint("NewApi")
// Class responsibly for getting and displaying news titles of UOA
public class ITCutiesReaderAppActivity extends Activity {
    
    // A reference to the local object
    private ITCutiesReaderAppActivity local;
    private LayoutInflater vi;
    
    //Hold screens size
    private Point scrSize;
    
    ProgressDialog load;
    
    // Name of the preference file
    public static final String PREFS_NAME = "MyPrefsFile";
    
    //Name of the Set that will hold the already clicked titles
    public static final String CLICKED_TITLES="Titles_Clicked";
     
    /**
     * This method creates main application view
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //set back button
        ActionBar bar = getActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        TextView bv = new TextView(ITCutiesReaderAppActivity.this);
        bv.setTextSize(18);
        bar.setCustomView(bv);
        bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.bd1));
        //getActionBar().setDisplayShowTitleEnabled(false);
        
        // Set view
        setContentView(R.layout.activity_display_news);

        // Set reference to this activity
        local = this;
        
        scrSize=new Point();		//size of screen
        getWindowManager().getDefaultDisplay().getSize(scrSize);
         
        GetRSSDataTask task = new GetRSSDataTask();
         
        // Start download RSS task
        task.execute("http://www.di.uoa.gr/rss.xml");
         
        // Debug the thread name
        Log.d("ITCRssReader", Thread.currentThread().getName());
    }
    
 
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {   
        startActivity(new Intent(this,AgendMainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        return true;
    }
    public Point getScreenSize(){
    	return this.scrSize;
    }
    
    
    //Create a custom adapter in order to change color to already clicked titles
    private class MyAdapter extends ArrayAdapter<RssItem> {

    	private Context c;
    	@SuppressWarnings("unused")
		private int id;
    	private List<RssItem>items;
    	private Set<String> mySet;

    	public MyAdapter(Context context, int viewResourceId, List<RssItem> objects,Set<String> s){
    	    super(context,viewResourceId,objects);
    	    c=context;
    	    id=viewResourceId;
    	    items=objects;
    	    mySet=s;
    	}

    	@Override
    	public View getView(final int position, View convertView, ViewGroup parent) {
    	    View v = convertView;
    	    if (v == null) {
    	        vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	        v = vi.inflate(R.layout.list_row, null);
    	        
    	        
    	    }
    	    
    	    TextView tit = (TextView)v.findViewById(R.id.title);
	        TextView dat = (TextView)v.findViewById(R.id.date);
	        String[] sp;
    	    final RssItem o = items.get(position);
    	    sp = o.getPabDate().split(" ");
    	    
    	    if (o != null) {
    	    	/*TextView tx=(TextView)v;
    	    	tx.setTextSize(18);
    	    	tx.setText(o.getTitle());*/
    	    	tit.setText(o.getTitle());
    	    	dat.setText(sp[0]+sp[1]);
    	    	if(!mySet.isEmpty()){			//check if title exist in the Set
    	    		if (mySet.contains(o.getTitle())) {
    	    			v.setAlpha(0.6f);		//change color if exists
    	    		}
    	    	}		
    	    }
    	    return v;
    	}
    }
     
    @SuppressLint("NewApi")
	private class GetRSSDataTask extends AsyncTask<String, Void, List<RssItem> > {
    	
        @Override
        protected List<RssItem> doInBackground(String... urls) {
             
            // Debug the task thread name
            Log.d("ITCRssReader", Thread.currentThread().getName());
             
            try {
                // Create RSS reader
                RssReader rssReader = new RssReader(urls[0]);
             
                // Parse RSS, get items
                return rssReader.getItems();
             
            } catch (Exception e) {
                Log.e("ITCRssReader", e.getMessage());
            }
             
            return null;
        }
        
        @Override
        protected void onPreExecute()		//display a loading message
        {
            super.onPreExecute();
            load=ProgressDialog.show(ITCutiesReaderAppActivity.this, "Please wait", "Loading..", true);
            load.setCancelable(true);
        }
         
        @Override
        protected void onPostExecute(List<RssItem> result) {
        	//once the load has finished
            load.dismiss();
            
            Set<String> set;
            SharedPreferences.Editor prefEditor;
             
            // Get a ListView from main view
            ListView itcItems = (ListView) findViewById(R.id.list);
            
            //Retrieve shared preferences
            SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, 0);
            
            //A variable in order to check if this activity has launched for first time
    		Boolean FirstTimeLaunched = sharedPref.getBoolean("FirstTimeLaunched", true);
    		
    		//if is first time activity running, create a Set to keep which news has user already clicked
    		if(FirstTimeLaunched){
    			set = new HashSet<String>();
    			prefEditor = sharedPref.edit();
        		prefEditor.putBoolean("FirstTimeLaunched",false);
        		prefEditor.putStringSet(CLICKED_TITLES, set);
        		prefEditor.commit();
    		}
    		else{				//else retrieve the Set from shared preferences
    			set=sharedPref.getStringSet(CLICKED_TITLES, null);
    		}
    		// Create a list adapter
            MyAdapter adapter = new MyAdapter(local,android.R.layout.simple_list_item_1,result,set);
            
            // Set list adapter for the ListView
            itcItems.setAdapter(adapter);

            // Set list view item click listener
            itcItems.setOnItemClickListener(new NewsListListener(result, local));
        }
    }  
}