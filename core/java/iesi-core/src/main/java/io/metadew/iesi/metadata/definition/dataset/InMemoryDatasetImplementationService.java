package io.metadew.iesi.metadata.definition.dataset;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.dataset.DatasetHandler;
import io.metadew.iesi.datatypes.dataset.keyvalue.KeyValueDataset;
import io.metadew.iesi.script.execution.ExecutionRuntime;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryDatasetImplementationService implements IInMemoryDatasetImplementationService {
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
    public InMemoryDatasetImplementation createNewDatasetImplementation(String name, List<DatasetImplementationLabel> labels) {
        Dataset dataset;
        if (DatasetConfiguration.getInstance().getByName(name).isPresent()) {
            dataset = DatasetConfiguration.getInstance().getByName(name).get();
        } else {
            dataset = new Dataset(new DatasetKey(), name, new ArrayList<>());
            DatasetConfiguration.getInstance().insert(dataset);
        }
        InMemoryDatasetImplementation inMemoryDatasetImplementation = new InMemoryDatasetImplementation(
                new DatasetImplementationKey(),
                dataset.getMetadataKey(),
                dataset.getName(),
                labels,
                new ArrayList<>()
        );
        DatasetImplementationConfiguration.getInstance().insert(inMemoryDatasetImplementation);
        return inMemoryDatasetImplementation;
    }

    @Override
    public void clean(InMemoryDatasetImplementation datasetImplementation, ExecutionRuntime executionRuntime) {
        datasetImplementation.setKeyValues(new ArrayList<>());
        DatasetImplementationConfiguration.getInstance().update(datasetImplementation);
    }

    @Override
    public Optional<DataType> getDataItem(InMemoryDatasetImplementation datasetImplementation, String dataItem, ExecutionRuntime executionRuntime) {
        return datasetImplementation.getKeyValues().stream()
                .filter(inMemoryDatasetImplementationKeyValue -> inMemoryDatasetImplementationKeyValue.getKey().equals(dataItem))
                .findFirst()
                .map(InMemoryDatasetImplementationKeyValue::getValue)
                .map(dataValue -> DataTypeHandler.getInstance().resolve(dataValue, executionRuntime));
    }

    @Override
    public Map<String, DataType> getDataItems(InMemoryDatasetImplementation datasetImplementation, ExecutionRuntime executionRuntime) {
        return datasetImplementation.getKeyValues().stream()
                .collect(Collectors.toMap(
                        InMemoryDatasetImplementationKeyValue::getKey,
                        inMemoryDatasetImplementationKeyValue -> DataTypeHandler.getInstance().resolve(inMemoryDatasetImplementationKeyValue.getValue(), executionRuntime)
                ));
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



    public DataType resolve(KeyValueDataset dataset, String key, ObjectNode jsonNode, ExecutionRuntime executionRuntime) throws IOException {
        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        KeyValueDataset objectDataset = getObjectDataset(dataset, key);
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            DataType object = DataTypeHandler.getInstance().resolve(objectDataset, field.getKey(), field.getValue(), executionRuntime);
            DatasetHandler.getInstance().setDataItem(objectDataset, field.getKey(), object);
        }
        return objectDataset;
    }
}