package io.metadew.iesi.server.rest.pagination;

public class ComponentCriteria {

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	private int skip = 0;

	private int limit = 25;

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

	private ComponentCriteria() {
	}

	private String name;

	private String type;

	private String description;

	public String getName() {
		return name;
	}

}
