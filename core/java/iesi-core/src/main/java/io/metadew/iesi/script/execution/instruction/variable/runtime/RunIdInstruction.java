package io.metadew.iesi.script.execution.instruction.variable.runtime;

import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.instruction.variable.VariableInstruction;


public class RunIdInstruction implements VariableInstruction {

    private final ExecutionControl executionControl;

    public RunIdInstruction(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    @Override
    public String generateOutput() {
        return executionControl.getRunId();
    }

    @Override
    public String getKeyword() {
        return "run.id";
    }

}