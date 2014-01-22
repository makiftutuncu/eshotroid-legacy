package com.mehmetakiftutuncu.eshotroid.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.mehmetakiftutuncu.eshotroid.BuildConfig;
import com.mehmetakiftutuncu.eshotroid.R;
import com.mehmetakiftutuncu.eshotroid.adapter.BusListAdapter;

/** Fragment of the favorite busses page
 * 
 * @author mehmetakiftutuncu */
public class FavoriteBussesFragment extends Fragment
{
	/** Interface for ensuring the implementation of loading and selecting
	 * busses */
	public interface FavoriteBussesListener
	{
		/**	Loads favorite busses */
		public void onFavoriteBussesLoaded();
		
		/**	Sets the item click listener of the given list view
		 * 
		 * @param listView {@link AllBussesFragment#listView} */
		public void onSetFavoritedBusSelected(ListView listView);
	}
	
	/** Reference to the activity that implements {@link FavoriteBussesListener} */
	private FavoriteBussesListener listenerActivity;
	
	/** {@link ListView} in which the list of favorite busses will be shown */
	private ListView listView;
	
	/** Information text that will be shown when there is no favorited bus */
	private TextView info;
	
	/** {@link BusListAdapter} of the list of favorite busses */
	private BusListAdapter busListAdapter;
	
	/** Tag for debugging */
	private static final String LOG_TAG = "Eshotroid_FavoriteBussesFragment";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Log.d(LOG_TAG, "Creating fragment UI...");
		
		// Create the view of this fragment
		View view = inflater.inflate(R.layout.fragment_favorite_busses, container, false);
		
		// Get the list view
		listView = (ListView) view.findViewById(R.id.listView_favoriteBusses);
		
		// Get info text
		info = (TextView) view.findViewById(R.id.textView_favoriteBusses_info);
		
		return view;
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		// Ensure attached activity implements the interface
		try
		{
			if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Attaching FavoriteBussesFragment...");
			
			listenerActivity = (FavoriteBussesListener) activity;
		}
		catch(Exception e)
		{
			throw new ClassCastException(activity.getClass().getName() + " should implement FavoriteBussesListener!");
		}
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		// Load favorited busses
		listenerActivity.onFavoriteBussesLoaded();
		
		// Set item click listener
		listenerActivity.onSetFavoritedBusSelected(listView);
	}
	
	/** Gets the ListView of this fragment
	 * 
	 * @return {@link AllBussesFragment#listView} */
	public ListView getListView()
	{
		return listView;
	}
	
	/** Sets the adapter of this fragment
	 * 
	 * @param busListAdapter New adapter */
	public void setBusListAdapter(BusListAdapter busListAdapter)
	{
		// If there are favorite busses
		if(busListAdapter != null && busListAdapter.getCount() > 0)
		{
			if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Setting favorite bus list adapter...");
			this.busListAdapter = busListAdapter;
			listView.setAdapter(busListAdapter);
			
			// Hide info text 
			info.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		}
		else
		{
			this.busListAdapter = null;
			listView.setAdapter(null);
			
			// Show info text 
			info.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		}
	}
	
	/** Gets the adapter of this fragment
	 * 
	 * @return {@link AllBussesFragment#busListAdapter} */
	public BusListAdapter getBusListAdapter()
	{
		return busListAdapter;
	}
}