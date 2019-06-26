package io.metadew.iesi.datatypes.dataset;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.datatypes.Array;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeResolver;
import io.metadew.iesi.datatypes.Text;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Dataset extends DataType {

    private ExecutionRuntime executionRuntime;
    private String name;
    private List<String> labels;

    private final FrameworkFolderConfiguration frameworkFolderConfiguration;
    private Database datasetDatabase;
    private String tableName;
    private DatasetMetadata datasetMetadata;

    public Dataset(DataType name, DataType labels, FrameworkFolderConfiguration frameworkFolderConfiguration, ExecutionRuntime executionRuntime) throws IOException, SQLException {
        // TODO: centralize resolvement to circumvent ExecutionRuntime needs
        // TODO: include types
        this.frameworkFolderConfiguration = frameworkFolderConfiguration;
        this.executionRuntime = executionRuntime;
        this.datasetDatabase = initializeDatasetConnection(name, labels);
    }

    public Dataset(String name, List<String> labels, FrameworkFolderConfiguration frameworkFolderConfiguration, ExecutionRuntime executionRuntime) throws IOException, SQLException {
        this.frameworkFolderConfiguration = frameworkFolderConfiguration;
        this.executionRuntime = executionRuntime;
        this.name = name;
        this.labels = labels;
        this.datasetDatabase = initializeDatasetConnection(name, labels);
    }

    private Database initializeDatasetConnection(DataType name, DataType labels) throws IOException, SQLException {
        this.name = convertDatasetName(name);
        this.labels = convertDatasetLabels(labels);
        return initializeDatasetConnection(this.name, this.labels);
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
                    .forEach(datasetLabel -> labels.add(convertDatasetLabel(DataTypeResolver.resolveToDataType(datasetLabel.trim(), frameworkFolderConfiguration, executionRuntime))));
            return labels;
        } else if (datasetLabels instanceof Array) {
            ((Array) datasetLabels).getList()
                    .forEach(datasetLabel -> labels.add(convertDatasetLabel(datasetLabel)));
            return labels;
        } else {
            executionRuntime.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format("dataset does not accept {0} as type for datasetDatabase labels",
                    datasetLabels.getClass()), Level.WARN);
            return labels;
        }
    }

    private String convertDatasetName(DataType datasetName) {
        if (datasetName instanceof Text) {
            String variablesResolved = executionRuntime.resolveVariables(datasetName.toString());
            return executionRuntime.resolveConceptLookup(executionRuntime.getExecutionControl(), variablesResolved, true).getValue();
        } else {
            executionRuntime.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format("dataset does not accept {0} as type for datasetDatabase name",
                    datasetName.getClass()), Level.WARN);
            return datasetName.toString();
        }
    }

    private String convertDatasetLabel(DataType datasetLabel) {
        if (datasetLabel instanceof Text) {
            String variablesResolved = executionRuntime.resolveVariables(datasetLabel.toString());
            return executionRuntime.resolveConceptLookup(executionRuntime.getExecutionControl(), variablesResolved, true).getValue();
        } else {
            executionRuntime.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format("dataset does not accept {0} as type for a datasetDatabase label",
                    datasetLabel.getClass()), Level.WARN);
            return datasetLabel.toString();
        }
    }

    private Database initializeDatasetConnection(String datasetName, List<String> labels) throws SQLException, IOException {
        datasetMetadata = new DatasetMetadata(datasetName, executionRuntime, frameworkFolderConfiguration);
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

    public FrameworkFolderConfiguration getFrameworkFolderConfiguration() {
        return frameworkFolderConfiguration;
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


