package com.mehmetakiftutuncu.eshotroid.utility;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;

import com.devspark.appmsg.AppMsg;
import com.mehmetakiftutuncu.eshotroid.BuildConfig;

/** A utility class for showing messages to the user
 * 
 * @author mehmetakiftutuncu */
public class Messages
{
	/** Tag for debugging */
	public static final String LOG_TAG = "Eshotroid_Messages";
	
	/**	An instance of this class */
	private static Messages myInstance;
	
	/** Private constructor for singleton */
	private Messages() {}
	
	/** Creates and/or gets an instance to show messages
	 * 
	 * @return An instance of this class */
	public static Messages getInstance()
	{
		if(myInstance == null)
		{
			if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Creating a new Messages instance...");
			myInstance = new Messages();
		}
		
		return myInstance;
	}
	
	/** Shows given message with a positive style
	 * 
	 * @param activity Activity on which the message will be shown
	 * @param message Message to show */
	public void showPositive(Activity activity, String message)
	{
		show(activity, message, AppMsg.STYLE_POSITIVE);
	}
	
	/** Shows given message with a neutral style
	 * 
	 * @param activity Activity on which the message will be shown
	 * @param message Message to show */
	public void showNeutral(Activity activity, String message)
	{
		show(activity, message, AppMsg.STYLE_NEUTRAL);
	}
	
	/** Shows given message with a negative style
	 * 
	 * @param activity Activity on which the message will be shown
	 * @param message Message to show */
	public void showNegative(Activity activity, String message)
	{
		show(activity, message, AppMsg.STYLE_NEGATIVE);
	}
	
	/** Shows given message with given style
	 * 
	 * @param activity Activity on which the message will be shown
	 * @param message Message to show
	 * @param style Style of the message */
	private void show(final Activity activity, final String message, final AppMsg.Style style)
	{
		new Handler(Looper.getMainLooper()).post(new Runnable()
		{
			@Override
			public void run()
			{
				AppMsg appMsg = AppMsg.makeText(activity, message, style);
				appMsg.setLayoutGravity(Gravity.CENTER);
				appMsg.show();
			}
		});
	}
}