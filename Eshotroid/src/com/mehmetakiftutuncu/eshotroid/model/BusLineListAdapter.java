package com.mehmetakiftutuncu.eshotroid.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;

public class BusLineListAdapter extends ArrayAdapter<BusLine> implements SectionIndexer
{
	private HashMap<String, Integer> alphaIndexer;
	private String[] sections;
	private ArrayList<BusLine> items;

	public BusLineListAdapter(Context context, ArrayList<BusLine> items)
	{
		super(context, android.R.layout.simple_list_item_1, items);
		
		this.items = items;

		updateSectionlist();
	}
	
	public void updateSectionlist()
	{
		alphaIndexer = new HashMap<String, Integer>();
		int size = items.size();

		for(int i = 0; i < size; i++)
		{
			alphaIndexer.put(items.get(i).getNumber(), i);
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