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
}