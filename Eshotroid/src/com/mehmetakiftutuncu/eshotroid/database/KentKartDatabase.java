package com.mehmetakiftutuncu.eshotroid.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mehmetakiftutuncu.eshotroid.BuildConfig;
import com.mehmetakiftutuncu.eshotroid.model.KentKart;

/** A database class using singleton design pattern for Kent Kart information
 * 
 * @author mehmetakiftutuncu */
public class KentKartDatabase implements IDatabaseOperations<KentKart>
{
	/** Key for the id of a Kent Kart (This is row id in database) */
	public static final String KEY_KENT_KART_ID = "_id";
	/** Key for the name of a Kent Kart */
	public static final String KEY_KENT_KART_NAME = "name";
	/** Key for the alias no 1 of a Kent Kart */
	public static final String KEY_KENT_KART_ALIAS1 = "aliasNo1";
	/** Key for the alias no 2 of a Kent Kart */
	public static final String KEY_KENT_KART_ALIAS2 = "aliasNo2";
	/** Key for the alias no 3 of a Kent Kart */
	public static final String KEY_KENT_KART_ALIAS3 = "aliasNo3";
	
	/** Name of the database */
	private static final String DATABASE_NAME = "eshotroid_database_kentkart";
	/** Version of the database */
	private static final int DATABASE_VERSION = 1;
	
	/** Name of the Kent Kart table */
	private static final String TABLE_NAME_KENT_KART = "kentkart";
	
	/** SQL string for creating Kent Kart table */
	private static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME_KENT_KART
			+ " (" + KEY_KENT_KART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ KEY_KENT_KART_NAME + " TEXT NOT NULL, "
					+ KEY_KENT_KART_ALIAS1 + " TEXT NOT NULL, "
					+ KEY_KENT_KART_ALIAS2 + " TEXT NOT NULL, "
					+ KEY_KENT_KART_ALIAS3 + " TEXT NOT NULL);";
	
	/**	{@link SQLiteDatabase} object which is the actual database object to do
	 * database operations */
	private static SQLiteDatabase myDatabase;
	
	/**	Flag if the database is opened */
	protected static boolean isOpened = false;
	
	/**	{@link KentKartDatabaseHelper} instance for creating/opening the database */
	private static KentKartDatabaseHelper myHelper;
	
	/**	An instance of this database */
	private static KentKartDatabase myInstance;
	
	/** Tag for debugging */
	private static final String LOG_TAG = "Eshotroid_KentKartDatabase";
	
	/** A database helper class for creating database
	 * 
	 * @author mehmetakiftutuncu */
	private static class KentKartDatabaseHelper extends SQLiteOpenHelper
	{
		public KentKartDatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Running create Kent Kart database SQL...");
			db.execSQL(CREATE_SQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Upgrading Kent Kart database...");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_KENT_KART);
			onCreate(db);
		}
	}
	
	/** Private constructor for singleton */
	private KentKartDatabase() {}
	
	/** Creates and/or opens a writable database
	 * 
	 * @param Context 
	 * 
	 * @return An instance of this class
	 * 
	 * @throws SQLException <li>If database cannot be opened */
	public static KentKartDatabase getDatabase(Context context) throws SQLException
	{
		if(myInstance == null)
		{
			if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Creating a new KentKartDatabase instance...");
			myInstance = new KentKartDatabase();
		}
		
		if(myHelper == null)
		{
			if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Creating a new KentKartDatabaseHelper instance...");
			myHelper = new KentKartDatabaseHelper(context);
		}
		
		if(!isOpened)
		{
			if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Opening Kent Kart database...");
			isOpened = true;
			
			myDatabase = myHelper.getWritableDatabase();
		}
		
		return myInstance;
	}
	
	/** Closes the opened database */
	public void closeDatabase()
	{
		if(isOpened)
		{
			if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Closing Kent Kart database...");
			myHelper.close();
		}
		
		isOpened = false;
	}

