package com.mehmetakiftutuncu.eshotroid.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Filter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.mehmetakiftutuncu.eshotroid.Main;
import com.mehmetakiftutuncu.eshotroid.R;
import com.mehmetakiftutuncu.eshotroid.database.MyDatabase;
import com.mehmetakiftutuncu.eshotroid.model.Bus;
import com.mehmetakiftutuncu.eshotroid.tasks.GetTimesPageTask;

public class BusListAdapter extends ArrayAdapter<Bus> implements SectionIndexer
{
	private Context myContext;
	private HashMap<String, Integer> myIndexer;
	private String[] mySections;
	private ArrayList<Bus> myOriginalItems;
	private ArrayList<Bus> myCurrentItems;
	
	/**
	 * Tag for debugging
	 */
	public static final String LOG_TAG = "Eshotroid_BusListAdapter";
	
	static class ViewHolder
	{
		public CheckBox favorite;
		public TextView number;
		public TextView name;
	}

	public BusListAdapter(Context context, ArrayList<Bus> items)
	{
		super(context, R.layout.busses_row, items);
		
		myContext = context;
		myOriginalItems = items;
		myCurrentItems = new ArrayList<Bus>();
		myCurrentItems.addAll(myOriginalItems);
		
		updateSectionlist();
	}
	
	@Override
	public Filter getFilter()
	{
		return new Filter()
		{
			@Override
			protected FilterResults performFiltering(CharSequence constraint)
			{
				FilterResults result = new FilterResults();
				
				if(constraint != null && constraint.toString().length() > 0)
				{
					Locale trLocale = new Locale("tr");
					constraint = constraint.toString().toLowerCase(trLocale);
					
					ArrayList<Bus> filteredItems = new ArrayList<Bus>();
					
					for(int i = 0, l = myOriginalItems.size(); i < l; i++)
					{
						Bus bus = myOriginalItems.get(i);
						
						/* Custom search logic here; bus number, source, destination and route are checked for matching */
						String number = String.valueOf(bus.getNumber());
						String source = bus.getSource() != null ? bus.getSource() : "";
						String destination = bus.getDestination() != null ? bus.getDestination() : "";
						String route = bus.getRoute() != null ? bus.getRoute() : "";
						
						if(	number.contains(constraint) ||
							source.toLowerCase(trLocale).contains(constraint) ||
							destination.toLowerCase(trLocale).contains(constraint) ||
							route.toLowerCase(trLocale).contains(constraint))
						{
							filteredItems.add(bus);
						}
					}
					
					result.count = filteredItems.size();
					result.values = filteredItems;
				}
				else
				{
					result.count = -1;
				}
				
				return result;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results)
			{
				if(results != null && results.count >= 0)
				{
					myCurrentItems = (ArrayList<Bus>) results.values;
				}
				else
				{
					myCurrentItems = myOriginalItems;
				}
				
				notifyDataSetInvalidated();
			}
		};
	}
	
	@Override
	public int getCount()
	{
		return myCurrentItems != null ? myCurrentItems.size() : 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		
		if(row == null)
		{
			LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			row = inflater.inflate(R.layout.busses_row, parent, false);
			
			ViewHolder myViewHolder = new ViewHolder();
			
			myViewHolder.favorite = (CheckBox) row.findViewById(R.id.checkBox_busses_row);
			myViewHolder.number = (TextView) row.findViewById(R.id.textView_busses_row_number);
			myViewHolder.name = (TextView) row.findViewById(R.id.textView_busses_row_name);
			
			row.setTag(myViewHolder);
		}
		
		ViewHolder holder = (ViewHolder) row.getTag();
		
		final Bus bus = myCurrentItems.get(position);
		
		holder.favorite.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				// Do stuff only if the status is changed (^ is XOR which gives true if the boolean values are not the same)
				if(bus.isFavorited() ^ isChecked)
				{
					if(isChecked)
					{
						Log.d(LOG_TAG, "Adding " + bus.getNumber() + " to favorited busses...");
					}
					else
					{
						Log.d(LOG_TAG, "Removing " + bus.getNumber() + " from favorited busses...");
					}
					
					bus.setFavorited(isChecked);
					((Main) myContext).updateListHeader();
					
					MyDatabase db = new MyDatabase(myContext);
					
					db.openDB();
					db.addOrUpdate(bus);
					db.closeDB();
					
					if(isChecked)
					{
						db.openDB();
						Bus newBus = db.get(bus.getNumber());
						db.closeDB();
						
						if(newBus.getTimesH() == null)
						{
							GetTimesPageTask task = new GetTimesPageTask(myContext, newBus, "H");
							task.execute();
						}
						if(newBus.getTimesC() == null)
						{
							GetTimesPageTask task = new GetTimesPageTask(myContext, newBus, "C");
							task.execute();
						}
						if(newBus.getTimesP() == null)
						{
							GetTimesPageTask task = new GetTimesPageTask(myContext, newBus, "P");
							task.execute();
						}
					}
				}
			}
		});
		
		holder.favorite.setChecked(bus.isFavorited());
		holder.number.setText("" + bus.getNumber());
		holder.name.setText(bus.getSource() + " - " + bus.getDestination());
		
		return row;
	}
	
	public void updateSectionlist()
	{
		myIndexer = new HashMap<String, Integer>();
		int size = myCurrentItems.size();

		for(int i = 0; i < size; i++)
		{
			myIndexer.put("" + myCurrentItems.get(i).getNumber(), i);
		}

		Set<String> sectionLetters = myIndexer.keySet();

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

		mySections = new String[sectionList.size()];

		sectionList.toArray(mySections);
	}

	public int getPositionForSection(int section)
	{
		return myIndexer.get(mySections[section]);
	}

	public int getSectionForPosition(int position)
	{
		return 0;
	}

	public Object[] getSections()
	{
		return mySections;
	}
}