package io.metadew.iesi.script.execution.instruction.variable.runtime;

import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.instruction.variable.VariableInstruction;


public class RunidInstruction implements VariableInstruction {

    private final ExecutionControl executionControl;

    public RunidInstruction(ExecutionControl executionControl) {
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

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }
}