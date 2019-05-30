package io.metadew.iesi.server.rest.error;

public class DataNotFoundException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public  DataNotFoundException(String name) {
        super(name + " " + "was not found");
    }

}