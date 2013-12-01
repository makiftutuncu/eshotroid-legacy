package com.mehmetakiftutuncu.eshotroid.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.mehmetakiftutuncu.eshotroid.BuildConfig;
import com.mehmetakiftutuncu.eshotroid.R;
import com.mehmetakiftutuncu.eshotroid.activity.Times;
import com.mehmetakiftutuncu.eshotroid.model.Bus;
import com.mehmetakiftutuncu.eshotroid.model.BusTime;
import com.mehmetakiftutuncu.eshotroid.model.BusTimeTypes;
import com.mehmetakiftutuncu.eshotroid.utility.Constants;
import com.mehmetakiftutuncu.eshotroid.utility.Messages;

/** Fragment of all busses page
 * 
 * @author mehmetakiftutuncu */
public class BusTimesFragment extends Fragment
{
	/** Interface for ensuring the implementation of loading bus times */
	public interface BusTimesListener
	{
		/**	Reads bus times from the database if possible, if not tries to
		 * download them */
		public void onBusTimesLoaded();
	}
	
	/** Reference to the activity that implements {@link BusTimesListener} */
	private BusTimesListener listenerActivity;
	
	/** Root {@link View} of the fragment UI */
	private View layout;
	/** {@link TextView} in which the route of the bus will be shown */
	private TextView route;
	/** {@link TextView} in which the source of the bus will be shown */
	private TextView source;
	/** {@link TextView} in which the destination of the bus will be shown */
	private TextView destination;
	/** {@link TableLayout} in which the times of the bus will be shown */
	private TableLayout busTimesLayout;
	/** {@link ScrollView} that contains {@link BusTimesFragment#busTimesLayout} */
	private ScrollView busTimesScrollLayout;
	
	/** List of {@link BusTime} objects showing times of the current bus for
	 * current type */
	private ArrayList<BusTime> busTimes;
	
	/** Type of the bus times corresponding to one of the values returning from
	 * {@link BusTimeTypes#getCode()}
	 * 
	 * This will be set through the fragment's arguments */
	private String type;
	
	/** Flag indicating if the times of selected type for the bus is currently
	 * being refreshed */
	private boolean isRefreshing = true;
	
	/** Flag indicating if the UI is ready to show the times */
	private boolean isWaitingForLayout = true;
	
	/** Tag for debugging */
	private static final String LOG_TAG = "Eshotroid_BusTimesFragment";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Log.d(LOG_TAG, "Creating fragment UI for " + type + "...");
		
		// Create the view of this fragment
		layout = inflater.inflate(R.layout.fragment_bus_times, container, false);
		
		// Notify that layout is ready
		isWaitingForLayout = false;
		
