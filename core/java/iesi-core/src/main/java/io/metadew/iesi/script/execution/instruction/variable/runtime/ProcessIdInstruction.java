package io.metadew.iesi.script.execution.instruction.variable.runtime;

import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.instruction.variable.VariableInstruction;


public class ProcessIdInstruction implements VariableInstruction {

    private final ExecutionControl executionControl;

    public ProcessIdInstruction(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    @Override
    public String generateOutput() {
        return executionControl.getProcessId().toString();
    }

    @Override
    public String getKeyword() {
        return "process.id";
    }

}