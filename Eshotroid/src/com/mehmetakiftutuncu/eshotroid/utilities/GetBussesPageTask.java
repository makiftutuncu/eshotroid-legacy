package com.mehmetakiftutuncu.eshotroid.utilities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.ListAdapter;

import com.devspark.appmsg.AppMsg;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mehmetakiftutuncu.eshotroid.Constants;
import com.mehmetakiftutuncu.eshotroid.Main;
import com.mehmetakiftutuncu.eshotroid.R;
import com.mehmetakiftutuncu.eshotroid.database.MyDatabase;
import com.mehmetakiftutuncu.eshotroid.model.Bus;

/**
 * An asynchronous task for getting the busses page
 * 
 * @author Mehmet Akif Tütüncü
 */
public class GetBussesPageTask extends AsyncTask<Void, Void, Void> implements Runnable
{
	private Context context;
	private PullToRefreshListView ptrList;
	private ListAdapter oldAdapter;
	
	private ArrayList<Bus> busses;
	
	private String result;
	
	/**
	 * Tag for debugging
	 */
	public static final String LOG_TAG = "Eshotroid_GetBussesPageTask";
	
	public GetBussesPageTask(Context context, PullToRefreshListView ptrList)
	{
		this.context = context;
		this.ptrList = ptrList;
		
		((Main) context).toggleMode(true);
		((Main) context).toggleHeader(true);
		
		oldAdapter = ptrList.getRefreshableView().getAdapter();
		
		ptrList.setAdapter(null);
	}
	
	@Override
	protected Void doInBackground(Void... params)
	{
		result = Connection.getPage(context, Constants.BUSSES_URL);
		
		if(result != null)
		{
			busses = null;
			
			ArrayList<String> parsedBusses = Parser.parseBusses(result);
			if(parsedBusses != null)
			{
				busses = Processor.processBusses(parsedBusses);
			}
			
			if(busses != null)
			{
				MyDatabase db = new MyDatabase(context);
				db.openDB();
				for(Bus i : busses)
				{
					db.addOrUpdate(i);
				}
				db.closeDB();
			}
		}
		
		new Handler(context.getMainLooper()).post(this);
		
		return null;
	}
	
	@Override
	public void run()
	{
		if(result != null)
		{
			((Main) context).toggleHeader(false);
			
			if(busses != null)
			{
				AppMsg.makeText((Activity) context, context.getString(R.string.info_successful), AppMsg.STYLE_INFO).show();
				
				((Main) context).setBussesList(busses);
				
				ptrList.setAdapter(new BusListAdapter(context, busses));
			}
			else
			{
				// Downloaded but couldn't be parsed
				
				ptrList.setAdapter(oldAdapter);
			}
		}
		else
		{
			AppMsg.makeText((Activity) context, context.getString(R.string.error_noConnection), AppMsg.STYLE_ALERT).show();
		}
		
		((Main) context).toggleMode(false);
		
		ptrList.onRefreshComplete();
	}
}