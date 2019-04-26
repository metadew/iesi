package io.metadew.iesi.metadata.definition;

import java.util.List;

public class Script {

	private long id;
	private String type = "script";
	private String name;
	private String description;
	// Set a default script version if not provided
	private ScriptVersion version = new ScriptVersion();
	private List<ScriptParameter> parameters;
	private List<Action> actions;

	// Constructors
	public Script() {
		
	}

	// Getters and Setters
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public List<ScriptParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<ScriptParameter> parameters) {
		this.parameters = parameters;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ScriptVersion getVersion() {
		return version;
	}

	public void setVersion(ScriptVersion version) {
		this.version = version;
	}


}