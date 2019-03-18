package io.metadew.iesi.metadata.definition;

/**
 * Context is a generic object is defined by a name and a scope.
 * 
 * @author peter.billen
 *
 */
public class Context {

	private String name;
	private String scope;

	// Constructors
	public Context() {
		
	}

	// Getters and Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}


}