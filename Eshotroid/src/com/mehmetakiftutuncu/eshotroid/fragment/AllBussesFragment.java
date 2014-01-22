package com.mehmetakiftutuncu.eshotroid.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mehmetakiftutuncu.eshotroid.BuildConfig;
import com.mehmetakiftutuncu.eshotroid.R;
import com.mehmetakiftutuncu.eshotroid.adapter.BusListAdapter;

/** Fragment of all busses page
 * 
 * @author mehmetakiftutuncu */
public class AllBussesFragment extends Fragment
{
	/** Interface for ensuring the implementation of loading and selecting
	 * busses */
	public interface AllBussesListener
	{
		/**	Reads busses from the database if possible, if not tries to download
		 * them */
		public void onListOfBussesLoaded();
		
		/**	Sets the item click listener of the given list view
		 * 
		 * @param listView {@link AllBussesFragment#listView} */
		public void onSetBusSelected(ListView listView);
	}
	
	/** Reference to the activity that implements {@link AllBussesListener} */
	private AllBussesListener listenerActivity;
	
	/** {@link ListView} in which the list of all busses will be shown */
	private ListView listView;
	
	/** {@link BusListAdapter} of the list of busses */
	private BusListAdapter busListAdapter;
	
	/** Flag indicating if the list of all busses is currently being refreshed */
	private boolean isRefreshing = false;
	
	/** Tag for debugging */
	private static final String LOG_TAG = "Eshotroid_AllBussesFragment";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Log.d(LOG_TAG, "Creating fragment UI...");
		
		// Create the view of this fragment
		View view = inflater.inflate(R.layout.fragment_all_busses, container, false);
		
		// Get the list view
		listView = (ListView) view.findViewById(R.id.listView_allBusses);
		
		return view;
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		// Ensure attached activity implements the interface
		try
		{
			if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Attaching AllBussesFragment...");
			
			listenerActivity = (AllBussesListener) activity;
		}
		catch(Exception e)
		{
			throw new ClassCastException(activity.getClass().getName() + " should implement AllBussesListener!");
		}
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		// Load busses
		listenerActivity.onListOfBussesLoaded();
		
		// Set item click listener
		listenerActivity.onSetBusSelected(listView);
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
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Setting bus list adapter...");
		this.busListAdapter = busListAdapter;
		listView.setAdapter(busListAdapter);
	}
	
	/** Gets the adapter of this fragment
	 * 
	 * @return {@link AllBussesFragment#busListAdapter} */
	public BusListAdapter getBusListAdapter()
	{
		return busListAdapter;
	}
	
	/** @return {@link AllBussesFragment#isRefreshing} */
	public boolean isRefreshing()
	{
		return isRefreshing;
	}
	
	/** Sets {@link AllBussesFragment#isRefreshing} as given value */
	public void setRefreshing(boolean isRefreshing)
	{
		this.isRefreshing = isRefreshing;
	}
}