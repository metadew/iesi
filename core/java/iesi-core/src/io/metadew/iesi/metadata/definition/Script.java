package io.metadew.iesi.metadata.definition;

import io.metadew.iesi.metadata.tools.IdentifierTools;

import java.util.List;

public class Script {

    private String id;
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

    public Script(String id, String type, String name, String description, ScriptVersion version,
                  List<ScriptParameter> parameters, List<Action> actions) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.version = version;
        this.parameters = parameters;
        this.actions = actions;
    }
    public Script(String type, String name, String description, ScriptVersion version,
                   List<ScriptParameter> parameters, List<Action> actions) {
        this.id = IdentifierTools.getScriptIdentifier(name);
        this.type = type;
        this.name = name;
        this.description = description;
        this.version = version;
        this.parameters = parameters;
        this.actions = actions;
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

    public String getId() {
        if (id == null) this.id = IdentifierTools.getScriptIdentifier(this.getName());
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

	public boolean isEmpty() {
		return (this.name == null || this.name.isEmpty()) ;
	}

}