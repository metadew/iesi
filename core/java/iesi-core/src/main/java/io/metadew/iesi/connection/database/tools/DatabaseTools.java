package io.metadew.iesi.connection.database.tools;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.connection.DatabaseConnection;

public final class DatabaseTools {

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Database getDatabase(DatabaseConnection databaseConnection, String className) {
    	Object instance = null;
		try {
			Class classRef = Class.forName(databaseConnection.getType());
			instance = classRef.getDeclaredConstructor(databaseConnection.getClass()).newInstance(databaseConnection);
		} catch (Exception e) {
			throw new RuntimeException("Unable to get database object");
		}
    	return (Database) instance;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Database getDatabase(String className) {
    	Object instance = null;
		try {
			Class classRef = Class.forName(className);
			instance = classRef.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Unable to get database object");
		}
    	return (Database) instance;
    }
    
    // TODO add lookup for all database types
    public static void getDatabaseClassName() {
    	
    }
}