package com.mehmetakiftutuncu.eshotroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mehmetakiftutuncu.eshotroid.R;
import com.mehmetakiftutuncu.eshotroid.utility.Constants;

/** Help activity of the application which shows the user how to use the application
 * 
 * @author mehmetakiftutuncu */
public class Help extends ActionBarActivity implements OnClickListener
{
	private class HelpPage
	{
		int pageText;
		int pageImage;
		
		public HelpPage(int pageText, int pageImage)
		{
			this.pageText = pageText;
			this.pageImage = pageImage;
		}
	}
	
	private TextView text;
	private ImageView image;
	private ProgressBar progress;
	private Button next;
	private Button previous;
	
	private int pageNumber = -1;
	
	private HelpPage[] helpPages = new HelpPage[]
	{
		new HelpPage(R.string.help_navigation, R.drawable.help_navigation),
		new HelpPage(R.string.help_showing_times, R.drawable.help_showing_times),
		new HelpPage(R.string.help_times, R.drawable.help_times),
		new HelpPage(R.string.help_showing_remaining_time, R.drawable.help_showing_remaining_time),
		new HelpPage(R.string.help_searching, R.drawable.help_searching),
		new HelpPage(R.string.help_favoriting, R.drawable.help_favoriting),
		new HelpPage(R.string.help_refreshing, R.drawable.help_refreshing),
		new HelpPage(R.string.help_showing_menu, R.drawable.help_showing_menu)
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_help);
		
		text = (TextView) findViewById(R.id.textView_activityHelp);
		image = (ImageView) findViewById(R.id.imageView_activityHelp);
		progress = (ProgressBar) findViewById(R.id.progressBar_activityHelp);
		next = (Button) findViewById(R.id.button_activityHelp_next);
		previous = (Button) findViewById(R.id.button_activityHelp_previous);
		
		next.setOnClickListener(this);
		previous.setOnClickListener(this);
		
		progress.setMax(helpPages.length);
		
		changePage(true);
	}
	
	private void changePage(boolean nextPage)
	{
		if(nextPage)
		{
			if(pageNumber < helpPages.length - 1)
			{
				pageNumber++;
			}
		}
		else
		{
			if(pageNumber >= 0)
			{
				pageNumber--;
			}
		}
		
		if(pageNumber == 0)
		{
			previous.setEnabled(false);
			
			next.setText(getString(R.string.help_next));
		}
		else if(pageNumber == helpPages.length - 1)
		{
			previous.setEnabled(true);
			
			next.setText(getString(R.string.help_finish));
		}
		else
		{
			previous.setEnabled(true);
			
			next.setText(getString(R.string.help_next));
		}
		
		text.setText(getString(helpPages[pageNumber].pageText));
		image.setImageResource(helpPages[pageNumber].pageImage);
		
		progress.setProgress(pageNumber + 1);
	}
	
	@Override
	protected void onDestroy()
	{
		Bundle extras = getIntent().getExtras();
		if(extras != null)
		{
			if(extras.getBoolean(Constants.HELP_FROM_SETUP_WIZARD_EXTRA, false))
			{
				PreferenceManager.getDefaultSharedPreferences(this).edit()
				.putBoolean(Constants.IS_SETUP_WIZARD_FINISHED, true).commit();
				
				startActivity(new Intent(this, Main.class));
			}
		}
		
		super.onDestroy();
	}

	@Override
	public void onClick(View v)
	{
		if(v.getId() == R.id.button_activityHelp_next)
		{
			if(pageNumber == helpPages.length - 1)
				
				finish();
			else
				changePage(true);
		}
		else if(v.getId() == R.id.button_activityHelp_previous)
		{
			changePage(false);
		}
	}
}