package io.metadew.iesi.datatypes.dataset.implementation.inmemory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.IDataTypeService;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetConfiguration;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationService;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import lombok.extern.log4j.Log4j2;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class InMemoryDatasetImplementationService extends DatasetImplementationService<InMemoryDatasetImplementation> implements IInMemoryDatasetImplementationService, IDataTypeService<InMemoryDatasetImplementation> {

    private static InMemoryDatasetImplementationService INSTANCE;

    public synchronized static InMemoryDatasetImplementationService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InMemoryDatasetImplementationService();
        }
        return INSTANCE;
    }

    private InMemoryDatasetImplementationService() {
    }

    @Override
    public Optional<InMemoryDatasetImplementation> getDatasetImplementation(String name, List<String> labels) {
        return DatasetImplementationConfiguration.getInstance().getByNameAndLabels(name, labels)
                .map(datasetImplementation -> (InMemoryDatasetImplementation) datasetImplementation);
    }

    @Override
    public InMemoryDatasetImplementation createNewDatasetImplementation(String name, List<String> labels) {
        Dataset dataset;
        if (DatasetConfiguration.getInstance().getByName(name).isPresent()) {
            dataset = DatasetConfiguration.getInstance().getByName(name).get();
        } else {
            dataset = new Dataset(new DatasetKey(), name, new HashSet<>());
            DatasetConfiguration.getInstance().insert(dataset);
        }
        DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey();
        InMemoryDatasetImplementation inMemoryDatasetImplementation = new InMemoryDatasetImplementation(
                datasetImplementationKey,
                dataset.getMetadataKey(),
                dataset.getName(),
                labels.stream()
                        .map(s -> new DatasetImplementationLabel(new DatasetImplementationLabelKey(), datasetImplementationKey, s))
                        .collect(Collectors.toSet()),
                new HashSet<>()
        );
        DatasetImplementationConfiguration.getInstance().insert(inMemoryDatasetImplementation);
        return inMemoryDatasetImplementation;
    }

    @Override
    public void clean(InMemoryDatasetImplementation datasetImplementation, ExecutionRuntime executionRuntime) {
        datasetImplementation.setKeyValues(new HashSet<>());
        DatasetImplementationConfiguration.getInstance().update(datasetImplementation);
    }

    @Override
    public Optional<DataType> getDataItem(InMemoryDatasetImplementation datasetImplementation, String dataItem, ExecutionRuntime executionRuntime) {
        return datasetImplementation.getKeyValues().stream()
                .filter(inMemoryDatasetImplementationKeyValue -> inMemoryDatasetImplementationKeyValue.getKey().equals(dataItem))
                .findFirst()
                .map(inMemoryDatasetImplementationKeyValue -> DataTypeHandler.getInstance().resolve(inMemoryDatasetImplementationKeyValue.getValue(), executionRuntime));
    }

    @Override
    public Map<String, DataType> getDataItems(InMemoryDatasetImplementation datasetImplementation, ExecutionRuntime executionRuntime) {
        // https://bugs.openjdk.java.net/browse/JDK-8148463
        HashMap<String, DataType> map = new HashMap<>();
        datasetImplementation.getKeyValues().forEach(
                inMemoryDatasetImplementationKeyValue -> {
                    DataType dataType = DataTypeHandler.getInstance().resolve(inMemoryDatasetImplementationKeyValue.getValue(), executionRuntime);
                    map.put(inMemoryDatasetImplementationKeyValue.getKey(), dataType);
                }
        );
        return map;
    }

    @Override
    public void setDataItem(InMemoryDatasetImplementation datasetImplementation, String key, DataType value) {
        datasetImplementation.getKeyValues().stream()
                .filter(inMemoryDatasetImplementationKeyValue -> inMemoryDatasetImplementationKeyValue.getKey().equals(key))
                .findFirst()
                .map(inMemoryDatasetImplementationKeyValue -> {
                    inMemoryDatasetImplementationKeyValue.setValue(value.toString());
                    return datasetImplementation;
                })
                .orElseGet(() -> {
                    datasetImplementation.getKeyValues().add(new InMemoryDatasetImplementationKeyValue(new InMemoryDatasetImplementationKeyValueKey(), datasetImplementation.getMetadataKey(), key, value.toString()));
                    return datasetImplementation;
                });
        DatasetImplementationConfiguration.getInstance().update(datasetImplementation);
    }

    public DataType resolve(InMemoryDatasetImplementation dataset, String key, ObjectNode jsonNode, ExecutionRuntime executionRuntime) {
        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        InMemoryDatasetImplementation inMemoryDatasetImplementation = getObjectDataset(dataset, key);
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            DataType object = DataTypeHandler.getInstance().resolve(inMemoryDatasetImplementation, field.getKey(), field.getValue(), executionRuntime);
            setDataItem(inMemoryDatasetImplementation, field.getKey(), object);
        }
        return inMemoryDatasetImplementation;
    }


    private InMemoryDatasetImplementation getObjectDataset(InMemoryDatasetImplementation inMemoryDatasetImplementation, String keyPrefix) {
        if (keyPrefix != null) {
            List<String> labels = inMemoryDatasetImplementation.getDatasetImplementationLabels().stream()
                    .map(DatasetImplementationLabel::getValue)
                    .collect(Collectors.toList());
            labels.add(keyPrefix);
            return createNewDatasetImplementation(inMemoryDatasetImplementation.getName(), labels);
        } else {
            return inMemoryDatasetImplementation;
        }
    }

    @Override
    public Class<InMemoryDatasetImplementation> appliesTo() {
        return InMemoryDatasetImplementation.class;
    }

    @Override
    public String keyword() {
        return "dataset";
    }

    @Override
    public InMemoryDatasetImplementation resolve(String arguments, ExecutionRuntime executionRuntime) {
        log.trace(MessageFormat.format("resolving {0} for Dataset", arguments));
        arguments = executionRuntime.resolveVariables(arguments);
        List<String> splittedArguments = DataTypeHandler.getInstance().splitInstructionArguments(arguments);
        if (splittedArguments.size() == 2) {
            List<DataType> resolvedArguments = splittedArguments.stream()
                    .map(argument -> DataTypeHandler.getInstance().resolve(argument, executionRuntime))
                    .collect(Collectors.toList());
            return getDatasetImplementation(
                    convertDatasetName(resolvedArguments.get(0)),
                    convertDatasetLabels(resolvedArguments.get(1), executionRuntime))
                    .orElseGet(() -> createNewDatasetImplementation(convertDatasetName(resolvedArguments.get(0)), convertDatasetLabels(resolvedArguments.get(1), executionRuntime)));
        } else {
            throw new RuntimeException(MessageFormat.format("Cannot create dataset with arguments ''{0}''", splittedArguments.toString()));
        }
    }

    private String convertDatasetName(DataType datasetName) {
        if (datasetName instanceof Text) {
            return ((Text) datasetName).getString();
        } else {
            log.warn(MessageFormat.format("dataset does not accept {0} as type for datasetDatabase name",
                    datasetName.getClass()));
            return datasetName.toString();
        }
    }


    private List<String> convertDatasetLabels(DataType datasetLabels, ExecutionRuntime executionRuntime) {
        List<String> labels = new ArrayList<>();
        if (datasetLabels instanceof Text) {
            Arrays.stream(datasetLabels.toString().split(","))
                    .forEach(datasetLabel -> labels.add(convertDatasetLabel(DataTypeHandler.getInstance().resolve(datasetLabel.trim(), executionRuntime), executionRuntime)));
            return labels;
        } else if (datasetLabels instanceof Array) {
            ((Array) datasetLabels).getList()
                    .forEach(datasetLabel -> labels.add(convertDatasetLabel(datasetLabel, executionRuntime)));
            return labels;
        } else {
            log.warn(MessageFormat.format("dataset does not accept {0} as type for datasetDatabase labels",
                    datasetLabels.getClass()));
            return labels;
        }
    }


    private String convertDatasetLabel(DataType datasetLabel, ExecutionRuntime executionRuntime) {
        if (datasetLabel instanceof Text) {
            return executionRuntime.resolveVariables(((Text) datasetLabel).getString());
        } else {
            log.warn(MessageFormat.format("dataset does not accept {0} as type for a datasetDatabase label",
                    datasetLabel.getClass()));
            return executionRuntime.resolveVariables(datasetLabel.toString());
        }
    }

    @Override
    public boolean equals(InMemoryDatasetImplementation _this, InMemoryDatasetImplementation other, ExecutionRuntime executionRuntime) {
        if (_this == null && other == null) {
            return true;
        }
        if (_this == null || other == null) {
            return false;
        }
        if (!_this.getClass().equals(other.getClass())) {
            return false;
        }
        Map<String, DataType> thisDataItems = getDataItems(_this, executionRuntime);
        Map<String, DataType> otherDataItems = getDataItems(other, executionRuntime);
        if (!thisDataItems.keySet().equals(otherDataItems.keySet())) {
            return false;
        }
        for (Map.Entry<String, DataType> thisDataItem : thisDataItems.entrySet()) {
            if (!getDataItem(other, thisDataItem.getKey(), executionRuntime)
                    .map(dataType -> DataTypeHandler.getInstance().equals(dataType, thisDataItem.getValue(), executionRuntime))
                    .orElse(false)) {
                return false;
            }
        }
        return true;
    }
}