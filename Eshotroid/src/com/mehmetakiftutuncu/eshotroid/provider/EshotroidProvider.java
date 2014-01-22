package com.mehmetakiftutuncu.eshotroid.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * A ContentProvider for Eshotroid which provides access to all bus and Kent Kart
 * information in a URI based, unified way
 * 
 * @author mehmetakiftutuncu
 */
public class EshotroidProvider extends ContentProvider
{
	/** Name of the Eshotroid database */
	private static final String DATABASE_NAME = "eshotroid.db";
	/** Version of the Eshotroid database */
	private static final int DATABASE_VERSION = 1;
	
	/** Authority of the provider */
	public static final String AUTHORITY = "com.mehmetakiftutuncu.eshotroid";
	
	/** Contains constants related to busses */
	public static class Busses
	{
		/** Table name for the busses */
		public static final String TABLE_NAME = "busses";
		
		/** URI to use for the busses */
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
		
		/** Type of the single bus provided by EshotroidProvider */
		public static final String CONTENT_ITEM_TYPE = "bus";
		/** Type of the multiple busses provided by EshotroidProvider */
		public static final String CONTENT_DIR_TYPE = "busses";
		
		/** Column name for the number of a bus which is the id column */
		public static final String COLUMN_NUMBER = "_id";
		/** Column name for the source of a bus */
		public static final String COLUMN_SOURCE = "source";
		/** Column name for the destination of a bus */
		public static final String COLUMN_DESTINATION = "destination";
		/** Column name for the route of a bus */
		public static final String COLUMN_ROUTE = "route";
		/** Column name for the favorited flag of a bus */
		public static final String COLUMN_ISFAVORITED = "is_favorited";
		/** Column name for the weekday times of a bus */
		public static final String COLUMN_TIMESH = "times_h";
		/** Column name for the saturday times of a bus */
		public static final String COLUMN_TIMESC = "times_c";
		/** Column name for the sunday times of a bus */
		public static final String COLUMN_TIMESP = "times_p";
		/** Column name for the flag if weekday times of a bus exists */
		public static final String COLUMN_TIMESH_EXISTS = "times_h_exists";
		/** Column name for the flag if saturday times of a bus exists */
		public static final String COLUMN_TIMESC_EXISTS = "times_c_exists";
		/** Column name for the flag if sunday times of a bus exists */
		public static final String COLUMN_TIMESP_EXISTS = "times_p_exists";
		
		/** SQL string for creating busses table */
		private static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME
			+ " ("	+ COLUMN_NUMBER + " INTEGER PRIMARY KEY NOT NULL, "
					+ COLUMN_SOURCE + " TEXT NOT NULL, "
					+ COLUMN_DESTINATION + " TEXT NOT NULL, "
					+ COLUMN_ROUTE + " TEXT, "
					+ COLUMN_ISFAVORITED + " INTEGER NOT NULL, "
					+ COLUMN_TIMESH + " TEXT, "
					+ COLUMN_TIMESC + " TEXT, "
					+ COLUMN_TIMESP + " TEXT,"
					+ COLUMN_TIMESH_EXISTS + " INTEGER NOT NULL,"
					+ COLUMN_TIMESC_EXISTS + " INTEGER NOT NULL,"
					+ COLUMN_TIMESP_EXISTS + " INTEGER NOT NULL);";
		
		/** Array of all column names for simplicity */
		public static final String[] ALL_COLUMNS = new String[]
		{
			COLUMN_NUMBER,
			COLUMN_SOURCE,
			COLUMN_DESTINATION,
			COLUMN_ROUTE,
			COLUMN_ISFAVORITED,
			COLUMN_TIMESH,
			COLUMN_TIMESC,
			COLUMN_TIMESP,
			COLUMN_TIMESH_EXISTS,
			COLUMN_TIMESC_EXISTS,
			COLUMN_TIMESP_EXISTS
		};
	}
	
	/** Contains constants related to Kent Karts */
	public static class KentKarts
	{
		/** Table name for the Kent Karts */
		public static final String TABLE_NAME = "kentkarts";
		
		/** URI to use for the Kent Karts */
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
		
		/** Type of the single Kent Kart provided by EshotroidProvider */
		public static final String CONTENT_ITEM_TYPE = "kentkart";
		/** Type of the multiple Kent Karts provided by EshotroidProvider */
		public static final String CONTENT_DIR_TYPE = "kentkarts";
		
		/** Column name for the id of a Kent Kart (This is row id in database) */
		public static final String COLUMN_ID = "_id";
		/** Column name for the name of a Kent Kart */
		public static final String COLUMN_NAME = "name";
		/** Column name for the alias no 1 of a Kent Kart */
		public static final String COLUMN_ALIAS1 = "alias_no1";
		/** Column name for the alias no 2 of a Kent Kart */
		public static final String COLUMN_ALIAS2 = "alias_no2";
		/** Column name for the alias no 3 of a Kent Kart */
		public static final String COLUMN_ALIAS3 = "alias_no3";
		
