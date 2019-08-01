package io.metadew.iesi.script;

public class ScriptExecutionBuildException extends Exception {

    public ScriptExecutionBuildException(String message) {
        super(message);
    }

    public ScriptExecutionBuildException(Exception exception) {
        super(exception);
    }
}
