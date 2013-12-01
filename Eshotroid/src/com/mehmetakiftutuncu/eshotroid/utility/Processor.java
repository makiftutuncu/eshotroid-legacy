package com.mehmetakiftutuncu.eshotroid.utility;

import java.util.ArrayList;

import android.util.Log;

import com.mehmetakiftutuncu.eshotroid.BuildConfig;
import com.mehmetakiftutuncu.eshotroid.model.Bus;
import com.mehmetakiftutuncu.eshotroid.model.BusTime;

/** A utility class for processing the parsed information
 * 
 * @author mehmetakiftutuncu */
public class Processor
{
	// Separators used in processing
	public static final String BUS_NUMBER_SEPARATOR = ":";
	public static final String BUS_PLACE_SEPARATOR = "\\-";
	
	/** Tag for debugging */
	public static final String LOG_TAG = "Eshotroid_Processor";
	
	/** Processes the list of parsed busses and generates a list of {@link Bus}
	 * objects
	 * 
	 * @param busses List of parsed busses
	 * 
	 * @return List of {@link Bus} objects, null if any error occurs */
	public static ArrayList<Bus> processListOfBusses(ArrayList<String> busses)
	{
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Processing list of busses...");
		
		try
		{
			// Resulting list
			ArrayList<Bus> list = new ArrayList<Bus>();
			
			// For each String (parsed information) "i" in busses (the parsed list)
			for(String i : busses)
			{
				// Separate number and the places
				String[] temp = i.split(BUS_NUMBER_SEPARATOR);
				
				// Get the number
				int number = Integer.parseInt(temp[0].trim());
				
				// Separate the places as source and destination
				temp = temp[1].trim().split(BUS_PLACE_SEPARATOR);
				
				// Get source and destination
				String source = temp[0].trim();
				String destination = temp[1].trim();
				
				// Generate the object
				Bus bus = new Bus(number, source, destination, "", false, null, null, null, true, true, true);
				
				// Add the object to the list
				list.add(bus);
			}
			
			return list;
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, "Error occured while processing busses!", e);
			
			return null;
		}
	}
	
	/** Processes the list of parsed bus times and generates a list of
	 * {@link BusTime} objects
	 * 
	 * @param busTimes List of parsed bus times
	 * 
	 * @return List of {@link BusTime} objects, null if any error occurs
	 */
	public static ArrayList<BusTime> processBusTimes(ArrayList<String> busTimes)
	{
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Processing bus times...");
		
		try
		{
			// Resulting list
			ArrayList<BusTime> list = new ArrayList<BusTime>();
			
			// For every 2 items
			for(int i = 0; i < busTimes.size(); i += 2)
			{
				// Get first as source time
				String sourceText = busTimes.get(i);
				String[] sourceTokens = sourceText.split(BusTime.BUS_TIME_SEPARATOR);
				String source = sourceTokens[0];
				boolean isWheelChairEnabledSource = sourceTokens[1].equals("true");
				
				// Get second as destination time
				String destinationText = busTimes.get(i + 1);
				String[] destinationTokens = destinationText.split(BusTime.BUS_TIME_SEPARATOR);
				String destination = destinationTokens[0];
				boolean isWheelChairEnabledDestination = destinationTokens[1].equals("true");
				
				// If a time is not available, replace it with empty String
				if(source.startsWith("&nbsp;"))
				{
					source = "";
					
					isWheelChairEnabledSource = false;
				}
				if(destination.startsWith("&nbsp;"))
				{
					destination = "";
					
					isWheelChairEnabledDestination = false;
				}
				
				// Generate a new object and add the object to the list
				list.add(new BusTime(source, destination,
						isWheelChairEnabledSource,
						isWheelChairEnabledDestination));
			}
			
			return list;
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, "Error occured while processing bus times!", e);
			
			return null;
		}
	}
}