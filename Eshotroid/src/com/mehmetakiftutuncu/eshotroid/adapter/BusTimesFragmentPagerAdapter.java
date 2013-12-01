package com.mehmetakiftutuncu.eshotroid.adapter;

import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.mehmetakiftutuncu.eshotroid.BuildConfig;
import com.mehmetakiftutuncu.eshotroid.R;
import com.mehmetakiftutuncu.eshotroid.fragment.BusTimesFragment;
import com.mehmetakiftutuncu.eshotroid.utility.Constants;

/** A {@link FragmentPagerAdapter} that returns a fragment corresponding to one
 * of the sections/tabs/pages
 * 
 * @author mehmetakiftutuncu */
public class BusTimesFragmentPagerAdapter extends FragmentPagerAdapter
{
	/** {@link Context} of the activity that uses this adapter */
	private Context mContext;
	
	/** A reference to {@link BusTimesFragment} in the pager for times of H */
	private BusTimesFragment hBusTimesFragment;
	/** A reference to {@link BusTimesFragment} in the pager for times of C */
	private BusTimesFragment cBusTimesFragment;
	/** A reference to {@link BusTimesFragment} in the pager for times of P */
	private BusTimesFragment pBusTimesFragment;
	
	/** Tag for debugging */
	private static final String LOG_TAG = "Eshotroid_BusTimesFragment";
	
	/** Constructor of this adapter
	 * 
	 * @param context {@link BusTimesFragmentPagerAdapter#mContext}
	 * @param fm {@link FragmentManager} to manage fragments
	 */
	public BusTimesFragmentPagerAdapter(Context context, FragmentManager fm)
	{
		super(fm);
		
		mContext = context;
	}

	/** This method is called to instantiate the fragment for the given page
	 * 
	 * @param position Page number whose fragment will be returned */
	@Override
	public Fragment getItem(int position)
	{
		// Define a fragment
		Fragment fragment = null;
		Bundle arguments = new Bundle();
		
		// Decide which fragment to use
		switch(position)
		{
			case Constants.PAGE_ID_BUS_TIMES_H:
				if(hBusTimesFragment == null)
				{
					if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Creating a new BusTimesFragment for H...");
					hBusTimesFragment = new BusTimesFragment();
					
					arguments.putString(Constants.BUS_TIMES_TYPE_EXTRA, "H");
					hBusTimesFragment.setArguments(arguments);
				}
				
				if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Getting BusTimesFragment for H...");
				fragment = hBusTimesFragment;
				break;
			
			case Constants.PAGE_ID_BUS_TIMES_C:
				if(cBusTimesFragment == null)
				{
					if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Creating a new BusTimesFragment for C...");
					cBusTimesFragment = new BusTimesFragment();
					
					arguments.putString(Constants.BUS_TIMES_TYPE_EXTRA, "C");
					cBusTimesFragment.setArguments(arguments);
				}
				
				if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Getting BusTimesFragment for C...");
				fragment = cBusTimesFragment;
				break;
			
			case Constants.PAGE_ID_BUS_TIMES_P:
				if(pBusTimesFragment == null)
				{
					if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Creating a new BusTimesFragment for P...");
					pBusTimesFragment = new BusTimesFragment();
					
					arguments.putString(Constants.BUS_TIMES_TYPE_EXTRA, "P");
					pBusTimesFragment.setArguments(arguments);
				}
				
				if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Getting BusTimesFragment for P...");
				fragment = pBusTimesFragment;
				break;
		}
		
		// Return the result
		return fragment;
	}
	
	/** @return {@link BusTimesFragmentPagerAdapter#hBusTimesFragment} */
	public BusTimesFragment getHBusTimesFragment()
	{
		return (BusTimesFragment) getItem(Constants.PAGE_ID_BUS_TIMES_H);
	}
	
	/** @return {@link BusTimesFragmentPagerAdapter#cBusTimesFragment} */
	public BusTimesFragment getCBusTimesFragment()
	{
		return (BusTimesFragment) getItem(Constants.PAGE_ID_BUS_TIMES_C);
	}
	
	/** @return {@link BusTimesFragmentPagerAdapter#pBusTimesFragment} */
	public BusTimesFragment getPBusTimesFragment()
	{
		return (BusTimesFragment) getItem(Constants.PAGE_ID_BUS_TIMES_P);
	}

	/** Returns the number of pages */
	@Override
	public int getCount()
	{
		return 3;
	}
	
	/** Returns the number of pages
	 * 
	 * @param position Page number whose title will be returned */
	@Override
	public CharSequence getPageTitle(int position)
	{
		// Define a result
		String result = null;
		
		// Decide which title to use
		switch(position)
		{
			case Constants.PAGE_ID_BUS_TIMES_H:
				result = mContext.getString(R.string.busTimes_h).toUpperCase(Locale.getDefault());
				break;
			
			case Constants.PAGE_ID_BUS_TIMES_C:
				result = mContext.getString(R.string.busTimes_c).toUpperCase(Locale.getDefault());
				break;
			
			case Constants.PAGE_ID_BUS_TIMES_P:
				result = mContext.getString(R.string.busTimes_p).toUpperCase(Locale.getDefault());
				break;
		}
		
		// Return the result
		return result;
	}
}