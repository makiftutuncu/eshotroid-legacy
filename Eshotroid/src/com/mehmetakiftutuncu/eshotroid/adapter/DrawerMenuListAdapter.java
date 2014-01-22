package com.mehmetakiftutuncu.eshotroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mehmetakiftutuncu.eshotroid.R;

/** Drawer menu list adapter class is the adapter that supplies the drawer list
 * with items and their icons
 * 
 * @author mehmetakiftutuncu */
public class DrawerMenuListAdapter extends ArrayAdapter<String>
{
	/** Tag for debugging */
	public static final String LOG_TAG = "Eshotroid_DrawerMenuListAdapter";
	
	/** Holder class for each item */
	static class ViewHolder
	{
		public ImageView icon;
		public TextView name;
	}

	/** Constructor of the adapter */
	public DrawerMenuListAdapter(Context context, String[] items)
	{
		super(context, R.layout.item_drawable_menu, items);
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
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			// Inflate and create the view
			row = inflater.inflate(R.layout.item_drawable_menu, parent, false);
			
			/* Create a new holder for this view to hold references of it's
			 * components */
			ViewHolder myViewHolder = new ViewHolder();
			
			// Find references for components and store them in holder
			myViewHolder.icon = (ImageView) row.findViewById(R.id.imageView_item_drawableMenu_icon);
			myViewHolder.name = (TextView) row.findViewById(R.id.textView_item_drawableMenu_name);
			
			/* Add the holder to the view as a tag so references can be accessed
			later */
			row.setTag(myViewHolder);
		}
		
		// Get the holder of this view to access it's components
		ViewHolder holder = (ViewHolder) row.getTag();
		
		// Set content
		holder.name.setText(getItem(position));
		holder.icon.setImageResource(getIcon(getItem(position)));
		
		return row;
	}

	/** Gets the icon resource id for the given item name */
	private int getIcon(String item)
	{
		if(item.equals(getContext().getString(R.string.drawerMenu_rate)))
			return R.drawable.ic_action_good;
		else if(item.equals(getContext().getString(R.string.drawerMenu_contact)))
			return R.drawable.ic_action_email;
		else if(item.equals(getContext().getString(R.string.drawerMenu_website)))
			return R.drawable.ic_action_web_site;
		else if(item.equals(getContext().getString(R.string.drawerMenu_help)))
			return R.drawable.ic_action_help;
		else if(item.equals(getContext().getString(R.string.drawerMenu_about)))
			return R.drawable.ic_action_about;
		return 0;
	}
}