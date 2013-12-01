package com.mehmetakiftutuncu.eshotroid.utility;

import java.util.ArrayList;

import android.util.Log;

import com.mehmetakiftutuncu.eshotroid.BuildConfig;
import com.mehmetakiftutuncu.eshotroid.model.BusTime;
import com.mehmetakiftutuncu.eshotroid.model.KentKartBalanceQueryResult;

/** A utility class for parsing the pages
 * 
 * @author mehmetakiftutuncu */
public class Parser
{
	// Tags for a busses in Ulasim.aspx
	public static final String BUS_OPEN_START = "<option value=";
	public static final String BUS_OPEN_END = "\">";
	public static final String BUS_CLOSE = "</option>";
	
	// Tags for a bus route in Saatler.aspx
	public static final String BUSROUTE_OPEN_START = "<span ";
	public static final String BUSROUTE_OPEN_END = "Guzergah\">";
	public static final String BUSROUTE_CLOSE = "</span>";
	
	// Tags containing a bus time and wheel chair enabled status
	public static final String BUSTIME_CONTAINER_OPEN = "<div style=\"";
	public static final String BUSTIME_CONTAINER_CLOSE = "</div>";
	
	// Tags for a bus time in Saatler.aspx
	public static final String BUSTIME_OPEN_START = "<span ";
	public static final String BUSTIME_OPEN_END = "\">";
	public static final String BUSTIME_CLOSE = "</span>";
	
	// Indicates if the bus on that time is wheel chair enabled
	public static final String BUSTIME_WHEEL_CHAIR_ENABLED = "images/sandalye.png";
	
	// Tag that encloses bus times
	public static final String BUSTIME_TABLE = "<table cellspacing=";
	// This causes problems if there is no time for the selected bus with selected day (736P is an example)
	// public static final String BUSTIME_TABLE_TAG = "<table ";
	
	// Start expression of Kent Kart balance
	public static final String KENT_KART_BALANCE_START = "Bakiyeniz ";
	// End expression of Kent Kart balance
	public static final String KENT_KART_BALANCE_END = " TL";
	
	// Start expression of Kent Kart last load time
	public static final String KENT_KART_LOAD_TIME_START = "</div>";
	// End expression of Kent Kart last load time
	public static final String KENT_KART_LOAD_TIME_END = " tarihli";
	
	// Start expression of Kent Kart last load amount
	public static final String KENT_KART_LOAD_AMOUNT_START = "<b>";
	// End expression of Kent Kart last load amount
	public static final String KENT_KART_LOAD_AMOUNT_END = "</b>";
	
	// Start expression of Kent Kart last use time
	public static final String KENT_KART_USE_TIME_START = "<br />";
	// End expression of Kent Kart last use time
	public static final String KENT_KART_USE_TIME_END = " tarihli";
	
	// Start expression of Kent Kart last use amount
	public static final String KENT_KART_USE_AMOUNT_START = "<b>";
	// End expression of Kent Kart last use amount
	public static final String KENT_KART_USE_AMOUNT_END = "</b>";
	
	/** Tag for debugging */
	public static final String LOG_TAG = "Eshotroid_Parser";
	
	/** Gets the route of the bus from the times page of that bus
	 * 
	 * @param page Source of the page
	 * 
	 * @return Route of the bus, null if any error occurs */
	public static String parseBusRoute(String page)
	{
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Parsing bus route...");
		
		String result = null;
		
		// Start looking for bus route
        int start = 0, end = 0;
        
		// Get the bus route information start point
        start = page.indexOf(BUSROUTE_OPEN_START);
        if(start != -1)
        {
        	// Get where the start point ends
        	start = page.indexOf(BUSROUTE_OPEN_END, start);
        	if(start != -1)
        	{
        		// This is the actual start point of the bus route information
        		start += BUSROUTE_OPEN_END.length();
        		
        		// Now look for where the bus route information ends
        		end = page.indexOf(BUSROUTE_CLOSE, start);
        		if(end != -1)
        		{
        			// Now we extract the bus route information using the found start and end points
        			result = page.substring(start, end);
        		}
        	}
        }
        
		return result;
	}
	