		/** SQL string for creating Kent Karts table */
		private static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME
			+ " ("	+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ COLUMN_NAME + " TEXT NOT NULL, "
					+ COLUMN_ALIAS1 + " TEXT NOT NULL, "
					+ COLUMN_ALIAS2 + " TEXT NOT NULL, "
					+ COLUMN_ALIAS3 + " TEXT NOT NULL);";
		
		/** Array of all column names for simplicity */
		public static final String[] ALL_COLUMNS = new String[]
		{
			COLUMN_ID,
			COLUMN_NAME,
			COLUMN_ALIAS1,
			COLUMN_ALIAS2,
			COLUMN_ALIAS3
		};
	}
	
	/** URI match code for a single bus */
	private static final int SINGLE_BUS = 1;
	/** URI match code for multiple busses */
	private static final int MULTIPLE_BUSSES = 2;
	/** URI match code for a single Kent Kart */
	private static final int SINGLE_KENTKART = 3;
	/** URI match code for multiple Kent Karts */
	private static final int MULTIPLE_KENTKARTS = 4;

	/** UriMatcher for matching the query URI to the correct type */
	private static final UriMatcher uriMatcher;
	
	// Static initialization of the UriMatcher
	static
	{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, Busses.TABLE_NAME + "#", SINGLE_BUS);
		uriMatcher.addURI(AUTHORITY, Busses.TABLE_NAME, MULTIPLE_BUSSES);
		uriMatcher.addURI(AUTHORITY, KentKarts.TABLE_NAME + "#", SINGLE_KENTKART);
		uriMatcher.addURI(AUTHORITY, KentKarts.TABLE_NAME, MULTIPLE_KENTKARTS);
	}
	
	/** Tag for debugging */
    public static final String LOG_TAG = "Eshotroid_EshotroidProvider";
	
	/** An SQLiteOpenHelper class to provide a database access */
	public class EshotroidDBOpenHelper extends SQLiteOpenHelper
	{
		/** SQL statement that creates Eshotroid database */
		private static final String CREATE_SQL = Busses.CREATE_SQL + KentKarts.CREATE_SQL;
		
		public EshotroidDBOpenHelper(Context context)
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
			Log.w(LOG_TAG, "Upgrading Eshotroid database from version "
					+ oldVersion + " to " + newVersion + " which will destroy all data...");
			
			db.execSQL("DROP TABLE IF EXISTS " + Busses.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + KentKarts.TABLE_NAME);
			
			onCreate(db);
		}
	}
	
	/** SQLiteOpenHelper to provide access to the actual database */
	private EshotroidDBOpenHelper dbOpenHelper;
	
	@Override
	public boolean onCreate()
	{
		dbOpenHelper = new EshotroidDBOpenHelper(getContext());
		
		return true;
	}
	
	@Override
	public String getType(Uri uri)
	{
		switch(uriMatcher.match(uri))
		{
			case SINGLE_BUS:
				return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + Busses.CONTENT_ITEM_TYPE;
				
			case MULTIPLE_BUSSES:
				return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + Busses.CONTENT_DIR_TYPE;
				
			case SINGLE_KENTKART:
				return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + KentKarts.CONTENT_ITEM_TYPE;
				
			case MULTIPLE_KENTKARTS:
				return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + KentKarts.CONTENT_DIR_TYPE;
			
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
	{
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		
		switch(uriMatcher.match(uri))
		{
			case SINGLE_BUS:
				String busNumber = uri.getPathSegments().get(1);
				
				selection = getAppendedSelection(selection, Busses.COLUMN_NUMBER + "=?");
				selectionArgs = getAppendedSelectionArgs(selectionArgs, busNumber);
				
				Cursor singleBus = db.query(Busses.TABLE_NAME, Busses.ALL_COLUMNS, selection, selectionArgs, null, null, null);
				
				singleBus.setNotificationUri(getContext().getContentResolver(), uri);
				
				return singleBus;
				
			case MULTIPLE_BUSSES:
				Cursor multipleBusses = db.query(Busses.TABLE_NAME, Busses.ALL_COLUMNS, selection, selectionArgs, null, null, null);
				
				multipleBusses.setNotificationUri(getContext().getContentResolver(), uri);
				
				return multipleBusses;
				
			case SINGLE_KENTKART:
				String kentKartId = uri.getPathSegments().get(1);
				
				selection = getAppendedSelection(selection, KentKarts.COLUMN_ID + "=?");
				selectionArgs = getAppendedSelectionArgs(selectionArgs, kentKartId);
				
				Cursor singleKentKart = db.query(KentKarts.TABLE_NAME, KentKarts.ALL_COLUMNS, selection, selectionArgs, null, null, null);
				
				singleKentKart.setNotificationUri(getContext().getContentResolver(), uri);
				
				return singleKentKart;
				
			case MULTIPLE_KENTKARTS:
				Cursor multipleKentKarts = db.query(KentKarts.TABLE_NAME, KentKarts.ALL_COLUMNS, selection, selectionArgs, null, null, null);
				
				multipleKentKarts.setNotificationUri(getContext().getContentResolver(), uri);
				
				return multipleKentKarts;
			
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		
		switch(uriMatcher.match(uri))
		{
			case MULTIPLE_BUSSES:
				long busNumber = db.insert(Busses.TABLE_NAME, null, values);
				
				if(busNumber > -1)
				{
					getContext().getContentResolver().notifyChange(uri, null, false);
					
					return ContentUris.withAppendedId(Busses.CONTENT_URI, busNumber);
				}
				else
				{
					return null;
				}
				
			case MULTIPLE_KENTKARTS:
				long kentKartId = db.insert(KentKarts.TABLE_NAME, null, values);
				
				if(kentKartId > -1)
				{
					getContext().getContentResolver().notifyChange(uri, null, false);
					
					return ContentUris.withAppendedId(KentKarts.CONTENT_URI, kentKartId);
				}
				else
				{
					return null;
				}
			
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
	{
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		
		switch(uriMatcher.match(uri))
		{
			case SINGLE_BUS:
				String busNumber = uri.getPathSegments().get(1);
				
				selection = getAppendedSelection(selection, Busses.COLUMN_NUMBER + "=?");
				selectionArgs = getAppendedSelectionArgs(selectionArgs, busNumber);
				
				int updatedBusCount = db.update(Busses.TABLE_NAME, values, selection, selectionArgs);
				getContext().getContentResolver().notifyChange(uri, null);
				
				return updatedBusCount;
				
			case SINGLE_KENTKART:
				String kentKartId = uri.getPathSegments().get(1);
				
				selection = getAppendedSelection(selection, KentKarts.COLUMN_ID + "=?");
				selectionArgs = getAppendedSelectionArgs(selectionArgs, kentKartId);
				
				int updatedKentKartCount = db.update(KentKarts.TABLE_NAME, values, selection, selectionArgs);
				getContext().getContentResolver().notifyChange(uri, null);
				
				return updatedKentKartCount;
				
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		int deletedCount = 0;
		
		switch(uriMatcher.match(uri))
		{
			case SINGLE_BUS:
				String busNumber = uri.getPathSegments().get(1);
				
				selection = getAppendedSelection(selection, Busses.COLUMN_NUMBER + "=?");
				selectionArgs = getAppendedSelectionArgs(selectionArgs, busNumber);
				
				deletedCount = db.delete(Busses.TABLE_NAME, selection, selectionArgs);
				break;
				
			case MULTIPLE_BUSSES:
				deletedCount = db.delete(Busses.TABLE_NAME, selection, selectionArgs);
				break;
				
			case SINGLE_KENTKART:
				String kentKartId = uri.getPathSegments().get(1);
				
				selection = getAppendedSelection(selection, KentKarts.COLUMN_ID + "=?");
				selectionArgs = getAppendedSelectionArgs(selectionArgs, kentKartId);
				
				deletedCount = db.delete(KentKarts.TABLE_NAME, selection, selectionArgs);
				break;
				
			case MULTIPLE_KENTKARTS:
				deletedCount = db.delete(KentKarts.TABLE_NAME, selection, selectionArgs);
				break;
			
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		
		return deletedCount;
	}
	
	/**
	 * Generates an SQL selection clause by appending a new predicate
	 * 
	 * @param selection Initial clause
	 * @param newPredicate New predicate to append
	 * 
	 * @return A new clause with newPredicate appended to selection,
	 * or newPredicate itself if selection is empty
	 */
	private String getAppendedSelection(String selection, String newPredicate)
	{
		return !TextUtils.isEmpty(selection) ? selection + " AND (" + newPredicate + ")" : newPredicate;
	}
	
	/**
	 * Generates an array of selection arguments by appending a selection argument
	 * 
	 * @param selectionArgs Initial selection arguments
	 * @param newArg New selection argument to append
	 * 
	 * @return A new array of selection arguments with newArg appended to selectionArgs,
	 * or newArg itself as array if selectionArgs is empty
	 */
	private String[] getAppendedSelectionArgs(String[] selectionArgs, String newArg)
	{
		if(selectionArgs != null)
		{
			String[] newSelectionArgs = new String[selectionArgs.length];
			for(int i = 0; i < selectionArgs.length; i++)
			{
				newSelectionArgs[i] = selectionArgs[i];
			}
			newSelectionArgs[newSelectionArgs.length - 1] = newArg;
			
			return newSelectionArgs;
		}
		else
		{
			return new String[] {newArg};
		}
	}
}