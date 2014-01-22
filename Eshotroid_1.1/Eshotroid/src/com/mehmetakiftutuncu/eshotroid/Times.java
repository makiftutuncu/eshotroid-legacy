package com.mehmetakiftutuncu.eshotroid;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.appmsg.AppMsg;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnPullEventListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.mehmetakiftutuncu.eshotroid.database.MyDatabase;
import com.mehmetakiftutuncu.eshotroid.model.Bus;
import com.mehmetakiftutuncu.eshotroid.model.BusTime;
import com.mehmetakiftutuncu.eshotroid.tasks.GetTimesPageTask;
import com.mehmetakiftutuncu.eshotroid.utilities.Dialogs;
import com.mehmetakiftutuncu.eshotroid.utilities.MenuHandler;

/**
 * Times activity of the application
 * 
 * @author Mehmet Akif Tütüncü
 */
public class Times extends SherlockActivity
{
	private PullToRefreshScrollView ptrScrollView;
	private RadioGroup rgTypes;
	private RadioButton rbWeekDays;
	private RadioButton rbSaturday;
	private RadioButton rbSunday;
	private TextView tvRoute;
	private TableLayout tlTable;
	private TextView tvSource;
	private TextView tvDestination;
	private ProgressBar progressBar;
	
	private String type;
	private int number;
	private Bus bus;
	
	private boolean isClosestTime;
	
	private Menu moreMenu;
	
