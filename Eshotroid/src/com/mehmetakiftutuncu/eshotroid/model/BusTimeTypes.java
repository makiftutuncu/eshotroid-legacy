package com.mehmetakiftutuncu.eshotroid.model;

import com.mehmetakiftutuncu.eshotroid.R;

/** An enum for types of times of a bus
 * 
 * @author mehmetakiftutuncu */
public enum BusTimeTypes
{
	WEEK_DAY("H", R.string.busTimes_h),
	SATURDAY("C", R.string.busTimes_c),
	SUNDAY("P", R.string.busTimes_p);
	
	private String code;
	private int nameResourceId;
	
	private BusTimeTypes(String code, int nameResourceId)
	{
		this.code = code;
		this.nameResourceId = nameResourceId;
	}
	
	public String getCode()
	{
		return code;
	}
	
	public int getNameResourceId()
	{
		return nameResourceId;
	}
}