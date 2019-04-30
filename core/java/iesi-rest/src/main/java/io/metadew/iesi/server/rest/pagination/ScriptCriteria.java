package io.metadew.iesi.server.rest.pagination;

public class ScriptCriteria {
	private String query;

	private int skip = 0;

	private int limit = 25;

	private ScriptCriteria() {
	    }

	public String getQuery() {
		return query;
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