	/**
	 * Tag for debugging
	 */
	public static final String LOG_TAG = "Eshotroid_Times";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.times);
		
		Bundle bundle = getIntent().getExtras();
		if(bundle != null)
		{
			number = bundle.getInt(Constants.BUS_NUMBER_EXTRA);
		}
		
		initialize();
		
		loadBusTimes();
		
		Dialogs.showTimesHelpDialog(this, false);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
	{
		moreMenu = menu;
		
		menu.add("item_closesttime")
		.setIcon(R.drawable.ic_closesttime)
    	.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		MenuInflater inflater = getSherlock().getMenuInflater();
		inflater.inflate(R.menu.menu, menu);

        return true;
    }
	
	/*
	@Override
	public boolean onMenuItemSelected(int featureId, android.view.MenuItem item)
	{
		if(item.getTitle().equals("item_closesttime"))
		{
			if(isClosestTime)
			{
				isClosestTime = false;
			}
			else
			{
				isClosestTime = true;
			}
			
			updateInformation(bus);
			
			ptrScrollView.getRefreshableView().scrollTo(0, 0);
		}
		else if(item.getItemId() == android.R.id.home)
		{
			finish();
		}
		else
		{
			MenuHandler.handle(this, item);
		}
		
		return true;
	}
	*/
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getTitle().equals("item_closesttime"))
		{
			if(isClosestTime)
			{
				isClosestTime = false;
			}
			else
			{
				isClosestTime = true;
			}
			
			updateInformation(bus);
			
			ptrScrollView.getRefreshableView().scrollTo(0, 0);
		}
		else if(item.getItemId() == android.R.id.home)
		{
			finish();
		}
		else
		{
			MenuHandler.handle(this, item);
		}
		
		return true;
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
	private void initialize()
	{
		isClosestTime = true;
		
		rgTypes = (RadioGroup) findViewById(R.id.radioGroup_times_types);
		rbWeekDays = (RadioButton) findViewById(R.id.radioButton_times_weekDays);
		rbSaturday = (RadioButton) findViewById(R.id.radioButton_times_saturday);
		rbSunday = (RadioButton) findViewById(R.id.radioButton_times_sunday);
		tvRoute = (TextView) findViewById(R.id.textView_times_route);
		tlTable = (TableLayout) findViewById(R.id.tableLayout_times);
		tvSource = (TextView) findViewById(R.id.textView_times_source);
		tvDestination = (TextView) findViewById(R.id.textView_times_destination);
		
		updateSelectedType();
		
		ptrScrollView = (PullToRefreshScrollView) findViewById(R.id.pullToRefreshScrollView_times);
		ptrScrollView.getLoadingLayoutProxy().setPullLabel(getString(R.string.pullToRefresh_pull));
		ptrScrollView.getLoadingLayoutProxy().setReleaseLabel(getString(R.string.pullToRefresh_release));
		ptrScrollView.setOnPullEventListener(new OnPullEventListener<ScrollView>()
		{
			@Override
			public void onPullEvent(PullToRefreshBase<ScrollView> refreshView, State state, Mode direction)
			{
				ptrScrollView.getLoadingLayoutProxy().setRefreshingLabel(getString(R.string.pullToRefresh_refresh));
			}
		});
		ptrScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>()
		{
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView)
			{
				downloadBusTimes();
			}
		});
		ptrScrollView.setScrollingWhileRefreshingEnabled(false);
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar_times);
		
		rgTypes.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId)
			{
				loadBusTimes();
			}
		});
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	/**	Tries to download bus times */
	private void downloadBusTimes()
	{
		GetTimesPageTask task = new GetTimesPageTask(this, ptrScrollView, bus, type);
		task.execute();
	}
	
	/**	Loads bus times from the database if possible, if not tries to download them. */
	private void loadBusTimes()
	{
		type = getSelectedType();
		
		toggleProgressBar(true);
		
		MyDatabase db = new MyDatabase(this);
		db.openDB();
		bus = db.get(number);
		db.closeDB();
		
		if(bus != null && ((type.equals("H") && bus.getTimesH() != null) || (type.equals("C") && bus.getTimesC() != null) || (type.equals("P") && bus.getTimesP() != null)))
		{
			toggleProgressBar(false);
			
			updateInformation(bus);
		}
		else
		{
			Log.d(LOG_TAG, "Bus times for " + number + type + " could not be found on the database!");
			
			downloadBusTimes();
		}
	}
	
	/**	Updates the table with bus route and times */
	public void updateInformation(Bus bus)
	{
		this.bus = bus;
		
		tlTable.removeAllViews();
		
		setTitle("" + bus.getNumber());
		
		tvRoute.setText(bus.getRoute());
		tvSource.setText(bus.getSource());
		tvDestination.setText(bus.getDestination());
		
		LayoutInflater inflater = getLayoutInflater();
		
		ArrayList<BusTime> times = null;
		if(type.equals("H"))
		{
			times = bus.getTimesH();
		}
		else if(type.equals("C"))
		{
			times = bus.getTimesC();
		}
		else if(type.equals("P"))
		{
			times = bus.getTimesP();
		}
		
		if(times != null)
		{
			boolean isEmpty = true;
			for(BusTime i : times)
			{
				View row = inflater.inflate(R.layout.times_row, null);

				Button source = (Button) row.findViewById(R.id.button_times_row_source);
				Button destination = (Button) row.findViewById(R.id.button_times_row_destination);
				
				source.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						showRemainingTime(((Button) v).getText().toString());
					}
				});
				destination.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						showRemainingTime(((Button) v).getText().toString());
					}
				});
				
				if(isClosestTime)
				{
					String tempSource = i.getTimeFromSource();
					String tempDestination = i.getTimeFromDestination();
					
					if(!isTimePassed(tempSource))
					{
						source.setText(tempSource);
					}
					else
					{
						source.setText("");
					}
					
					if(!isTimePassed(tempDestination))
					{
						destination.setText(tempDestination);
					}
					else
					{
						destination.setText("");
					}
					
					if(!source.getText().toString().equals("") || !destination.getText().toString().equals(""))
					{
						tlTable.addView(row);
						
						isEmpty = false;
					}
				}
				else
				{
					source.setText(i.getTimeFromSource());
					destination.setText(i.getTimeFromDestination());
					
					tlTable.addView(row);
				}
			}
			
			if(isClosestTime && isEmpty)
			{
				isClosestTime = false;
				
				AppMsg.makeText(this, getString(R.string.info_noClosestTime), AppMsg.STYLE_CONFIRM).show();
				
				updateInformation(bus);
			}
		}
	}
	
	/**
	 * Shows the remaining time to the given time if the given time has not passed yet
	 * 
	 * @param time Time as a string with format HH:MM
	 */
	@SuppressLint("SimpleDateFormat")
	private void showRemainingTime(String time)
	{
		boolean isTimePassed = false;
		
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("HH:mm");
			
			String currentTime = format.format(new Date(System.currentTimeMillis()));
			String[] currentTokens = currentTime.split(":");
			
			String givenTime = format.format(format.parse(time));
			String[] givenTokens = givenTime.split(":");
			
			int currentHours = Integer.parseInt(currentTokens[0]);
			int currentMinutes = Integer.parseInt(currentTokens[1]);
			int givenHours = Integer.parseInt(givenTokens[0]);
			int givenMinutes = Integer.parseInt(givenTokens[1]);
			
			if(givenHours < currentHours)
			{
				isTimePassed = true;
			}
			else if(givenHours == currentHours)
			{
				if(givenMinutes < currentMinutes)
				{
					isTimePassed = true;
				}
			}
			
			if(!isTimePassed)
			{
				int remainingHours = givenHours - currentHours;
				int remainingMinutes = givenMinutes - currentMinutes;
				
				if(remainingMinutes < 0)
				{
					remainingHours--;
					remainingMinutes += 60;
				}
				
				AppMsg.makeText(this, getString(R.string.info_remainingTime, time, remainingHours, remainingMinutes), AppMsg.STYLE_INFO).show();
			}
			else
			{
				AppMsg.makeText(this, getString(R.string.info_selectedTimePassed), AppMsg.STYLE_CONFIRM).show();
			}
		}
		catch(Exception e)
		{
		}
	}
	
	/**
	 * Checks if the given time is passed
	 * 
	 * @param time Time as a string with format HH:MM
	 * 
	 * @return true if the time is passed, false otherwise or an error occures
	 */
	@SuppressLint("SimpleDateFormat")
	private boolean isTimePassed(String time)
	{
		boolean result = false;
		
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("HH:mm");
			
			String currentTime = format.format(new Date(System.currentTimeMillis()));
			String[] currentTokens = currentTime.split(":");
			
			String givenTime = format.format(format.parse(time));
			String[] givenTokens = givenTime.split(":");
			
			int currentHours = Integer.parseInt(currentTokens[0]);
			int currentMinutes = Integer.parseInt(currentTokens[1]);
			int givenHours = Integer.parseInt(givenTokens[0]);
			int givenMinutes = Integer.parseInt(givenTokens[1]);
			
			if(givenHours < currentHours)
			{
				return true;
			}
			else if(givenHours == currentHours)
			{
				if(givenMinutes < currentMinutes)
				{
					return true;
				}
			}
		}
		catch(Exception e)
		{
		}
		
		return result;
	}
	
	/**	Updates the selected type radio buttons according to the current day */
	private void updateSelectedType()
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
	 * Gets the appropriate type parameter according to the selected radio button
	 * 
	 * @return The appropriate type parameter
	 */
	private String getSelectedType()
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
			tlTable.setVisibility(View.GONE);
			tvDestination.setVisibility(View.GONE);
			tvSource.setVisibility(View.GONE);
		}
		else
		{
			progressBar.setVisibility(View.GONE);
			tlTable.setVisibility(View.VISIBLE);
			tvDestination.setVisibility(View.VISIBLE);
			tvSource.setVisibility(View.VISIBLE);
		}
	}
}