package com.mehmetakiftutuncu.eshotroid.task;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.widget.ListView;

import com.mehmetakiftutuncu.eshotroid.R;
import com.mehmetakiftutuncu.eshotroid.activity.Main;
import com.mehmetakiftutuncu.eshotroid.activity.SetupWizard;
import com.mehmetakiftutuncu.eshotroid.adapter.BusListAdapter;
import com.mehmetakiftutuncu.eshotroid.database.BusDatabase;
import com.mehmetakiftutuncu.eshotroid.fragment.AllBussesFragment;
import com.mehmetakiftutuncu.eshotroid.model.Bus;
import com.mehmetakiftutuncu.eshotroid.model.BusTimeTypes;
import com.mehmetakiftutuncu.eshotroid.utility.Connection;
import com.mehmetakiftutuncu.eshotroid.utility.Constants;
import com.mehmetakiftutuncu.eshotroid.utility.Messages;
import com.mehmetakiftutuncu.eshotroid.utility.Parser;
import com.mehmetakiftutuncu.eshotroid.utility.Processor;

/** An asynchronous task for getting the list of busses page
 * 
 * @author mehmetakiftutuncu */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GetListOfBussesPageTask extends AsyncTask<Void, Void, Void>
{
	/** {@link Context} of the activity that uses runs this task */
	private Context myContext;
	/** {@link AllBussesFragment} whose list will be updated */
	private AllBussesFragment myAllBussesFragment;
	/** {@link ListView} in which the list of all busses will be shown */
	private ListView myListView;
	
	/** Adapter of {@link GetListOfBussesPageTask#myListView} before new data is
	 * downloaded and set */
	private BusListAdapter oldAdapter;
	/** The contents of the page that will be downloaded and later parsed */
	private String result;
	/** The resulting list of {@link Bus} objects after processing downloaded data */
	private ArrayList<Bus> busses;
	
	/** Constructor for background downloading
	 *
	 * @param context {@link GetListOfBussesPageTask#myContext} */
	public GetListOfBussesPageTask(Context context)
	{
		myContext = context;
	}
	
	/** Constructor for downloading and updating UI afterwards
	 *
	 * @param context {@link GetListOfBussesPageTask#myContext}
	 * @param allBussesFragment {@link GetListOfBussesPageTask#allBussesFragment} */
	public GetListOfBussesPageTask(Context context, AllBussesFragment allBussesFragment)
	{
		myContext = context;
		myAllBussesFragment = allBussesFragment;
		myListView = allBussesFragment.getListView();
		
		// Get adapter to oldAdapter
		oldAdapter = (BusListAdapter) myListView.getAdapter();
		
		// Remove the current adapter so the list will be shown as empty
		myAllBussesFragment.setBusListAdapter(null);
	}
	
	@Override
	protected Void doInBackground(Void... params)
	{
		// Post a message to the UI thread to notify user that refreshing started
		new Handler(myContext.getMainLooper()).post(new Runnable()
		{
			@Override
			public void run()
			{
				if(myAllBussesFragment != null)
				{
					// Show the progress in all busses page
					((Main) myContext).toggleMode(true, Constants.PAGE_ID_ALL_BUSSES);
				}
				else
				{
					// Show the progress in setup wizard
					((SetupWizard) myContext).toggleProgressBar(true);
				}
				
				Messages.getInstance().showNeutral((Activity) myContext,
						myContext.getString(R.string.info_allBusses_refreshing));
			}
		});
		
		// Go and get the page
		result = Connection.getPage(myContext, Constants.LIST_OF_BUSSES_URL);
		
		// If page is downloaded successfully
		if(result != null)
		{
			busses = null;
			
			// Parse and extract bus information from the page
			ArrayList<String> parsedBusses = Parser.parseBusses(result);
			if(parsedBusses != null)
			{
				// Create Bus objects from the parsed information
				busses = Processor.processListOfBusses(parsedBusses);
			}
			
			// If Bus objects were created successfully
			if(busses != null)
			{
				// Write all new Bus objects to the database
				BusDatabase db = BusDatabase.getDatabase(myContext);
				for(Bus i : busses)
				{
					/* Update favorited status of the new bus item so user won't
					 * lose previously favorited busses during refreshing */
					Bus bus = db.get(i.getNumber());
					if(bus != null)
					{
						i.setFavorited(bus.isFavorited());
						
						// Bus was favorited, download all times again
						if(bus.isFavorited())
						{
							if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
							{
								new GetBusTimesPageTask(myContext, bus, BusTimeTypes.WEEK_DAY).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
								new GetBusTimesPageTask(myContext, bus, BusTimeTypes.SATURDAY).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
								new GetBusTimesPageTask(myContext, bus, BusTimeTypes.SUNDAY).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
							}
							else
							{
								new GetBusTimesPageTask(myContext, bus, BusTimeTypes.WEEK_DAY).execute();
								new GetBusTimesPageTask(myContext, bus, BusTimeTypes.SATURDAY).execute();
								new GetBusTimesPageTask(myContext, bus, BusTimeTypes.SUNDAY).execute();
							}
						}
					}
					
					db.addOrUpdate(i);
				}
				db.closeDatabase();
			}
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Void res)
	{
		super.onPostExecute(res);
		
		// If page is downloaded successfully
		if(result != null)
		{
			// If busses are created successfully
			if(busses != null)
			{
				Messages.getInstance().showPositive((Activity) myContext,
						myContext.getString(R.string.info_allBusses_successful));
				
				if(myAllBussesFragment != null)
				{
					// Set new bus list
					myAllBussesFragment.setBusListAdapter(new BusListAdapter(myContext, busses));
				}
			}
			else
			{
				Messages.getInstance().showNegative((Activity) myContext,
						myContext.getString(R.string.error_parse));
				
				if(myAllBussesFragment != null)
				{
					// Page is downloaded but busses couldn't be parsed
					myAllBussesFragment.setBusListAdapter(oldAdapter);
				}
			}
		}
		else
		{
			Messages.getInstance().showNegative((Activity) myContext,
					myContext.getString(R.string.error_noConnection));
		}
		
		if(myAllBussesFragment != null)
		{
			// Hide the progress in all busses page
			((Main) myContext).toggleMode(false, Constants.PAGE_ID_ALL_BUSSES);
		}
		else
		{
			((SetupWizard) myContext).setBusses(busses);
			
			// Hide the progress in setup wizard
			((SetupWizard) myContext).toggleProgressBar(false);
		}
	}
}