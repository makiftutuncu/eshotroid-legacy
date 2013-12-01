package com.mehmetakiftutuncu.eshotroid.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mehmetakiftutuncu.eshotroid.model.Bus;
import com.mehmetakiftutuncu.eshotroid.model.BusTime;

/**
 * A database class using singleton design pattern
 * 
 * @author Mehmet Akif Tütüncü
 */
public class MyDatabase implements IDatabaseOperations<Bus>
{
	/** Key for the number of a bus */
	public static final String KEY_BUSSES_NUMBER = "busNumber";
	/** Key for the source of a bus */
	public static final String KEY_BUSSES_SOURCE = "busSource";
	/** Key for the destination of a bus */
	public static final String KEY_BUSSES_DESTINATION = "busDestination";
	/** Key for the route of a bus */
	public static final String KEY_BUSSES_ROUTE = "busRoute";
	/** Key for the favorited flag of a bus */
	public static final String KEY_BUSSES_ISFAVORITED = "busIsFavorited";
	/** Key for the weekday times of a bus */
	public static final String KEY_BUSSES_TIMESH = "busTimesH";
	/** Key for the saturday times of a bus */
	public static final String KEY_BUSSES_TIMESC = "busTimesC";
	/** Key for the sunday times of a bus */
	public static final String KEY_BUSSES_TIMESP = "busTimesP";
	
	/** Name of the database */
	private static final String DATABASE_NAME = "eshotroid_database";
	/** Version of the database */
	private static final int DATABASE_VERSION = 1;
	
	/** Name of the busses table */
	private static final String TABLE_NAME_BUSSES = "busses";
	
	/** SQL string for creating busses table */
	private static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME_BUSSES + " ("
											+ KEY_BUSSES_NUMBER + " INTEGER PRIMARY KEY NOT NULL, "
											+ KEY_BUSSES_SOURCE + " TEXT NOT NULL, "
											+ KEY_BUSSES_DESTINATION + " TEXT NOT NULL, "
											+ KEY_BUSSES_ROUTE + " TEXT, "
											+ KEY_BUSSES_ISFAVORITED + " INTEGER NOT NULL, "
											+ KEY_BUSSES_TIMESH + " TEXT, "
											+ KEY_BUSSES_TIMESC + " TEXT, "
											+ KEY_BUSSES_TIMESP + " TEXT);";
	
	/**	SQLiteDatabase object which is the actual database object to do database operations */
	private SQLiteDatabase myDatabase;
	
	/**	Flag if the database is opened */
	protected boolean isOpened = false;
	
	/**	MyDatabaseHelper instance for creating/opening the database */
	private MyDatabaseHelper myHelper;
	
	/**
	 * Tag for debugging
	 */
	public static final String LOG_TAG = "Eshotroid_Database";
	
