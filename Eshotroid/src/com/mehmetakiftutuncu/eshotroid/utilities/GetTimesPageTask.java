package com.mehmetakiftutuncu.eshotroid.utilities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.devspark.appmsg.AppMsg;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.mehmetakiftutuncu.eshotroid.Constants;
import com.mehmetakiftutuncu.eshotroid.R;
import com.mehmetakiftutuncu.eshotroid.Times;
import com.mehmetakiftutuncu.eshotroid.database.MyDatabase;
import com.mehmetakiftutuncu.eshotroid.model.Bus;
import com.mehmetakiftutuncu.eshotroid.model.BusTime;

/**
 * An asynchronous task for getting the bus times page
 * 
 * @author Mehmet Akif Tütüncü
 */
public class GetTimesPageTask extends AsyncTask<Void, Void, Void> implements Runnable
{
	private Context context;
	private PullToRefreshScrollView ptrView;
	private Bus bus;
	private String type;
	
	private String url;
	
	private ArrayList<BusTime> busTimes;
	
	private String result;
	
	public GetTimesPageTask(Context context, PullToRefreshScrollView ptrView, Bus bus, String type)
	{
		this.context = context;
		this.ptrView = ptrView;
		this.bus = bus;
		this.type = type;
		
		this.url = String.format("%s?%s=%s&%s=%s", Constants.BUS_TIMES_URL, Constants.NUMBER_PARAMETER, bus.getNumber(), Constants.TYPE_PARAMETER, type);
		
		((Times) context).toggleProgressBar(true);
	}
	
	@Override
	protected Void doInBackground(Void... params)
	{
		result = Connection.getPage(context, url);
		
		if(result != null)
		{
			bus.setRoute(Parser.parseBusRoute(result));
			
			ArrayList<String> parsedBusTimes = Parser.parseBusTimes(result);
			if(parsedBusTimes != null)
			{
				busTimes = Processor.processBusTimes(parsedBusTimes);
			}

			if(busTimes != null)
			{
				if(type.equals("H"))
				{
					bus.setTimesH(busTimes);
				}
				else if(type.equals("C"))
				{
					bus.setTimesC(busTimes);
				}
				else if(type.equals("P"))
				{
					bus.setTimesP(busTimes);
				}
				
				MyDatabase db = new MyDatabase(context);
				db.openDB();
				db.addOrUpdate(bus);
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
			if(busTimes != null)
			{
				AppMsg.makeText((Activity) context, context.getString(R.string.info_successful), AppMsg.STYLE_INFO).show();
				
				((Times) context).updateInformation(bus);
			}
			else
			{
				// Downloaded but couldn't be parsed
			}
		}
		else
		{
			AppMsg.makeText((Activity) context, context.getString(R.string.error_noConnection), AppMsg.STYLE_ALERT).show();
		}
		
		((Times) context).toggleProgressBar(false);
		
		ptrView.onRefreshComplete();
	}
}