	@Override
	public synchronized boolean addOrUpdate(KentKart entry)
	{
		// Result flag
		boolean result = true;
		
		// Set the row
		ContentValues values = new ContentValues();
		values.put(KEY_KENT_KART_NAME, entry.getName());
		values.put(KEY_KENT_KART_ALIAS1, entry.getAliasNo1());
		values.put(KEY_KENT_KART_ALIAS2, entry.getAliasNo2());
		values.put(KEY_KENT_KART_ALIAS3, entry.getAliasNo3());
		
		try
		{
			// Get the entry from the database
			KentKart bus = get(entry.getId());
			
			// If the entry is already in the database
			if(bus != null)
			{
				// Updating
				myDatabase.update(TABLE_NAME_KENT_KART, values,
						KEY_KENT_KART_ID + "=" + entry.getId(), null);
			}
			else
			{
				// Adding
				myDatabase.insert(TABLE_NAME_KENT_KART, null, values);
			}
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, "Error occurred while adding to/updating database!", e);
			
			result = false;
		}
		
		// Return the result
		return result;
	}

	@Override
	public synchronized ArrayList<KentKart> get()
	{
		// Resulting list
		ArrayList<KentKart> list = new ArrayList<KentKart>();
		
		// Columns to select which are all
		String[] columns = new String[]
		{
			KEY_KENT_KART_ID,
			KEY_KENT_KART_NAME,
			KEY_KENT_KART_ALIAS1,
			KEY_KENT_KART_ALIAS2,
			KEY_KENT_KART_ALIAS3
		};
		
		// Cursor to query the database
		Cursor cursor = myDatabase.query(TABLE_NAME_KENT_KART, columns, null, null, null, null, null);
		
		// For every item that the cursor finds until the end
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
		{
			// Fields of the item
			int id;
			String name;
			String aliasNo1;
			String aliasNo2;
			String aliasNo3;
			
			// Get values for all fields
			id = cursor.getInt(cursor.getColumnIndex(KEY_KENT_KART_ID));
			name = cursor.getString(cursor.getColumnIndex(KEY_KENT_KART_NAME));
			aliasNo1 = cursor.getString(cursor.getColumnIndex(KEY_KENT_KART_ALIAS1));
			aliasNo2 = cursor.getString(cursor.getColumnIndex(KEY_KENT_KART_ALIAS2));
			aliasNo3 = cursor.getString(cursor.getColumnIndex(KEY_KENT_KART_ALIAS3));
			
			// Add the item to the resulting list
			list.add(new KentKart(id, name, aliasNo1, aliasNo2, aliasNo3));
		}
		
		// Close cursor
		cursor.close();
		
		// Return the result */
		return list;
	}

	@Override
	public synchronized KentKart get(int id)
	{
		// Resulting item */
		KentKart item = null;
		
		/* Columns to select which are all except for number because we know
		 * number */
		String[] columns = new String[]
		{
			KEY_KENT_KART_NAME,
			KEY_KENT_KART_ALIAS1,
			KEY_KENT_KART_ALIAS2,
			KEY_KENT_KART_ALIAS3
		};
		
		// Cursor to query the database
		Cursor cursor = myDatabase.query(TABLE_NAME_KENT_KART, columns,
				KEY_KENT_KART_ID + "=" + id, null, null, null, null);
		
		// If successfully queried
		if(cursor != null)
		{
			// If any match is found
			if(cursor.getCount() > 0)
			{
				// Go to the first match
				cursor.moveToFirst();
				
				// Fields of the item
				String name;
				String aliasNo1;
				String aliasNo2;
				String aliasNo3;
				
				// Get values for all fields
				name = cursor.getString(cursor.getColumnIndex(KEY_KENT_KART_NAME));
				aliasNo1 = cursor.getString(cursor.getColumnIndex(KEY_KENT_KART_ALIAS1));
				aliasNo2 = cursor.getString(cursor.getColumnIndex(KEY_KENT_KART_ALIAS2));
				aliasNo3 = cursor.getString(cursor.getColumnIndex(KEY_KENT_KART_ALIAS3));
				
				// Generate the resulting item
				item = new KentKart(id, name, aliasNo1, aliasNo2, aliasNo3);
			}
			
			// Close cursor
			cursor.close();
		}
		
		// Return the result
		return item;
	}

	@Override
	public synchronized boolean delete(int id)
	{
		// Result flag
		boolean result = true;
		
		// Delete
		int affectedRows = myDatabase.delete(TABLE_NAME_KENT_KART,
				KEY_KENT_KART_ID + "=" + id, null);
		if(affectedRows == 0)
		{
			result = false;
		}
		
		// Return the result
		return result;
	}
}