	private static class MyDatabaseHelper extends SQLiteOpenHelper
	{
		public MyDatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(CREATE_SQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_BUSSES);
			onCreate(db);
		}
	}
	
	/**
	 * Constructor setting the context
	 * 
	 * @param context The context to be set
	 */
	public MyDatabase(Context context)
	{
		myHelper = new MyDatabaseHelper(context);
	}
	
	/**
	 * Creates and/or opens a writable database
	 * 
	 * @return An instance of this class
	 * 
	 * @throws SQLException <li>If database cannot be opened
	 */
	public MyDatabase openDB() throws SQLException
	{
		isOpened = true;
		
		myDatabase = myHelper.getWritableDatabase();
		
		return this;
	}
	
	/**
	 * Closes the opened database
	 */
	public void closeDB()
	{
		isOpened = false;
		
		myHelper.close();
	}
	
	/**
	 * Gets the database of this instance
	 * 
	 * @return The database of this instance
	 */
	protected SQLiteDatabase getDatabase()
	{
		return myDatabase;
	}

	@Override
	public boolean addOrUpdate(Bus entry)
	{
		/* Result flag */
		boolean result = true;
		
		/* Set the row */
		ContentValues values = new ContentValues();
		values.put(KEY_BUSSES_NUMBER, entry.getNumber());
		values.put(KEY_BUSSES_SOURCE, entry.getSource());
		values.put(KEY_BUSSES_DESTINATION, entry.getDestination());
		values.put(KEY_BUSSES_ROUTE, (entry.getRoute() != null ? entry.getRoute() : ""));
		values.put(KEY_BUSSES_ISFAVORITED, (entry.isFavorited() ? 1 : 0));
		values.put(KEY_BUSSES_TIMESH, (entry.getTimesH() != null ? new Gson().toJson(entry.getTimesH()) : ""));
		values.put(KEY_BUSSES_TIMESC, (entry.getTimesC() != null ? new Gson().toJson(entry.getTimesC()) : ""));
		values.put(KEY_BUSSES_TIMESP, (entry.getTimesP() != null ? new Gson().toJson(entry.getTimesP()) : ""));
		
		try
		{
			/* Check if the entry is already in the database */
			if(get(entry.getNumber()) != null)
			{
				/* Updating */
				getDatabase().update(TABLE_NAME_BUSSES, values, KEY_BUSSES_NUMBER + "=" + entry.getNumber(), null);
			}
			else
			{
				/* Adding */
				getDatabase().insert(TABLE_NAME_BUSSES, null, values);
			}
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, "Error occurred while adding to/updating database!", e);
			
			result = false;
		}
		
		/* Return the result */
		return result;
	}

	@Override
	public ArrayList<Bus> get()
	{
		/* Resulting list */
		ArrayList<Bus> list = new ArrayList<Bus>();
		
		/* Columns to select which are all except for route and times because they are not needed in the bus list */
		String[] columns = new String[]
		{
			KEY_BUSSES_NUMBER,
			KEY_BUSSES_SOURCE,
			KEY_BUSSES_DESTINATION,
			KEY_BUSSES_ISFAVORITED
		};
		
		/* Cursor to query the database */
		Cursor cursor = getDatabase().query(TABLE_NAME_BUSSES, columns, null, null, null, null, null);
		
		/* For every item that the cursor finds until the end */
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
		{
			/* Fields of the item */
			int number;
			String source;
			String destination;
			boolean isFavorited;
			
			/* Get values for all fields */
			number = cursor.getInt(cursor.getColumnIndex(KEY_BUSSES_NUMBER));
			source = cursor.getString(cursor.getColumnIndex(KEY_BUSSES_SOURCE));
			destination = cursor.getString(cursor.getColumnIndex(KEY_BUSSES_DESTINATION));
			isFavorited = (cursor.getInt(cursor.getColumnIndex(KEY_BUSSES_ISFAVORITED)) == 1);
			
			/* Add the item to the resulting list */
			list.add(new Bus(number, source, destination, null, isFavorited, null, null, null));
		}
		
		/* Return the result */
		return list;
	}

	@Override
	public Bus get(int number)
	{
		/* Resulting item */
		Bus item = null;
		
		/* Columns to select which are all except for number because we know number */
		String[] columns = new String[]
		{
			KEY_BUSSES_SOURCE,
			KEY_BUSSES_DESTINATION,
			KEY_BUSSES_ROUTE,
			KEY_BUSSES_ISFAVORITED,
			KEY_BUSSES_TIMESH,
			KEY_BUSSES_TIMESC,
			KEY_BUSSES_TIMESP
		};
		
		/* Cursor to query the database */
		Cursor cursor = getDatabase().query(TABLE_NAME_BUSSES, columns, KEY_BUSSES_NUMBER + "=" + number, null, null, null, null);
		
		/* If successfully queried */
		if(cursor != null)
		{
			/* If any match is found */
			if(cursor.getCount() > 0)
			{
				/* Go to the first match */
				cursor.moveToFirst();
				
				/* Fields of the item */
				String source;
				String destination;
				String route;
				boolean isFavorited;
				ArrayList<BusTime> timesH;
				ArrayList<BusTime> timesC;
				ArrayList<BusTime> timesP;
				
				/* Get values for all fields */
				source = cursor.getString(cursor.getColumnIndex(KEY_BUSSES_SOURCE));
				destination = cursor.getString(cursor.getColumnIndex(KEY_BUSSES_DESTINATION));
				route = cursor.getString(cursor.getColumnIndex(KEY_BUSSES_ROUTE));
				isFavorited = (cursor.getInt(cursor.getColumnIndex(KEY_BUSSES_ISFAVORITED)) == 1);
				timesH = new Gson().fromJson(cursor.getString(cursor.getColumnIndex(KEY_BUSSES_TIMESH)), new TypeToken<ArrayList<BusTime>>(){}.getType());
				timesC = new Gson().fromJson(cursor.getString(cursor.getColumnIndex(KEY_BUSSES_TIMESC)), new TypeToken<ArrayList<BusTime>>(){}.getType());
				timesP = new Gson().fromJson(cursor.getString(cursor.getColumnIndex(KEY_BUSSES_TIMESP)), new TypeToken<ArrayList<BusTime>>(){}.getType());
				
				/* Generate the resulting item */
				item = new Bus(number, source, destination, route, isFavorited, timesH, timesC, timesP);
			}
		}
		
		/* Return the result */
		return item;
	}

	@Override
	public boolean delete(int number)
	{
		/* Result flag */
		boolean result = true;
		
		/* Delete */
		int affectedRows = getDatabase().delete(TABLE_NAME_BUSSES, KEY_BUSSES_NUMBER + "=" + number, null);
		if(affectedRows == 0)
		{
			result = false;
		}
		
		/* Return the result */
		return result;
	}
}