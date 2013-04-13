package com.mehmetakiftutuncu.eshotroid;

/**
 * Constant definitions
 * 
 * @author Mehmet Akif Tütüncü
 */
public class Constants
{
	/**	Package name of the application */
	public static String PACKAGE_NAME;
	
	/**	URL of the mobile ESHOT web site to which requests will be sent to get bus lines */
	public static final String BUS_LINES_URL = "http://mobil.eshot.gov.tr/Ulasim.aspx";
	/**	URL of the mobile ESHOT web site to which requests will be sent to get times of a specific bus line */
	public static final String BUS_TIMES_URL = "http://mobil.eshot.gov.tr/Saatler.aspx";
	
	/**	Type parameter of the {@link BUS_TIMES_URL} */
	public static final String TYPE_PARAMETER = "tip";
	/**	Line parameter of the {@link BUS_TIMES_URL} */
	public static final String LINE_PARAMETER = "hatno";
}