	/** Gets the list of the bus times from the specified page
	 * 
	 * @param page Source of the page
	 * 
	 * @return List of the bus times, null if any error occurs */
	public static ArrayList<String> parseBusTimes(String page)
    {
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Parsing bus times...");
		
		try
        {
			// Resulting list
			ArrayList<String> list = new ArrayList<String>();
			
        	// First find where the actual bus times start
        	int beginning = page.lastIndexOf(BUSTIME_TABLE);
        	if(beginning != -1)
        	{
        		page = page.substring(beginning);
        	}
        	else
        	{
        		Log.e(LOG_TAG, "No times were found in the page!");
                
                return null;
        	}
        	
        	// Start looking for bus times
            int start = 0, end = 0, last = 0;
            do
            {
            	// Get the next bus time container start point starting from the last known position
                start = page.indexOf(BUSTIME_CONTAINER_OPEN, last);
                if(start != -1)
                {
                	// This is the actual start point of the bus time container
            		start += BUSTIME_CONTAINER_OPEN.length();
            		
            		// Now look for where the bus time container ends
            		end = page.indexOf(BUSTIME_CONTAINER_CLOSE, start);
            		if(end != -1)
            		{
            			// Move the end point of the bus time container forward
            			end += BUSTIME_CONTAINER_CLOSE.length();
            			
            			// Get actual bus time information
            			String currentBus = page.substring(start, end);
            			int busStart = 0, busEnd = 0;
            			
            			// Check wheel chair enabled state
            			boolean isWheelChairEnabled = currentBus.contains(BUSTIME_WHEEL_CHAIR_ENABLED);
            			
            			// Get the next bus time information start point
            			busStart = currentBus.indexOf(BUSTIME_OPEN_START);
                        if(busStart != -1)
                        {
                        	// Get where the start point ends
                        	busStart = currentBus.indexOf(BUSTIME_OPEN_END, busStart);
                        	if(busStart != -1)
                        	{
                        		// This is the actual start point of the bus time information
                        		busStart += BUSTIME_OPEN_END.length();
                        		
                        		// Now look for where the bus time information ends
                        		busEnd = currentBus.indexOf(BUSTIME_CLOSE, busStart);
                        		if(busEnd != -1)
                        		{
                        			// Now we extract the bus time information using the found start and end points
                        			String item = currentBus.substring(busStart, busEnd);
                        			
                        			// Add wheel chair enabled status
                        			item += BusTime.BUS_TIME_SEPARATOR + isWheelChairEnabled;
                        			
                        			// Finally add the found and extracted bus time information to the list
                        			list.add(item);
                        		}
                        	}

                        	// Mark the last known position and continue
                            last = end;
                        }
            		}

                	// Mark the last known position and continue
                    last = end;
                }
            } while(start != -1);
            
            return list;
        }
        catch(Exception e)
        {
            Log.e(LOG_TAG, "Error occured while parsing bus times!", e);
            
            return null;
        }
    }

	/** Gets the list of the busses from the specified page
	 * 
	 * @param page Source of the page
	 * 
	 * @return List of the busses, null if any error occurs */
    public static ArrayList<String> parseBusses(String page)
    {
    	if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Parsing busses...");
    	
    	try
        {
    		// Resulting list
        	ArrayList<String> list = new ArrayList<String>();
        	
        	// Start looking for busses
            int start = 0, end = 0, last = 0;
            do
            {
            	// Get the next bus information start point starting from the last known position
                start = page.indexOf(BUS_OPEN_START, last);
                if(start != -1)
                {
                	// Get where the start point ends
                	start = page.indexOf(BUS_OPEN_END, start);
                	if(start != -1)
                	{
                		// This is the actual start point of the bus information
                		start += BUS_OPEN_END.length();
                		
                		// Now look for where the bus information ends
                		end = page.indexOf(BUS_CLOSE, start);
                		if(end != -1)
                		{
                			// Now we extract the bus information using the found start and end points
                			// Plus we fix the Turkish characters in the result
                			String item = fixTurkishHtmlEntityCharacters(page.substring(start, end));
                			
                			// Finally add the found and extracted bus information to the list
                			list.add(item);
                			
                			// Move the end point of the bus information forward
                			end += BUS_CLOSE.length();
                		}
                	}

                	// Mark the last known position and continue
                    last = end;
                }
            } while(start != -1);
            
            return list;
        }
        catch(Exception e)
        {
        	Log.e(LOG_TAG, "Error occured while parsing busses!", e);
        	
        	return null;
        }
    }
    
