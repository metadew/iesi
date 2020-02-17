package io.metadew.iesi.datatypes.dataset;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.dataset.keyvalue.KeyValueDatasetService;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

@Log4j2
public class DatasetHandler {

    private static DatasetHandler INSTANCE;
    private Map<Class<? extends Dataset>, DatasetService> datasetServiceMap;

    public synchronized static DatasetHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DatasetHandler();
        }
        return INSTANCE;
    }

    private DatasetHandler() {
        datasetServiceMap = new HashMap<>();
        datasetServiceMap.put(KeyValueDatasetService.getInstance().appliesTo(), KeyValueDatasetService.getInstance());
    }

    @SuppressWarnings("unchecked")
    public void clean(Dataset dataset, ExecutionRuntime executionRuntime) {
        getDatasetService(dataset).clean(dataset, executionRuntime);
    }

    @SuppressWarnings("unchecked")
    public Dataset getByNameAndLabels(String name, List<String> labels, ExecutionRuntime executionRuntime) throws IOException {
        return KeyValueDatasetService.getInstance().getByNameAndLabels(name, labels, executionRuntime);
    }

    @SuppressWarnings("unchecked")
    public Dataset getByNameAndLabels(DataType name, DataType labels, ExecutionRuntime executionRuntime) throws IOException {
        return KeyValueDatasetService.getInstance().getByNameAndLabels(convertDatasetName(name), convertDatasetLabels(labels, executionRuntime), executionRuntime);
    }

    @SuppressWarnings("unchecked")
    public Optional<DataType> getDataItem(Dataset dataset, String dataItem, ExecutionRuntime executionRuntime) {
        return getDatasetService(dataset).getDataItem(dataset, dataItem, executionRuntime);
    }

    @SuppressWarnings("unchecked")
    public Map<String, DataType> getDataItems(Dataset dataset, ExecutionRuntime executionRuntime) {return getDatasetService(dataset).getDataItems(dataset, executionRuntime);}

    @SuppressWarnings("unchecked")
    public void setDataItem(Dataset dataset, String key, DataType value) {
        getDatasetService(dataset).setDataItem(dataset, key, value);
    }

    private DatasetService getDatasetService(Dataset dataset) {
        DatasetService datasetService = datasetServiceMap.get(dataset.getClass());
        if (datasetService == null) {
            throw new RuntimeException("No dataset service found to handle dataset of type " + dataset.getClass().getSimpleName());
        } else {
            return datasetService;
        }
    }

    public List<String> convertDatasetLabels(DataType datasetLabels, ExecutionRuntime executionRuntime) {
        List<String> labels = new ArrayList<>();
        if (datasetLabels instanceof Text) {
            Arrays.stream(datasetLabels.toString().split(","))
                    .forEach(datasetLabel -> labels.add(convertDatasetLabel(DataTypeHandler.getInstance().resolve(datasetLabel.trim(), executionRuntime))));
            return labels;
        } else if (datasetLabels instanceof Array) {
            ((Array) datasetLabels).getList()
                    .forEach(datasetLabel -> labels.add(convertDatasetLabel(datasetLabel)));
            return labels;
        } else {
            log.warn(MessageFormat.format("dataset does not accept {0} as type for datasetDatabase labels",
                    datasetLabels.getClass()));
            return labels;
        }
    }

    public String convertDatasetName(DataType datasetName) {
        if (datasetName instanceof Text) {
            // String variablesResolved = executionRuntime.resolveVariables(datasetName.toString());
            // return executionRuntime.resolveConceptLookup(variablesResolved).getValue();
            return ((Text) datasetName).getString();
        } else {
            log.warn(MessageFormat.format("dataset does not accept {0} as type for datasetDatabase name",
                    datasetName.getClass()));
            return datasetName.toString();
        }
    }

    public String convertDatasetLabel(DataType datasetLabel) {
        if (datasetLabel instanceof Text) {
            return ((Text) datasetLabel).getString();
        } else {
            log.warn(MessageFormat.format("dataset does not accept {0} as type for a datasetDatabase label",
                    datasetLabel.getClass()));
            return datasetLabel.toString();
        }
    }
}
