package com.mehmetakiftutuncu.eshotroid.model;

import java.util.ArrayList;

/**
 * A model class for a bus
 * 
 * @author Mehmet Akif Tütüncü
 */
public class Bus
{
	private int number;
	private String source;
	private String destination;
	private String route;
	private boolean isFavorited;
	private ArrayList<BusTime> timesH;
	private ArrayList<BusTime> timesC;
	private ArrayList<BusTime> timesP;
	
	public Bus(int number, String source, String destination, String route, boolean isFavorited, ArrayList<BusTime> timesH, ArrayList<BusTime> timesC, ArrayList<BusTime> timesP)
	{
		setNumber(number);
		setSource(source);
		setDestination(destination);
		setRoute(route);
		setFavorited(isFavorited);
		setTimesH(timesH);
		setTimesC(timesC);
		setTimesP(timesP);
	}

	public int getNumber()
	{
		return number;
	}
	
	public void setNumber(int number)
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
	
	public boolean isFavorited()
	{
		return isFavorited;
	}
	
	public void setFavorited(boolean isFavorited)
	{
		this.isFavorited = isFavorited;
	}
	
	public ArrayList<BusTime> getTimesH()
	{
		return timesH;
	}
	
	public void setTimesH(ArrayList<BusTime> timesH)
	{
		this.timesH = timesH;
	}
	
	public ArrayList<BusTime> getTimesC()
	{
		return timesC;
	}
	
	public void setTimesC(ArrayList<BusTime> timesC)
	{
		this.timesC = timesC;
	}
	
	public ArrayList<BusTime> getTimesP()
	{
		return timesP;
	}
	
	public void setTimesP(ArrayList<BusTime> timesP)
	{
		this.timesP = timesP;
	}
}