package io.metadew.iesi.metadata.definition;

import java.util.List;

public class GenerationControlRuleType {

    private String name;
    private String description;
    private List<GenerationControlRuleTypeParameter> parameters;

    //Constructors
    public GenerationControlRuleType() {

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

    public List<GenerationControlRuleTypeParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<GenerationControlRuleTypeParameter> parameters) {
        this.parameters = parameters;
    }

}