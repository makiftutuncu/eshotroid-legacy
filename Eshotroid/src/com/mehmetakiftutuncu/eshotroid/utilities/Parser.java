package com.mehmetakiftutuncu.eshotroid.utilities;

import java.util.ArrayList;

import com.mehmetakiftutuncu.eshotroid.Constants;
import com.mehmetakiftutuncu.eshotroid.model.BusLine;
import com.mehmetakiftutuncu.eshotroid.model.BusLineTimes;
import com.mehmetakiftutuncu.eshotroid.model.BusTime;


/**
 * A utility class for parsing the pages
 * 
 * @author Mehmet Akif Tütüncü
 */
public class Parser
{
	/**
	 * Tag for debugging
	 */
	public static final String LOG_TAG = "Eshotroid_Parser";
	
	/**
	 * Gets the list of bus lines from web page
	 * 
	 * @param page Contents of web page
	 * 
	 * @return List of bus lines
	 */
	public static ArrayList<BusLine> getBusLines(String page)
	{
		ArrayList<BusLine> lines = new ArrayList<BusLine>();
		
		int lastStart, lastEnd;
		
		while(true)
		{
			lastStart = page.indexOf(Constants.BUS_LINE_OPEN_TAG);
			if(lastStart != -1)
			{
				lastEnd = page.indexOf(Constants.BUS_LINE_CLOSE_TAG);
				if(lastEnd != -1)
				{
					String line = page.substring(lastStart, lastEnd);
					int offset = line.indexOf(Constants.BUS_LINE_OPEN_TAG_OFFSET) + 1;
					line = line.substring(offset);
					
					String[] temp = line.split(Constants.BUS_LINE_SEPERATOR);
					
					temp[0] = fixTurkishHtmlEntityCharacters(temp[0]);
					temp[1] = fixTurkishHtmlEntityCharacters(temp[1]);
					
					BusLine busLine = new BusLine(temp[0], temp[1]);
					
					lines.add(busLine);
					
					page = page.substring(lastEnd + Constants.BUS_LINE_CLOSE_TAG.length());
					
					lastStart = lastEnd = 0;
				}
				else
				{
					break;
				}
			}
			else
			{
				break;
			}
		}
		
		return lines;
	}
	
	/**
	 * Gets the times of a bus from its web page
	 * 
	 * @param page Contents of web page
	 * 
	 * @return Times of a bus line
	 */
	public static BusLineTimes getBusLineTimes(BusLine line, String page)
	{
		ArrayList<BusTime> times = new ArrayList<BusTime>();
		
		
		
		return new BusLineTimes(line, times);
	}
	
	/**
	 * Replaces all HTML entity characters in Turkish
	 * 
	 * @param source Source string
	 * 
	 * @return Source string with fixed Turkish characters
	 */
	public static String fixTurkishHtmlEntityCharacters(String source)
	{
		String result = source;
		
		result = result.replaceAll("&#304;", "İ");
		result = result.replaceAll("&#305;", "ı");
		result = result.replaceAll("&#214;", "Ö");
		result = result.replaceAll("&#246;", "ö");
		result = result.replaceAll("&#220;", "Ü");
		result = result.replaceAll("&#252;", "ü");
		result = result.replaceAll("&#199;", "Ç");
		result = result.replaceAll("&#231;", "ç");
		result = result.replaceAll("&#286;", "Ğ");
		result = result.replaceAll("&#287;", "ğ");
		result = result.replaceAll("&#350;", "Ş");
		result = result.replaceAll("&#351;", "ş");
		
		return result;
	}
}