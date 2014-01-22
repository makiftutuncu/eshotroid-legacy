package com.mehmetakiftutuncu.eshotroid.model;

/** A model class for times of a bus for a single run
 * 
 * @author mehmetakiftutuncu */
public class BusTime
{
	public static final String BUS_TIME_SEPARATOR = ",";
	
	private String timeFromSource;
	private String timeFromDestination;
	private boolean isWheelChairEnabledSource;
	private boolean isWheelChairEnabledDestination;
	
	public BusTime(String timeFromSource, String timeFromDestination,
			boolean isWheelChairEnabledSource, boolean isWheelChairEnabledDestination)
	{
		setTimeFromSource(timeFromSource);
		setTimeFromDestination(timeFromDestination);
		setWheelChairEnabledSource(isWheelChairEnabledSource);
		setWheelChairEnabledDestination(isWheelChairEnabledDestination);
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
	
	public boolean isWheelChairEnabledSource()
	{
		return isWheelChairEnabledSource;
	}
	
	public void setWheelChairEnabledSource(boolean isWheelChairEnabledSource)
	{
		this.isWheelChairEnabledSource = isWheelChairEnabledSource;
	}
	
	public boolean isWheelChairEnabledDestination()
	{
		return isWheelChairEnabledDestination;
	}
	
	public void setWheelChairEnabledDestination(boolean isWheelChairEnabledDestination)
	{
		this.isWheelChairEnabledDestination = isWheelChairEnabledDestination;
	}
	
	@Override
	public String toString()
	{
		return String.format("%s - %s", getTimeFromSource(), getTimeFromDestination());
	}
}