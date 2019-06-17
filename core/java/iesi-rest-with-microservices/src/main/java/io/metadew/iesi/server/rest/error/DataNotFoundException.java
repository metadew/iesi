package io.metadew.iesi.server.rest.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class DataNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DataNotFoundException(String name) {
		super("Object" + " '" + name + "' " + " was not found. ");
	}

	public DataNotFoundException(String name, String name2) {
		super("Mismatch between query parameter" + " " + " '" + name + "' " + " and query parameter " + " '" + name2
				+ "' ");
	}

	public DataNotFoundException(String name, Long integer) {
		super("Mismatch between query parameter" + " " + " '" + name + "' " + " and query parameter " + " '" + integer
				+ "' ");
	}

}