package com.mehmetakiftutuncu.eshotroid.activity;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mehmetakiftutuncu.eshotroid.R;
import com.mehmetakiftutuncu.eshotroid.database.BusDatabase;
import com.mehmetakiftutuncu.eshotroid.model.Bus;
import com.mehmetakiftutuncu.eshotroid.model.BusTimeTypes;
import com.mehmetakiftutuncu.eshotroid.task.GetBusTimesPageTask;
import com.mehmetakiftutuncu.eshotroid.task.GetListOfBussesPageTask;
import com.mehmetakiftutuncu.eshotroid.utility.Constants;
import com.mehmetakiftutuncu.eshotroid.utility.Messages;

/** Setup wizard activity of the application which lets user to initialize the
 * application
 * 
 * @author mehmetakiftutuncu */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SetupWizard extends ActionBarActivity implements OnClickListener
{
	private RelativeLayout downloadBussesProgressBarLayout;
	private RelativeLayout optionsLayout;
	private RadioGroup options;
	private RelativeLayout downloadTimesProgressBarLayout;
	private TextView downloadTimesStatus;
	private ProgressBar downloadTimesProgressBar;
	private Button start;
	
	private ArrayList<Bus> busses;
	
	/** Tag for debugging */
	private static final String LOG_TAG = "Eshotroid_SetupWizard";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_setup_wizard);
		
		initialize();
	}
	
	private void initialize()
	{
		downloadBussesProgressBarLayout = (RelativeLayout) findViewById(R.id.relativeLayout_activitySetupWizard_downloadBusses);
		optionsLayout = (RelativeLayout) findViewById(R.id.relativeLayout_activitySetupWizard_options);
		start = (Button) findViewById(R.id.button_activitySetupWizard_start);
		options = (RadioGroup) findViewById(R.id.radioGroup_activitySetupWizard_options);
		downloadTimesProgressBarLayout = (RelativeLayout) findViewById(R.id.relativeLayout_activitySetupWizard_downloadTimes);
		downloadTimesStatus = (TextView) findViewById(R.id.textView_activitySetupWizard_downloadTimesStatus);
		downloadTimesProgressBar = (ProgressBar) findViewById(R.id.progressBar_activitySetupWizard_downloadTimesProgressBar);
		
		start.setOnClickListener(this);
		
		BusDatabase db = BusDatabase.getDatabase(this);
		busses = db.get();
		db.closeDatabase();
		
		if(busses != null && busses.size() > 0)
		{
			toggleProgressBar(false);
		}
		else
		{
			downloadBusList();
		}
	}
	
	@Override
	public void onBackPressed()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle(R.string.setupWizard_cancelDialog_title);
		builder.setMessage(R.string.setupWizard_cancelDialog_message);
		builder.setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				openHelp();
			}
		});
		builder.setNegativeButton(getString(R.string.dialog_no), null);
		
		builder.show();
	}
	
	private void downloadBusList()
	{
		toggleProgressBar(true);
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			new GetListOfBussesPageTask(this).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
		else
			new GetListOfBussesPageTask(this).execute();
	}
	
	public void setBusses(ArrayList<Bus> busses)
	{
		this.busses = busses;
	}
	
	public void toggleProgressBar(boolean isVisible)
	{
		downloadBussesProgressBarLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
		
		optionsLayout.setVisibility(!isVisible ? View.VISIBLE : View.GONE);
		start.setVisibility(!isVisible ? View.VISIBLE : View.GONE);
	}
	
	private void toggleDownloadTimesProgressBar(boolean isVisible)
	{
		downloadTimesProgressBarLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
		
		optionsLayout.setVisibility(!isVisible ? View.VISIBLE : View.GONE);
		start.setVisibility(!isVisible ? View.VISIBLE : View.GONE);
	}
	
	private void showDownloadSelectedDailog()
	{
		if(busses == null)
		{
			Messages.getInstance().showNegative(this, getString(R.string.error_setupWizard_noBusses));
			
			return;
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		String[] busNames = new String[busses.size()];
		for(int i = 0; i < busNames.length; i++)
		{
			busNames[i] = busses.get(i).toString();
		}
		builder.setMultiChoiceItems(busNames, new boolean[busses.size()], new OnMultiChoiceClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked)
			{
				busses.get(which).setFavorited(isChecked);
			}
		});
		builder.setTitle(R.string.setupWizard_downloadSelectedDialog_title);
		builder.setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				downloadSelected();
			}
		});
		
		builder.show();
	}
	
	private void downloadTimes(final ArrayList<Bus> bussesToDownload)
	{
		Handler handler = new Handler(Looper.getMainLooper());
		Runnable incrementProgress = new Runnable()
		{
			@Override
			public void run()
			{
				downloadTimesProgressBar.incrementProgressBy(1);
			}
		};
		handler.post(new Runnable()
		{
			@Override
			public void run()
			{
				downloadTimesProgressBar.setMax(bussesToDownload.size() * 3);
				toggleDownloadTimesProgressBar(true);
			}
		});
		
		for(final Bus i : bussesToDownload)
		{
			try
			{
				BusDatabase db = BusDatabase.getDatabase(this);
				db.addOrUpdate(i);
				db.closeDatabase();
				
				handler.post(new Runnable()
				{
					@Override
					public void run()
					{
						downloadTimesStatus.setText(getString(R.string.setupWizard_downloadTimes_status, i.getNumber() + " " + getString(BusTimeTypes.WEEK_DAY.getNameResourceId())));
					}
				});
				GetBusTimesPageTask taskH = new GetBusTimesPageTask(this, i, BusTimeTypes.WEEK_DAY);
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
					taskH.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR).get();
				else
					taskH.execute().get();
				handler.post(incrementProgress);
				
				handler.post(new Runnable()
				{
					@Override
					public void run()
					{
						downloadTimesStatus.setText(getString(R.string.setupWizard_downloadTimes_status, i.getNumber() + " " + getString(BusTimeTypes.SATURDAY.getNameResourceId())));
					}
				});
				GetBusTimesPageTask taskC = new GetBusTimesPageTask(this, i, BusTimeTypes.SATURDAY);
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
					taskC.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR).get();
				else
					taskC.execute().get();
				handler.post(incrementProgress);
				
				handler.post(new Runnable()
				{
					@Override
					public void run()
					{
						downloadTimesStatus.setText(getString(R.string.setupWizard_downloadTimes_status, i.getNumber() + " " + getString(BusTimeTypes.SUNDAY.getNameResourceId())));
					}
				});
				GetBusTimesPageTask taskP = new GetBusTimesPageTask(this, i, BusTimeTypes.SUNDAY);
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
					taskP.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR).get();
				else
					taskP.execute().get();
				handler.post(incrementProgress);
			}
			catch(Exception e)
			{
				Log.e(LOG_TAG, "Error occurred while downloading bus times!", e);
			}
		}
		
		openHelp();
	}
	
	private void downloadSelected()
	{
		final ArrayList<Bus> bussesToDownload = new ArrayList<Bus>();
		for(Bus i : busses)
		{
			if(i.isFavorited())
				bussesToDownload.add(i);
		}
		
		if(bussesToDownload.size() <= 0)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			
			builder.setTitle(R.string.setupWizard_noBusSelectedDialog_title);
			builder.setMessage(R.string.setupWizard_noBusSelectedDialog_message);
			builder.setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					openHelp();
				}
			});
			builder.setNegativeButton(getString(R.string.dialog_no), null);
			
			builder.show();
		}
		else
		{
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					downloadTimes(bussesToDownload);
				}
			}).start();
		}
	}
	
	private void openHelp()
	{
		Intent helpIntent = new Intent(SetupWizard.this, Help.class);
		helpIntent.putExtra(Constants.HELP_FROM_SETUP_WIZARD_EXTRA, true);
		finish();
		startActivity(helpIntent);
	}

	@Override
	public void onClick(View v)
	{
		if(v.getId() == R.id.button_activitySetupWizard_start)
		{
			int checkedRadioButtonId = options.getCheckedRadioButtonId();
			
			switch(checkedRadioButtonId)
			{
				case R.id.radioButton_activitySetupWizard_downloadSelected:
					showDownloadSelectedDailog();
					break;
				
				case R.id.radioButton_activitySetupWizard_downloadNothing:
					openHelp();
					break;
			}
		}
	}
}