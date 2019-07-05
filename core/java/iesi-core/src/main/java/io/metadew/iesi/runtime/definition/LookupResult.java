package io.metadew.iesi.runtime.definition;

public class LookupResult {

    private String value;
    private String type;
    private String context;
    private String inputValue;

    // Constructors
    public LookupResult() {
    }


    // Getters and setters
    public String getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public String getContext() {
        return context;
    }


    public void setContext(String context) {
        this.context = context;
    }


    public String getInputValue() {
        return inputValue;
    }


    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
    }


}