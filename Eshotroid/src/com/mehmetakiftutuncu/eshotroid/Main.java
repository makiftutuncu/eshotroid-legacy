package com.mehmetakiftutuncu.eshotroid;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SearchViewCompat;
import android.support.v4.widget.SearchViewCompat.OnQueryTextListenerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.Gson;
import com.mehmetakiftutuncu.eshotroid.model.BusLine;
import com.mehmetakiftutuncu.eshotroid.model.BusLineListAdapter;
import com.mehmetakiftutuncu.eshotroid.utilities.GetPageTask;
import com.mehmetakiftutuncu.eshotroid.utilities.Parser;
import com.mehmetakiftutuncu.eshotroid.utilities.Processor;
import com.mehmetakiftutuncu.eshotroid.utilities.StorageManager;

/**
 * Main activity of the application
 * 
 * @author Mehmet Akif Tütüncü
 */
public class Main extends SherlockActivity
{
	private RadioButton rbWeekDays;
	private RadioButton rbSaturday;
	private RadioButton rbSunday;
	private ListView lvList;
	
	private String searchQuery;
	private BusLineListAdapter busLineListAdapter;
	
	/**
	 * Tag for debugging
	 */
	public static final String LOG_TAG = "Eshotroid_Main";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Constants.PACKAGE_NAME = getPackageName();
		
		initialize();
		
		loadBusLines();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
	{
		View searchView = SearchViewCompat.newSearchView(getSupportActionBar().getThemedContext());
		
		menu.add("Search").setIcon(R.drawable.ic_search)
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
        		busLineListAdapter.getFilter().filter(searchQuery);
        		return true;
        	}
        });

        return true;
    }
	
	/**	Initializes components */
	private void initialize()
	{
		rbWeekDays = (RadioButton) findViewById(R.id.main_radioButton_weekDays);
		rbSaturday = (RadioButton) findViewById(R.id.main_radioButton_saturday);
		rbSunday = (RadioButton) findViewById(R.id.main_radioButton_sunday);
		
		updateSelectedLineType();
		
		lvList = (ListView) findViewById(R.id.main_indexableListView_busLines);
		lvList.setFastScrollEnabled(true);
		lvList.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int index, long id)
			{
				String line = new Gson().toJson(BusLine.create(((TextView) view).getText().toString()));
				String type = getSelectedLineType();
				
				Intent intent = new Intent(Main.this, Times.class);
				intent.putExtra(Constants.BUS_LINE_EXTRA, line);
				intent.putExtra(Constants.TYPE_EXTRA, type);
				startActivity(intent);
			}
		});
	}
	
	/**	Loads bus lines from external storage if possible, if not tries to download them. */
	private void loadBusLines()
	{
		ArrayList<BusLine> busLines = StorageManager.readBusLines();
		if(busLines != null)
		{
			Log.d(LOG_TAG, "Bus lines already exist on the external storage.");
		}
		else
		{
			Log.d(LOG_TAG, "Bus lines don't exist on the external storage. Trying to download...");
			
			GetPageTask task = new GetPageTask(this);
			task.execute(Constants.BUS_LINES_URL);
			
			try
			{
				String busLinesPage = task.get();
				
				if(busLinesPage != null)
				{
					ArrayList<String> parsedBusLines = Parser.parseBusLines(busLinesPage);
					busLines = Processor.processBusLines(parsedBusLines);
					
					StorageManager.storeBusLines(busLines);
				}
			}
			catch(Exception e)
			{
				Log.e(LOG_TAG, "Couldn't download bus times!", e);
			}
		}
		
		busLineListAdapter = new BusLineListAdapter(this, busLines);
		lvList.setAdapter(busLineListAdapter);
	}
	
	/**	Updates the selected line type radio buttons according to the current day */
	private void updateSelectedLineType()
	{
		switch(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))
		{
			case Calendar.MONDAY:
			case Calendar.TUESDAY:
			case Calendar.WEDNESDAY:
			case Calendar.THURSDAY:
			case Calendar.FRIDAY:
				rbWeekDays.setChecked(true);
				break;
			case Calendar.SATURDAY:
				rbSaturday.setChecked(true);
				break;
			case Calendar.SUNDAY:
				rbSunday.setChecked(true);
				break;
		}
	}
	
	/**
	 * Gets the appropriate line type parameter according to the selected radio button
	 * 
	 * @return The appropriate line type parameter
	 */
	private String getSelectedLineType()
	{
		if(rbSaturday.isChecked())
		{
			return "C";
		}
		else if(rbSunday.isChecked())
		{
			return "P";
		}
		else
		{
			return "H";
		}
	}
}