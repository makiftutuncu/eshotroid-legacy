package com.mehmetakiftutuncu.eshotroid;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
	
	private String selectedLine;
	private String selectedLineFull;
	
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
				lBusLines.setOnItemClickListener(new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> adapter, View view, int index, long id)
					{
						TextView t = (TextView) view;
						selectedLineFull = t.getText().toString();
						selectedLine = selectedLineFull.split(Constants.BUS_LINE_SEPERATOR)[0];
						tSelectedLine.setText(getString(R.string.selectedLine) + ": " + selectedLine);
					}
				});
			}
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, "Couldn't load bus lines!", e);
		}
	}
	
	private void initialize()
	{
		bWeekDay = (Button) findViewById(R.id.main_button_weekDays);
		bSaturday = (Button) findViewById(R.id.main_button_saturday);
		bSunday = (Button) findViewById(R.id.main_button_sunday);
		lBusLines = (ListView) findViewById(R.id.main_listView_busLines);
		tSelectedLine = (TextView) findViewById(R.id.main_textView_selectedLine);
		
		bWeekDay.setOnClickListener(this);
		bSaturday.setOnClickListener(this);
		bSunday.setOnClickListener(this);
	}
	
	private void getLineInformation(String type, String line, String info)
	{
		Intent intent = new Intent(this, Times.class);
		intent.putExtra(Constants.TYPE_EXTRA, type);
		intent.putExtra(Constants.LINE_EXTRA, line);
		intent.putExtra(Constants.FULL_INFO_EXTRA, info);
		startActivity(intent);
	}

	@Override
	public void onClick(View v)
	{
		if(selectedLine == null)
		{
			Toast.makeText(this, getString(R.string.selectLineFirst), Toast.LENGTH_SHORT).show();
			
			return;
		}
		switch(v.getId())
		{
			case R.id.main_button_weekDays:
				getLineInformation("H", selectedLine, selectedLineFull);
				break;

			case R.id.main_button_saturday:
				getLineInformation("C", selectedLine, selectedLineFull);
				break;

			case R.id.main_button_sunday:
				getLineInformation("P", selectedLine, selectedLineFull);
				break;
		}
	}
}
