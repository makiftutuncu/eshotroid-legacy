package com.mehmetakiftutuncu.eshotroid.model;

/**
 * A model class for a bus line
 * 
 * @author Mehmet Akif Tütüncü
 */
public class BusLine
{
	private String number;
	private String source;
	private String destination;
	private String route;
	
	public BusLine(String number, String source, String destination, String route)
	{
		setNumber(number);
		setSource(source);
		setDestination(destination);
		setRoute(route);
	}

	public String getNumber()
	{
		return number;
	}
	
	public void setNumber(String number)
	{
		this.number = number;
	}
	
	public String getSource()
	{
		return source;
	}
	
	public void setSource(String source)
	{
		this.source = source;
	}
	
	public String getDestination()
	{
		return destination;
	}
	
	public void setDestination(String destination)
	{
		this.destination = destination;
	}
	
	public String getRoute()
	{
		return route;
	}
	
	public void setRoute(String route)
	{
		this.route = route;
	}

	@Override
	public String toString()
	{
		return String.format("%s: %s - %s", getNumber(), getSource(), getDestination());
	}
}