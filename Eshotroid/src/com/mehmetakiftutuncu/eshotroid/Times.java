package com.mehmetakiftutuncu.eshotroid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.mehmetakiftutuncu.eshotroid.utilities.GetPageTask;

/**
 * Times activity of the application
 * 
 * @author Mehmet Akif Tütüncü
 */
public class Times extends Activity
{
	/**
	 * Tag for debugging
	 */
	public static final String LOG_TAG = "Eshotroid_Times";
	
	private TextView tSelectedLine;
	
	private String url;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.times);
		
		initialize();
		
		GetPageTask task = new GetPageTask(this);
		task.execute(url);
		
		try
		{
			String lineTimesPage = task.get();
			
			if(lineTimesPage != null)
			{
				Toast.makeText(this, lineTimesPage, Toast.LENGTH_SHORT).show();
			}
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, "Couldn't load bus lines!", e);
		}
	}
	
	private void initialize()
	{
		Bundle extras = getIntent().getExtras();
		String type = extras.getString(Constants.TYPE_EXTRA);
		String line = extras.getString(Constants.LINE_EXTRA);
		String info = extras.getString(Constants.FULL_INFO_EXTRA);
		
		tSelectedLine = (TextView) findViewById(R.id.times_textView_selectedLine);
		
		url = String.format("%s?%s=%s&%s=%s", Constants.LINE_TIMES_URL, Constants.TYPE_PARAMETER, type, Constants.LINE_PARAMETER, line);
		tSelectedLine.setText(info);
	}
}
