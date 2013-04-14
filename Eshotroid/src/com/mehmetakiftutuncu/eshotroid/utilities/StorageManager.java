package com.mehmetakiftutuncu.eshotroid.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mehmetakiftutuncu.eshotroid.Constants;
import com.mehmetakiftutuncu.eshotroid.model.BusLine;
import com.mehmetakiftutuncu.eshotroid.model.BusTime;

/**
 * A utility class for storing and reading processed data
 * 
 * @author Mehmet Akif Tütüncü
 */
public class StorageManager
{
	/**	Path to the external storage of the device */
	public static final String EXTERNAL_STORAGE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/";
	
	/**	Path to the external storage of the device */
	public static final String BUS_LINES_FILE = "/buslines.json";
	
	/**
	 * Tag for debugging
	 */
	public static final String LOG_TAG = "Eshotroid_StorageManager";
	
	/**
	 * Stores bus lines to the external storage of the device
	 * 
	 * @param busLines Bus lines to store
	 * 
	 * @return true if successful, false if any error occurred
	 */
	public static boolean storeBusLines(ArrayList<BusLine> busLines)
	{
		if(!isExternalStorageAvailable())
		{
			Log.e(LOG_TAG, "Cannot store bus lines! External storage is not available.");
			
			return false;
		}
		
		// Form the full path
		String path = EXTERNAL_STORAGE + Constants.PACKAGE_NAME + BUS_LINES_FILE;
		
		// Convert the data to JSON
		Gson gson = new Gson();
		String json = gson.toJson(busLines);
		
		// Try and write the data to the specified file
		try
		{
			new File(EXTERNAL_STORAGE + Constants.PACKAGE_NAME).mkdirs();
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			
			writer.write(json);
			
			writer.close();
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, "Error occured while storing bus lines!", e);
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * Reads bus lines from the external storage of the device
	 * 
	 * @return Bus lines, null if any error occurs
	 */
	public static ArrayList<BusLine> readBusLines()
	{
		if(!isExternalStorageAvailable())
		{
			Log.e(LOG_TAG, "Cannot read bus lines! External storage is not available.");
			
			return null;
		}
		
		// Form the full path
		String path = EXTERNAL_STORAGE + Constants.PACKAGE_NAME + BUS_LINES_FILE;
		
		// Try and read the data from the specified file
		try
		{
			if(!(new File(path).exists()))
			{
				Log.e(LOG_TAG, "Cannot read bus lines! Bus lines file doesn\'t exist.");
				
				return null;
			}
			
			BufferedReader reader = new BufferedReader(new FileReader(path));
			
			String json = "";
			
			while(reader.ready())
			{
				json += reader.readLine();
			}
			reader.close();
			
			// Convert the data from JSON
			Gson gson = new Gson();
			return gson.fromJson(json, new TypeToken<ArrayList<BusLine>>(){}.getType());
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, "Error occured while reading bus lines!", e);
			
			return null;
		}
	}
	
	/**
	 * Stores bus times of a bus to the external storage of the device
	 * 
	 * @param fileName Name of the file to store
	 * @param busTimes Bus times to store
	 * 
	 * @return true if successful, false if any error occurred
	 */
	public static boolean storeBusTimes(String fileName, ArrayList<BusTime> busTimes)
	{
		if(!isExternalStorageAvailable())
		{
			Log.e(LOG_TAG, "Cannot store bus times! External storage is not available.");
			
			return false;
		}
		
		// Form the full path
		String path = EXTERNAL_STORAGE + Constants.PACKAGE_NAME + "/" + fileName + ".json";
		
		// Convert the data to JSON
		Gson gson = new Gson();
		String json = gson.toJson(busTimes);
		
		// Try and write the data to the specified file
		try
		{
			new File(EXTERNAL_STORAGE + Constants.PACKAGE_NAME).mkdirs();
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			
			writer.write(json);
			
			writer.close();
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, "Error occured while storing bus times!", e);
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * Reads bus times of a bus from the external storage of the device
	 * 
	 * @param fileName Name of the file to read
	 * 
	 * @return Bus times, null if any error occurs
	 */
	public static ArrayList<BusTime> readBusTimes(String fileName)
	{
		if(!isExternalStorageAvailable())
		{
			Log.e(LOG_TAG, "Cannot read bus times! External storage is not available.");
			
			return null;
		}
		
		// Form the full path
		String path = EXTERNAL_STORAGE + Constants.PACKAGE_NAME + "/" + fileName + ".json";
		
		// Try and read the data from the specified file
		try
		{
			if(!(new File(path).exists()))
			{
				Log.e(LOG_TAG, "Cannot read bus times! Bus times file doesn\'t exist.");
				
				return null;
			}
			
			BufferedReader reader = new BufferedReader(new FileReader(path));
			
			String json = "";
			
			while(reader.ready())
			{
				json += reader.readLine();
			}
			reader.close();
			
			// Convert the data from JSON
			Gson gson = new Gson();
			return gson.fromJson(json, new TypeToken<ArrayList<BusTime>>(){}.getType());
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, "Error occured while reading bus times!", e);
			
			return null;
		}
	}
	
	/**
	 * Checks if the external storage is available to read/write
	 * 
	 * @return true if the external storage is available to read/write, false otherwise
	 */
	public static boolean isExternalStorageAvailable()
	{
		String state = Environment.getExternalStorageState();

		if(Environment.MEDIA_MOUNTED.equals(state))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}