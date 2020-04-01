package io.metadew.iesi.script.execution.instruction.variable.framework;

import io.metadew.iesi.common.configuration.Configuration;
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
        return FilenameUtils.normalize((String) Configuration.getInstance().getProperty("iesi.home")
                .orElseThrow(() -> new RuntimeException("No value found for 'home'")));
    }

    @Override
    public String getKeyword() {
        return "fwk.home";
    }

}