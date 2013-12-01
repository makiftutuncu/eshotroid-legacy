package com.mehmetakiftutuncu.eshotroid.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.view.ViewPager;
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

import com.mehmetakiftutuncu.eshotroid.BuildConfig;
import com.mehmetakiftutuncu.eshotroid.R;
import com.mehmetakiftutuncu.eshotroid.activity.Main;
import com.mehmetakiftutuncu.eshotroid.database.BusDatabase;
import com.mehmetakiftutuncu.eshotroid.model.Bus;
import com.mehmetakiftutuncu.eshotroid.model.BusTimeTypes;
import com.mehmetakiftutuncu.eshotroid.task.GetBusTimesPageTask;
import com.mehmetakiftutuncu.eshotroid.utility.Constants;

/** Bus list adapter class is the adapter that supplies the bus list with items
 * from an ArrayList of Bus items
 * 
 * @author mehmetakiftutuncu */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class BusListAdapter extends ArrayAdapter<Bus> implements SectionIndexer
{
	/** {@link Context} of the activity that uses this adapter */
	private Context myContext;
	/** A map between the sections and bus numbers */
	private HashMap<String, Integer> myIndexer;
	/** Sections in the list (which will in fact be bus numbers themselves for
	 * fast scrolling) */
	private String[] mySections;
	/** Original list of items containing all the busses */
	private ArrayList<Bus> myOriginalItems;
	/** Current list of items whose contents depend on the search query that
	 * filters this adapter */
	private ArrayList<Bus> myCurrentItems;
	
	/** Tag for debugging */
	public static final String LOG_TAG = "Eshotroid_BusListAdapter";
	
	/** Holder class for each item */
	static class ViewHolder
	{
		public CheckBox favorite;
		public TextView number;
		public TextView name;
	}

	/** Constructor of the adapter */
	public BusListAdapter(Context context, ArrayList<Bus> items)
	{
		super(context, R.layout.item_bus, items);
		
		myContext = context;
		myOriginalItems = items;
		myCurrentItems = new ArrayList<Bus>();
		myCurrentItems.addAll(myOriginalItems);
		
		// Generate the sections based on current items
		updateSectionlist();
	}
	
	@Override
	public Filter getFilter()
	{
		// Return a custom filter
		return new Filter()
		{
			@Override
			protected FilterResults performFiltering(CharSequence constraint)
			{
				FilterResults result = new FilterResults();
				
				// If there is a constraint (i.e. there is a search query)
				if(constraint != null && constraint.toString().length() > 0)
				{
					// Modify the constraint for easier searching
					Locale trLocale = new Locale("tr");
					constraint = constraint.toString().trim().toLowerCase(trLocale);
					
					ArrayList<Bus> filteredItems = new ArrayList<Bus>();
					
					// Check each item
					for(int i = 0, l = myOriginalItems.size(); i < l; i++)
					{
						Bus bus = myOriginalItems.get(i);
						
						/* Custom search logic here; bus number, source,
						 * destination and route are checked for matching */
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
					
					// Update the filter attributes
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
				// If there is at least a match for the  search
				if(results != null && results.count >= 0)
				{
					// Set the current items as the result
					myCurrentItems = (ArrayList<Bus>) results.values;
				}
				else
				{
					// No match, show all items
					myCurrentItems = myOriginalItems;
				}
				
				// Notify that the list should update the items it shows
				notifyDataSetInvalidated();
			}
		};
	}
	
	@Override
	public int getCount()
	{
		/* This is important, if not checked here, the list will never know how
		 * many items it has causing crashes */
		return myCurrentItems != null ? myCurrentItems.size() : 0;
	}
	
	@Override
	public Bus getItem(int position)
	{
		/* If there is a set of current items meaning that the list is filtered
		 * after some search operation */
		if(myCurrentItems != null && myCurrentItems.size() > 0)
		{
			// Return the item in current items list
			return myCurrentItems.get(position);
		}
		
		return super.getItem(position);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// Try to use the view in memory
		View row = convertView;
		
		// If the view is not ready
		if(row == null)
		{
			// Get inflater
			LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			// Inflate and create the view
			row = inflater.inflate(R.layout.item_bus, parent, false);
			
			/* Create a new holder for this view to hold references of it's
			 * components */
			ViewHolder myViewHolder = new ViewHolder();
			
			// Find references for components and store them in holder
			myViewHolder.favorite = (CheckBox) row.findViewById(R.id.checkBox_item_bus_isFavorited);
			myViewHolder.number = (TextView) row.findViewById(R.id.textView_item_bus_number);
			myViewHolder.name = (TextView) row.findViewById(R.id.textView_item_bus_name);
			
			/* Add the holder to the view as a tag so references can be accessed
			later */
			row.setTag(myViewHolder);
		}
		
		// Get the holder of this view to access it's components
		ViewHolder holder = (ViewHolder) row.getTag();
		
		// Get the current bus
		final Bus bus = myCurrentItems.get(position);
		
		holder.favorite.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				/* Do stuff only if the status is changed (^ is XOR which gives
				 * true if the boolean values are not the same) */
				if(bus.isFavorited() ^ isChecked)
				{
					if(isChecked)
					{
						if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Adding " + bus.getNumber() + " to favorited busses...");
					}
					else
					{
						if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Removing " + bus.getNumber() + " from favorited busses...");
					}
					
					// Update the value of the bus
					bus.setFavorited(isChecked);
					
					// Update the value in database
					BusDatabase db = BusDatabase.getDatabase(myContext);
					db.addOrUpdate(bus);
					db.closeDatabase();
					
					// Get main activity
					Main main = (Main) myContext;
					// Get main view pager
					ViewPager viewPager = main.getViewPager();
					
					// If on favorite busses page
					if(viewPager.getCurrentItem() == Constants.PAGE_ID_FAVORITE_BUSSES)
					{
						// Reload list of all busses
						main.onListOfBussesLoaded();
					}
					
					// Reload favorite busses
					main.onFavoriteBussesLoaded();
					
					if(isChecked)
					{
						db = BusDatabase.getDatabase(myContext);
						Bus newBus = db.get(bus.getNumber());
						db.closeDatabase();
						
						if(newBus.getTimesH() == null)
						{
							if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Bus is favorited. Downloading times for H...");
							if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
								new GetBusTimesPageTask(myContext, newBus, BusTimeTypes.WEEK_DAY).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
							else
								new GetBusTimesPageTask(myContext, newBus, BusTimeTypes.WEEK_DAY).execute();
						}
						if(newBus.getTimesC() == null)
						{
							if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Bus is favorited. Downloading times for C...");
							if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
								new GetBusTimesPageTask(myContext, newBus, BusTimeTypes.SATURDAY).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
							else
								new GetBusTimesPageTask(myContext, newBus, BusTimeTypes.SATURDAY).execute();
						}
						if(newBus.getTimesP() == null)
						{
							if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Bus is favorited. Downloading times for P...");
							if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
								new GetBusTimesPageTask(myContext, newBus, BusTimeTypes.SUNDAY).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
							else
								new GetBusTimesPageTask(myContext, newBus, BusTimeTypes.SUNDAY).execute();
						}
					}
				}
			}
		});
		
		// Set the values of each component using the references in holder 
		holder.favorite.setChecked(bus.isFavorited());
		holder.number.setText("" + bus.getNumber());
		holder.name.setText(bus.getSource() + " - " + bus.getDestination());
		
		return row;
	}
	
	/** Updates the section list so that sections can be seen during fast
	 * scrolling */
	public void updateSectionlist()
	{
		myIndexer = new HashMap<String, Integer>();
		int size = myOriginalItems.size();

		// Put each bus and it's index in the map
		for(int i = 0; i < size; i++)
		{
			myIndexer.put("" + myOriginalItems.get(i).getNumber(), i);
		}

		Set<String> sectionLetters = myIndexer.keySet();

		ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);

		// Sort busses according to their numbers using a custom comparator
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

		// Update sections
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