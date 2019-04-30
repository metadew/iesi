package io.metadew.iesi.server.rest.pagination;

public class EnvironmentCriteria {

	private String name;

	private String description;

	private String parametersName;

	private String parametersValue;

	private int skip = 0;

	private int limit = 100;
	

	private EnvironmentCriteria() {
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

	public String getParametersName() {
		return parametersName;
	}

	public String getParametersValue() {
		return parametersValue;
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