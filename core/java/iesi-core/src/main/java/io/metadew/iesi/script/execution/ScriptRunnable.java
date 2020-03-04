package io.metadew.iesi.script.execution;

import io.metadew.iesi.metadata.definition.script.Script;

public class ScriptRunnable implements Runnable {
    private Script script;
    private ScriptExecution scriptExecution;


    ScriptRunnable(ScriptExecution scriptExecution, Script script) {
        this.setScriptExecution(scriptExecution);
        this.setScript(script);
    }

    @Override
    public void run() {
        ScriptExecution scriptExecution = new ScriptExecutionBuilder(true, true)
                .script(script)
                .executionControl(this.scriptExecution.getExecutionControl())
                .executionMetrics(this.scriptExecution.getExecutionMetrics())
                .actionSelectOperation(this.scriptExecution.getActionSelectOperation())
                .parentScriptExecution(this.scriptExecution.getParentScriptExecution().orElse(null))
                .build();
        scriptExecution.execute();
    }

    // Getters and setters
    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;
    }

    public ScriptExecution getScriptExecution() {
        return scriptExecution;
    }

    public void setScriptExecution(ScriptExecution scriptExecution) {
        this.scriptExecution = scriptExecution;
    }
}