package com.mehmetakiftutuncu.eshotroid.adapter;

import java.util.Locale;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.mehmetakiftutuncu.eshotroid.BuildConfig;
import com.mehmetakiftutuncu.eshotroid.R;
import com.mehmetakiftutuncu.eshotroid.fragment.AllBussesFragment;
import com.mehmetakiftutuncu.eshotroid.fragment.FavoriteBussesFragment;
import com.mehmetakiftutuncu.eshotroid.fragment.KentKartBalanceFragment;
import com.mehmetakiftutuncu.eshotroid.utility.Constants;

/** A {@link FragmentPagerAdapter} that returns a fragment corresponding to one
 * of the sections/tabs/pages
 * 
 * @author mehmetakiftutuncu */
public class MainPagesFragmentPagerAdapter extends FragmentPagerAdapter
{
	/** {@link Context} of the activity that uses this adapter */
	private Context mContext;
	
	/** A reference to {@link FavoriteBussesFragment} in the pager */
	private FavoriteBussesFragment favoriteBussesFragment;
	/** A reference to {@link AllBussesFragment} in the pager */
	private AllBussesFragment allBussesFragment;
	/** A reference to {@link KentKartBalanceFragment} in the pager */
	private KentKartBalanceFragment kentKartBalanceFragment;
	
	/** Tag for debugging */
	private static final String LOG_TAG = "Eshotroid_MainPagesFragmentPagerAdapter";
	
	/** Constructor of this adapter
	 * 
	 * @param context {@link MainPagesFragmentPagerAdapter#mContext}
	 * @param fm {@link FragmentManager} to manage fragments
	 */
	public MainPagesFragmentPagerAdapter(Context context, FragmentManager fm)
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
		
		// Decide which fragment to use
		switch(position)
		{
			case Constants.PAGE_ID_FAVORITE_BUSSES:
				if(favoriteBussesFragment == null)
				{
					if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Creating a new FavoriteBussesFragment...");
					favoriteBussesFragment = new FavoriteBussesFragment();
				}
				
				if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Getting FavoriteBussesFragment...");
				fragment = favoriteBussesFragment;
				break;
			
			case Constants.PAGE_ID_ALL_BUSSES:
				if(allBussesFragment == null)
				{
					if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Creating a new AllBussesFragment...");
					allBussesFragment = new AllBussesFragment();
				}
				
				if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Getting AllBussesFragment...");
				fragment = allBussesFragment;
				break;
			
			case Constants.PAGE_ID_KENT_KART_BALANCE:
				if(kentKartBalanceFragment == null)
				{
					if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Creating a new KentKartBalanceFragment...");
					kentKartBalanceFragment = new KentKartBalanceFragment();
				}
				
				if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Getting KentKartBalanceFragment...");
				fragment = kentKartBalanceFragment;
				break;
		}
		
		// Return the result
		return fragment;
	}
	
	/** @return {@link FavoriteBussesFragment} in the pager */
	public FavoriteBussesFragment getFavoriteBussesFragment()
	{
		return (FavoriteBussesFragment) getItem(Constants.PAGE_ID_FAVORITE_BUSSES);
	}
	
	/** @return {@link AllBussesFragment} in the pager */
	public AllBussesFragment getAllBussesFragment()
	{
		return (AllBussesFragment) getItem(Constants.PAGE_ID_ALL_BUSSES);
	}
	
	/** @return {@link KentKartBalanceFragment} in the pager */
	public KentKartBalanceFragment getKentKartBalanceFragment()
	{
		return (KentKartBalanceFragment) getItem(Constants.PAGE_ID_KENT_KART_BALANCE);
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
			case Constants.PAGE_ID_FAVORITE_BUSSES:
				result = mContext.getString(R.string.mainPages_favoriteBusses).toUpperCase(Locale.getDefault());
				break;
			
			case Constants.PAGE_ID_ALL_BUSSES:
				result = mContext.getString(R.string.mainPages_allBusses).toUpperCase(Locale.getDefault());
				break;
			
			case Constants.PAGE_ID_KENT_KART_BALANCE:
				result = mContext.getString(R.string.mainPages_kentKartBalance).toUpperCase(Locale.getDefault());
				break;
		}
		
		// Return the result
		return result;
	}
}