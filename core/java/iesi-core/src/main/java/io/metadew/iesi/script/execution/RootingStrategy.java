package io.metadew.iesi.script.execution;

import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.script.operation.ActionSelectOperation;

public interface RootingStrategy {

    void prepareExecution(ScriptExecution scriptExecution);

    boolean executionAllowed(ActionSelectOperation actionSelectOperation, Action action);

    void endExecution(ScriptExecution scriptExecution);

    void continueAction(ActionSelectOperation actionSelectOperation, Action action);
}
