package io.metadew.iesi.script.execution.instruction.lookup;

import java.util.regex.Pattern;

public class ListLookup implements LookupInstruction {

    private final String CONNECTION_NAME_KEY = "name";

    private final String CONNECTION_PARAMETER_NAME_KEY = "parameterName";

    @SuppressWarnings("unused")
	private final Pattern INPUT_PARAMETER_PATTERN = Pattern
            .compile("\\s*\"?(?<" + CONNECTION_NAME_KEY + ">(\\w|\\.)+)\"?\\s*,\\s*(?<" + CONNECTION_PARAMETER_NAME_KEY + ">(\\w|\\.)+)\\s*");

    @Override
    public String generateOutput(String parameters) {
        return null;
    }

    @Override
    public String getKeyword() {
        return null;
    }
}
