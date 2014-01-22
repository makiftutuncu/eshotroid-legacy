package com.mehmetakiftutuncu.eshotroid.utility;

/** Some constant definitions used across the application
 * 
 * @author mehmetakiftutuncu */
public class Constants
{
	/** Page id of the favorite busses page */
	public static final int PAGE_ID_FAVORITE_BUSSES = 0;
	/** Page id of the all busses page */
	public static final int PAGE_ID_ALL_BUSSES = 1;
	/** Page id of the Kent Kart balance page */
	public static final int PAGE_ID_KENT_KART_BALANCE = 2;
	
	/** Page id of the bus times for H */
	public static final int PAGE_ID_BUS_TIMES_H = 0;
	/** Page id of the bus times for C */
	public static final int PAGE_ID_BUS_TIMES_C = 1;
	/** Page id of the bus times for P */
	public static final int PAGE_ID_BUS_TIMES_P = 2;
	
	/**	Preference tag for the status of setup wizard */
	public static final String IS_SETUP_WIZARD_FINISHED = "isSetupWizardFinished";
	
	/**	Extra tag for the status of help */
	public static final String HELP_FROM_SETUP_WIZARD_EXTRA= "helpFromSetupWizardExtra";
	
	/**	URL of the page having the list of busses */
	public static final String LIST_OF_BUSSES_URL = "http://mobil.eshot.gov.tr/Ulasim.aspx";
	/**	URL of the page to get times of a specific bus */
	public static final String BUS_TIMES_URL = "http://mobil.eshot.gov.tr/Saatler.aspx";
	
	/**	Extra tag for the bus number */
	public static final String BUS_NUMBER_EXTRA = "busNumber";
	/**	Extra tag for the bus times type */
	public static final String BUS_TIMES_TYPE_EXTRA = "type";
	
	/**	Type parameter of the {@link BUS_TIMES_URL} */
	public static final String TYPE_PARAMETER = "tip";
	/**	Number parameter of the {@link BUS_TIMES_URL} */
	public static final String NUMBER_PARAMETER = "hatno";
	
	/**	URL of Eshotroid in Play Store */
	public static final String APPLICATION_URL = "market://details?id=com.mehmetakiftutuncu.eshotroid";
	/**	E-mail contact address of the developer */
	public static final String CONTACT = "m.akif.tutuncu@gmail.com";
	/**	URL of developer web site */
	public static final String WEBSITE_URL = "http://mehmetakiftutuncu.blogspot.com";
}