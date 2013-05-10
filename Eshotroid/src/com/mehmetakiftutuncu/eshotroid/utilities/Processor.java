package com.mehmetakiftutuncu.eshotroid.utilities;

import java.util.ArrayList;

import android.util.Log;

import com.mehmetakiftutuncu.eshotroid.model.Bus;
import com.mehmetakiftutuncu.eshotroid.model.BusTime;

/**
 * A utility class for processing the parsed information
 * 
 * @author Mehmet Akif Tütüncü
 */
public class Processor
{
	// Separators used in processing
	public static final String BUS_NUMBER_SEPARATOR = ":";
	public static final String BUS_PLACE_SEPARATOR = "\\-";
	
	/**
	 * Tag for debugging
	 */
	public static final String LOG_TAG = "Eshotroid_Processor";
	
	/**
	 * Processes the list of parsed busses and generates a list of {@link Bus} objects
	 * 
	 * @param busses List of parsed busses
	 * 
	 * @return List of {@link Bus} objects, null if any error occurs
	 */
	public static ArrayList<Bus> processBusses(ArrayList<String> busses)
	{
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
				Bus bus = new Bus(number, source, destination, "", false, null, null, null);
				
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
	
	/**
	 * Processes the list of parsed bus times and generates a list of {@link BusTime} objects
	 * 
	 * @param busTimes List of parsed bus times
	 * 
	 * @return List of {@link BusTime} objects, null if any error occurs
	 */
	public static ArrayList<BusTime> processBusTimes(ArrayList<String> busTimes)
	{
		try
		{
			// Resulting list
			ArrayList<BusTime> list = new ArrayList<BusTime>();
			
			// For every 2 items
			for(int i = 0; i < busTimes.size(); i += 2)
			{
				// Get first as source time
				String source = busTimes.get(i);
				// Get second as destination time
				String destination = busTimes.get(i + 1);
				
				// If a time is not available, replace it with empty String
				if(source.equalsIgnoreCase("&nbsp;"))
				{
					source = "";
				}
				if(destination.equalsIgnoreCase("&nbsp;"))
				{
					destination = "";
				}
				
				// Generate a new object and add the object to the list
				list.add(new BusTime(source, destination));
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