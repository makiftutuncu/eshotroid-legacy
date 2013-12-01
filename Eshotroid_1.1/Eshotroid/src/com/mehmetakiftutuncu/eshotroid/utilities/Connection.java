package com.mehmetakiftutuncu.eshotroid.utilities;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

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
		
		HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(pageUrl);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        
        try
        {
        	// Get the response
			result = client.execute(request, responseHandler);
        }
		catch(Exception e)
		{
			Log.e(LOG_TAG, "Couldn't get the page!", e);
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