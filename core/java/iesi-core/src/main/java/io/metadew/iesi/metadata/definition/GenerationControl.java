package io.metadew.iesi.metadata.definition;

import java.util.List;

public class GenerationControl {

    private long id;
    private String name;
    private String type = "";
    private String description;
    private List<GenerationControlParameter> parameters;
    private List<GenerationControlRule> rules;

    // Constructors
    public GenerationControl() {

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<GenerationControlParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<GenerationControlParameter> parameters) {
        this.parameters = parameters;
    }

    public List<GenerationControlRule> getRules() {
        return rules;
    }

    public void setRules(List<GenerationControlRule> rules) {
        this.rules = rules;
    }

}