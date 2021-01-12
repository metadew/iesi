package io.metadew.iesi.script.execution.instruction.lookup;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.template.TemplateService;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.regex.Pattern;

public class ListLookup implements LookupInstruction {

    private final String ARRAY_KEY = "list";

    private final String ELEMENT_KEY = "parameterName";

    private final Pattern INPUT_PARAMETER_PATTERN = Pattern
            .compile("\\s*\"?(?<" + ARRAY_KEY + ">(\\w|\\.)+)\"?\\s*,\\s*(?<" + ELEMENT_KEY + ">(\\w|\\.)+)\\s*");

    private static final Logger LOGGER = LogManager.getLogger();
    private final ExecutionRuntime executionRuntime;

    public ListLookup(ExecutionRuntime executionRuntime) {
        this.executionRuntime = executionRuntime;
    }

    @Override
    public String generateOutput(String parameters) {

        String[] arguments = splitInput(parameters);
        // TODO: parse with antlr
//        Matcher inputParameterMatcher = INPUT_PARAMETER_PATTERN.matcher(parameters);
//        if (!inputParameterMatcher.find()) {
//            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to list lookup: {0}", parameters));
//        }
        LOGGER.debug(MessageFormat.format("fetching element {0} of list {1}", arguments[1], arguments[0]));

        Array array = getArray(DataTypeHandler.getInstance().resolve(arguments[0], executionRuntime));
        DataType elementSelector = DataTypeHandler.getInstance().resolve(arguments[1], executionRuntime);
        if (elementSelector instanceof Text) {
            int index = Integer.parseInt(((Text) elementSelector).getString().trim()) - 1;
            return array.getList().get(index).toString();
        } else if (elementSelector instanceof Template) {
            for (DataType dataType : array.getList()) {
                if (TemplateService.getInstance().matches(dataType, (Template) elementSelector, executionRuntime)) {
                    return dataType.toString();
                }
            }
            throw new RuntimeException(MessageFormat.format("List {0} does not contain element matching template {1}", array.toString(), elementSelector.toString()));
        } else {
            throw new IllegalArgumentException(MessageFormat.format("Cannot lookup {0} in list {1}", elementSelector, array.toString()));
        }
    }

    private int getIndex(DataType index) {
        if (index instanceof Text) {
            return Integer.parseInt(((Text) index).getString());
        } else {
            throw new IllegalArgumentException(MessageFormat.format("index cannot be of type {0}", index.getClass()));
        }
    }

    private Array getArray(DataType array) {
        if (array instanceof Array) {
            return (Array) array;
        } else if (array instanceof Text) {
            return executionRuntime.getArray(((Text) array).getString())
                    .orElseThrow(() -> new IllegalArgumentException(MessageFormat.format("No array found with reference name {0}", ((Text) array).getString())));
        } else {
            throw new IllegalArgumentException(MessageFormat.format("list cannot be of type {0}", array.getClass()));
        }
    }

    @Override
    public String getKeyword() {
        return "list";
    }

    private String[] splitInput(String input) {
        String lookupConceptStartKey = "{{";
        String lookupConceptStopKey = "}}";
        int lookupConceptStopIndex = 0;

        if (!input.contains(lookupConceptStartKey) && !input.contains(lookupConceptStopKey)) {
            return input.split(",");
        }

        int lookupConceptStartIndex = input.indexOf(lookupConceptStartKey, lookupConceptStopIndex);
        if (input.indexOf(lookupConceptStopKey, lookupConceptStartIndex) == -1) {
            LOGGER.warn(MessageFormat.format("concept.lookup.resolve.error=error during concept lookup resolvement of {0}. Concept lookup instruction not properly closed.", input));
            throw new RuntimeException();
        }
        lookupConceptStopIndex = input.indexOf(lookupConceptStopKey, lookupConceptStartIndex);
        int nextLookupConceptStartIndex = input.indexOf(lookupConceptStartKey, lookupConceptStartIndex + lookupConceptStartKey.length());
        while (nextLookupConceptStartIndex > 0 && nextLookupConceptStartIndex < lookupConceptStopIndex) {
            lookupConceptStopIndex = input.indexOf(lookupConceptStopKey, lookupConceptStopIndex + lookupConceptStopKey.length());
            if (lookupConceptStopIndex < 0) {
                LOGGER.warn(MessageFormat.format("concept.lookup.resolve.error=error during concept lookup resolvement of {0}. Concept lookup instruction not properly closed.", input));
                throw new RuntimeException();
            }
            nextLookupConceptStartIndex = input.indexOf(lookupConceptStartKey, nextLookupConceptStartIndex + lookupConceptStartKey.length());
        }

        String[] splitted = new String[2];
        splitted[0] = input.substring(lookupConceptStartIndex, lookupConceptStopIndex + lookupConceptStopKey.length());
        splitted[1] = input.substring(lookupConceptStopIndex + lookupConceptStopKey.length() + 1).trim();
        return splitted;
    }
}
