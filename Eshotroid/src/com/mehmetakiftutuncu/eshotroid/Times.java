package com.mehmetakiftutuncu.eshotroid;

import android.app.Activity;
import android.os.Bundle;

/**
 * Times activity of the application
 * 
 * @author Mehmet Akif Tütüncü
 */
public class Times extends Activity
{
	/**
	 * Tag for debugging
	 */
	public static final String LOG_TAG = "Eshotroid_Times";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.times);
		
		initialize();
	}
	
	private void initialize()
	{
	}
}