package com.mehmetakiftutuncu.eshotroid;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SearchViewCompat;
import android.support.v4.widget.SearchViewCompat.OnQueryTextListenerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
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
import com.mehmetakiftutuncu.eshotroid.utilities.ExpandableHeightGridView;

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
	
	private String searchQuery;
	private BusListAdapter busListAdapter;
	
	private ArrayList<Bus> busses;
	
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
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
	{
		View searchView = SearchViewCompat.newSearchView(getSupportActionBar().getThemedContext());
		
		menu.add("item_search")
			.setIcon(R.drawable.ic_search)
        	.setActionView(searchView)
        	.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		
		MenuInflater inflater = getSherlock().getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
        
        SearchViewCompat.setOnQueryTextListener(searchView, new OnQueryTextListenerCompat()
        {
        	@Override
        	public boolean onQueryTextChange(String newText)
        	{
        		searchQuery = !TextUtils.isEmpty(newText) ? newText : null;
        		busListAdapter.getFilter().filter(searchQuery);
        		return true;
        	}
        });

        return true;
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
				Bus bus = (Bus) adapter.getItemAtPosition(position);
				
				Intent intent = new Intent(Main.this, Times.class);
				intent.putExtra(Constants.BUS_NUMBER_EXTRA, bus.getNumber());
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
		
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
		{
			ptrList.getRefreshableView().setFastScrollAlwaysVisible(true);
		}
		
		header = (LinearLayout) getLayoutInflater().inflate(R.layout.busses_header, null);
		
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
	
	/** Updates the list header and adds the favorited busses to the top */
	public void updateListHeader()
	{
		if(header != null)
		{
			toggleHeader(true);
		}
		
		ExpandableHeightGridView favoritedBussesGrid = (ExpandableHeightGridView) header.findViewById(R.id.gridView_favoritedBusses_grid);
		
		favoritedBussesGrid.setExpanded(true);
		
		ptrList.getRefreshableView().addHeaderView(header, null, false);
		
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
		toggleHeader(true);
		
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
			toggleHeader(false);
			
			/* Fill the list */
			busListAdapter = new BusListAdapter(this, busses);
			ptrList.setAdapter(busListAdapter);
		}
	}
	
	/**
	 * Toggles the list header
	 * 
	 * @param isGone If true, the header will be removed, else it will be updated and re-added
	 */
	public void toggleHeader(boolean isGone)
	{
		if(isGone)
		{
			ptrList.getRefreshableView().removeHeaderView(header);
		}
		else
		{
			updateListHeader();
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
		}
		else
		{
			progressBar.setVisibility(View.GONE);
		}
	}
}