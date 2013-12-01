package com.mehmetakiftutuncu.eshotroid.activity;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mehmetakiftutuncu.eshotroid.BuildConfig;
import com.mehmetakiftutuncu.eshotroid.R;
import com.mehmetakiftutuncu.eshotroid.adapter.BusTimesFragmentPagerAdapter;
import com.mehmetakiftutuncu.eshotroid.database.BusDatabase;
import com.mehmetakiftutuncu.eshotroid.fragment.BusTimesFragment;
import com.mehmetakiftutuncu.eshotroid.model.Bus;
import com.mehmetakiftutuncu.eshotroid.model.BusTime;
import com.mehmetakiftutuncu.eshotroid.model.BusTimeTypes;
import com.mehmetakiftutuncu.eshotroid.task.GetBusTimesPageTask;
import com.mehmetakiftutuncu.eshotroid.utility.Constants;
import com.mehmetakiftutuncu.eshotroid.utility.Messages;

/** Times activity of the application which shows the times of a selected bus
 * and contains a {@link ViewPager} for switching between three main types of
 * the time table; week days, saturday and sunday
 * 
 * @author mehmetakiftutuncu */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Times extends ActionBarActivity implements BusTimesFragment.BusTimesListener
{
	/** A {@link BusTimesFragmentPagerAdapter} for switching between types */
	private BusTimesFragmentPagerAdapter pagerAdapter;
	/** A {@link ViewPager} containing the pages to switch */
	private ViewPager viewPager;
	
	/** Reference to the refresh item in the action bar menu */
	private MenuItem refreshMenuItem;
	
	/** {@link Bus} object whose times will be displayed */
	private Bus bus;
	/** Number of the bus to load from database into {@link Times#bus} */
	private int busNumber;
	
	/** Tag for debugging */
	private static final String LOG_TAG = "Eshotroid_Times";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_times);
		
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Creating times activity...");
		
		// Get bus number that was passed
		Bundle bundle = getIntent().getExtras();
		if(bundle != null)
		{
			busNumber = bundle.getInt(Constants.BUS_NUMBER_EXTRA);
			
			if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Bus number is read from bundle as " + busNumber + ".");
			
			// Set the title in ActionBar with the bus number
			getSupportActionBar().setTitle(getString(R.string.times_title, busNumber));
		}
		
		// Create and initialize pages
		initializePages();
		
		/* Show home button in the action bar which will go back to main
		 * activity */
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onBusTimesLoaded()
	{
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Loading bus times...");
		
		// Get bus from the database
		BusDatabase db = BusDatabase.getDatabase(this);
		bus = db.get(busNumber);
		db.closeDatabase();
		
		// Check current page to decide the type of times
		BusTimeTypes type = null;
		switch(viewPager.getCurrentItem())
     	{
     		case Constants.PAGE_ID_BUS_TIMES_H:
     			type = BusTimeTypes.WEEK_DAY;
     			break;
     		
     		case Constants.PAGE_ID_BUS_TIMES_C:
     			type = BusTimeTypes.SATURDAY;
     			break;
     		
     		case Constants.PAGE_ID_BUS_TIMES_P:
     			type = BusTimeTypes.SUNDAY;
     			break;
     	}
		
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Type of bus times is " + type + ".");
		
		// If bus is found on the database
		if(bus != null)
		{
			BusTimesFragment busTimesFragment = null;
			ArrayList<BusTime> busTimes = null;
			boolean busTimesExist = true;
			
			/* Check the type to decide what fragment and times data set to use
			 * 
			 * By getting fragments here it is guaranteed that the fragments
			 * will be created because corresponding get methods ensure it. */
			switch(type)
	     	{
	     		case WEEK_DAY:
	     			busTimesFragment = pagerAdapter.getHBusTimesFragment();
	     			busTimes = bus.getTimesH();
	     			busTimesExist = bus.timesHExists();
	     			break;
	     		
	     		case SATURDAY:
	     			busTimesFragment = pagerAdapter.getCBusTimesFragment();
	     			busTimes = bus.getTimesC();
	     			busTimesExist = bus.timesCExists();
	     			break;
	     		
	     		case SUNDAY:
	     			busTimesFragment = pagerAdapter.getPBusTimesFragment();
	     			busTimes = bus.getTimesP();
	     			busTimesExist = bus.timesPExists();
	     			break;
	     	}
			
			if(busTimes == null)
 			{
				if(!busTimesExist)
				{
					// There is no bus that day
					Messages.getInstance().showNeutral(this,
							getString(R.string.info_busTimes_noTimes,
							bus.getNumber(), getString(type.getNameResourceId())));
				}
				else
				{
					if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Times for " + busNumber + type.getCode() + " are not in the database! Downloading...");

					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
						new GetBusTimesPageTask(this, busTimesFragment, bus, type).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
					else
						new GetBusTimesPageTask(this, busTimesFragment, bus, type).execute();
				}
 			}
 			else
 			{
 				if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Times for " + busNumber + type.getCode() + " are loaded from the database!");
 				
 				// Set bus times of the fragment so the times will be shown
 				busTimesFragment.setBusTimes(busTimes);
 				
 				busTimesFragment.setRefreshing(false);
 			}
		}
		else
		{
			if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Times for " + busNumber + " are not in the database! Downloading...");
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				new GetBusTimesPageTask(this, pagerAdapter.getHBusTimesFragment(), bus, BusTimeTypes.WEEK_DAY).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
			else
				new GetBusTimesPageTask(this, pagerAdapter.getHBusTimesFragment(), bus, BusTimeTypes.WEEK_DAY).execute();
		}
	}
	
	/** This method is called when menu (ActionBar items) is being created */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the refresh menu and add it
     	getMenuInflater().inflate(R.menu.refresh, menu);
     	
     	// Get refresh menu item
     	refreshMenuItem = menu.findItem(R.id.item_refresh);
     	
     	// Get fragment of current page 
     	BusTimesFragment fragment = null;
     	switch(viewPager.getCurrentItem())
     	{
     		case Constants.PAGE_ID_BUS_TIMES_H:
     			fragment = pagerAdapter.getHBusTimesFragment();
     			break;
     		
     		case Constants.PAGE_ID_BUS_TIMES_C:
     			fragment = pagerAdapter.getCBusTimesFragment();
     			break;
     		
     		case Constants.PAGE_ID_BUS_TIMES_P:
     			fragment = pagerAdapter.getPBusTimesFragment();
     			break;
     	}
     	
     	// Show progress if necessary
     	if(fragment.isRefreshing())
        {
     		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Setting refresh button as progress...");
        	MenuItemCompat.setActionView(refreshMenuItem, R.layout.item_progress);
        }
     	
		return super.onCreateOptionsMenu(menu);
	}
	
	/** This method is called when a menu item is clicked */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Check which menu item is selected
        switch(item.getItemId())
        {
	        case android.R.id.home:
	        	if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Home button is selected for going up.");
	        	// Up button to go to the list of busses so finish activity
	        	finish();
	        	break;
	        	
	        case R.id.item_refresh:
	        	// Check current page to decide what to refresh 
	         	switch(viewPager.getCurrentItem())
	         	{
	         		case Constants.PAGE_ID_BUS_TIMES_H:
	         			if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Refreshing bus times for H...");
	         			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
	         				new GetBusTimesPageTask(this, pagerAdapter.getHBusTimesFragment(), bus, BusTimeTypes.WEEK_DAY).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
	         			else
	         				new GetBusTimesPageTask(this, pagerAdapter.getHBusTimesFragment(), bus, BusTimeTypes.WEEK_DAY).execute();
	         			break;
	         		
	         		case Constants.PAGE_ID_BUS_TIMES_C:
	         			if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Refreshing bus times for C...");
	         			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
	         				new GetBusTimesPageTask(this, pagerAdapter.getCBusTimesFragment(), bus, BusTimeTypes.SATURDAY).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
	         			else
	         				new GetBusTimesPageTask(this, pagerAdapter.getCBusTimesFragment(), bus, BusTimeTypes.SATURDAY).execute();
	         			break;
	         		
	         		case Constants.PAGE_ID_BUS_TIMES_P:
	         			if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Refreshing bus times for P...");
	         			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
	         				new GetBusTimesPageTask(this, pagerAdapter.getPBusTimesFragment(), bus, BusTimeTypes.SUNDAY).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
	         			else
	         				new GetBusTimesPageTask(this, pagerAdapter.getPBusTimesFragment(), bus, BusTimeTypes.SUNDAY).execute();
	         			break;
	         	}
	        	break;
        }
        
		return super.onOptionsItemSelected(item);
	}
	
	/** Toggles the UI mode to either ready or waiting (hides other content and
	 * shows a progress bar)
	 * 
	 * @param isWaiting If true, mode will change to waiting, else it will
	 * change to ready mode
	 * @param pageId Id of the page whose contents are being refreshed */
	public void toggleMode(boolean isRefreshing, int pageId)
	{
		if(refreshMenuItem != null)
		{
			/* Check the current page to decide for what page refreshing state
			 * should change */
			switch(viewPager.getCurrentItem())
	     	{
	     		case Constants.PAGE_ID_BUS_TIMES_H:
	     			if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Setting refreshing state for H times page as " + isRefreshing + "...");
	     			pagerAdapter.getHBusTimesFragment().setRefreshing(isRefreshing);
	     			break;
	     		
	     		case Constants.PAGE_ID_BUS_TIMES_C:
	     			if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Setting refreshing state for C times page as " + isRefreshing + "...");
	     			pagerAdapter.getCBusTimesFragment().setRefreshing(isRefreshing);
	     			break;
	     		
	     		case Constants.PAGE_ID_BUS_TIMES_P:
	     			if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Setting refreshing state for P times page as " + isRefreshing + "...");
	     			pagerAdapter.getPBusTimesFragment().setRefreshing(isRefreshing);
	     			break;
	     	}
			
			// Cause action bar menu to be created again
			supportInvalidateOptionsMenu();
		}
	}
	
	/** Creates and initializes pages */
	private void initializePages()
	{
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Initializing pages...");
		
		// Get view pager
		viewPager = (ViewPager) findViewById(R.id.viewPager_times);
		
		/* Set off screen page limit to 2 indicating that there may be 2
		 * non-visible pages in memory */
		viewPager.setOffscreenPageLimit(2);
		
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Setting bus times fragment pager adapter...");
		/* Create the adapter that will return a fragment for each of the three
		 * main types of time tables */
		pagerAdapter = new BusTimesFragmentPagerAdapter(this, getSupportFragmentManager());
		// Set up the ViewPager with the pages adapter.
		viewPager.setAdapter(pagerAdapter);
		
		/* Set page change listener to load correct times each time the page is
		 * changed */
		viewPager.setOnPageChangeListener(new OnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Page " + position + " is selected.");
				
				// Load bus times for the selected page
				onBusTimesLoaded();
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0)
			{
			}
		});
		
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Selecting default page...");
		// Going to select the default page on startup according to current day
		switch(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))
		{
			case Calendar.MONDAY:
			case Calendar.TUESDAY:
			case Calendar.WEDNESDAY:
			case Calendar.THURSDAY:
			case Calendar.FRIDAY:
				if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Selecting H times page as default...");
				viewPager.setCurrentItem(Constants.PAGE_ID_BUS_TIMES_H);
				break;
			case Calendar.SATURDAY:
				if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Selecting C times page as default...");
				viewPager.setCurrentItem(Constants.PAGE_ID_BUS_TIMES_C);
				break;
			case Calendar.SUNDAY:
				if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Selecting P times page as default...");
				viewPager.setCurrentItem(Constants.PAGE_ID_BUS_TIMES_P);
				break;
		}
	}
	
	/** @return {@link Times#bus} */
	public Bus getBus()
	{
		return bus;
	}
}