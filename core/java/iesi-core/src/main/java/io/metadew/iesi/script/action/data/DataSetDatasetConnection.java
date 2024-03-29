package io.metadew.iesi.script.action.data;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes._null.Null;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetConfiguration;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationService;
import io.metadew.iesi.datatypes.dataset.implementation.in.memory.InMemoryDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
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

    public void prepareAction() {
        referenceName = convertDatasetReferenceName(getParameterResolvedValue(NAME_KEY));
        datasetName = convertDatasetName(getParameterResolvedValue(DATASET_KEY));
        datasetType = convertDatasetType(getParameterResolvedValue(TYPE_KEY));
        datasetLabels = convertDatasetLabels(getParameterResolvedValue(LABELS_KEY));
    }

    protected boolean executeAction() throws IOException {
        SecurityGroup securityGroup = SpringContext.getBean(SecurityGroupConfiguration.class).getByName("PUBLIC")
                .orElseThrow(() -> new RuntimeException("As the dataset doesn't exist, tried to create new one with the security group PUBLIC, but the group doesn't exist"));
        DatasetKey datasetKey = SpringContext.getBean(DatasetConfiguration.class)
                .getIdByName(datasetName)
                .orElseGet(() -> {
                    log.warn(MessageFormat.format("Dataset {0} does not exists. Creating dataset now.", datasetName));
                    Dataset newDataset = Dataset.builder()
                            .metadataKey(new DatasetKey())
                            .securityGroupKey(securityGroup.getMetadataKey())
                            .securityGroupName(securityGroup.getName())
                            .name(datasetName)
                            .datasetImplementations(new HashSet<>())
                            .build();
                    SpringContext.getBean(DatasetConfiguration.class).insert(newDataset);
                    return newDataset.getMetadataKey();
                });

        List<String> resolvedDatasetLabels = datasetLabels.stream()
                .map(datasetLabel -> getExecutionControl().getExecutionRuntime().resolveVariables(datasetLabel))
                .collect(Collectors.toList());

        DatasetImplementation datasetImplementation;
        if (datasetType != null && datasetType.equals("database")) {
            datasetImplementation = DatabaseDatasetImplementationService.getInstance()
                    .getDatasetImplementation(datasetKey, resolvedDatasetLabels)
                    .orElseGet(() -> {
                        log.warn(MessageFormat.format("DatasetImplementation {0}-{1} does not exists. Creating dataset implementation now", datasetName, resolvedDatasetLabels));
                        DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey();
                        DatabaseDatasetImplementation newDatabaseDatasetImplementation = new DatabaseDatasetImplementation(
                                datasetImplementationKey,
                                datasetKey,
                                datasetName,
                                resolvedDatasetLabels.stream()
                                        .map(s -> new DatasetImplementationLabel(new DatasetImplementationLabelKey(), datasetImplementationKey, s))
                                        .collect(Collectors.toSet()),
                                new HashSet<>());
                        SpringContext.getBean(DatasetImplementationConfiguration.class).insert(newDatabaseDatasetImplementation);
                        return newDatabaseDatasetImplementation;
                    });
        } else if (datasetType != null && datasetType.equals("in_memory")) {
            DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey();
            datasetImplementation = new InMemoryDatasetImplementation(datasetImplementationKey,
                    datasetKey,
                    datasetName,
                    resolvedDatasetLabels.stream()
                            .map(s -> new DatasetImplementationLabel(new DatasetImplementationLabelKey(), datasetImplementationKey, s))
                            .collect(Collectors.toSet()),
                    new HashSet<>());
        } else {
            throw new RuntimeException(String.format("{0} is not recognized dataset type"));
        }
        getExecutionControl().getExecutionRuntime()
                .setKeyValueDataset(referenceName, datasetImplementation);
        return true;
    }

    @Override
    protected String getKeyword() {
        return "data.setDatasetConnection";
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
                    .forEach(datasetLabel -> labels.add(convertDatasetLabel(SpringContext.getBean(DataTypeHandler.class).resolve(datasetLabel.trim(), getExecutionControl().getExecutionRuntime()))));
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
        if (datasetType == null || datasetType instanceof Null) {
            return "in_memory";
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