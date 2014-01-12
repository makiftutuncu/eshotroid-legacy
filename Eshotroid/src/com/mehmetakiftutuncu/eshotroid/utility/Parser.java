package com.mehmetakiftutuncu.eshotroid.utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.util.Log;

import com.google.gson.Gson;
import com.mehmetakiftutuncu.eshotroid.BuildConfig;
import com.mehmetakiftutuncu.eshotroid.model.BusTime;
import com.mehmetakiftutuncu.eshotroid.model.KentKartBalanceQueryResult;
import com.mehmetakiftutuncu.eshotroid.model.KentKartQueryResponse;

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
	
	// Format pattern of Kent Kart use time format in response
	public static final String KENT_KART_TIME_FORMAT_RESPONSE = "yyyyMMddHHmmss";
	
	// Format pattern of Kent Kart use time format in result
	public static final String KENT_KART_TIME_FORMAT_RESULT = "dd MMMM yyyy, HH:mm:ss";
	
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
	 * @param json Source of the page
	 * 
	 * @return A {@link KentKartBalanceQueryResult} object containing balance
	 * information, or null if any error occurs */
    public static KentKartBalanceQueryResult parseKentKartBalance(String json)
    {
    	if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Parsing Kent Kart balance...");
    	
    	try
        {
    		// Parse resulting json
    		Gson gson = new Gson();
    		KentKartQueryResponse result = gson.fromJson(json, KentKartQueryResponse.class);
    		
    		// Format the parsed result correctly
    		SimpleDateFormat responseFormatter = new SimpleDateFormat(KENT_KART_TIME_FORMAT_RESPONSE, Locale.getDefault());
    		SimpleDateFormat resultFormatter = new SimpleDateFormat(KENT_KART_TIME_FORMAT_RESULT, Locale.getDefault());
    		
    		// Define result attributes
    		String balance = result.balanceresult.equals("0") || result.balanceresult.equals("") ? null : result.balanceresult;
    		String lastLoadTime = null;
			try
			{
				lastLoadTime = resultFormatter.format(responseFormatter.parse(result.chargeresult));
			}
			catch(Exception e)
			{
				Log.e(LOG_TAG, "Error occurred while parsing last load time!", e);
			}
    		String lastLoadAmount = result.chargeAmt.equals("0") || result.chargeAmt.equals("") ? null : result.chargeAmt;
    		String lastUseTime = null;
			try
			{
				lastUseTime = resultFormatter.format(responseFormatter.parse(result.usageresult));
			}
			catch(Exception e)
			{
				Log.e(LOG_TAG, "Error occurred while parsing last use time!", e);
			}
    		String lastUseAmount = result.usageAmt.equals("0") || result.usageAmt.equals("") ? null : result.usageAmt;
            
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