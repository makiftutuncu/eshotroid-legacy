package com.mehmetakiftutuncu.eshotroid.model;

import java.util.ArrayList;

/**
 * A model class for all times of a bus line
 * 
 * @author Mehmet Akif Tütüncü
 */
public class BusLineTimes
{
	private BusLine line;
	private ArrayList<BusTime> times;
	
	public BusLineTimes(BusLine line, ArrayList<BusTime> times)
	{
		setLine(line);
		setTimes(times);
	}

	public BusLine getLine()
	{
		return line;
	}
	
	public void setLine(BusLine line)
	{
		this.line = line;
	}
	
	public ArrayList<BusTime> getTimes()
	{
		return times;
	}
	
	public void setTimes(ArrayList<BusTime> times)
	{
		this.times = times;
	}
}