package com.mehmetakiftutuncu.eshotroid.utilities;

import java.util.ArrayList;

import com.mehmetakiftutuncu.eshotroid.model.BusLine;
import com.mehmetakiftutuncu.eshotroid.model.BusTime;

/**
 * A utility class for processing the parsed information
 * 
 * @author Mehmet Akif Tütüncü
 */
public class Processor
{
	// Separators used in processing
	public static final String BUS_LINE_NUMBER_SEPARATOR = ":";
	public static final String BUS_LINE_PLACE_SEPARATOR = "\\-";
	
	/**
	 * Tag for debugging
	 */
	public static final String LOG_TAG = "Eshotroid_Processor";
	
	/**
	 * Processes the list of parsed bus lines and generates a list of {@link BusLine} objects
	 * 
	 * @param busLines List of parsed bus lines
	 * 
	 * @return List of {@link BusLine} objects
	 */
	public static ArrayList<BusLine> processBusLines(ArrayList<String> busLines)
	{
		// Resulting list
		ArrayList<BusLine> list = new ArrayList<BusLine>();
		
		// For each String (parsed information) "i" in busLines (the parsed list)
		for(String i : busLines)
		{
			// Separate number and the places
			String[] temp = i.split(BUS_LINE_NUMBER_SEPARATOR);
			
			// Get the number
			String number = temp[0].trim();
			
			// Separate the places as source and destination
			temp = temp[1].trim().split(BUS_LINE_PLACE_SEPARATOR);
			
			// Get source and destination
			String source = temp[0].trim();
			String destination = temp[1].trim();
			
			// Generate the object
			BusLine busLine = new BusLine(number, source, destination, "");
			
			// Add the object to the list
			list.add(busLine);
		}
		
		return list;
	}
	
	/**
	 * Processes the list of parsed bus times and generates a list of {@link BusTime} objects
	 * 
	 * @param busTimes List of parsed bus times
	 * 
	 * @return List of {@link BusTime} objects
	 */
	public static ArrayList<BusTime> processBusTimes(ArrayList<String> busTimes)
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
}