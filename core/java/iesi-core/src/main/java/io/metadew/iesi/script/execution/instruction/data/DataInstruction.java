package io.metadew.iesi.script.execution.instruction.data;

import io.metadew.iesi.script.execution.instruction.Instruction;

public interface DataInstruction extends Instruction {
    public abstract String generateOutput(String parameters);
}
