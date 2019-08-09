package io.metadew.iesi.script.execution.instruction.lookup;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeService;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.script.execution.ExecutionControl;

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
    private final DataTypeService dataTypeService;

    public DatasetLookup(ExecutionControl executionControl) {
        this.executionControl = executionControl;
        this.dataTypeService = new DataTypeService(executionControl.getExecutionRuntime());
    }

    @Override
    public String getKeyword() {
        return "dataset";
    }

    @Override
    public String generateOutput(String parameters) {
        // TODO: parse with antlr
        Matcher inputParameterMatcher = INPUT_PARAMETER_PATTERN.matcher(parameters);
        if (!inputParameterMatcher.find()) {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to dataset lookup: {0}", parameters));
        }

        Dataset dataset = getDataset(dataTypeService.resolve(inputParameterMatcher.group(DATASET_NAME_KEY)));
        String datasetItemName = inputParameterMatcher.group(DATASET_ITEM_NAME_KEY);

        Optional<DataType> dataItem = dataset.getDataItem(datasetItemName);

        if (!dataItem.isPresent()) {
            throw new IllegalArgumentException(MessageFormat.format("No dataset item {0} is attached to dataset {1}", datasetItemName, dataset.toString()));
        } else {
            return dataItem.get().toString();
        }
    }

    private Dataset getDataset(DataType dataset) {
        if (dataset instanceof Text) {
            return executionControl.getExecutionRuntime().getDataset(((Text) dataset).getString())
                    .orElseThrow(() -> new IllegalArgumentException(MessageFormat.format("No dataset found with reference name {0}", ((Text) dataset).getString())));
        } else if (dataset instanceof Dataset) {
            return (Dataset) dataset;
        } else {
            throw new IllegalArgumentException(MessageFormat.format("Dataset cannot be of type {0}", dataset.getClass()));
        }
    }
}
