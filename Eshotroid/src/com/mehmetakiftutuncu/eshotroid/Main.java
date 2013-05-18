package com.mehmetakiftutuncu.eshotroid;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import com.actionbarsherlock.widget.SearchView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnPullEventListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mehmetakiftutuncu.eshotroid.adapters.BusListAdapter;
import com.mehmetakiftutuncu.eshotroid.adapters.FavoritedBusGridAdapter;
import com.mehmetakiftutuncu.eshotroid.database.MyDatabase;
import com.mehmetakiftutuncu.eshotroid.model.Bus;
import com.mehmetakiftutuncu.eshotroid.tasks.GetBussesPageTask;
import com.mehmetakiftutuncu.eshotroid.utilities.Dialogs;
import com.mehmetakiftutuncu.eshotroid.utilities.ExpandableHeightGridView;
import com.mehmetakiftutuncu.eshotroid.utilities.MenuHandler;

/**
 * Main activity of the application
 * 
 * @author Mehmet Akif Tütüncü
 */
public class Main extends SherlockActivity
{
	private PullToRefreshListView ptrList;
	private ProgressBar progressBar;
	private LinearLayout header;
	
	private BusListAdapter busListAdapter;
	
	private ArrayList<Bus> busses;
	
	private Menu moreMenu;
	
	/**
	 * Tag for debugging
	 */
	public static final String LOG_TAG = "Eshotroid_Main";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		initialize();
		
		loadBusses();
		
		updateListHeader();
		
