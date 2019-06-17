package io.metadew.iesi.server.rest.error;

public class SqlNotFoundException extends RuntimeException {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SqlNotFoundException() {
		super("[SQLITE_CONSTRAINT_NOTNULL]  A NOT NULL constraint failed");
	}
}