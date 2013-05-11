package com.mehmetakiftutuncu.eshotroid.tasks;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.devspark.appmsg.AppMsg;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.mehmetakiftutuncu.eshotroid.Constants;
import com.mehmetakiftutuncu.eshotroid.R;
import com.mehmetakiftutuncu.eshotroid.Times;
import com.mehmetakiftutuncu.eshotroid.database.MyDatabase;
import com.mehmetakiftutuncu.eshotroid.model.Bus;
import com.mehmetakiftutuncu.eshotroid.model.BusTime;
import com.mehmetakiftutuncu.eshotroid.utilities.Connection;
import com.mehmetakiftutuncu.eshotroid.utilities.Parser;
import com.mehmetakiftutuncu.eshotroid.utilities.Processor;

/**
 * An asynchronous task for getting the bus times page
 * 
 * @author Mehmet Akif Tütüncü
 */
public class GetTimesPageTask extends AsyncTask<Void, Void, Void> implements Runnable
{
	private Context myContext;
	private PullToRefreshScrollView myPtrView;
	private Bus myBus;
	private String myType;
	
	private String url;
	private String result;
	private ArrayList<BusTime> busTimes;
	
	/**
	 * Tag for debugging
	 */
	public static final String LOG_TAG = "Eshotroid_GetTimesPageTask";
	
	/**
	 * Constructor for the use of just getting times, not showing them in anywhere yet (This will implicitly pass null to the ptrView parameter)
	 * 
	 * @param context Context of the activity
	 * @param bus Bus for which the times will be got
	 * @param type Type of the times to get
	 */
	public GetTimesPageTask(Context context, Bus bus, String type)
	{
		this(context, null, bus, type);
	}
	
	/**
	 * Constructor for the use in Times activity
	 * 
	 * @param context Context of the activity
	 * @param ptrView PullToRefreshView in the activity in which to show the results (If this is null, then this task will just act as a background task, won't show any result to user yet)
	 * @param bus Bus for which the times will be got
	 * @param type Type of the times to get
	 */
	public GetTimesPageTask(Context context, PullToRefreshScrollView ptrView, Bus bus, String type)
	{
		myContext = context;
		myPtrView = ptrView;
		myBus = bus;
		myType = type;
		
		url = String.format("%s?%s=%s&%s=%s", Constants.BUS_TIMES_URL, Constants.NUMBER_PARAMETER, bus.getNumber(), Constants.TYPE_PARAMETER, type);
		
		if(myPtrView != null)
		{
			((Times) context).toggleProgressBar(true);
		}
	}
	
	@Override
	protected Void doInBackground(Void... params)
	{
		Log.d(LOG_TAG, "Getting times for " + myBus.getNumber() + myType + "...");
		
		result = Connection.getPage(myContext, url);
		
		if(result != null)
		{
			myBus.setRoute(Parser.parseBusRoute(result));
			
			ArrayList<String> parsedBusTimes = Parser.parseBusTimes(result);
			if(parsedBusTimes != null)
			{
				busTimes = Processor.processBusTimes(parsedBusTimes);
			}

			if(busTimes != null)
			{
				Log.d(LOG_TAG, "Saving times for " + myBus.getNumber() + myType + "...");
				
				if(myType.equals("H"))
				{
					myBus.setTimesH(busTimes);
				}
				else if(myType.equals("C"))
				{
					myBus.setTimesC(busTimes);
				}
				else if(myType.equals("P"))
				{
					myBus.setTimesP(busTimes);
				}
				
				MyDatabase db = new MyDatabase(myContext);
				db.openDB();
				db.addOrUpdate(myBus);
				db.closeDB();
			}
		}
		
		if(myPtrView != null)
		{
			new Handler(myContext.getMainLooper()).post(this);
		}
		
		return null;
	}

	@Override
	public void run()
	{
		if(result != null)
		{
			if(busTimes != null)
			{
				AppMsg.makeText((Activity) myContext, myContext.getString(R.string.info_successful), AppMsg.STYLE_INFO).show();
				
				((Times) myContext).updateInformation(myBus);
			}
			else
			{
				// Downloaded but couldn't be parsed
			}
		}
		else
		{
			AppMsg.makeText((Activity) myContext, myContext.getString(R.string.error_noConnection), AppMsg.STYLE_ALERT).show();
		}
		
		((Times) myContext).toggleProgressBar(false);
		
		myPtrView.onRefreshComplete();
	}
}