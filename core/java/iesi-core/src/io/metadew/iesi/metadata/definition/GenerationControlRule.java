package io.metadew.iesi.metadata.definition;

import java.util.List;

public class GenerationControlRule {

    private long id;
    private long number;
    private String type;
    private String name;
    private String description;
    private List<GenerationControlRuleParameter> parameters;

    //Constructors
    public GenerationControlRule() {

    }

    //Getters and Setters
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

    public List<GenerationControlRuleParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<GenerationControlRuleParameter> parameters) {
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}