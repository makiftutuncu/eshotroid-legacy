package com.mehmetakiftutuncu.eshotroid;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mehmetakiftutuncu.eshotroid.model.BusLine;
import com.mehmetakiftutuncu.eshotroid.model.BusTime;
import com.mehmetakiftutuncu.eshotroid.utilities.GetPageTask;
import com.mehmetakiftutuncu.eshotroid.utilities.Parser;
import com.mehmetakiftutuncu.eshotroid.utilities.Processor;
import com.mehmetakiftutuncu.eshotroid.utilities.StorageManager;

/**
 * Times activity of the application
 * 
 * @author Mehmet Akif Tütüncü
 */
public class Times extends SherlockActivity
{
	private TableLayout tlTable;
	private TextView tvSource;
	private TextView tvDestination;
	
	private String type;
	private BusLine line;
	
	private ArrayList<BusTime> busTimes;
	
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
			type = bundle.getString(Constants.TYPE_EXTRA);
			line = new Gson().fromJson(bundle.getString(Constants.BUS_LINE_EXTRA), new TypeToken<BusLine>(){}.getType());
		}
		
		initialize();
		
		loadBusLines();
		
		update();
	}
	
	/**	Loads bus times from external storage if possible, if not tries to download them. */
	private void loadBusLines()
	{
		busTimes = StorageManager.readBusTimes(line.getNumber() + type);
		if(busTimes != null)
		{
			Log.d(LOG_TAG, "Bus times already exist on the external storage.");
		}
		else
		{
			Log.d(LOG_TAG, "Bus times don't exist on the external storage. Trying to download...");
			
			GetPageTask task = new GetPageTask(this);
			task.execute(String.format("%s?%s=%s&%s=%s", Constants.BUS_TIMES_URL, Constants.LINE_PARAMETER, line.getNumber(), Constants.TYPE_PARAMETER, type));
			
			try
			{
				String busTimesPage = task.get();
				
				if(busTimesPage != null)
				{
					ArrayList<String> parsedBusTimes = Parser.parseBusTimes(busTimesPage);
					busTimes = Processor.processBusTimes(parsedBusTimes);
					
					StorageManager.storeBusTimes(line.getNumber() + type, busTimes);
				}
			}
			catch(Exception e)
			{
				Log.e(LOG_TAG, "Couldn't download bus times!", e);
			}
		}
	}
	
	/**	Initializes components */
	private void initialize()
	{
		tlTable = (TableLayout) findViewById(R.id.tableLayout_times);
		tvSource = (TextView) findViewById(R.id.textView_times_source);
		tvDestination = (TextView) findViewById(R.id.textView_times_destination);
	}
	
	/**	Updates the table with bus times */
	private void update()
	{
		setTitle(line.toString());
		
		tvSource.setText(line.getSource());
		tvDestination.setText(line.getDestination());
		
		LayoutInflater inflator = getLayoutInflater();
		
		for(BusTime i : busTimes)
		{
			View row = inflator.inflate(R.layout.times_row, null);

			TextView source = (TextView) row.findViewById(R.id.textView_times_row_source);
			TextView destination = (TextView) row.findViewById(R.id.textView_times_row_destination);
			
			source.setText(i.getTimeFromSource());
			destination.setText(i.getTimeFromDestination());
			
			tlTable.addView(row);
		}
	}
}