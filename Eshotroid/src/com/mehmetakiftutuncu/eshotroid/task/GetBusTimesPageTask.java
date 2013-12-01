package com.mehmetakiftutuncu.eshotroid.task;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.mehmetakiftutuncu.eshotroid.BuildConfig;
import com.mehmetakiftutuncu.eshotroid.R;
import com.mehmetakiftutuncu.eshotroid.activity.Times;
import com.mehmetakiftutuncu.eshotroid.database.BusDatabase;
import com.mehmetakiftutuncu.eshotroid.fragment.BusTimesFragment;
import com.mehmetakiftutuncu.eshotroid.model.Bus;
import com.mehmetakiftutuncu.eshotroid.model.BusTime;
import com.mehmetakiftutuncu.eshotroid.model.BusTimeTypes;
import com.mehmetakiftutuncu.eshotroid.utility.Connection;
import com.mehmetakiftutuncu.eshotroid.utility.Constants;
import com.mehmetakiftutuncu.eshotroid.utility.Messages;
import com.mehmetakiftutuncu.eshotroid.utility.Parser;
import com.mehmetakiftutuncu.eshotroid.utility.Processor;

/** An asynchronous task for getting the times of a bus page
 * 
 * @author mehmetakiftutuncu */
public class GetBusTimesPageTask extends AsyncTask<Void, Void, Void> implements Runnable
{
	/** {@link Context} of the activity that uses runs this task */
	private Context myContext;
	/** {@link BusTimesFragment} whose contents will be updated */
	private BusTimesFragment myBusTimesFragment;
	/** {@link Bus} object whose times will be updated */
	private Bus myBus;
	/** Type of the times, one of the values of {@link BusTimeTypes} */
	private BusTimeTypes myType;
	
	/** URL of the page to be downloaded generated from
	 * {@link GetBusTimesPageTask#myBus} and {@link GetBusTimesPageTask#myType} */
	private String url;
	/** The contents of the page that will be downloaded and later parsed */
	private String result;
	/** The resulting list of {@link BusTime} objects after processing
	 * downloaded data */
	private ArrayList<BusTime> busTimes;
	
	/** Tag for debugging */
	private static final String LOG_TAG = "Eshotroid_GetBusTimesPageTask";
	
	/** Constructor for background downloading
	 *
	 * @param context {@link GetBusTimesPageTask#myContext}
	 * @param bus {@link GetBusTimesPageTask#myBus}
	 * @param type {@link GetBusTimesPageTask#myType} */
	public GetBusTimesPageTask(Context context, Bus bus, BusTimeTypes type)
	{
		this(context, null, bus, type);
	}
	
	/** Constructor for downloading and updating UI afterwards
	 *
	 * @param context {@link GetBusTimesPageTask#myContext}
	 * @param busTimesFragment {@link GetBusTimesPageTask#myBusTimesFragment}
	 * @param bus {@link GetBusTimesPageTask#myBus}
	 * @param type {@link GetBusTimesPageTask#myType} */
	public GetBusTimesPageTask(Context context, BusTimesFragment busTimesFragment, Bus bus, BusTimeTypes type)
	{
		myContext = context;
		myBusTimesFragment = busTimesFragment;
		myBus = bus;
		myType = type;
		
		url = String.format("%s?%s=%s&%s=%s", Constants.BUS_TIMES_URL, Constants.NUMBER_PARAMETER, bus.getNumber(), Constants.TYPE_PARAMETER, type.getCode());
	}
	
