package io.metadew.iesi.metadata.definition;

public class MetadataField {
	
	private String name;
	private String description;
	private int order;
	private String type;
	private int length;
	private String nullable;
	private String defaultTimestamp;
	
	//Constructors
	public MetadataField() {
		
	}
	
	//Getters and Setters
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNullable() {
		return nullable;
	}

	public void setNullable(String nullable) {
		this.nullable = nullable;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getDefaultTimestamp() {
		return defaultTimestamp;
	}

	public void setDefaultTimestamp(String defaultTimestamp) {
		this.defaultTimestamp = defaultTimestamp;
	}



	

}