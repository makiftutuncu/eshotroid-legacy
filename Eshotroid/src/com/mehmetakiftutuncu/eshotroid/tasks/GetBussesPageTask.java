package com.mehmetakiftutuncu.eshotroid.tasks;

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
import com.mehmetakiftutuncu.eshotroid.adapters.BusListAdapter;
import com.mehmetakiftutuncu.eshotroid.database.MyDatabase;
import com.mehmetakiftutuncu.eshotroid.model.Bus;
import com.mehmetakiftutuncu.eshotroid.utilities.Connection;
import com.mehmetakiftutuncu.eshotroid.utilities.Parser;
import com.mehmetakiftutuncu.eshotroid.utilities.Processor;

/**
 * An asynchronous task for getting the busses page
 * 
 * @author Mehmet Akif Tütüncü
 */
public class GetBussesPageTask extends AsyncTask<Void, Void, Void> implements Runnable
{
	private Context myContext;
	private PullToRefreshListView myPtrList;
	
	private ListAdapter oldAdapter;
	private ArrayList<Bus> busses;
	private String result;
	
	/**
	 * Tag for debugging
	 */
	public static final String LOG_TAG = "Eshotroid_GetBussesPageTask";
	
	public GetBussesPageTask(Context context, PullToRefreshListView ptrList)
	{
		myContext = context;
		myPtrList = ptrList;
		
		((Main) context).toggleMode(true);
		
		oldAdapter = ptrList.getRefreshableView().getAdapter();
		
		ptrList.setAdapter(null);
	}
	
	@Override
	protected Void doInBackground(Void... params)
	{
		result = Connection.getPage(myContext, Constants.BUSSES_URL);
		
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
				MyDatabase db = new MyDatabase(myContext);
				db.openDB();
				for(Bus i : busses)
				{
					db.addOrUpdate(i);
				}
				db.closeDB();
			}
		}
		
		new Handler(myContext.getMainLooper()).post(this);
		
		return null;
	}
	
	@Override
	public void run()
	{
		if(result != null)
		{
			if(busses != null)
			{
				AppMsg.makeText((Activity) myContext, myContext.getString(R.string.info_successful), AppMsg.STYLE_INFO).show();
				
				((Main) myContext).setBussesList(busses);
				
				myPtrList.setAdapter(new BusListAdapter(myContext, busses));
			}
			else
			{
				// Downloaded but couldn't be parsed
				myPtrList.setAdapter(oldAdapter);
			}
		}
		else
		{
			AppMsg.makeText((Activity) myContext, myContext.getString(R.string.error_noConnection), AppMsg.STYLE_ALERT).show();
		}
		
		((Main) myContext).toggleMode(false);
		
		myPtrList.onRefreshComplete();
	}
}