package com.mehmetakiftutuncu.eshotroid.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.mehmetakiftutuncu.eshotroid.R;

/** About activity of the application which shows the information about the
 * application, developer and credits
 * 
 * @author mehmetakiftutuncu */
public class About extends ActionBarActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_about);
		
		String versionName = "";
		try
		{
			versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		}
		catch(Exception e) {}
		
		TextView version = (TextView) findViewById(R.id.textView_activityAbout_version);
		version.setText(getString(R.string.app_name) + " " + versionName);
		
		TextView content = (TextView) findViewById(R.id.textView_activityAbout_content);
		content.setText(Html.fromHtml(getResources().getText(R.string.about_content).toString()));
		content.setMovementMethod(LinkMovementMethod.getInstance());
	}
}