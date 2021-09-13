package io.metadew.iesi.script.execution.instruction.lookup;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.DatabaseDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.DatabaseDatasetImplementationService;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.regex.Pattern;

public class DatasetLookup implements LookupInstruction {

    private final String DATASET_NAME_KEY = "name";

    private final String DATASET_ITEM_NAME_KEY = "datasetItemName";

    private final Pattern INPUT_PARAMETER_PATTERN = Pattern
            .compile("\\s*\"?(?<" + DATASET_NAME_KEY + ">(\\w|\\.)+)\"?\\s*,\\s*(?<" + DATASET_ITEM_NAME_KEY + ">(\\w|\\.)+)\\s*");

    private final ExecutionRuntime executionRuntime;
    private static final Logger LOGGER = LogManager.getLogger();

    public DatasetLookup(ExecutionRuntime executionRuntime) {
        this.executionRuntime = executionRuntime;
    }

    @Override
    public String getKeyword() {
        return "dataset";
    }

    @Override
    public String generateOutput(String parameters) {
        // TODO: parse with antlr

        String[] arguments = splitInput(parameters);
        DatabaseDatasetImplementation dataset = getDataset(DataTypeHandler.getInstance().resolve(arguments[0].trim(), executionRuntime));
        DataType lookupVariable = convertLookupVariable(DataTypeHandler.getInstance().resolve(arguments[1].trim(), executionRuntime));
        Optional<DataType> matchedDataItem;
        if (lookupVariable instanceof Text) {
            matchedDataItem = DatabaseDatasetImplementationService.getInstance().getDataItem(dataset, ((Text) lookupVariable).getString(), executionRuntime);
        } else {
            throw new IllegalArgumentException(MessageFormat.format("Cannot lookup {0} in dataset {1}", lookupVariable, dataset.toString()));
        }

        if (!matchedDataItem.isPresent()) {
            throw new IllegalArgumentException(MessageFormat.format("No dataset item {0} is attached to dataset {1}", arguments[1], dataset.toString()));
        } else {
            return matchedDataItem.get().toString();
        }
    }

    private DataType convertLookupVariable(DataType lookupVariable) {
        return lookupVariable;
    }

    private DatabaseDatasetImplementation getDataset(DataType dataset) {
        if (dataset instanceof Text) {
            return executionRuntime.getDataset(((Text) dataset).getString())
                    .orElseThrow(() -> new IllegalArgumentException(MessageFormat.format("No dataset found with reference name {0}", ((Text) dataset).getString())));
        } else if (dataset instanceof DatabaseDatasetImplementation) {
            return (DatabaseDatasetImplementation) dataset;
        } else {
            throw new IllegalArgumentException(MessageFormat.format("Dataset cannot be of type {0}", dataset.getClass()));
        }
    }

    private String[] splitInput(String input) {
        String lookupConceptStartKey = "{{";
        String lookupConceptStopKey = "}}";
        int lookupConceptStopIndex = 0;

        String[] splitted = new String[2];
        if (!input.contains(lookupConceptStartKey) && !input.contains(lookupConceptStopKey)) {
            splitted[0] = input.split(",")[0].trim();
            splitted[1] = input.split(",")[1].trim();
            return splitted;
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

        splitted[0] = input.substring(lookupConceptStartIndex, lookupConceptStopIndex + lookupConceptStopKey.length()).trim();
        splitted[1] = input.substring(lookupConceptStopIndex + lookupConceptStopKey.length() + 1).trim();
        return splitted;
    }
}
