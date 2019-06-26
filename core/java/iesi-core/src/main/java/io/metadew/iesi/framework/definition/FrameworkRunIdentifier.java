package io.metadew.iesi.framework.definition;

import java.util.UUID;

public class FrameworkRunIdentifier {

    private String id = "";

    //Constructors
    public FrameworkRunIdentifier() {
    	this.id = UUID.randomUUID().toString();
    }

    //Getters and Setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


}