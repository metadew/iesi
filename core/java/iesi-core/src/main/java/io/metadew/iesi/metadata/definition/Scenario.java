package io.metadew.iesi.metadata.definition;

import io.metadew.iesi.metadata.tools.IdentifierTools;

import java.util.List;

public class Scenario {

    private String id;
    private long number;
    private String type;
    private String name;
    private String description;
    private String dependencies;
    private String script;
    private long version;
    private long gain;
    private List<ScenarioParameter> parameters;

    //Constructors
    public Scenario() {

    }

    // TODO: make optional Parameters of type Optional instead of ""

    public Scenario(String id, long number, String type, String name, String description, String dependencies, String script, long version, long gain, List<ScenarioParameter> parameters) {
        this.id = id;
        this.number = number;
        this.type = type;
        this.name = name;
        this.description = description;
        this.dependencies = dependencies;
        this.setScript(script);
        this.setVersion(version);
        this.gain = gain;
        this.setParameters(parameters);
    }

    public Scenario(long number, String type, String name, String description, String dependencies, String script, long version, long gain, List<ScenarioParameter> parameters) {
        this.id = IdentifierTools.getScenarioIdentifier(name);
        this.number = number;
        this.type = type;
        this.name = name;
        this.description = description;
        this.dependencies = dependencies;
        this.setScript(script);
        this.setVersion(version);
        this.gain = gain;
        this.setParameters(parameters);
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

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        if (id == null) this.id = IdentifierTools.getScenarioIdentifier(this.getName());
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

	public String getDependencies() {
		return dependencies;
	}

	public void setDependencies(String dependencies) {
		this.dependencies = dependencies;
	}

	public List<ScenarioParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<ScenarioParameter> parameters) {
		this.parameters = parameters;
	}

	public long getGain() {
		return gain;
	}

	public void setGain(long gain) {
		this.gain = gain;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

}