		Dialogs.showMainHelpDialog(this, false);
	}
	
	/* When user initiates a search, Main activity will be called
	 * with a new intent with Intent.ACTION_SEARCH as action
	 * and an extra value which is query string */
	@Override
	protected void onNewIntent(Intent intent)
	{
		setIntent(intent);
		
		if(Intent.ACTION_SEARCH.equals(intent.getAction()))
		{
			String query = intent.getStringExtra(SearchManager.QUERY);
			
			searchBusses(query);
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
	{
		moreMenu = menu;
		
		final SearchView searchView = new SearchView(getSherlock().getActionBar().getThemedContext());
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		
		SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
		searchView.setSearchableInfo(info);
		
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
		{
			@Override
			public boolean onQueryTextSubmit(String query)
			{
				/* Since the query will be performed when the text changes,
				 * when submitted just request focus to hide keyboard
				 * and then indicate that it was handled */
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
				
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String query)
			{
				searchBusses(query);
				
				return true;
			}
		});
		
		MenuItem searchItem = menu.add("item_search").setIcon(R.drawable.ic_search).setActionView(searchView);
		
		searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		
		searchItem.setOnActionExpandListener(new OnActionExpandListener()
		{
			@Override
			public boolean onMenuItemActionExpand(MenuItem item)
			{
				return true;
			}
			
			@Override
			public boolean onMenuItemActionCollapse(MenuItem item)
			{
				// SearchView is collapsing, list all the busses again.
				searchBusses(null);
				
				return true;
			}
		});
		
		MenuInflater inflater = getSherlock().getMenuInflater();
		inflater.inflate(R.menu.menu, menu);

        return true;
    }
	
	/*
	@Override
	public boolean onMenuItemSelected(int featureId, android.view.MenuItem item)
	{
		switch(item.getItemId())
		{
			// SearchView is collapsing, list all the busses again.
			case android.R.id.home:
				searchBusses(null);
				return true;
				
			default:
				MenuHandler.handle(this, item);
				return true;
		}
	}
	*/
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			// SearchView is collapsing, list all the busses again.
			case android.R.id.home:
				searchBusses(null);
				return true;
				
			default:
				MenuHandler.handle(this, item);
				return true;
		}
	}
	
	// Using onKeyUp instead of onKeyDown is working, otherwise the menu would just close itself after opening
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_MENU)
		{
			if (event.getAction() == KeyEvent.ACTION_UP && moreMenu != null)
			{
				moreMenu.performIdentifierAction(R.id.item_more, 0);
				
				return true;
			}
		}
		
		return super.onKeyUp(keyCode, event);
	}
	
	/**	Initializes components */
	@SuppressLint("NewApi")
	private void initialize()
	{
		ptrList = (PullToRefreshListView) findViewById(R.id.pullToRefreshListView_main_busses);
		ptrList.getLoadingLayoutProxy().setPullLabel(getString(R.string.pullToRefresh_pull));
		ptrList.getLoadingLayoutProxy().setReleaseLabel(getString(R.string.pullToRefresh_release));
		ptrList.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
			{
				String number = ((TextView) view.findViewById(R.id.textView_busses_row_number)).getText().toString();
				
				Intent intent = new Intent(Main.this, Times.class);
				intent.putExtra(Constants.BUS_NUMBER_EXTRA, Integer.parseInt(number));
				startActivity(intent);
			}
		});
		ptrList.setOnPullEventListener(new OnPullEventListener<ListView>()
		{
			@Override
			public void onPullEvent(PullToRefreshBase<ListView> refreshView, State state, Mode direction)
			{
				ptrList.getLoadingLayoutProxy().setRefreshingLabel(getString(R.string.pullToRefresh_refresh));
			}
		});
		ptrList.setOnRefreshListener(new OnRefreshListener<ListView>()
		{
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView)
			{
				downloadBusses();
			}
		});
		ptrList.setScrollingWhileRefreshingEnabled(false);
		ptrList.getRefreshableView().setTextFilterEnabled(true);
		
		header = (LinearLayout) getLayoutInflater().inflate(R.layout.busses_header, null);
		ptrList.getRefreshableView().addHeaderView(header, null, false);
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar_main);
	}
	
	/**
	 * Sets the list of busses
	 * 
	 * @param busses New list of busses to be set
	 */
	public void setBussesList(ArrayList<Bus> busses)
	{
		this.busses = busses;
	}
	
	/**
	 * Gets the list of busses
	 */
	public ArrayList<Bus> getBussesList()
	{
		return busses;
	}
	
	/**
	 * Gets the bus list adapter
	 */
	public BusListAdapter getBusListAdapter()
	{
		return busListAdapter;
	}
	
	/** Updates the list header and adds the favorited busses to the top */
	public void updateListHeader()
	{
		ExpandableHeightGridView favoritedBussesGrid = (ExpandableHeightGridView) header.findViewById(R.id.gridView_favoritedBusses_grid);
		favoritedBussesGrid.setExpanded(true);
		
		ArrayList<Bus> favorited = new ArrayList<Bus>();
		
		for(Bus i : busses)
		{
			if(i.isFavorited())
			{
				favorited.add(i);
			}
		}
		
		if(favorited.size() > 0)
		{
			favoritedBussesGrid.setAdapter(new FavoritedBusGridAdapter(this, favorited));
			header.findViewById(R.id.linearLayout_favoritedBusses).setVisibility(View.VISIBLE);
		}
		else
		{
			header.findViewById(R.id.linearLayout_favoritedBusses).setVisibility(View.GONE);
		}
	}
	
	/**	Searches busses with the given query */
	private void searchBusses(String query)
	{
		if(busListAdapter != null && busListAdapter.getFilter() != null)
		{
			busListAdapter.getFilter().filter(query);
			busListAdapter.notifyDataSetChanged();
		}
	}
	
	/**	Tries to download busses */
	private void downloadBusses()
	{
		GetBussesPageTask task = new GetBussesPageTask(this, ptrList);
		task.execute();
	}
	
	/**	Reads busses from the database if possible, if not tries to download them. */
	private void loadBusses()
	{
		/* Change the UI to waiting mode */
		toggleMode(true);
		
		/* Read busses from the database */
		MyDatabase db = new MyDatabase(this);
		db.openDB();
		busses = db.get();
		db.closeDB();
		
		/* If no busses exist on the database */
		if(busses.size() == 0)
		{
			Log.d(LOG_TAG, "Busses could not be found on the database!");
			
			downloadBusses();
		}
		else
		{
			/* Change the UI to ready mode */
			toggleMode(false);
			
			/* Fill the list */
			busListAdapter = new BusListAdapter(this, busses);
			ptrList.setAdapter(busListAdapter);
		}
	}
	
	/**
	 * Toggles the UI mode to either ready or waiting (hides other content and shows a progress bar)
	 * 
	 * @param isWaiting If true, mode will change to waiting, else it will change to ready mode
	 */
	public void toggleMode(boolean isWaiting)
	{
		if(isWaiting)
		{
			progressBar.setVisibility(View.VISIBLE);
			ptrList.getRefreshableView().setVisibility(View.GONE);
		}
		else
		{
			progressBar.setVisibility(View.GONE);
			ptrList.getRefreshableView().setVisibility(View.VISIBLE);
		}
	}
}