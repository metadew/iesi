package io.metadew.iesi.script.execution.instruction.variable.runtime;

import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.instruction.variable.VariableInstruction;


public class EnvironmentInstruction implements VariableInstruction {

    private final ExecutionControl executionControl;

    public EnvironmentInstruction(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    @Override
    public String generateOutput() {
        return this.getExecutionControl().getEnvName();
    }

    @Override
    public String getKeyword() {
        return "run.env";
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }
}