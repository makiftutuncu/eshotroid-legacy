package com.mehmetakiftutuncu.eshotroid.model;

/**
 * A model class for times of a bus for a single run
 * 
 * @author Mehmet Akif Tütüncü
 */
public class BusTime
{
	private String timeFromSource;
	private String timeFromDestination;
	
	public BusTime(String timeFromSource, String timeFromDestination)
	{
		setTimeFromSource(timeFromSource);
		setTimeFromDestination(timeFromDestination);
	}

	public String getTimeFromSource()
	{
		return timeFromSource;
	}

	public void setTimeFromSource(String timeFromSource)
	{
		this.timeFromSource = timeFromSource;
	}

	public String getTimeFromDestination()
	{
		return timeFromDestination;
	}

	public void setTimeFromDestination(String timeFromDestination)
	{
		this.timeFromDestination = timeFromDestination;
	}
	
	@Override
	public String toString()
	{
		return String.format("%s - %s", getTimeFromSource(), getTimeFromDestination());
	}
}