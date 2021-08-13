package io.metadew.iesi.script.execution.instruction.data.text;

import io.metadew.iesi.script.execution.instruction.data.DataInstruction;

import java.util.UUID;

public class RandomUUID implements DataInstruction {

    @Override
    public String getKeyword() {
        return "text.uuid";
    }

    @Override
    public String generateOutput(String parameters) {
        return UUID.randomUUID().toString();
    }
}
