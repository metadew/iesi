package io.metadew.iesi.script.execution;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.Script;

public class ScriptRunnable implements Runnable {
    private Script script;
    private FrameworkExecution frameworkExecution;
    private ScriptExecution scriptExecution;


    ScriptRunnable(FrameworkExecution frameworkExecution, ScriptExecution scriptExecution, Script script) {
        this.setFrameworkExecution(frameworkExecution);
        this.setScriptExecution(scriptExecution);
        this.setScript(script);
    }

    @Override
    public void run() {
        ScriptExecution scriptExecution = new ScriptExecution(this.getFrameworkExecution(), this.getScript());
        scriptExecution.initializeAsRouteExecution(this.getScriptExecution());
        scriptExecution.execute();
    }

    // Getters and setters
    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

    public ScriptExecution getScriptExecution() {
        return scriptExecution;
    }

    public void setScriptExecution(ScriptExecution scriptExecution) {
        this.scriptExecution = scriptExecution;
    }
}