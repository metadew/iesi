package io.metadew.iesi.script.execution.instruction.lookup;

import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.operation.DatasetOperation;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatasetLookup implements LookupInstruction {

    private final String DATASET_NAME_KEY = "name";

    private final String DATASET_ITEM_NAME_KEY = "datasetItemName";

    private final Pattern INPUT_PARAMETER_PATTERN = Pattern
            .compile("\\s*\"?(?<" + DATASET_NAME_KEY + ">(\\w|\\.)+)\"?\\s*,\\s*(?<" + DATASET_ITEM_NAME_KEY + ">(\\w|\\.)+)\\s*");

    private final ExecutionControl executionControl;

    public DatasetLookup(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    @Override
    public String getKeyword() {
        return "dataset";
    }

    @Override
    public String generateOutput(String parameters) {
        Matcher inputParameterMatcher = INPUT_PARAMETER_PATTERN.matcher(parameters);
        if (!inputParameterMatcher.find()) {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to dataset lookup: {0}", parameters));
        }
        String datasetName = inputParameterMatcher.group(DATASET_NAME_KEY);
        String datasetItemName = inputParameterMatcher.group(DATASET_ITEM_NAME_KEY);

        DatasetOperation datasetOperation = executionControl.getExecutionRuntime().getDatasetOperation(datasetName);
        Optional<String> datasetItemValue = datasetOperation.getDataItem(datasetItemName);
        if (!datasetItemValue.isPresent()) {
            throw new IllegalArgumentException(MessageFormat.format("No dataset item {0} is attached to dataset {1}", datasetItemName, datasetName));
        } else {
            return datasetItemValue.get();
        }
    }
}
