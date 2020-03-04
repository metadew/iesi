package io.metadew.iesi.script.execution.instruction.variable.framework;

import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.instruction.variable.VariableInstruction;
import org.apache.commons.io.FilenameUtils;


public class FrameworkHomeInstruction implements VariableInstruction {

    private final ExecutionControl executionControl;

    public FrameworkHomeInstruction(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    @Override
    public String generateOutput() {
        return FilenameUtils.normalize(FrameworkConfiguration.getInstance().getFrameworkHome().toString());
    }

    @Override
    public String getKeyword() {
        return "fwk.home";
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }
}