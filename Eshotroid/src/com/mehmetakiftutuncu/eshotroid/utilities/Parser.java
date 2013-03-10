package com.mehmetakiftutuncu.eshotroid.utilities;

import java.util.ArrayList;

import com.mehmetakiftutuncu.eshotroid.Constants;
import com.mehmetakiftutuncu.eshotroid.model.BusLine;


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
}