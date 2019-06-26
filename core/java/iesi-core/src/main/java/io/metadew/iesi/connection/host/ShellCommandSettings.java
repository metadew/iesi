package io.metadew.iesi.connection.host;

import io.metadew.iesi.framework.execution.FrameworkExecution;

/**
 * Object containing all settings for shell command executions.
 *
 * @author peter.billen
 */
public class ShellCommandSettings {

    private FrameworkExecution frameworkExecution = null;
    private String environment = "";
    private boolean setRunVar;
    private String setRunVarPrefix = "";
    private String setRunVarMode = "";

    public ShellCommandSettings() {

    }

    public ShellCommandSettings(boolean setRunVar, String setRunVarPrefix, String setRunVarMode) {
        this.setSetRunVar(setRunVar);
        this.setSetRunVarPrefix(setRunVarPrefix);
        this.setSetRunVarMode(setRunVarMode);
    }

    // Getters and Setters
    public boolean getSetRunVar() {
        return setRunVar;
    }

    public void setSetRunVar(boolean setRunVar) {
        this.setRunVar = setRunVar;
    }

    public String getSetRunVarPrefix() {
        return setRunVarPrefix;
    }

    public void setSetRunVarPrefix(String setRunVarPrefix) {
        this.setRunVarPrefix = setRunVarPrefix;
    }

    public String getSetRunVarMode() {
        return setRunVarMode;
    }

    public void setSetRunVarMode(String setRunVarMode) {
        this.setRunVarMode = setRunVarMode;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }


}
