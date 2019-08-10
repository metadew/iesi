package io.metadew.iesi.datatypes.dataset;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeService;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Dataset extends DataType {

    private ExecutionRuntime executionRuntime;
    private String name;
    private List<String> labels;

    private Database datasetDatabase;
    private String tableName;
    private DatasetMetadata datasetMetadata;
    protected DataTypeService dataTypeService;

    private final static Logger LOGGER = LogManager.getLogger();

    public Dataset(DataType name, DataType labels, ExecutionRuntime executionRuntime) throws IOException, SQLException {
        this.executionRuntime = executionRuntime;
        this.dataTypeService = new DataTypeService(executionRuntime);
        this.name = convertDatasetName(name);
        this.labels = convertDatasetLabels(labels);
        this.datasetDatabase = initializeDatasetConnection(this.name, this.labels);
    }

    public Dataset(String name, List<String> labels, ExecutionRuntime executionRuntime) throws IOException, SQLException {
        LOGGER.trace(MessageFormat.format("Creating dataset ''{0}'' with labels {1}", name, labels.toString()));
        this.executionRuntime = executionRuntime;
        this.dataTypeService = new DataTypeService(executionRuntime);
        this.name = name;
        this.labels = labels;
        this.datasetDatabase = initializeDatasetConnection(name, labels);
    }

    public abstract void clean();

    protected abstract Database createNewDatasetDatabase(String datasetName, String filename, String tableName, int inventoryId) throws IOException;

    public abstract Optional<DataType> getDataItem(String dataItem);
    public abstract Map<String, DataType> getDataItems();
    public abstract void setDataItem(String key, DataType value);

    private List<String> convertDatasetLabels(DataType datasetLabels) {
        List<String> labels = new ArrayList<>();
        if (datasetLabels instanceof Text) {
            Arrays.stream(datasetLabels.toString().split(","))
                    .forEach(datasetLabel -> labels.add(convertDatasetLabel(dataTypeService.resolve(datasetLabel.trim()))));
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

    private String convertDatasetName(DataType datasetName) {
        if (datasetName instanceof Text) {
            String variablesResolved = executionRuntime.resolveVariables(datasetName.toString());
            return executionRuntime.resolveConceptLookup(executionRuntime.getExecutionControl(), variablesResolved, true).getValue();
        } else {
            LOGGER.warn(MessageFormat.format("dataset does not accept {0} as type for datasetDatabase name",
                    datasetName.getClass()));
            return datasetName.toString();
        }
    }

    private String convertDatasetLabel(DataType datasetLabel) {
        if (datasetLabel instanceof Text) {
            String variablesResolved = executionRuntime.resolveVariables(datasetLabel.toString());
            return executionRuntime.resolveConceptLookup(executionRuntime.getExecutionControl(), variablesResolved, true).getValue();
        } else {
            LOGGER.warn(MessageFormat.format("dataset does not accept {0} as type for a datasetDatabase label",
                    datasetLabel.getClass()));
            return datasetLabel.toString();
        }
    }

    private Database initializeDatasetConnection(String datasetName, List<String> labels) throws SQLException, IOException {
        datasetMetadata = new DatasetMetadata(datasetName, executionRuntime);
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
        LOGGER.trace(MessageFormat.format("Initializing new dataset for ''{0}'' with labels {1}", datasetName, labels.toString()));
        int nextInventoryId = getDatasetMetadata().getLatestInventoryId() + 1;
        String datasetFilename = UUID.randomUUID().toString() + ".db3";
        setTableName("data");
        getDatasetMetadata().insertDatasetLabelInformation(nextInventoryId, labels);
        return createNewDatasetDatabase(datasetName, datasetFilename, "data", nextInventoryId);
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

    public ExecutionRuntime getExecutionRuntime() {
        return executionRuntime;
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


