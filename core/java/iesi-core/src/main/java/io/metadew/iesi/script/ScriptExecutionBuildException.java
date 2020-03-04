package io.metadew.iesi.script;

public class ScriptExecutionBuildException extends RuntimeException {

    public ScriptExecutionBuildException(String message) {
        super(message);
    }

    public ScriptExecutionBuildException(Exception exception) {
        super(exception);
    }
}
