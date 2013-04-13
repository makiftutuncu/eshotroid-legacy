package com.mehmetakiftutuncu.eshotroid;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.mehmetakiftutuncu.eshotroid.model.BusLine;
import com.mehmetakiftutuncu.eshotroid.model.BusTime;
import com.mehmetakiftutuncu.eshotroid.utilities.GetPageTask;
import com.mehmetakiftutuncu.eshotroid.utilities.Parser;
import com.mehmetakiftutuncu.eshotroid.utilities.Processor;

/**
 * Main activity of the application
 * 
 * @author Mehmet Akif Tütüncü
 */
public class Main extends Activity
{
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
		
		Log.i(LOG_TAG, "Getting all bus lines...");
		GetPageTask task = new GetPageTask(this);
		task.execute(Constants.BUS_LINES_URL);
		
		try
		{
			String busLinesPage = task.get();
			
			if(busLinesPage != null)
			{
				ArrayList<String> busLines = Parser.parseBusLines(busLinesPage);
				
				Log.i(LOG_TAG, "These are what we get as bus lines from the page:");
				for(String i : busLines)
				{
					Log.i(LOG_TAG, i);
				}
				
				Log.i(LOG_TAG, "Let\'s process these motherfuckers.");
				ArrayList<BusLine> busLineObjects = Processor.processBusLines(busLines);
				
				Log.i(LOG_TAG, "Cool! Now let\'s get the times for a random bus.");
				BusLine randomBus = busLineObjects.get(new Random().nextInt(busLineObjects.size()));
				String url = String.format("%s?%s=%s&%s=%s", 	Constants.BUS_TIMES_URL,
																Constants.TYPE_PARAMETER,
																"H",
																Constants.LINE_PARAMETER,
																randomBus.getNumber());
				
				Log.i(LOG_TAG, "Getting bus times for " + randomBus.getNumber() + "...");
				task = new GetPageTask(this);
				task.execute(url);
				
				try
				{
					String busTimesPage = task.get();
					
					if(busTimesPage != null)
					{
						ArrayList<String> busTimes = Parser.parseBusTimes(busTimesPage);
						
						Log.i(LOG_TAG, "These are what we get as bus times from the page:");
						for(String i : busTimes)
						{
							Log.i(LOG_TAG, i);
						}
						
						Log.i(LOG_TAG, "Let\'s process these motherfuckers as well.");
						ArrayList<BusTime> busTimeObjects = Processor.processBusTimes(busTimes);
						
						Log.i(LOG_TAG, "Cool! Cool, cool, cool! Now let\'s see the final result.");
						
						Log.i(LOG_TAG, "Bus: " + randomBus.getNumber());
						Log.i(LOG_TAG, randomBus.getSource() + " / " + randomBus.getDestination());
						for(BusTime i : busTimeObjects)
						{
							Log.i(LOG_TAG, i.getTimeFromSource() + " / " + i.getTimeFromDestination());
						}
						
						Log.i(LOG_TAG, "Voila!");
					}
				}
				catch(Exception e)
				{
					Log.e(LOG_TAG, "Couldn't load bus times!", e);
				}
			}
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, "Couldn't load bus lines!", e);
		}
	}
	
	private void initialize()
	{
	}
}
