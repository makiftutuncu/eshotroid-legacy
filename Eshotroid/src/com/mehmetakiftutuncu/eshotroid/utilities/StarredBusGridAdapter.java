package com.mehmetakiftutuncu.eshotroid.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.mehmetakiftutuncu.eshotroid.Constants;
import com.mehmetakiftutuncu.eshotroid.R;
import com.mehmetakiftutuncu.eshotroid.Times;
import com.mehmetakiftutuncu.eshotroid.model.Bus;

public class StarredBusGridAdapter extends ArrayAdapter<Bus>
{
	private Context context;
	private ArrayList<Bus> items;
	
	private static class ViewHolder
	{
		protected Button button;
	}

	public StarredBusGridAdapter(Context context, ArrayList<Bus> items)
	{
		super(context, R.layout.busses_row, items);
		
		this.context = context;
		this.items = items;
		
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
	public View getView(final int position, View item, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		ViewHolder viewHolder = null;
		if(item == null)
		{
			item = inflater.inflate(R.layout.busses_starredline, parent, false);
			viewHolder = new ViewHolder();
			
			viewHolder.button = (Button) item.findViewById(R.id.button_starredLine);
			
			item.setTag(viewHolder);
			item.setTag(R.id.button_starredLine, viewHolder.button);
		}
		else
		{
			viewHolder = (ViewHolder) item.getTag();
		}
		
		final Bus bus = items.get(position);
		
		viewHolder.button.setText("" + bus.getNumber());
		viewHolder.button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(context, Times.class);
				intent.putExtra(Constants.BUS_NUMBER_EXTRA, bus.getNumber());
				context.startActivity(intent);
			}
		});
		
		return item;
	}
}