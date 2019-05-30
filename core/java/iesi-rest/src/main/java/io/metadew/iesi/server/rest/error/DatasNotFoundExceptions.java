package io.metadew.iesi.server.rest.error;

public class DatasNotFoundExceptions extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long version;
	private String name;



	public DatasNotFoundExceptions(Long version, String name) {
		super();
		this.version = version;
		this.name = name;
	}


}