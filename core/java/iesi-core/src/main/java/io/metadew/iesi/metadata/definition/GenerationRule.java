package io.metadew.iesi.metadata.definition;

import java.util.List;

public class GenerationRule {

    private long id;
    private long number;
    private String type;
    private String field;
    private String description;
    private String blankInjectionFlag = "N";
    private String blankInjectionUnit = "NUMBER";
    private long blankInjectionMeasure = 0;
    private String blankInjectionValue = "";
    private List<GenerationRuleParameter> parameters;

    //Constructors
    public GenerationRule() {

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

    public List<GenerationRuleParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<GenerationRuleParameter> parameters) {
        this.parameters = parameters;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getBlankInjectionFlag() {
        return blankInjectionFlag;
    }

    public void setBlankInjectionFlag(String blankInjectionFlag) {
        this.blankInjectionFlag = blankInjectionFlag;
    }

    public String getBlankInjectionUnit() {
        return blankInjectionUnit;
    }

    public void setBlankInjectionUnit(String blankInjectionUnit) {
        this.blankInjectionUnit = blankInjectionUnit;
    }

    public String getBlankInjectionValue() {
        return blankInjectionValue;
    }

    public void setBlankInjectionValue(String blankInjectionValue) {
        this.blankInjectionValue = blankInjectionValue;
    }

    public long getBlankInjectionMeasure() {
        return blankInjectionMeasure;
    }

    public void setBlankInjectionMeasure(long blankInjectionMeasure) {
        this.blankInjectionMeasure = blankInjectionMeasure;
    }

}