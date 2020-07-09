package io.metadew.iesi.runtime.script;

import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;

public interface ScriptExecutor <T extends ScriptExecutionRequest> {

    public Class<T> appliesTo();
    public void execute(T scriptExecutionRequest) throws Exception;
}
