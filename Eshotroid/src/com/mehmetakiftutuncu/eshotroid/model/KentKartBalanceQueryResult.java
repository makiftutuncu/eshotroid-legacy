package com.mehmetakiftutuncu.eshotroid.model;

/** A model class for the result of a Kent Kart balance query
 * 
 * @author mehmetakiftutuncu */
public class KentKartBalanceQueryResult
{
	private String balance;
	private String lastLoadTime;
	private String lastLoadAmount;
	private String lastUseTime;
	private String lastUseAmount;
	
	public KentKartBalanceQueryResult(String balance, String lastLoadTime,
			String lastLoadAmount, String lastUseTime, String lastUseAmount)
	{
		setBalance(balance);
		setLastLoadTime(lastLoadTime);
		setLastLoadAmount(lastLoadAmount);
		setLastUseTime(lastUseTime);
		setLastUseAmount(lastUseAmount);
	}
	
	public String getBalance()
	{
		return balance;
	}
	
	public void setBalance(String balance)
	{
		this.balance = balance;
	}
	
	public String getLastLoadTime()
	{
		return lastLoadTime;
	}
	
	public void setLastLoadTime(String lastLoadTime)
	{
		this.lastLoadTime = lastLoadTime;
	}
	
	public String getLastLoadAmount()
	{
		return lastLoadAmount;
	}
	
	public void setLastLoadAmount(String lastLoadAmount)
	{
		this.lastLoadAmount = lastLoadAmount;
	}
	
	public String getLastUseTime()
	{
		return lastUseTime;
	}
	
	public void setLastUseTime(String lastUseTime)
	{
		this.lastUseTime = lastUseTime;
	}
	
	public String getLastUseAmount()
	{
		return lastUseAmount;
	}
	
	public void setLastUseAmount(String lastUseAmount)
	{
		this.lastUseAmount = lastUseAmount;
	}
}