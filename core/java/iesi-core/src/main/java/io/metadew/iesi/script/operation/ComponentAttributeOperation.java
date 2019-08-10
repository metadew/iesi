package io.metadew.iesi.script.operation;

import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;

public class ComponentAttributeOperation extends AttributeOperation {

    // Constructors
    public ComponentAttributeOperation(ExecutionControl executionControl, ActionExecution actionExecution, String componentName) {
        super(executionControl, actionExecution, "component", componentName);
    }


}