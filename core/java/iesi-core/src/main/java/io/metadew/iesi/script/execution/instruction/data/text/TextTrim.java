package io.metadew.iesi.script.execution.instruction.data.text;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import io.metadew.iesi.script.execution.instruction.data.DataInstruction;

public class TextTrim implements DataInstruction {

    private final ExecutionRuntime executionRuntime;

    public TextTrim(ExecutionRuntime executionRuntime) {
        this.executionRuntime = executionRuntime;
    }

    @Override
    public String getKeyword() {
        return "text.trim";
    }

    @Override
    public String generateOutput(String parameters) {
        DataType resolvedParameters = SpringContext.getBean(DataTypeHandler.class).resolve(parameters, executionRuntime);
        if (!(resolvedParameters instanceof Text)) {
            throw new IllegalArgumentException(String.format("The instruction parameter cannot be a type of %s", resolvedParameters.getClass()));
        }

        return ((Text) resolvedParameters).getString().trim();
    }
}
