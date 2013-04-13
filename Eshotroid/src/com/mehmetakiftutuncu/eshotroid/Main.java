package com.mehmetakiftutuncu.eshotroid;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.mehmetakiftutuncu.eshotroid.model.BusLine;
import com.mehmetakiftutuncu.eshotroid.utilities.GetPageTask;
import com.mehmetakiftutuncu.eshotroid.utilities.Parser;
import com.mehmetakiftutuncu.eshotroid.utilities.Processor;
import com.mehmetakiftutuncu.eshotroid.utilities.StorageManager;

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
		
		Constants.PACKAGE_NAME = getPackageName();
		
		initialize();
		
		loadBusLines();
	}
	
	/**	Initializes components */
	private void initialize()
	{
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
					
					// Now that we downloaded bus lines, let's store them
					StorageManager.storeBusLines(busLines);
				}
			}
			catch(Exception e)
			{
				Log.e(LOG_TAG, "Couldn't download bus times!", e);
			}
		}
		
		// TODO: Set the list or whatever
	}
}