    /** Gets the Kent Kart balance information from the specified page
	 * 
	 * @param page Source of the page
	 * 
	 * @return A {@link KentKartBalanceQueryResult} object containing balance
	 * information, or null if any error occurs */
    public static KentKartBalanceQueryResult parseKentKartBalance(String page)
    {
    	if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Parsing Kent Kart balance...");
    	
    	try
        {
    		// Define result attributes
    		String balance = null;
    		String lastLoadTime = null;
    		String lastLoadAmount = null;
    		String lastUseTime = null;
    		String lastUseAmount = null;
    		
    		// Define index variables
            int start = 0, end = 0, last = 0;
            
            // Get balance
            start = page.indexOf(KENT_KART_BALANCE_START);
            if(start != -1)
            {
            	start += KENT_KART_BALANCE_START.length();
            	end = page.indexOf(KENT_KART_BALANCE_END, start);
            	
            	balance = page.substring(start, end);
            	
            	last = end + KENT_KART_BALANCE_END.length();
            	start = 0;
            	end = 0;
            }
            
            // Get last load time
            start = page.indexOf(KENT_KART_LOAD_TIME_START, last);
            if(start != -1)
            {
            	start += KENT_KART_LOAD_TIME_START.length();
            	end = page.indexOf(KENT_KART_LOAD_TIME_END, start);
            	
            	lastLoadTime = page.substring(start, end);
            	
            	last = end + KENT_KART_LOAD_TIME_END.length();
            	start = 0;
            	end = 0;
            }
            
            // Get last load amount
            start = page.indexOf(KENT_KART_LOAD_AMOUNT_START, last);
            if(start != -1)
            {
            	start += KENT_KART_LOAD_AMOUNT_START.length();
            	end = page.indexOf(KENT_KART_LOAD_AMOUNT_END, start);
            	
            	lastLoadAmount = page.substring(start, end);
            	
            	last = end + KENT_KART_LOAD_AMOUNT_END.length();
            	start = 0;
            	end = 0;
            }
            
            // Get last use time
            start = page.indexOf(KENT_KART_USE_TIME_START, last);
            if(start != -1)
            {
            	start += KENT_KART_USE_TIME_START.length();
            	end = page.indexOf(KENT_KART_USE_TIME_END, start);
            	
            	lastUseTime = page.substring(start, end);
            	
            	last = end + KENT_KART_USE_TIME_END.length();
            	start = 0;
            	end = 0;
            }
            
            // Get last use amount
            start = page.indexOf(KENT_KART_USE_AMOUNT_START, last);
            if(start != -1)
            {
            	start += KENT_KART_USE_AMOUNT_START.length();
            	end = page.indexOf(KENT_KART_USE_AMOUNT_END, start);
            	
            	lastUseAmount = page.substring(start, end);
            }
            
            // Generate and return resulting object
            return new KentKartBalanceQueryResult(balance, lastLoadTime,
            		lastLoadAmount, lastUseTime, lastUseAmount);
        }
        catch(Exception e)
        {
        	Log.e(LOG_TAG, "Error occured while parsing Kent Kart balance!", e);
        	
        	return null;
        }
    }
    
    /** Extracts the information in a string between the specified tags
	 * 
	 * @param source Original string
	 * @param openStart Beginning of the open tag
	 * @param openEnd Ending of the open tag
	 * @param close Close tag
	 * 
	 * @return Information between the specified tags */
    /*
	private static String extractFromTags(String source, String openStart, String openEnd, String close)
	{
		String result = source;
		
		int start = 0, end = 0;

        start = source.indexOf(openStart);
        if(start != -1)
        {
            start = source.indexOf(openEnd, start);
            if(start != -1)
            {
            	start += openEnd.length();
            	
            	end = source.indexOf(close, start);

           		if(end != -1)
           		{
           			result = fixTurkishHtmlEntityCharacters(source.substring(start, end));
           		}
            }
        }
        
        return result;
	}
	*/
    
    /** Replaces all HTML entity characters in Turkish
	 * 
	 * @param source Source string
	 * 
	 * @return Source string with fixed Turkish characters */
	private static String fixTurkishHtmlEntityCharacters(String source)
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