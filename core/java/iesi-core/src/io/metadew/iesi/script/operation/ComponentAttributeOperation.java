package io.metadew.iesi.script.operation;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;

public class ComponentAttributeOperation extends AttributeOperation {

    // Constructors
    public ComponentAttributeOperation(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ActionExecution actionExecution, String componentName) {
        super(frameworkExecution, executionControl, actionExecution, "component", componentName);
    }


}