package io.metadew.iesi.server.rest.pagination;

public class ScriptCriteria {
	private String name;

	private String description;

	private int skip = 0;

	private int limit = 25;

	private ScriptCriteria() {
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