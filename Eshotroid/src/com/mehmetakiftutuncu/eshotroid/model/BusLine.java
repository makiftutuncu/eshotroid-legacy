package com.mehmetakiftutuncu.eshotroid.model;

import com.mehmetakiftutuncu.eshotroid.Constants;

/**
 * A model class for a bus line
 * 
 * @author Mehmet Akif Tütüncü
 */
public class BusLine
{
	private String number;
	private String name;
	
	public BusLine(String number, String name)
	{
		setNumber(number);
		setName(name);
	}

	public String getNumber()
	{
		return number;
	}
	
	public void setNumber(String number)
	{
		this.number = number;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return String.format("%s%s%s", getNumber(), Constants.BUS_LINE_SEPERATOR, getName());
	}
}