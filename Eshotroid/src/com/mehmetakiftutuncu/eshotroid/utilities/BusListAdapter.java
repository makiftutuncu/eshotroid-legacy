package com.mehmetakiftutuncu.eshotroid.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.mehmetakiftutuncu.eshotroid.Main;
import com.mehmetakiftutuncu.eshotroid.R;
import com.mehmetakiftutuncu.eshotroid.model.Bus;

public class BusListAdapter extends ArrayAdapter<Bus> implements SectionIndexer, OnCheckedChangeListener
{
	private Context context;
	private HashMap<String, Integer> alphaIndexer;
	private String[] sections;
	private ArrayList<Bus> items;
	
	private static class ViewHolder
	{
		protected Bus bus;
		protected CheckBox star;
		protected TextView number;
		protected TextView name;
	}
	
	ViewHolder viewHolder;

	public BusListAdapter(Context context, ArrayList<Bus> items)
	{
		super(context, R.layout.busses_row, items);
		
		this.context = context;
		this.items = items;

		updateSectionlist();
	}
	
	@Override
	public View getView(final int position, View row, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		if(row == null)
		{
			row = inflater.inflate(R.layout.busses_row, parent, false);
			
			viewHolder = new ViewHolder();
			
			viewHolder.bus = items.get(position);
			viewHolder.star = (CheckBox) row.findViewById(R.id.checkBox_busses_row);
			viewHolder.number = (TextView) row.findViewById(R.id.textView_busses_row_number);
			viewHolder.name = (TextView) row.findViewById(R.id.textView_busses_row_name);
			
			viewHolder.star.setOnCheckedChangeListener(this);
			
			row.setTag(viewHolder);
			row.setTag(R.id.linearLayout_busses_row, viewHolder.bus);
			row.setTag(R.id.checkBox_busses_row, viewHolder.star);
			row.setTag(R.id.textView_busses_row_number, viewHolder.number);
			row.setTag(R.id.textView_busses_row_name, viewHolder.name);
		}
		else
		{
			viewHolder = (ViewHolder) row.getTag();
		}
		
		viewHolder.bus = items.get(position);
		viewHolder.star.setChecked(viewHolder.bus.isStarred());
		viewHolder.number.setText("" + viewHolder.bus.getNumber());
		viewHolder.name.setText(viewHolder.bus.getSource() + " - " + viewHolder.bus.getDestination());
		
		return row;
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		viewHolder.bus.setStarred(isChecked);
		
		/*
		MyDatabase db = new MyDatabase(context);
		db.openDB();
		db.addOrUpdate(viewHolder.bus);
		db.closeDB();
		*/
		
		((Main) context).updateListHeader();
	}
	
	public void updateSectionlist()
	{
		alphaIndexer = new HashMap<String, Integer>();
		int size = items.size();

		for(int i = 0; i < size; i++)
		{
			alphaIndexer.put("" + items.get(i).getNumber(), i);
		}

		Set<String> sectionLetters = alphaIndexer.keySet();

		ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);

		Collections.sort(sectionList, new Comparator<String>()
		{
			@Override
			public int compare(String lhs, String rhs)
			{
				if(lhs.length() < rhs.length())
				{
					return -1;
				}
				else if(lhs.length() > rhs.length())
				{
					return 1;
				}
				else
				{
					int l = Integer.parseInt(lhs);
					int r = Integer.parseInt(rhs);
					
					if(l < r)
					{
						return -1;
					}
					else if(l > r)
					{
						return 1;
					}
					else
					{
						return 0;
					}
				}
			}
		});

		sections = new String[sectionList.size()];

		sectionList.toArray(sections);
	}

	public int getPositionForSection(int section)
	{
		return alphaIndexer.get(sections[section]);
	}

	public int getSectionForPosition(int position)
	{
		return 0;
	}

	public Object[] getSections()
	{
		return sections;
	}
}