package io.metadew.iesi.script.execution.instruction.lookup;

import io.metadew.iesi.datatypes.Array;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeResolver;
import io.metadew.iesi.datatypes.Text;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.script.execution.ExecutionControl;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListLookup implements LookupInstruction {

    private final String ARRAY_KEY = "list";

    private final String ELEMENT_KEY = "parameterName";

	private final Pattern INPUT_PARAMETER_PATTERN = Pattern
            .compile("\\s*\"?(?<" + ARRAY_KEY + ">(\\w|\\.)+)\"?\\s*,\\s*(?<" + ELEMENT_KEY + ">(\\w|\\.)+)\\s*");
    private final ExecutionControl executionControl;

    public ListLookup(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    @Override
    public String generateOutput(String parameters) {
        // TODO: parse with antlr
        Matcher inputParameterMatcher = INPUT_PARAMETER_PATTERN.matcher(parameters);
        if (!inputParameterMatcher.find()) {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to list lookup: {0}", parameters));
        }

        Array array = getArray(DataTypeResolver.resolveToDataType(inputParameterMatcher.group(ARRAY_KEY), executionControl.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration() , executionControl.getExecutionRuntime()));
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
