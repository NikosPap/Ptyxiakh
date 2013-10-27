package com.example.courses;

import java.io.IOException;
import java.util.HashMap;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabContentFactory;

import com.example.agenda.R;
import com.example.agendaMain.AgendMainActivity;

// This class is responsibly for creating the customized tabhost view of Courses
public class Courses extends FragmentActivity implements TabHost.OnTabChangeListener {

	private TabHost mTabHost;
	private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, TabInfo>();
	private TabInfo mLastTab = null;

	private class TabInfo {
		 private String tag;
         private Class<?> clss;
         private Bundle args;
         private ListFragment fragment;
         
         
         TabInfo(String tag, Class<?> clazz, Bundle args) {
        	 this.tag = tag;
        	 this.clss = clazz;
        	 this.args = args;
        	 
         }

	}

	class TabFactory implements TabContentFactory {

		private final Context mContext;

	    /**
	     * @param context
	     */
	    public TabFactory(Context context) {
	        mContext = context;
	    }

	    /** (non-Javadoc)
	     * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
	     */
	    public View createTabContent(String tag) {
	        View v = new View(mContext);
	        v.setMinimumWidth(0);
	        v.setMinimumHeight(0);
	        return v;
	    }

	}
	/** (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//set back button
        ActionBar bar = getActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        TextView bv = new TextView(Courses.this);
        bv.setTextSize(18);
        bar.setCustomView(bv);
        bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.bd1));
		
		// Step 1: Inflmate layout
		setContentView(R.layout.courses);
		
		// Step 2: Setup TabHost
		initialiseTabHost(savedInstanceState);
		if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab")); //set the tab as per the saved state
        }
		
		// Connect to courses database
		CoursesDataBaseHelper myDbHelper = new CoursesDataBaseHelper(this);
        try { 
        	myDbHelper.createDataBase();
        } catch (IOException ioe) {
        	throw new Error("Unable to create database");
        }
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem menuItem){   
        startActivity(new Intent(this,AgendMainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        return true;
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_courses, menu);
		return true;
	}

	/** (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("tab", mTabHost.getCurrentTabTag()); //save the tab selected
        super.onSaveInstanceState(outState);
    }

	/**
	 * Step 2: Setup TabHost
	 */
	private void initialiseTabHost(Bundle args) {
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        TabInfo tabInfo = null;
        Courses.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("T1").setIndicator(" "), ( tabInfo = new TabInfo("T1", Fragment1_K.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        Courses.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("T2").setIndicator("»–"), ( tabInfo = new TabInfo("T2", Fragment2_Th.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        Courses.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("T3").setIndicator("’”"), ( tabInfo = new TabInfo("T3", Fragment3_Ys.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        Courses.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("T4").setIndicator("≈–"), ( tabInfo = new TabInfo("T4", Fragment4_Ep.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        Courses.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("T5").setIndicator("√–-≈À"), ( tabInfo = new TabInfo("T5", Fragment5_Gp_El.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        
        // Default to first tab
        this.onTabChanged("T1");
        //
        mTabHost.setOnTabChangedListener(this);
	}

	/**
	 * @param activity
	 * @param tabHost
	 * @param tabSpec
	 * @param clss
	 * @param args
	 */
	private static void addTab(Courses activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
		// Attach a Tab view factory to the spec
		tabSpec.setContent(activity.new TabFactory(activity));
        String tag = tabSpec.getTag();

        // Check to see if we already have a fragment for this tab, probably
        // from a previously saved state.  If so, deactivate it, because our
        // initial state is that a tab isn't shown.
        tabInfo.fragment = (ListFragment) activity.getSupportFragmentManager().findFragmentByTag(tag);
        if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.detach(tabInfo.fragment);
            ft.commit();
            activity.getSupportFragmentManager().executePendingTransactions();
        }

        tabHost.addTab(tabSpec);
	}

	/** (non-Javadoc)
	 * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
	 */
	public void onTabChanged(String tag) {
		TabInfo newTab = (TabInfo) this.mapTabInfo.get(tag);
		if (mLastTab != newTab) {
			FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
            if (mLastTab != null) {
                if (mLastTab.fragment != null) {
                	//ft.detach(mLastTab.fragment);
                	ft.hide(mLastTab.fragment);
                }
            }
            if (newTab != null) {
                if (newTab.fragment == null) {
                    newTab.fragment = (ListFragment) Fragment.instantiate(this, newTab.clss.getName(), newTab.args);
                    ft.add(R.id.realtabcontent, newTab.fragment, newTab.tag);
                } else {
                	ft.show(newTab.fragment);
                    //ft.attach(newTab.fragment);
                }
            }

            mLastTab = newTab;
            ft.commit();
            this.getSupportFragmentManager().executePendingTransactions();
		}
    }

}
