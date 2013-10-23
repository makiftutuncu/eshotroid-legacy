package com.mehmetakiftutuncu.eshotroid;

/**
 * Constant definitions
 * 
 * @author Mehmet Akif Tütüncü
 */
public class Constants
{
	/**	URL of the mobile ESHOT web site to which requests will be sent to get busses */
	public static final String BUSSES_URL = "http://mobil.eshot.gov.tr/Ulasim.aspx";
	/**	URL of the mobile ESHOT web site to which requests will be sent to get times of a specific bus */
	public static final String BUS_TIMES_URL = "http://mobil.eshot.gov.tr/Saatler.aspx";
	
	/**	Type parameter of the {@link BUS_TIMES_URL} */
	public static final String TYPE_PARAMETER = "tip";
	/**	Number parameter of the {@link BUS_TIMES_URL} */
	public static final String NUMBER_PARAMETER = "hatno";
	
	/**	Extra tag for the type parameter of the {@link BUS_TIMES_URL} */
	public static final String TYPE_EXTRA = "type";
	/**	Extra tag for the bus number */
	public static final String BUS_NUMBER_EXTRA = "busNumber";
	
	/**	Tag for the flag indicating main activity was the run before */
	public static final String ISRUNBEFORE_MAIN = "isRunBefore_main";
	/**	Tag for the flag indicating times activity was the run before */
	public static final String ISRUNBEFORE_TIMES = "isRunBefore_times";
	
	/**	URL of Eshotroid in Play Store */
	public static final String APPLICATION_URL = "market://details?id=com.mehmetakiftutuncu.eshotroid";
	/**	E-mail contact address of the developer */
	public static final String CONTACT = "m.akif.tutuncu@gmail.com";
	/**	URL of developer web site */
	public static final String WEBSITE_URL = "http://mehmetakiftutuncu.blogspot.com";
}