	@Override
	protected Void doInBackground(Void... params)
	{
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Getting times for " + myBus.getNumber() + myType.getCode() + "...");
		
		// Post a message to the UI thread to update UI to busy
		new Handler(myContext.getMainLooper()).post(new Runnable()
		{
			@Override
			public void run()
			{
				// If a fragment is provided
				if(myBusTimesFragment != null)
				{
					// Hide bus times
					myBusTimesFragment.showBusTimes(false);
					
					// Toggle to busy
					switch(myType)
					{
						case WEEK_DAY:
							((Times) myContext).toggleMode(true, Constants.PAGE_ID_BUS_TIMES_H);
							break;
						case SATURDAY:
							((Times) myContext).toggleMode(true, Constants.PAGE_ID_BUS_TIMES_C);
							break;
						case SUNDAY:
							((Times) myContext).toggleMode(true, Constants.PAGE_ID_BUS_TIMES_P);
							break;
					}
				}
				
				if(myBusTimesFragment != null)
				{
					Messages.getInstance().showNeutral((Activity) myContext,
						myContext.getString(R.string.info_busTimes_refreshing,
								myBus.getNumber(),
								myContext.getString(myType.getNameResourceId())));
				}
			}
		});
		
		// Go and get the page
		result = Connection.getPage(myContext, url);
		
		// If page is downloaded successfully
		if(result != null)
		{
			// Parse and update the route of the bus
			myBus.setRoute(Parser.parseBusRoute(result));
			
			// Parse and extract bus times information from the page
			ArrayList<String> parsedBusTimes = Parser.parseBusTimes(result);
			if(parsedBusTimes != null)
			{
				// Create BusTime objects from the parsed information
				busTimes = Processor.processBusTimes(parsedBusTimes);
			}

			// If BusTime objects were created successfully
			if(busTimes != null)
			{
				if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Saving times for " + myBus.getNumber() + myType.getCode() + "...");
				
				// Update the times of the bus according to the type
				if(myType.equals(BusTimeTypes.WEEK_DAY))
				{
					myBus.setTimesH(busTimes);
				}
				else if(myType.equals(BusTimeTypes.SATURDAY))
				{
					myBus.setTimesC(busTimes);
				}
				else if(myType.equals(BusTimeTypes.SUNDAY))
				{
					myBus.setTimesP(busTimes);
				}
				
				// Write the bus with all new BusTime objects to the database
				BusDatabase db = BusDatabase.getDatabase(myContext);
				db.addOrUpdate(myBus);
				db.closeDatabase();
			}
		}
		
		// If a fragment is provided
		if(myBusTimesFragment != null)
		{
			// Post a message to the UI thread to update the list with new data
			new Handler(myContext.getMainLooper()).post(this);
		}
		
		return null;
	}

	@Override
	public void run()
	{
		// If page is downloaded successfully
		if(result != null)
		{
			// If bus times are created successfully
			if(busTimes != null)
			{
				// If a fragment is provided
				if(myBusTimesFragment != null)
				{
					// Toggle to normal
					switch(myType)
					{
						case WEEK_DAY:
							((Times) myContext).toggleMode(false, Constants.PAGE_ID_BUS_TIMES_H);
							break;
						case SATURDAY:
							((Times) myContext).toggleMode(false, Constants.PAGE_ID_BUS_TIMES_C);
							break;
						case SUNDAY:
							((Times) myContext).toggleMode(false, Constants.PAGE_ID_BUS_TIMES_P);
							break;
					}
				}
				
				if(myBusTimesFragment != null)
				{
					Messages.getInstance().showPositive((Activity) myContext,
						myContext.getString(R.string.info_busTimes_successful,
								myBus.getNumber(),
								myContext.getString(myType.getNameResourceId())));
				}
			}
			else
			{
				/* Downloaded but couldn't be parsed (meaning there was no time
				 * for selected type) */
				Messages.getInstance().showNeutral((Activity) myContext,
						myContext.getString(R.string.info_busTimes_noTimes,
							myBus.getNumber(),
							myContext.getString(myType.getNameResourceId())));
				
				// Update flag on database
				switch(myType)
				{
					case WEEK_DAY:
						myBus.setTimesHExists(false);
						break;
					case SATURDAY:
						myBus.setTimesCExists(false);
						break;
					case SUNDAY:
						myBus.setTimesPExists(false);
						break;
				}
				
				// Write the bus with updated flag to the database
				BusDatabase db = BusDatabase.getDatabase(myContext);
				db.addOrUpdate(myBus);
				db.closeDatabase();
			}
			
			// Update times in UI
			myBusTimesFragment.setBusTimes(busTimes);
			myBusTimesFragment.showBusTimes(true);
		}
		
		// Toggle to ready
		switch(myType)
		{
			case WEEK_DAY:
				((Times) myContext).toggleMode(false, Constants.PAGE_ID_BUS_TIMES_H);
				break;
			case SATURDAY:
				((Times) myContext).toggleMode(false, Constants.PAGE_ID_BUS_TIMES_C);
				break;
			case SUNDAY:
				((Times) myContext).toggleMode(false, Constants.PAGE_ID_BUS_TIMES_P);
				break;
		}
	}
}