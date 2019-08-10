package io.metadew.iesi.script.execution.instruction.lookup;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeService;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.script.execution.ExecutionControl;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListLookup implements LookupInstruction {

    private final String ARRAY_KEY = "list";

    private final String ELEMENT_KEY = "parameterName";

	private final Pattern INPUT_PARAMETER_PATTERN = Pattern
            .compile("\\s*\"?(?<" + ARRAY_KEY + ">(\\w|\\.)+)\"?\\s*,\\s*(?<" + ELEMENT_KEY + ">(\\w|\\.)+)\\s*");
    private final DataTypeService dataTypeService;

    public ListLookup(ExecutionControl executionControl) {
        this.dataTypeService = new DataTypeService(executionControl.getExecutionRuntime());
    }

    @Override
    public String generateOutput(String parameters) {
        // TODO: parse with antlr
        Matcher inputParameterMatcher = INPUT_PARAMETER_PATTERN.matcher(parameters);
        if (!inputParameterMatcher.find()) {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to list lookup: {0}", parameters));
        }

        Array array = getArray(dataTypeService.resolve(inputParameterMatcher.group(ARRAY_KEY)));
        int arrayElementIndex = Integer.parseInt(inputParameterMatcher.group(ELEMENT_KEY)) - 1;

        return array.getList().get(arrayElementIndex).toString();
    }

    private Array getArray(DataType array) {
        if (array instanceof Array) {
            return (Array) array;
        } else {
            throw new IllegalArgumentException(MessageFormat.format("Dataset cannot be of type {0}", array.getClass()));
        }
    }

    @Override
    public String getKeyword() {
        return "list";
    }
}
