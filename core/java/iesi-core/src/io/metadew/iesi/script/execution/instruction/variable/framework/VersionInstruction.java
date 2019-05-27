package io.metadew.iesi.script.execution.instruction.variable.framework;

import io.metadew.iesi.framework.configuration.FrameworkSettings;
import io.metadew.iesi.script.execution.instruction.variable.VariableInstruction;


public class VersionInstruction implements VariableInstruction {

    public VersionInstruction() {

    }

    @Override
    public String generateOutput() {
        return FrameworkSettings.VERSION.value();
    }

    @Override
    public String getKeyword() {
        return "fwk.version";
    }
}