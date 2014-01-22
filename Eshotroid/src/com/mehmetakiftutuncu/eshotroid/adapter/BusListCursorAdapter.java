package com.mehmetakiftutuncu.eshotroid.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.mehmetakiftutuncu.eshotroid.BuildConfig;
import com.mehmetakiftutuncu.eshotroid.R;
import com.mehmetakiftutuncu.eshotroid.model.Bus;
import com.mehmetakiftutuncu.eshotroid.provider.EshotroidProvider;

/**
 * A cursor adapter that binds the busses with the bus list UI
 * 
 * @author mehmetakiftutuncu */
public class BusListCursorAdapter extends CursorAdapter
{
	/** Holder class for each item */
    static class Holder
    {
    	public int number;
    	public TextView numberTextView;
    	public TextView nameTextView;
    	public CheckBox favoriteCheckBox;
    }
    
	/** {@link Context} of the activity that uses this adapter */
	private Context mContext;
	
	/** {@link LayoutInflater} for creating new views */
	private LayoutInflater inflater;
	
	/** Tag for debugging */
    public static final String LOG_TAG = "Eshotroid_BusListAdapter";
	
	public BusListCursorAdapter(Context context)
	{
		super(context, null, false);
	
		mContext = context;
		
		// Get layout inflater
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor)
	{
		// Get the holder of this view
		Holder holder = (Holder) view.getTag();
		
		// Read values from cursor
		final Bus bus = Bus.fromCursor(cursor);
		
		// Set the values of each component using the references in holder
		holder.number = bus.getNumber();
		holder.numberTextView.setText("" + bus.getNumber());
		holder.nameTextView.setText(bus.getSource() + " - " + bus.getDestination());
		holder.favoriteCheckBox.setChecked(bus.isFavorited());
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent)
	{
		// Inflate a new view
		View view = inflater.inflate(R.layout.item_bus, parent, false);
		
		// Create a new holder for this view
		Holder newHolder = new Holder();
		
		// Keep the references to this view's components in the holder
		newHolder.favoriteCheckBox = (CheckBox) view.findViewById(R.id.checkBox_item_bus_isFavorited);
        newHolder.numberTextView = (TextView) view.findViewById(R.id.textView_item_bus_number);
        newHolder.nameTextView = (TextView) view.findViewById(R.id.textView_item_bus_name);
		
        // Set checked changed listener for the favorite attribute
        newHolder.favoriteCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
        	@Override
        	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        	{
        		View viewParent = (View) buttonView.getParent();
        		Holder holder = (Holder) viewParent.getTag();

        		if(holder != null)
        		{
        			int busNumber = holder.number;
        			
        			Cursor busCursor = mContext.getContentResolver().query(Uri.withAppendedPath(EshotroidProvider.Busses.CONTENT_URI, "" + busNumber), EshotroidProvider.Busses.ALL_COLUMNS, null, null, null);
        			if(busCursor != null && busCursor.getCount() > 0)
        			{
        				Bus bus = Bus.fromCursor(busCursor);
        				
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

            				// Update favorite status on database
            				mContext.getContentResolver().update(Uri.withAppendedPath(EshotroidProvider.Busses.CONTENT_URI, "" + bus.getNumber()), bus.toContentValues(), null, null);
            			}
        			}
        		}
        	}
        });
		
		/* Set the holder as the tag of this view object so it can be
		 * accessed later */ 
		view.setTag(newHolder);
		
		return view;
	}
}