package com.mehmetakiftutuncu.eshotroid.activity;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mehmetakiftutuncu.eshotroid.BuildConfig;
import com.mehmetakiftutuncu.eshotroid.R;
import com.mehmetakiftutuncu.eshotroid.adapter.BusListAdapter;
import com.mehmetakiftutuncu.eshotroid.adapter.DrawerMenuListAdapter;
import com.mehmetakiftutuncu.eshotroid.adapter.MainPagesFragmentPagerAdapter;
import com.mehmetakiftutuncu.eshotroid.database.BusDatabase;
import com.mehmetakiftutuncu.eshotroid.fragment.AllBussesFragment;
import com.mehmetakiftutuncu.eshotroid.fragment.FavoriteBussesFragment;
import com.mehmetakiftutuncu.eshotroid.model.Bus;
import com.mehmetakiftutuncu.eshotroid.task.GetListOfBussesPageTask;
import com.mehmetakiftutuncu.eshotroid.utility.Constants;

/** Main activity of the application which contains a {@link ViewPager} for
 * switching between three main pages of the application; favorite busses, all
 * busses and Kent Kart balance
 * 
 * @author mehmetakiftutuncu */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Main extends ActionBarActivity
								implements SearchView.OnQueryTextListener,
								MenuItemCompat.OnActionExpandListener,
								AllBussesFragment.AllBussesListener,
								FavoriteBussesFragment.FavoriteBussesListener
{
	/** A {@link FragmentPagerAdapter} for switching between pages */
	private MainPagesFragmentPagerAdapter pagerAdapter;
	/** A {@link ViewPager} containing the pages to switch */
	private ViewPager viewPager;
	
	/** {@link DrawerLayout} for the activity to show a drawer menu */
	private DrawerLayout drawerMenu;
	/** {@link ListView} for the drawer menu containing menu items */
	private ListView drawerMenuList;
	/** {@link ActionBarDrawerToggle} of the {@link Main#drawerMenu} to handle
	 * opening and closing of drawer menu */
	private ActionBarDrawerToggle drawerMenuToggle;
	
	/** {@link SearchView} to perform searches */
	private SearchView searchView;
	
	/** Reference to the refresh item in the action bar menu */
	private MenuItem refreshMenuItem;
	
	/** First visible position in the all busses list, this will be used to
	 * restore the list to the position it was left */
	private int firstVisiblePosition = -1;
	
	/** Tag for debugging */
	private static final String LOG_TAG = "Eshotroid_Main";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		if(!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.IS_SETUP_WIZARD_FINISHED, false))
		{
			// Run setup wizard
			startActivity(new Intent(this, Welcome.class));
			
			finish();
			
			return;
		}
		
		setContentView(R.layout.activity_main);
		
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Creating main activity...");
		
		// Create and initialize drawer menu
        initializeDrawerMenu();
        
        /* Show home button in the action bar which will in fact trigger drawer
		 * menu */
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        
        // Create and initialize pages
     	initializePages();
	}
	
	@Override
	public void onListOfBussesLoaded()
	{
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Loading list of all busses...");
		
		// Read busses from the database
		BusDatabase db = BusDatabase.getDatabase(this);
		ArrayList<Bus> listOfBusses = db.get();
		db.closeDatabase();
		
		// If no busses exist on the database
		if(listOfBusses.size() == 0)
		{
			if(BuildConfig.DEBUG) Log.d(LOG_TAG, "List of all busses doesn't exist in database. Downloading...");
			
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				new GetListOfBussesPageTask(this, pagerAdapter.getAllBussesFragment()).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
			else
				new GetListOfBussesPageTask(this, pagerAdapter.getAllBussesFragment()).execute();
		}
		else
		{
			if(BuildConfig.DEBUG) Log.d(LOG_TAG, "List of all busses is loaded from database.");
			
			// Set the adapter
			pagerAdapter.getAllBussesFragment().setBusListAdapter(new BusListAdapter(this, listOfBusses));
			
			// Set the scroll position if it was changed before
			if(firstVisiblePosition != -1)
			{
				pagerAdapter.getAllBussesFragment().getListView().setSelectionFromTop(firstVisiblePosition, 0);
				firstVisiblePosition = -1;
			}
		}
	}
	
	@Override
	public void onSetBusSelected(ListView listView)
	{
		// Set the item click listener
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
			{
				// Get bus list adapter
				BusListAdapter busListAdapter = pagerAdapter
						.getAllBussesFragment().getBusListAdapter();
				
				// Get selected bus
				Bus bus = busListAdapter.getItem(position);
				
				if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Starting times activity for " + bus.getNumber() + "...");
				
				// Save current list position
				firstVisiblePosition = pagerAdapter.getAllBussesFragment().getListView().getFirstVisiblePosition();
				
				// Start times activity with selected bus number
				Intent intent = new Intent(Main.this, Times.class);
				intent.putExtra(Constants.BUS_NUMBER_EXTRA, bus.getNumber());
				startActivity(intent);
			}
		});
	}
	
	@Override
	public void onFavoriteBussesLoaded()
	{
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Loading favorite busses...");
		
		// Read busses from the database
		BusDatabase db = BusDatabase.getDatabase(this);
		ArrayList<Bus> listOfBusses = db.get();
		db.closeDatabase();
		
		// If some busses exist on the database
		if(listOfBusses.size() != 0)
		{
			// Create favorited busses list
			ArrayList<Bus> favoritedBusses = new ArrayList<Bus>();
			
			// Add favorited busses to the list
			for(Bus i : listOfBusses)
			{
				if(i.isFavorited())
				{
					favoritedBusses.add(i);
				}
			}
			
			// Set the adapter
			pagerAdapter.getFavoriteBussesFragment().setBusListAdapter(new BusListAdapter(this, favoritedBusses));
		}
	}

	@Override
	public void onSetFavoritedBusSelected(ListView listView)
	{
		// Set the item click listener
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
			{
				// Get favorite bus list adapter
				BusListAdapter busListAdapter = pagerAdapter
						.getFavoriteBussesFragment().getBusListAdapter();
				
				// Get selected bus
				Bus bus = busListAdapter.getItem(position);
				
				if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Starting times activity for " + bus.getNumber() + "...");
				// Start times activity with selected bus number
				Intent intent = new Intent(Main.this, Times.class);
				intent.putExtra(Constants.BUS_NUMBER_EXTRA, bus.getNumber());
				startActivity(intent);
			}
		});
	}

	/** This method is called when menu (ActionBar items) is being created */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Check if the drawer menu is open
		boolean isDrawerOpen = drawerMenu.isDrawerOpen(drawerMenuList);
		
		// If drawer is not open
		if(!isDrawerOpen)
		{
			// Get current page id
			int currentPage = viewPager.getCurrentItem();
			
			// Check current page id to decide which items to show
			switch(currentPage)
			{
				// Favorite busses, no items needed
				case Constants.PAGE_ID_FAVORITE_BUSSES:
					break;
				
				// All busses, search and refresh needed
				case Constants.PAGE_ID_ALL_BUSSES:
					// Inflate the search menu and add it
					getMenuInflater().inflate(R.menu.search, menu);
					
					// Get the search menu item and search view
					MenuItem searchItem = menu.findItem(R.id.item_search);
					searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
			        
					// Set the query hint
					searchView.setQueryHint(getString(R.string.menu_search_hint));
					
					// Set search listeners
					MenuItemCompat.setOnActionExpandListener(searchItem, this);
			        searchView.setOnQueryTextListener(this);
			        
			        // Inflate the refresh menu and add it
			     	getMenuInflater().inflate(R.menu.refresh, menu);
			     	
			     	// Get refresh menu item
			     	refreshMenuItem = menu.findItem(R.id.item_refresh);
			     	
			     	// Show progress if necessary
			     	if(pagerAdapter.getAllBussesFragment().isRefreshing())
			        {
			     		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Setting refresh button as progress for all busses page...");
			        	MenuItemCompat.setActionView(refreshMenuItem, R.layout.item_progress);
			        }
					break;
				
				// Favorite busses, no items needed
				case Constants.PAGE_ID_KENT_KART_BALANCE:
					// Inflate the refresh menu and add it
			     	getMenuInflater().inflate(R.menu.refresh, menu);
			     	
			     	// Get refresh menu item and hide it
			     	refreshMenuItem = menu.findItem(R.id.item_refresh);
			     	refreshMenuItem.setVisible(false);
			     	
			     	// Show progress if necessary
			     	if(pagerAdapter.getKentKartBalanceFragment().isQuerying())
			        {
			     		refreshMenuItem.setVisible(true);
			     		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Setting refresh button as progress for all Kent Kart page...");
			        	MenuItemCompat.setActionView(refreshMenuItem, R.layout.item_progress);
			        }
					break;
			}
		}
		
		return super.onCreateOptionsMenu(menu);
	}
	
	/** This method is called when a menu item is clicked */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		/* Pass the event to ActionBarDrawerToggle, if it returns true, then it
		 * has handled the app icon touch event */
		if(drawerMenuToggle.onOptionsItemSelected(item))
		{
			if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Home button is selected for drawer.");
			// Action is completed because drawer is opened
			return true;
		}
        
        // Check which menu item is selected
        switch(item.getItemId())
        {
        	// Search
	        case R.id.item_search:
	        	// Expand search view
	        	MenuItemCompat.expandActionView(item);
	        	break;
	        
	        case R.id.item_refresh:
	        	// Get current page id
				int currentPage = viewPager.getCurrentItem();
				
				// Check current page id to decide what to refresh
				switch(currentPage)
				{
					// Refresh the list of all busses
					case Constants.PAGE_ID_ALL_BUSSES:
						if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Refreshing the list of all busses...");
						// Start downloading
						if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
							new GetListOfBussesPageTask(this,
								pagerAdapter.getAllBussesFragment()).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
						else
							new GetListOfBussesPageTask(this,
								pagerAdapter.getAllBussesFragment()).execute();
						
						break;
				}
	        	break;
        }
        
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerMenuToggle.syncState();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		
		// Apply the configuration changes to drawer menu as well
		drawerMenuToggle.onConfigurationChanged(newConfig);
	}

	/** This method is called when the query text in {@link Main#searchView} is
	 * changed */
	@Override
	public boolean onQueryTextChange(String query)
	{
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Searching for " + query + "...");
		searchBusses(query);
		
		return true;
	}

	/** This method is called when the query text in {@link Main#searchView} is
	 * submitted */
	@Override
	public boolean onQueryTextSubmit(String query)
	{
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Search query submitted " + query + ".");
		searchView.clearFocus();
		
		return false;
	}

	/** This method is called when the {@link Main#searchView} is collapsed */
	@Override
	public boolean onMenuItemActionCollapse(MenuItem item)
	{
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Search is closed.");
		searchBusses(null);
		
		return true;
	}

	/** This method is called when the {@link Main#searchView} is expanded */
	@Override
	public boolean onMenuItemActionExpand(MenuItem item)
	{
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Search is opened.");
		return true;
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
			//switch(viewPager.getCurrentItem())
			switch(pageId)
			{
				case Constants.PAGE_ID_ALL_BUSSES:
					if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Setting refreshing state for all busses page as " + isRefreshing + "...");
					pagerAdapter.getAllBussesFragment().setRefreshing(isRefreshing);
					break;
				
				case Constants.PAGE_ID_KENT_KART_BALANCE:
					if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Setting refreshing state for Kent Kart page as " + isRefreshing + "...");
					pagerAdapter.getKentKartBalanceFragment().setQuerying(isRefreshing);
					break;
			}
			
			// Cause action bar menu to be created again
			supportInvalidateOptionsMenu();
		}
	}
	
	/** @return {@link Main#viewPager} */
	public ViewPager getViewPager()
	{
		return viewPager;
	}
	
	/** Creates and initializes pages */
	private void initializePages()
	{
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Initializing pages...");
		
		// Get view pager
		viewPager = (ViewPager) findViewById(R.id.viewPager_main);
		
		/* Set off screen page limit to 2 indicating that there may be 2
		 * non-visible pages in memory */
		viewPager.setOffscreenPageLimit(2);
		
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Setting main pages fragment pager adapter...");
		/* Create the adapter that will return a fragment for each of the three
		 * main pages of the application */
		pagerAdapter = new MainPagesFragmentPagerAdapter(this, getSupportFragmentManager());
		// Set up the ViewPager with the pages adapter.
		viewPager.setAdapter(pagerAdapter);
		
		// Set page change listener to update options menu for each page
		viewPager.setOnPageChangeListener(new OnPageChangeListener()
		{
			@Override
			public void onPageSelected(int arg0)
			{
				// Cause action bar menu to be created again
				supportInvalidateOptionsMenu();
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

		// Going to select the default page on startup
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Selecting default page...");
		
		// Read busses from the database
		BusDatabase db = BusDatabase.getDatabase(this);
		ArrayList<Bus> listOfBusses = db.get();
		db.closeDatabase();
		
		// Assume there is no favorited bus
		boolean hasFavorited = false;
		
		// Add favorited busses to the list
		for(Bus i : listOfBusses)
		{
			if(i.isFavorited())
			{
				hasFavorited = true;
				
				break;
			}
		}
		
		// If found any favorited bus
		if(hasFavorited)
		{
			if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Selecting favorite busses page as default...");
			// Start with favorited busses page
			viewPager.setCurrentItem(Constants.PAGE_ID_FAVORITE_BUSSES);
		}
		else
		{
			if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Selecting all busses page as default...");
			// By default start with all busses page
			viewPager.setCurrentItem(Constants.PAGE_ID_ALL_BUSSES);
		}
	}
	
	/** Creates and initializes drawer menu */
	private void initializeDrawerMenu()
	{
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Initializing drawer menu...");
		
		// Get drawer menu and drawer menu list
		drawerMenu = (DrawerLayout) findViewById(R.id.drawerLayout_main);
		drawerMenuList = (ListView) findViewById(R.id.listView_drawerMenu);
		
		// Set this to let back button open the drawable menu, and when it is opened, finish the activtiy
		drawerMenu.setFocusableInTouchMode(false);
		
		// Set menu items
		String[] drawerMenuItems = new String[]
		{
			getString(R.string.drawerMenu_rate),
			getString(R.string.drawerMenu_contact),
			getString(R.string.drawerMenu_website),
			getString(R.string.drawerMenu_help),
			getString(R.string.drawerMenu_about)
		};
		
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Setting drawer menu adapter...");
		
		// Set the drawer adapter
		drawerMenuList.setAdapter(new DrawerMenuListAdapter(this, drawerMenuItems));
		
		// Set the drawer menu list item click listener
		drawerMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
			{
				String selectedItem = (String) adapter.getItemAtPosition(position);
				
				if(selectedItem.equals(getString(R.string.drawerMenu_rate)))
				{
					Intent intentRate = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.APPLICATION_URL));
					startActivity(Intent.createChooser(intentRate, getString(R.string.drawerMenu_rate)));
				}
				else if(selectedItem.equals(getString(R.string.drawerMenu_contact)))
				{
					Intent intentContact = new Intent(Intent.ACTION_SEND);
					intentContact.setType("message/rfc822");
					intentContact.putExtra(Intent.EXTRA_EMAIL, new String[] {Constants.CONTACT});
					intentContact.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.about_title));
					startActivity(Intent.createChooser(intentContact, getString(R.string.drawerMenu_contact)));
				}
				else if(selectedItem.equals(getString(R.string.drawerMenu_website)))
				{
					Intent intentWebsite = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.WEBSITE_URL));
					startActivity(Intent.createChooser(intentWebsite, getString(R.string.drawerMenu_website)));
				}
				else if(selectedItem.equals(getString(R.string.drawerMenu_help)))
				{
					startActivity(new Intent(Main.this, Help.class));
				}
				else if(selectedItem.equals(getString(R.string.drawerMenu_about)))
				{
					startActivity(new Intent(Main.this, About.class));
				}
			}
		});
		
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Setting drawer menu toggle...");
		// Set drawer toggle
		drawerMenuToggle = new ActionBarDrawerToggle(this, drawerMenu,R.drawable.ic_drawer,
				R.string.drawerMenu_title, R.string.app_name)
		{
			@Override
			public void onDrawerOpened(View drawerView)
			{
				if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Drawer menu is opened.");
				
				// Change title
				getSupportActionBar().setTitle(R.string.drawerMenu_title);
				
				// Cause action bar menu to be created again
				supportInvalidateOptionsMenu();
			}
			
			@Override
			public void onDrawerClosed(View drawerView)
			{
				if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Drawer menu is closed.");
				
				// Change title
				getSupportActionBar().setTitle(R.string.app_name);
				
				// Cause action bar menu to be created again
				supportInvalidateOptionsMenu();
			}
		};
		drawerMenu.setDrawerListener(drawerMenuToggle);
		
		// Set the drawer menu shadow
		drawerMenu.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
	}
	
	/**	Searches busses with the given query */
	private void searchBusses(String query)
	{
		// Get bus list adapter
		BusListAdapter busListAdapter = pagerAdapter.getAllBussesFragment().getBusListAdapter();
		
		// If adapter is set and can be searched
		if(busListAdapter != null && busListAdapter.getFilter() != null)
		{
			// Filter the list according to the search query
			busListAdapter.getFilter().filter(query);
			busListAdapter.notifyDataSetChanged();
		}
	}
}