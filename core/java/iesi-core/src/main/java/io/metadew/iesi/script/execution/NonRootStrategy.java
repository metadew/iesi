package io.metadew.iesi.script.execution;

import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.script.operation.ActionSelectOperation;

public class NonRootStrategy implements RootingStrategy {

    @Override
    public void prepareExecution(ScriptExecution scriptExecution) {

    }

    @Override
    public boolean executionAllowed(ActionSelectOperation actionSelectOperation, Action action) {
        return true;
    }

    @Override
    public void endExecution(ScriptExecution scriptExecution) {
        scriptExecution.getExecutionControl().setActionErrorStop(false);
    }

    @Override
    public void continueAction(ActionSelectOperation actionSelectOperation, Action action) {

    }
}
