package com.mehmetakiftutuncu.eshotroid;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mehmetakiftutuncu.eshotroid.model.BusLine;
import com.mehmetakiftutuncu.eshotroid.utilities.GetPageTask;
import com.mehmetakiftutuncu.eshotroid.utilities.Parser;

/**
 * Main activity of the application
 * 
 * @author Mehmet Akif Tütüncü
 */
public class Main extends Activity implements OnClickListener
{
	private Button bWeekDay;
	private Button bSaturday;
	private Button bSunday;
	private ListView lBusLines;
	private TextView tSelectedLine;
	
	/**
	 * Tag for debugging
	 */
	public static final String LOG_TAG = "Eshotroid_Main";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		initialize();
		
		GetPageTask task = new GetPageTask(this);
		task.execute(Constants.BUS_LINES_URL);
		
		try
		{
			String busLinesPage = task.get();
			
			if(busLinesPage != null)
			{
				ArrayList<BusLine> lines = Parser.getBusLines(busLinesPage);
				String[] lineTexts = new String[lines.size()];
				
				for(int i = 0; i < lines.size(); i++)
				{
					lineTexts[i] = lines.get(i).toString();
				}
				
				lBusLines.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lineTexts));
			}
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, "Couldn't load bus lines!", e);
		}
	}
	
	private void initialize()
	{
		bWeekDay = (Button) findViewById(R.id.button_weekDays);
		bSaturday = (Button) findViewById(R.id.button_saturday);
		bSunday = (Button) findViewById(R.id.button_sunday);
		lBusLines = (ListView) findViewById(R.id.listView_busLines);
		tSelectedLine = (TextView) findViewById(R.id.textView_selectedLine);
		
		bWeekDay.setOnClickListener(this);
		bSaturday.setOnClickListener(this);
		bSunday.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.button_weekDays:
				break;

			case R.id.button_saturday:
				break;

			case R.id.button_sunday:
				break;
		}
	}
}
