package io.metadew.iesi.datatypes.dataset.implementation;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationService;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationService;
import io.metadew.iesi.script.execution.ExecutionRuntime;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DatasetImplementationHandler implements IDatasetImplementationHandler{

    private Map<Class<? extends DatasetImplementation>, IDatasetImplementationService> datasetImplementationServiceMap;

    private static DatasetImplementationHandler instance;

    public static synchronized DatasetImplementationHandler getInstance() {
        if (instance == null) {
            instance = new DatasetImplementationHandler();
        }
        return instance;
    }

    private DatasetImplementationHandler() {
        datasetImplementationServiceMap = new HashMap<>();
        datasetImplementationServiceMap.put(DatabaseDatasetImplementationService.getInstance().appliesTo(), DatabaseDatasetImplementationService.getInstance());
        datasetImplementationServiceMap.put(InMemoryDatasetImplementationService.getInstance().appliesTo(), InMemoryDatasetImplementationService.getInstance());
    }

    @Override
    public boolean isEmpty(DatasetImplementation datasetImplementation) {
        return getDatasetImplementationService(datasetImplementation).isEmpty(datasetImplementation);
    }

    @Override
    public void delete(DatasetImplementation datasetImplementation, ExecutionRuntime executionRuntime) {
        getDatasetImplementationService(datasetImplementation).delete(datasetImplementation, executionRuntime);
    }

    @Override
    public void setDataItem(DatasetImplementation datasetImplementation, String key, DataType value) {
        getDatasetImplementationService(datasetImplementation).setDataItem(datasetImplementation, key, value);
    }

    @Override
    public Optional<DataType> getDataItem(DatasetImplementation datasetImplementation, String dataItem, ExecutionRuntime executionRuntime) {
        return getDatasetImplementationService(datasetImplementation).getDataItem(datasetImplementation, dataItem, executionRuntime);
    }

    @Override
    public Map<String, DataType> getDataItems(DatasetImplementation datasetImplementation, ExecutionRuntime executionRuntime) {
        return getDatasetImplementationService(datasetImplementation).getDataItems(datasetImplementation, executionRuntime);
    }

    @Override
    public void clean(DatasetImplementation datasetImplementation, ExecutionRuntime executionRuntime) {
        getDatasetImplementationService(datasetImplementation).clean(datasetImplementation, executionRuntime);
    }

    @Override
    public DataType resolve(DatasetImplementation dataset, String key, ObjectNode jsonNode, ExecutionRuntime executionRuntime) {
        return getDatasetImplementationService(dataset).resolve(dataset, key, jsonNode, executionRuntime);
    }

    private IDatasetImplementationService getDatasetImplementationService(DatasetImplementation datasetImplementation) {
        return datasetImplementationServiceMap.entrySet().stream()
                .filter(entry -> entry.getKey().isAssignableFrom(datasetImplementation.getClass()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find DatasetImplementationService for " + datasetImplementation.getClass().getSimpleName()));
    }

    @Override
    public Class<DatasetImplementation> appliesTo() {
        return DatasetImplementation.class;
    }

    @Override
    public String keyword() {
        return "dataset";
    }

    @Override
    public DatasetImplementation resolve(String input, ExecutionRuntime executionRuntime) {
        List<String> splittedArguments = DataTypeHandler.getInstance().splitInstructionArguments(input);
        if (splittedArguments.size() == 3) {
            if (splittedArguments.get(2).equalsIgnoreCase("database")){
                return DatabaseDatasetImplementationService.getInstance().resolve(input, executionRuntime);
            }
            else if (splittedArguments.get(2).equalsIgnoreCase("in_memory")){
                return InMemoryDatasetImplementationService.getInstance().resolve(input, executionRuntime);
            }
            else {
                throw new RuntimeException(MessageFormat.format("Cannot create dataset with arguments ''{0}''", splittedArguments.toString()));
            }
        } else if (splittedArguments.size() == 2) {
            return InMemoryDatasetImplementationService.getInstance().resolve(input, executionRuntime);
        }
        else {
            throw new RuntimeException(MessageFormat.format("Cannot create dataset with arguments ''{0}''", splittedArguments.toString()));
        }
    }

    @Override
    public boolean equals(DatasetImplementation _this, DatasetImplementation other, ExecutionRuntime executionRuntime) {
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
