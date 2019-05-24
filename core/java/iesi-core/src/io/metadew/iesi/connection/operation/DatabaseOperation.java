package io.metadew.iesi.connection.operation;

import io.metadew.iesi.connection.operation.database.ScriptBuilder;
import io.metadew.iesi.framework.execution.FrameworkExecution;

public class DatabaseOperation {

    private FrameworkExecution frameworkExecution;
    private ScriptBuilder scriptBuilder;

    public DatabaseOperation() {
        this.setScriptBuilder(new ScriptBuilder());
    }

    // Getters and Setters
    public ScriptBuilder getScriptBuilder() {
        return scriptBuilder;
    }

    public void setScriptBuilder(ScriptBuilder scriptBuilder) {
        this.scriptBuilder = scriptBuilder;
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

}