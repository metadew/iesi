package io.metadew.iesi.script.action.data;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetConfiguration;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationService;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class DataSetDatasetConnection extends ActionTypeExecution {

    private static final String DATASET_KEY = "dataset";
    private static final String NAME_KEY = "name";
    private static final String LABELS_KEY = "labels";
    private static final String TYPE_KEY = "type";

    // Parameters
    private String referenceName;
    private String datasetName;
    private String datasetType;
    private List<String> datasetLabels;

    public DataSetDatasetConnection(ExecutionControl executionControl,
                                    ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        // Reset Parameters
        ActionParameterOperation referenceNameActionParameterOperation = new ActionParameterOperation(
                this.getExecutionControl(),
                this.getActionExecution(),
                this.getActionExecution().getAction().getType(),
                NAME_KEY);
        ActionParameterOperation datasetTypeActionParameterOperation = new ActionParameterOperation(
                this.getExecutionControl(),
                this.getActionExecution(),
                this.getActionExecution().getAction().getType(),
                TYPE_KEY);
        ActionParameterOperation datasetNameActionParameterOperation = new ActionParameterOperation(
                this.getExecutionControl(),
                this.getActionExecution(),
                this.getActionExecution().getAction().getType(),
                DATASET_KEY);
        ActionParameterOperation datasetLabelsActionParameterOperation = new ActionParameterOperation(
                this.getExecutionControl(),
                this.getActionExecution(),
                this.getActionExecution().getAction().getType(),
                LABELS_KEY);

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(NAME_KEY)) {
                referenceNameActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(TYPE_KEY)) {
                datasetTypeActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(DATASET_KEY)) {
                datasetNameActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(LABELS_KEY)) {
                datasetLabelsActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Default values
        if (datasetLabelsActionParameterOperation.getValue() == null)
            datasetLabelsActionParameterOperation.setInputValue("", getExecutionControl().getExecutionRuntime());

        // Create parameter list
        getActionParameterOperationMap().put(NAME_KEY, referenceNameActionParameterOperation);
        getActionParameterOperationMap().put(TYPE_KEY, datasetTypeActionParameterOperation);
        getActionParameterOperationMap().put(DATASET_KEY, datasetNameActionParameterOperation);
        getActionParameterOperationMap().put(LABELS_KEY, datasetLabelsActionParameterOperation);

        referenceName = convertDatasetReferenceName(referenceNameActionParameterOperation.getValue());
        datasetName = convertDatasetName(datasetNameActionParameterOperation.getValue());
        datasetType = convertDatasetType(datasetTypeActionParameterOperation.getValue());
        datasetLabels = convertDatasetLabels(datasetLabelsActionParameterOperation.getValue());
    }

    protected boolean executeAction() throws IOException {
        DatasetKey datasetKey = DatasetConfiguration.getInstance()
                .getIdByName(datasetName)
                .orElseGet(() -> {
                    log.warn(MessageFormat.format("Dataset {0} does not exists. Creating dataset now.", datasetName));
                    Dataset newDataset = Dataset.builder()
                            .metadataKey(new DatasetKey())
                            .name(datasetName)
                            .datasetImplementations(new HashSet<>())
                            .build();
                    DatasetConfiguration.getInstance().insert(newDataset);
                    return newDataset.getMetadataKey();
                });

        List<String> resolvedDatasetLabels = datasetLabels.stream()
                .map(datasetLabel -> getExecutionControl().getExecutionRuntime().resolveVariables(datasetLabel))
                .collect(Collectors.toList());

        InMemoryDatasetImplementation inMemoryDatasetImplementation = InMemoryDatasetImplementationService.getInstance()
                .getDatasetImplementation(datasetKey, resolvedDatasetLabels)
                .orElseGet(() -> {
                    log.warn(MessageFormat.format("DatasetImplementation {0}-{1} does not exists. Creating dataset implementation now", datasetName, resolvedDatasetLabels));
                    DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey();
                    InMemoryDatasetImplementation newInMemoryDatasetImplementation = new InMemoryDatasetImplementation(
                            datasetImplementationKey,
                            datasetKey,
                            datasetName,
                            resolvedDatasetLabels.stream()
                                    .map(s -> new DatasetImplementationLabel(new DatasetImplementationLabelKey(), datasetImplementationKey, s))
                                    .collect(Collectors.toSet()),
                            new HashSet<>());
                    DatasetImplementationConfiguration.getInstance().insert(newInMemoryDatasetImplementation);
                    return newInMemoryDatasetImplementation;
                });
        getExecutionControl().getExecutionRuntime()
                .setKeyValueDataset(referenceName, inMemoryDatasetImplementation);
        return true;
    }

    private String convertDatasetReferenceName(DataType referenceName) {
        if (!(referenceName instanceof Text)) {
            log.warn(MessageFormat.format("{0} does not accept {1} as type for reference name",
                    this.getActionExecution().getAction().getType(),
                    referenceName.getClass()));
        }
        return referenceName.toString();
    }


    private List<String> convertDatasetLabels(DataType datasetLabels) {
        List<String> labels = new ArrayList<>();
        if (datasetLabels instanceof Text) {
            Arrays.stream(datasetLabels.toString().split(","))
                    .forEach(datasetLabel -> labels.add(convertDatasetLabel(DataTypeHandler.getInstance().resolve(datasetLabel.trim(), getExecutionControl().getExecutionRuntime()))));
            return labels;
        } else if (datasetLabels instanceof Array) {
            ((Array) datasetLabels).getList()
                    .forEach(datasetLabel -> labels.add(convertDatasetLabel(datasetLabel)));
            return labels;
        } else {
            log.warn(MessageFormat.format("{0} does not accept {1} as type for dataset labels",
                    this.getActionExecution().getAction().getType(),
                    datasetLabels.getClass()));
            return labels;
        }
    }

    private String convertDatasetType(DataType datasetType) {
        if (datasetType == null) {
            return "";
        }
        if (!(datasetType instanceof Text)) {
            log.warn(MessageFormat.format("{0} does not accept {1} as type for dataset type",
                    this.getActionExecution().getAction().getType(),
                    datasetType.getClass()));
        }
        return datasetType.toString();
    }

    private String convertDatasetName(DataType datasetName) {
        if (!(datasetName instanceof Text)) {
            log.warn(MessageFormat.format("{0} does not accept {1} as type for dataset name",
                    this.getActionExecution().getAction().getType(),
                    datasetName.getClass()));
        }
        return datasetName.toString();
    }

    private String convertDatasetLabel(DataType datasetLabel) {
        if (!(datasetLabel instanceof Text)) {
            log.warn(MessageFormat.format("{0} does not accept {1} as type for a dataset label",
                    this.getActionExecution().getAction().getType(),
                    datasetLabel.getClass()));
        }
        return datasetLabel.toString();
    }

}