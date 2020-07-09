package io.metadew.iesi.script.execution;

import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.script.operation.ActionSelectOperation;

public interface RootingStrategy {

    public void prepareExecution(ScriptExecution scriptExecution) throws Exception;

    public boolean executionAllowed(ActionSelectOperation actionSelectOperation, Action action);

    public void endExecution(ScriptExecution scriptExecution);

    void continueAction(ActionSelectOperation actionSelectOperation, Action action);
}
