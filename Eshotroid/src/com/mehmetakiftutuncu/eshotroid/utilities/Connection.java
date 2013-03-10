package com.mehmetakiftutuncu.eshotroid.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * A utility class for network connections
 * 
 * @author Mehmet Akif Tütüncü
 */
public class Connection
{
	/**
	 * Tag for debugging
	 */
	public static final String LOG_TAG = "Eshotroid_Connection";
	
	/**
	 * Gets the contents of a page
	 * 
	 * @param context {@link Context} for checking the network state
	 * @param pageUrl Url of the page to get
	 * 
	 * @return A {@link String} containing all the content of the page, null if any error occurs
	 */
	public static String getPage(Context context, String pageUrl)
	{
		if(!Connection.isNetworkAvailable(context))
		{
			Log.e(LOG_TAG, "No network connection!");
			
			return null;
		}
		
		String result = null;
		
		try
		{
			URL url = new URL(pageUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			result = readStream(connection.getInputStream());
			
			if(result == null)
			{
				Log.e(LOG_TAG, "Data read from stream was null!");
				
				return null;
			}
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, "Couldn't get the page!", e);
		}
		
		return result;
	}
	
	/**
	 * Reads all the data from an {@link InputStream}
	 * 
	 * @param inputStream {@link InputStream} object to read from
	 * 
	 * @return A {@link String} containing all the data, null if any error occurs
	 */
	private static String readStream(InputStream inputStream)
	{
		BufferedReader bufferedReader = null;
		String result = "";
		
		try
		{
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			
			String line = "";
			while ((line = bufferedReader.readLine()) != null)
			{
				result += line;
		    }
		}
		catch(IOException e)
		{
			Log.e(LOG_TAG, "Couldn't read the stream!", e);
			
			return null;
		}
		finally
		{
			if (bufferedReader != null)
			{
				try
				{
					bufferedReader.close();
				}
				catch(IOException e)
				{
					Log.e(LOG_TAG, "Couldn't close the buffered reader!", e);
					
					return null;
		        }
		    }
		}
		
		return result;
	} 
	
	/**
	 * Checks the network availability
	 * 
	 * @param context Context to access system services
	 * 
	 * @return true if there is a network connection, false otherwise
	 */
	public static boolean isNetworkAvailable(Context context)
	{
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
	    
	    if(networkInfo != null && networkInfo.isConnected())
	    {
	        return true;
	    }
	    return false;
	} 
}