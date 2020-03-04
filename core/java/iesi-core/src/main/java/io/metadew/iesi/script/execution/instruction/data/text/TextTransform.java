package io.metadew.iesi.script.execution.instruction.data.text;

import io.metadew.iesi.script.execution.instruction.data.DataInstruction;

public class TextTransform  implements DataInstruction {

    @Override
    public String generateOutput(String parameters) {
        return null;
    }

    @Override
    public String getKeyword() {
        return "text.transform";
    }
}
