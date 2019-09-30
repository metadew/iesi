package io.metadew.iesi.datatypes.dataset;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeService;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Dataset extends DataType {

    private String name;
    private List<String> labels;

    private Database datasetDatabase;
    private String tableName;
    private DatasetMetadata datasetMetadata;
    protected DataTypeService dataTypeService;

    private final static Logger LOGGER = LogManager.getLogger();

    public Dataset(DataType name, DataType labels, ExecutionRuntime executionRuntime) throws IOException {
        this.dataTypeService = new DataTypeService();
        this.name = convertDatasetName(name, executionRuntime);
        this.labels = convertDatasetLabels(labels, executionRuntime);
        this.datasetDatabase = initializeDatasetConnection(this.name, this.labels);
    }

    public Dataset(String name, List<String> labels, ExecutionRuntime executionRuntime) throws IOException {
        this.dataTypeService = new DataTypeService();
        this.name = name;
        this.labels = labels;
        name = executionRuntime.resolveVariables(name);
        labels = labels.stream()
                .map(executionRuntime::resolveVariables)
                .collect(Collectors.toList());
        this.datasetDatabase = initializeDatasetConnection(name, labels);
    }

    public abstract void clean();

    protected abstract Database createNewDatasetDatabase(String datasetName, String filename, String tableName, int inventoryId) throws IOException;

    public abstract Optional<DataType> getDataItem(String dataItem, ExecutionRuntime executionRuntime);
    public abstract Map<String, DataType> getDataItems(ExecutionRuntime executionRuntime);
    public abstract void setDataItem(String key, DataType value);

    private List<String> convertDatasetLabels(DataType datasetLabels, ExecutionRuntime executionRuntime) {
        List<String> labels = new ArrayList<>();
        if (datasetLabels instanceof Text) {
            Arrays.stream(datasetLabels.toString().split(","))
                    .forEach(datasetLabel -> labels.add(convertDatasetLabel(dataTypeService.resolve(datasetLabel.trim(), executionRuntime))));
            return labels;
        } else if (datasetLabels instanceof Array) {
            ((Array) datasetLabels).getList()
                    .forEach(datasetLabel -> labels.add(convertDatasetLabel(datasetLabel)));
            return labels;
        } else {
            LOGGER.warn(MessageFormat.format("dataset does not accept {0} as type for datasetDatabase labels",
                    datasetLabels.getClass()));
            return labels;
        }
    }

    private String convertDatasetName(DataType datasetName, ExecutionRuntime executionRuntime) {
        if (datasetName instanceof Text) {
            // String variablesResolved = executionRuntime.resolveVariables(datasetName.toString());
            // return executionRuntime.resolveConceptLookup(variablesResolved).getValue();
            return ((Text) datasetName).getString();
        } else {
            LOGGER.warn(MessageFormat.format("dataset does not accept {0} as type for datasetDatabase name",
                    datasetName.getClass()));
            return datasetName.toString();
        }
    }

    private String convertDatasetLabel(DataType datasetLabel) {
        if (datasetLabel instanceof Text) {
            // String variablesResolved = executionRuntime.resolveVariables(datasetLabel.toString());
            // return executionRuntime.resolveConceptLookup(variablesResolved).getValue();
            return ((Text) datasetLabel).getString();
        } else {
            LOGGER.warn(MessageFormat.format("dataset does not accept {0} as type for a datasetDatabase label",
                    datasetLabel.getClass()));
            return datasetLabel.toString();
        }
    }

    private Database initializeDatasetConnection(String datasetName, List<String> labels) throws IOException {
        datasetMetadata = new DatasetMetadata(datasetName);
        if (labels.isEmpty()) {
            return null;
        }
        Optional<Long> id = datasetMetadata.getId(labels);
        if (!id.isPresent()) {
            return createNewDataset(datasetName, labels);
        }
        long datasetInventoryId = id.get();
        tableName = datasetMetadata.getTableName(datasetInventoryId);
        return datasetMetadata.getDatasetDatabase(datasetInventoryId);
    }

    protected Database createNewDataset(String datasetName, List<String> labels) throws IOException {
        LOGGER.trace(MessageFormat.format("datatype.dataset=initializing new dataset database for ''{0}'' with labels {1}", datasetName, labels.toString()));
        int nextInventoryId = getDatasetMetadata().getLatestInventoryId() + 1;
        String datasetFilename = UUID.randomUUID().toString() + ".db3";
        tableName = "data";
        getDatasetMetadata().insertDatasetLabelInformation(nextInventoryId, labels);
        return createNewDatasetDatabase(datasetName, datasetFilename, tableName, nextInventoryId);
    }


    public List<String> getLabels() {
        return labels;
    }

    public String getName() {
        return name;
    }

    public String getTableName() {
        return tableName;
    }

    protected void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public DatasetMetadata getDatasetMetadata() {
        return datasetMetadata;
    }


    public DataType getNameAsDataType() {
        return new Text(name);
    }

    public DataType getLabelsAsDataType() {
        return new Array(labels.stream().map(Text::new).collect(Collectors.toList()));
    }

    public Database getDatasetDatabase() {
        return datasetDatabase;
    }
}


