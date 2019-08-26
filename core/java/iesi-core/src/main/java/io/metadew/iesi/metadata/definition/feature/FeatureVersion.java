package io.metadew.iesi.metadata.definition.feature;

public class FeatureVersion {
    private String id;
    private long number = 0;
    private String description = "Default version";

    // Constructors
    public FeatureVersion() {

    }

    public FeatureVersion(String id, long number, String description) {
        this.setId(id);
        this.number = number;
        this.description = description;
    }

    // Getters and Setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}