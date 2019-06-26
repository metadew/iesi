package io.metadew.iesi.script.configuration;

import java.util.HashMap;


public class IterationInstance {

    private boolean empty = true;
    private long iterationNumber;
    private HashMap<String, String> variableMap;

    public IterationInstance() {
        this.setVariableMap(new HashMap<String, String>());
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public HashMap<String, String> getVariableMap() {
        return variableMap;
    }

    public void setVariableMap(HashMap<String, String> variableMap) {
        this.variableMap = variableMap;
    }

    public long getIterationNumber() {
        return iterationNumber;
    }

    public void setIterationNumber(long iterationNumber) {
        this.iterationNumber = iterationNumber;
    }
}