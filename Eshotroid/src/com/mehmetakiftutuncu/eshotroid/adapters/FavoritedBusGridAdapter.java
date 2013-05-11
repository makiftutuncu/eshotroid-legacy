package com.mehmetakiftutuncu.eshotroid.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.mehmetakiftutuncu.eshotroid.Constants;
import com.mehmetakiftutuncu.eshotroid.R;
import com.mehmetakiftutuncu.eshotroid.Main;
import com.mehmetakiftutuncu.eshotroid.Times;
import com.mehmetakiftutuncu.eshotroid.database.MyDatabase;
import com.mehmetakiftutuncu.eshotroid.model.Bus;

public class FavoritedBusGridAdapter extends ArrayAdapter<Bus>
{
	private Context myContext;
	private ArrayList<Bus> myItems;
	
	static class ViewHolder
	{
		public Button button;
	}
	
	/**
	 * Tag for debugging
	 */
	public static final String LOG_TAG = "Eshotroid_FavoritedBusGridAdapter";

	public FavoritedBusGridAdapter(Context context, ArrayList<Bus> items)
	{
		super(context, R.layout.busses_row, items);
		
		this.myContext = context;
		this.myItems = items;
		
		Collections.sort(items, new Comparator<Bus>()
		{
			@Override
			public int compare(Bus lhs, Bus rhs)
			{
				return (lhs.getNumber() < rhs.getNumber()) ? -1 : ((lhs.getNumber() > rhs.getNumber()) ? 1 : 0);
			}
		});
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View item = convertView;
		
		if(item == null)
		{
			LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			item = inflater.inflate(R.layout.busses_favoritedbus, parent, false);
			
			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.button = (Button) item.findViewById(R.id.button_favoritedBus);
			
			item.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) item.getTag();
		
		final Bus bus = myItems.get(position);
		
		holder.button.setText("" + bus.getNumber());
		holder.button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(myContext, Times.class);
				intent.putExtra(Constants.BUS_NUMBER_EXTRA, bus.getNumber());
				myContext.startActivity(intent);
			}
		});
		holder.button.setOnLongClickListener(new OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v)
			{
				int number = Integer.parseInt(((Button) v).getText().toString());
				
				Log.d(LOG_TAG, "Removing " + number + " from favorited busses...");
				
				ArrayList<Bus> bussesList = ((Main) myContext).getBussesList();
				
				for(int i  = 0; i < bussesList.size(); i++)
				{
					if(bussesList.get(i).getNumber() == number)
					{
						bussesList.get(i).setFavorited(false);
						
						((Main) myContext).updateListHeader();
						
						MyDatabase db = new MyDatabase(myContext);
						db.openDB();
						db.addOrUpdate(bussesList.get(i));
						db.closeDB();
						
						break;
					}
				}
				
				return true;
			}
		});
		
		return item;
	}
}