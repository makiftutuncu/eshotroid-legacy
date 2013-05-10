package com.mehmetakiftutuncu.eshotroid;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SearchViewCompat;
import android.support.v4.widget.SearchViewCompat.OnQueryTextListenerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.mehmetakiftutuncu.eshotroid.database.MyDatabase;
import com.mehmetakiftutuncu.eshotroid.model.Bus;
import com.mehmetakiftutuncu.eshotroid.utilities.BusListAdapter;
import com.mehmetakiftutuncu.eshotroid.utilities.GetBussesPageTask;

/**
 * Main activity of the application
 * 
 * @author Mehmet Akif Tütüncü
 */
public class Main extends SherlockActivity
{
	private PullToRefreshListView ptrList;
	private ProgressBar progressBar;
	
	private String searchQuery;
	private BusListAdapter busListAdapter;
	
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
	private void initialize()
	{
		ptrList = (PullToRefreshListView) findViewById(R.id.pullToRefreshListView_main_busses);
		ptrList.getLoadingLayoutProxy().setPullLabel(getString(R.string.pullToRefresh_pull));
		ptrList.getLoadingLayoutProxy().setReleaseLabel(getString(R.string.pullToRefresh_release));
		ptrList.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int index, long id)
			{
				Bus bus = (Bus) adapter.getItemAtPosition(index);
				
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
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar_main);
	}
	
	/**	Tries to download busses */
	private void downloadBusses()
	{
		Log.d(LOG_TAG, "Trying to download busses...");
		
		GetBussesPageTask task = new GetBussesPageTask(this, ptrList);
		task.execute();
	}
	
	/**	Reads busses from the database if possible, if not tries to download them. */
	private void loadBusses()
	{
		/* Change the UI to waiting mode */
		toggleProgressBar(true);
		
		/* Read busses from the database */
		MyDatabase db = new MyDatabase(this);
		db.openDB();
		ArrayList<Bus> busses = db.get();
		db.closeDB();
		
		/* If no busses exist on the database */
		if(busses.size() == 0)
		{
			Log.d(LOG_TAG, "Busses are not on the database.");
			
			downloadBusses();
		}
		else
		{
			Log.d(LOG_TAG, "Busses are already on the database.");
			
			/* Change the UI to ready mode */
			toggleProgressBar(false);
			
			/* Fill the list */
			busListAdapter = new BusListAdapter(this, busses);
			ptrList.setAdapter(busListAdapter);
		}
	}
	
	/**
	 * Toggles the progress bar
	 * 
	 * @param turnOn Should be true to turn progress bar on
	 */
	public void toggleProgressBar(boolean turnOn)
	{
		if(turnOn)
		{
			progressBar.setVisibility(View.VISIBLE);
		}
		else
		{
			progressBar.setVisibility(View.GONE);
		}
	}
}