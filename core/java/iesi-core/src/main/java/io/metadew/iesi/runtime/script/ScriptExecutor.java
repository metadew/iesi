package io.metadew.iesi.runtime.script;

import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;

public interface ScriptExecutor <T extends ScriptExecutionRequest> {

    Class<T> appliesTo();
    void execute(T scriptExecutionRequest);
}
