package com.mehmetakiftutuncu.eshotroid.model;

/** A model class for a Kent Kart
 * 
 * @author mehmetakiftutuncu */
public class KentKart
{
	private int id;
	private String name;
	private String aliasNo1;
	private String aliasNo2;
	private String aliasNo3;
	
	public KentKart(int id, String name, String aliasNo1, String aliasNo2, String aliasNo3)
	{
		setId(id);
		setName(name);
		setAliasNo1(aliasNo1);
		setAliasNo2(aliasNo2);
		setAliasNo3(aliasNo3);
	}
	
	public KentKart(String name, String aliasNo1, String aliasNo2, String aliasNo3)
	{
		this(0, name, aliasNo1, aliasNo2, aliasNo3);
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getAliasNo1()
	{
		return aliasNo1;
	}
	
	public void setAliasNo1(String aliasNo1)
	{
		this.aliasNo1 = aliasNo1;
	}
	
	public String getAliasNo2()
	{
		return aliasNo2;
	}
	
	public void setAliasNo2(String aliasNo2)
	{
		this.aliasNo2 = aliasNo2;
	}
	
	public String getAliasNo3()
	{
		return aliasNo3;
	}
	
	public void setAliasNo3(String aliasNo3)
	{
		this.aliasNo3 = aliasNo3;
	}
	
	@Override
	public String toString()
	{
		if(name == null || name.equals(""))
		{
			return aliasNo1 + " " + aliasNo2 + " " + aliasNo3;
		}
		
		return name + " - " + aliasNo1 + " " + aliasNo2 + " " + aliasNo3;
	}
}