		return layout;
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		// Ensure attached activity implements the interface
		try
		{
			Bundle arguments = getArguments();
			if(arguments != null && arguments.containsKey(Constants.BUS_TIMES_TYPE_EXTRA))
			{
				type = arguments.getString(Constants.BUS_TIMES_TYPE_EXTRA);
			}
			
			if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Attaching BusTimesFragment for " + type + "...");
			
			listenerActivity = (BusTimesListener) activity;
		}
		catch(Exception e)
		{
			throw new ClassCastException(activity.getClass().getName() + " should implement BusTimesListener!");
		}
	}
	
	/** @return {@link BusTimesFragment#isRefreshing} */
	public boolean isRefreshing()
	{
		return isRefreshing;
	}
	
	/** Sets {@link BusTimesFragment#isRefreshing} as given value */
	public void setRefreshing(boolean isRefreshing)
	{
		this.isRefreshing = isRefreshing;
		
		if(layout != null)
		{
			if(isRefreshing)
			{
				layout.setVisibility(View.GONE);
			}
			else
			{
				layout.setVisibility(View.VISIBLE);
			}
		}
	}
	
	/** @return {@link BusTimesFragment#busTimes} */
	public ArrayList<BusTime> getBusTimes()
	{
		return busTimes;
	}
	
	/** Sets {@link BusTimesFragment#busTimes} as given value */
	public void setBusTimes(ArrayList<BusTime> busTimes)
	{
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Setting bus times data set...");
		this.busTimes = busTimes;
		
		// Show loaded bus times
		showBusTimes(true);
	}
	
	/** Shows the bus times
	 * 
	 * @param isShowing If true, times will be shown, if false, times will be
	 * hidden */
	public void showBusTimes(final boolean isShowing)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// Wait here until layout is not null
				while(isWaitingForLayout);
				
				// When layout is ready, post following to UI thread to update times
				new Handler(Looper.getMainLooper()).post(new Runnable()
				{
					@Override
					public void run()
					{
						// Get components
						if(route == null)
						{
							route = (TextView) layout.findViewById(R.id.textView_busTimes_route);
						}
						if(source == null)
						{
							source = (TextView) layout.findViewById(R.id.textView_busTimes_source);
						}
						if(destination == null)
						{
							destination = (TextView) layout.findViewById(R.id.textView_busTimes_destination);
						}
						if(busTimesLayout == null)
						{
							busTimesLayout = (TableLayout) layout.findViewById(R.id.tableLayout_busTimes);
						}
						if(busTimesScrollLayout == null)
						{
							busTimesScrollLayout = (ScrollView) layout.findViewById(R.id.scrollView_busTimes);
						}
						
						// Clear route, source and destination
						route.setText("");
						source.setText("");
						destination.setText("");
						
						// Clear the times table
						busTimesLayout.removeAllViews();
						
						if(isShowing)
						{
							if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Showing bus times...");
							
							// Get the current bus
							Bus bus = ((Times) listenerActivity).getBus();
							
							// Set route, source and destination
							route.setText(bus.getRoute());
							source.setText(bus.getSource());
							destination.setText(bus.getDestination());
							
							// Get a layout inflater
							ActionBarActivity activity = (ActionBarActivity) listenerActivity;
							LayoutInflater inflater = activity.getLayoutInflater();
							
							// If there are bus times loaded
							if(busTimes != null)
							{
								boolean isSourceNextTimeFound = false;
								boolean isDestinationNextTimeFound = false;
								
								int sourceNextTimeIndex = -1;
								int destinationNextTimeIndex = -1;
								
								// For each bus time
								for(int i = 0; i < busTimes.size(); i++)
								{
									// Get current bus time
									BusTime busTime = busTimes.get(i);
									
									// Inflate the view for a row
									final View row = inflater.inflate(R.layout.item_times, null);
									
									// Find references to source and destination
									RelativeLayout sourceLayout = (RelativeLayout) row.findViewById(R.id.relativeLayout_item_times_source);
									RelativeLayout destinationLayout = (RelativeLayout) row.findViewById(R.id.relativeLayout_item_times_destination);
									TextView source = (TextView) row.findViewById(R.id.textView_item_times_source);
									TextView destination = (TextView) row.findViewById(R.id.textView_item_times_destination);
									ImageView isWheelChairEnabledSource = (ImageView) row.findViewById(R.id.imageView_item_times_wheelChair_source);
									ImageView isWheelChairEnabledDestination = (ImageView) row.findViewById(R.id.imageView_item_times_wheelChair_destination);
									
									/* Set click listeners so users can see the remaining time once
									 * they click on a time */
									source.setOnClickListener(new OnClickListener()
									{
										@Override
										public void onClick(View v)
										{
											showRemainingTime(((TextView) v).getText().toString());
										}
									});
									destination.setOnClickListener(new OnClickListener()
									{
										@Override
										public void onClick(View v)
										{
											showRemainingTime(((TextView) v).getText().toString());
										}
									});
									
									// Set the text inside the source and destination fields
									source.setText(busTime.getTimeFromSource());
									destination.setText(busTime.getTimeFromDestination());
									
									// Show wheel chair enabled status
									if(busTime.isWheelChairEnabledSource() && !busTime.getTimeFromSource().equals(""))
									{
										isWheelChairEnabledSource.setVisibility(View.VISIBLE);
									}
									if(busTime.isWheelChairEnabledDestination() && !busTime.getTimeFromDestination().equals(""))
									{
										isWheelChairEnabledDestination.setVisibility(View.VISIBLE);
									}
									
									// Set time status for source
									if(!isSourceNextTimeFound && !isTimePassed(busTime.getTimeFromSource()))
									{
										sourceLayout.setBackgroundColor(getResources().getColor(R.color.item_times_status_next));
										
										isSourceNextTimeFound = true;
										
										sourceNextTimeIndex = i;
									}
									
									// Set time status for destination
									if(!isDestinationNextTimeFound && !isTimePassed(busTime.getTimeFromDestination()))
									{
										destinationLayout.setBackgroundColor(getResources().getColor(R.color.item_times_status_next));
										
										isDestinationNextTimeFound = true;
										
										destinationNextTimeIndex = i;
									}
									
									// Add the generated row to the table
									busTimesLayout.addView(row);
								}
								
								// Scroll
								scrollToShowNextTime(sourceNextTimeIndex, destinationNextTimeIndex);
							}
						}
					}
				});
			}
		}).start();
	}
	
	private void scrollToShowNextTime(final int sourceNextTimeIndex, final int destinationNextTimeIndex)
	{
		new Handler(Looper.getMainLooper()).post(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					// Get item
					RelativeLayout nextTimeRow = (RelativeLayout) ((ViewGroup) busTimesLayout.getChildAt(0)).getChildAt(0);
					TextView nextTimeText = (TextView) nextTimeRow.getChildAt(0);
					
					int numberOfRowsToScroll = 0;
					
					// Calculate number of rows to scroll
					if(sourceNextTimeIndex != -1 && destinationNextTimeIndex != -1)
					{
						if(sourceNextTimeIndex < destinationNextTimeIndex)
							numberOfRowsToScroll = destinationNextTimeIndex;
						else
							numberOfRowsToScroll = sourceNextTimeIndex;
					}
					else
					{
						if(sourceNextTimeIndex != -1)
							numberOfRowsToScroll = sourceNextTimeIndex;
						else
							numberOfRowsToScroll = destinationNextTimeIndex;
					}
					
					// Calculate the height of a row
					int textSize = (int) nextTimeText.getTextSize();
					int textPadding = getResources().getDimensionPixelSize(R.dimen.item_times_padding) * 2;
					int rowHeight = textSize + textPadding;
					int scrollAmount = numberOfRowsToScroll * rowHeight;
					
					// Scroll
					busTimesScrollLayout.scrollTo(0, scrollAmount);
				}
				catch(Exception e)
				{
					Log.e(LOG_TAG, "Error occurred while scrolling to show the next time!", e);
				}
			}
		});
	}
	
	/** Shows the remaining time to the given time if the given time has not
	 * passed yet
	 * 
	 * @param time Time as a string with format HH:MM */
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
				
				if(remainingHours == 0)
				{
					Messages.getInstance().showPositive(getActivity(),
							getString(R.string.info_remainingTimeShort, time, remainingMinutes));
				}
				else
				{
					Messages.getInstance().showPositive(getActivity(),
							getString(R.string.info_remainingTime, time, remainingHours,
									remainingMinutes));
				}
			}
			else
			{
				Messages.getInstance().showNeutral(getActivity(),
						getString(R.string.info_selectedTimePassed));
			}
		}
		catch(Exception e)
		{
		}
	}
	
	/** Checks if the given time is passed
	 * 
	 * @param time Time as a string with format HH:MM
	 * 
	 * @return true if the time is passed, false otherwise or an error occurs */
	@SuppressLint("SimpleDateFormat")
	private boolean isTimePassed(String time)
	{
		boolean result = false;
		
		try
		{
			// If given time is empty, assume it is passed
			if(time.equals(""))
			{
				return true;
			}
			
			SimpleDateFormat format = new SimpleDateFormat("HH:mm");
			
			String currentTime = format.format(new Date(System.currentTimeMillis()));
			String[] currentTokens = currentTime.split(":");
			
			String givenTime = format.format(format.parse(time));
			String[] givenTokens = givenTime.split(":");
			
			int currentHours = Integer.parseInt(currentTokens[0]);
			int currentMinutes = Integer.parseInt(currentTokens[1]);
			int givenHours = Integer.parseInt(givenTokens[0]);
			int givenMinutes = Integer.parseInt(givenTokens[1]);
			
			if(givenHours == 0)
			{
				givenHours = 24;
			}
			
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
}