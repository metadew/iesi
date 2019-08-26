package io.metadew.iesi.metadata.definition.feature;

import io.metadew.iesi.metadata.definition.scenario.Scenario;
import io.metadew.iesi.metadata.tools.IdentifierTools;

import java.util.List;

public class Feature {

	private String id;
	private String type = "feature";
	private String name;
	private String description;
	// Set a default version if not provided
	private FeatureVersion version = new FeatureVersion();
	private List<FeatureParameter> parameters;
	private List<Scenario> scenarios;

	// Constructors
	public Feature() {
	}

	public Feature(String id, String type, String name, String description, FeatureVersion version,
			List<FeatureParameter> parameters, List<Scenario> scenarios) {
		this.id = id;
		this.type = type;
		this.name = name;
		this.description = description;
		this.setVersion(version);
		this.setParameters(parameters);
		this.scenarios = scenarios;
	}

	public Feature(String type, String name, String description, FeatureVersion version,
			List<FeatureParameter> parameters, List<Scenario> scenarios) {
		this.id = IdentifierTools.getFeatureIdentifier(name);
		this.type = type;
		this.name = name;
		this.description = description;
		this.setVersion(version);
		this.setParameters(parameters);
		this.setScenarios(scenarios);
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		if (id == null)
			this.id = IdentifierTools.getFeatureIdentifier(this.getName());
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isEmpty() {
		return (this.name == null || this.name.isEmpty());
	}

	public List<FeatureParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<FeatureParameter> parameters) {
		this.parameters = parameters;
	}

	public List<Scenario> getScenarios() {
		return scenarios;
	}

	public void setScenarios(List<Scenario> scenarios) {
		this.scenarios = scenarios;
	}

	public FeatureVersion getVersion() {
		return version;
	}

	public void setVersion(FeatureVersion version) {
		this.version = version;
	}

}