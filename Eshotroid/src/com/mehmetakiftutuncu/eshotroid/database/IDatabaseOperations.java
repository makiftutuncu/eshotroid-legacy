package com.mehmetakiftutuncu.eshotroid.database;

import java.util.ArrayList;

/** An interface for database operations
 * 
 * @author mehmetakiftutuncu */
public interface IDatabaseOperations<Type>
{
	/** Adds an entry to the database if not already in the database, or updates
	 * the entry if it is already in the database
	 * 
	 * @param entry The entry to be added to the database or updated
	 * 
	 * @return true if successfully added/updated, false if any error occurred */
	public boolean addOrUpdate(Type entry);
	
	/** Gets all the entries from database
	 * 
	 * @return An ArrayList with all the entries read from database, or with
	 * size 0 if no entries were able to read */
	public ArrayList<Type> get();
	
	/** Gets the entry with the specified number if it is in the database
	 * 
	 * @param number The number of the entry to be looked for in the database
	 * 
	 * @return The specified entry if it is in the database, null if it is not
	 * in the database */
	public Type get(int number);
	
	/** Deletes an entry with the specified number
	 * 
	 * @param number Number of the entry to be deleted
	 * 
	 * @return true if successfully deleted, false if any error occurred */
	public boolean delete(int number);
}