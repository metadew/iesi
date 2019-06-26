package io.metadew.iesi.script.execution.instruction.lookup;

import io.metadew.iesi.script.execution.instruction.Instruction;

public interface LookupInstruction extends Instruction {
    public abstract String generateOutput(String parameters);
}
