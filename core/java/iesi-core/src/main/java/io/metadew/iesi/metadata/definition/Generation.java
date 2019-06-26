package io.metadew.iesi.metadata.definition;

import java.util.List;

public class Generation {

    private long id;
    private String type = "default";
    private String name;
    private String description;
    private List<GenerationParameter> parameters;
    private List<GenerationOutput> outputs;
    private List<GenerationControl> controls;
    private List<GenerationRule> rules;

    // Constructors
    public Generation() {

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<GenerationParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<GenerationParameter> parameters) {
        this.parameters = parameters;
    }

    public List<GenerationRule> getRules() {
        return rules;
    }

    public void setRules(List<GenerationRule> rules) {
        this.rules = rules;
    }

    public List<GenerationOutput> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<GenerationOutput> outputs) {
        this.outputs = outputs;
    }

    public List<GenerationControl> getControls() {
        return controls;
    }

    public void setControls(List<GenerationControl> controls) {
        this.controls = controls;
    }


}