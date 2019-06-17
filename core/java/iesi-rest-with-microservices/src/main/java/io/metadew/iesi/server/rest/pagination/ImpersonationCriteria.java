package io.metadew.iesi.server.rest.pagination;

public class ImpersonationCriteria {

	private String name;

	private String description;

	private String parametersConnection;
	
	private String parametersDescription;

	private String parametersImpersonation;

	private int skip = 0;

	private int limit = 25;

	private ImpersonationCriteria() {

	}

	public String getParametersConnection() {
		return parametersConnection;
	}

	public void setParametersConnection(String parametersConnection) {
		this.parametersConnection = parametersConnection;
	}

	public String getParametersDescription() {
		return parametersDescription;
	}

	public void setParametersDescription(String parametersDescription) {
		this.parametersDescription = parametersDescription;
	}

	public String getParametersImpersonation() {
		return parametersImpersonation;
	}

	public void setParametersImpersonation(String parametersImpersonation) {
		this.parametersImpersonation = parametersImpersonation;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getSkip() {
		return skip;
	}

	public void setSkip(int skip) {
		this.skip = skip;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

}
