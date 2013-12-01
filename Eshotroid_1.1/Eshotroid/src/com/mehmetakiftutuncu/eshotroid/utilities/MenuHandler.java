package com.mehmetakiftutuncu.eshotroid.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.actionbarsherlock.view.MenuItem;
import com.mehmetakiftutuncu.eshotroid.Constants;
import com.mehmetakiftutuncu.eshotroid.Main;
import com.mehmetakiftutuncu.eshotroid.R;
import com.mehmetakiftutuncu.eshotroid.Times;

/**
 * A utility class for handling menu actions
 * 
 * @author Mehmet Akif Tütüncü
 */
public class MenuHandler
{
	/**
	 * Handle the menu item click
	 * 
	 * @param context Context of the activity
	 * @param item Menu item that is clicked
	 */
	public static void handle(Context context, MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.item_rate:
				Intent intentRate = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.APPLICATION_URL));
				context.startActivity(Intent.createChooser(intentRate, context.getString(R.string.menu_rate)));
				break;
				
			case R.id.item_contact:
				Intent intentContact = new Intent(Intent.ACTION_SEND);
				intentContact.setType("message/rfc822");
				intentContact.putExtra(Intent.EXTRA_EMAIL, new String[] {Constants.CONTACT});
				intentContact.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.contact_subject));
				context.startActivity(Intent.createChooser(intentContact, context.getString(R.string.menu_contact)));
				break;
				
			case R.id.item_website:
				Intent intentWebsite = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.WEBSITE_URL));
				context.startActivity(Intent.createChooser(intentWebsite, context.getString(R.string.menu_website)));
				break;
				
			case R.id.item_help:
				if(((Activity) context) instanceof Main)
				{
					Dialogs.showMainHelpDialog(context, true);
				}
				else if(((Activity) context) instanceof Times)
				{
					Dialogs.showTimesHelpDialog(context, true);
				}
				break;
				
			case R.id.item_about:
				Dialogs.showAboutDialog(context);
				break;
		}
	}
}