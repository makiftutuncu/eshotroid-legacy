package com.mehmetakiftutuncu.eshotroid.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.preference.PreferenceManager;
import android.text.Html;

import com.mehmetakiftutuncu.eshotroid.Constants;
import com.mehmetakiftutuncu.eshotroid.R;

/**
 * A utility class for dialogs
 * 
 * @author Mehmet Akif Tütüncü
 */
public class Dialogs
{
	/**
	 * Shows the help dialog for main activity
	 * 
	 * @param context Context of the activity
	 * @param isFromMenu If this is true, the dialog will always be shown whether or not it is the first time of running
	 */
	public static void showMainHelpDialog(Context context, boolean isFromMenu)
	{
		if(isFromMenu)
		{
			showDialog(context, R.string.help_main_content);
		}
		else if(!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.ISRUNBEFORE_MAIN, false))
		{
			showDialog(context, R.string.help_main_content);
			PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(Constants.ISRUNBEFORE_MAIN, true).commit();
		}
	}
	
	/**
	 * Shows the help dialog for times activity
	 * 
	 * @param context Context of the activity
	 * @param isFromMenu If this is true, the dialog will always be shown whether or not it is the first time of running
	 */
	public static void showTimesHelpDialog(Context context, boolean isFromMenu)
	{
		if(isFromMenu)
		{
			showDialog(context, R.string.help_times_content);
		}
		else if(!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.ISRUNBEFORE_TIMES, false))
		{
			showDialog(context, R.string.help_times_content);
			PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(Constants.ISRUNBEFORE_TIMES, true).commit();
		}
	}
	
	/**
	 * Shows the about dialog
	 * 
	 * @param context Context of the activity
	 */
	public static void showAboutDialog(final Context context)
	{
		AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		
		String versionName = "";
		try
		{
			versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		builder.setTitle(context.getString(R.string.about_title, versionName));
		builder.setIcon(R.drawable.ic_launcher);
		builder.setMessage(Html.fromHtml(context.getString(R.string.about_content)));
		builder.setPositiveButton(context.getString(R.string.about_ok), null);
		
		dialog = builder.create();
		
		dialog.show();
	}
	
	/**
	 * Shows a dialog with a title, icon and a custom message
	 * 
	 * @param context Context of the activity
	 * @param messageResource String resource id of the custom message
	 */
	private static void showDialog(Context context, int messageResource)
	{
		AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		
		builder.setTitle(context.getString(R.string.help_title));
		builder.setIcon(R.drawable.ic_launcher);
		builder.setMessage(context.getString(messageResource));
		builder.setPositiveButton(context.getString(R.string.help_ok), null);
		
		dialog = builder.create();
		
		dialog.show();
	}
}