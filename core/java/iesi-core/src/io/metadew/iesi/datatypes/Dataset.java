package io.metadew.iesi.datatypes;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LeafPropertyLoader;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.metadata_repository.repository.database.Database;
import io.metadew.iesi.metadata_repository.repository.database.SqliteDatabase;
import io.metadew.iesi.metadata_repository.repository.database.connection.SqliteDatabaseConnection;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEventListener;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Dataset extends DataType {

    private ExecutionRuntime executionRuntime;
    private String name;
    private List<String> labels;

    private final FrameworkFolderConfiguration frameworkFolderConfiguration;
    private Database dataset;
    private String tableName;
    private Database datasetMetadataConnection;

    public Dataset(DataType name, DataType labels, FrameworkFolderConfiguration frameworkFolderConfiguration, ExecutionRuntime executionRuntime) throws IOException, SQLException {
        // TODO: centralize resolvement to circumvent ExecutionRuntime needs
        this.frameworkFolderConfiguration = frameworkFolderConfiguration;
        this.executionRuntime = executionRuntime;
        this.dataset = initializeDatasetConnection(name, labels);
    }

    public Dataset(String name, List<String> labels, FrameworkFolderConfiguration frameworkFolderConfiguration, ExecutionRuntime executionRuntime) throws IOException, SQLException {
        this.frameworkFolderConfiguration = frameworkFolderConfiguration;
        this.executionRuntime = executionRuntime;
        this.dataset = initializeDatasetConnection(name, labels);
    }

    public DataType getNameAsDataType() {
        return new Text(name);
    }

    public DataType getLabelsAsDataType() {
        return new Array(labels.stream().map(Text::new).collect(Collectors.toList()));
    }

    public Map<String, DataType> getDataItems() {
        CachedRowSet crs;
        String query;
        query = "select key, value from " +
                tableName + ";";

        Map<String, DataType> dataItems = new HashMap<>();
        crs = dataset.executeQuery(query);
        try {
            while (crs.next()) {
                dataItems.put(crs.getString("key"), DataTypeResolver.resolveToDataType(crs.getString("value"), frameworkFolderConfiguration, executionRuntime));
            }
            crs.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            return dataItems;
        }
        return dataItems;
    }

    public Optional<DataType> getDataItem(String dataItem) {
        CachedRowSet crs;
        String query;
        query = "select value from \"" +
                tableName +
                "\" where key = '" + dataItem + "'";

        DataType value = null;
        crs = dataset.executeQuery(query);
        try {
            while (crs.next()) {
                value = DataTypeResolver.resolveToDataType(crs.getString("VALUE"), frameworkFolderConfiguration, executionRuntime);
            }
            crs.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            return Optional.empty();
        }
        return Optional.ofNullable(value);
    }

    public Database getDatasetDatabase() {
        return dataset;
    }

    private Database initializeDatasetConnection(DataType name, DataType labels) throws IOException, SQLException {
        this.name = convertDatasetName(name);
        this.labels = convertDatasetLabels(labels);
        return initializeDatasetConnection(this.name, this.labels);
    }

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
            executionRuntime.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format("Dataset does not accept {0} as type for dataset labels",
                    datasetLabels.getClass()), Level.WARN);
            return labels;
        }
    }

    private String convertDatasetName(DataType datasetName) {
        if (datasetName instanceof Text) {
            String variablesResolved = executionRuntime.resolveVariables(datasetName.toString());
            return executionRuntime.resolveConceptLookup(executionRuntime.getExecutionControl(), variablesResolved, true).getValue();
        } else {
            executionRuntime.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format("Dataset does not accept {0} as type for dataset name",
                    datasetName.getClass()), Level.WARN);
            return datasetName.toString();
        }
    }

    private String convertDatasetLabel(DataType datasetLabel) {
        if (datasetLabel instanceof Text) {
            String variablesResolved = executionRuntime.resolveVariables(datasetLabel.toString());
            return executionRuntime.resolveConceptLookup(executionRuntime.getExecutionControl(), variablesResolved, true).getValue();
        } else {
            executionRuntime.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format("Dataset does not accept {0} as type for a dataset label",
                    datasetLabel.getClass()), Level.WARN);
            return datasetLabel.toString();
        }
    }

    private Database getDatasetMetadata(String datasetName) {
        return new SqliteDatabase(new SqliteDatabaseConnection(frameworkFolderConfiguration.getFolderAbsolutePath("data") + File.separator + "datasets"
                + File.separator + datasetName + File.separator + "metadata" + File.separator + "metadata.db3"));
    }

    private Database initializeDatasetConnection(String datasetName, List<String> labels) throws SQLException, IOException {
        datasetMetadataConnection = getDatasetMetadata(datasetName);
        if (labels.isEmpty()) {
            return null;
        }

        String fetchLabelQuery = "SELECT DATASET_INV_ID FROM CFG_DATASET_LBL WHERE DATASET_LBL_VAL = \"{0}\"";
        StringBuilder labelMatchQuery = new StringBuilder();
        int labelIndex = 0;
        for (String label : labels) {
            labelMatchQuery.append(MessageFormat.format(fetchLabelQuery, label));
            if (labelIndex != labels.size() - 1) {
                labelMatchQuery.append(" and DATASET_INV_ID in (");
            }
            labelIndex++;
        }
        for (int i = 1; i < labels.size(); i++) {
            labelMatchQuery.append(")");
        }
        String strictLabelMatchQuery = "SELECT DATASET_INV_ID from (SELECT b.DATASET_INV_ID, count(b.DATASET_LBL_VAL) as label_count from (" +
                labelMatchQuery.toString() +
                ") as a inner join CFG_DATASET_LBL as b on a.DATASET_INV_ID = b.DATASET_INV_ID group by b.DATASET_INV_ID) " +
                "where label_count = " + labels.size() + ";";

        int datasetInventoryId;
        CachedRowSet cachedRowSetLabels = datasetMetadataConnection.executeQuery(strictLabelMatchQuery);
        if (cachedRowSetLabels.size() == 0) {
            return createNewDataset(datasetName, labels);
        } else if (cachedRowSetLabels.size() == 1) {
            cachedRowSetLabels.next();
            datasetInventoryId = cachedRowSetLabels.getInt("DATASET_INV_ID");
            executionRuntime.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format("Found dataset id {0} for labels {1}-{2}." , Integer.toString(datasetInventoryId), datasetName, String.join(", ", labels)), Level.TRACE);
        } else {
            List<Integer> datasetInventoryIds = new ArrayList<>();
            while (cachedRowSetLabels.next()) {
                datasetInventoryIds.add(cachedRowSetLabels.getInt("DATASET_INV_ID"));
            }
            executionRuntime.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format("Found more than one dataset id ({0}) for name ''{1}'' and labels ''{2}''. " +
                            "Returning first occurrence.", datasetInventoryIds.stream().map(id -> Integer.toString(id)).collect(Collectors.joining(", ")),
                    datasetName, String.join(", ", labels)), Level.WARN);
            datasetInventoryId = datasetInventoryIds.get(0);
        }

        String query = "select DATASET_FILE_NM, DATASET_TABLE_NM from CFG_DATASET_INV where DATASET_INV_ID = " + datasetInventoryId;
        CachedRowSet cachedRowSetFileTable = datasetMetadataConnection.executeQuery(query);


        if (cachedRowSetFileTable.size() == 0) {
            String datasetFilename = UUID.randomUUID().toString() + ".db3";
            return createNewDatasetDatabase(datasetName, datasetFilename, "data", datasetInventoryId);
        } else if (cachedRowSetFileTable.size() == 1) {
            cachedRowSetFileTable.next();
            tableName = cachedRowSetFileTable.getString("DATASET_TABLE_NM");
            return new SqliteDatabase(new SqliteDatabaseConnection(frameworkFolderConfiguration.getFolderAbsolutePath("data") + File.separator + "datasets"
                    + File.separator + datasetName + File.separator + "data" + File.separator + cachedRowSetFileTable.getString("DATASET_FILE_NM")));
        } else {
            executionRuntime.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format("Found more than one dataset for name ''{0}'' and labels ''{1}''. " +
                            "Returning first occurrence.",
                    datasetName, String.join(", ", labels)), Level.WARN);
            cachedRowSetFileTable.next();
            tableName = cachedRowSetFileTable.getString("DATASET_TABLE_NM");
            return new SqliteDatabase(new SqliteDatabaseConnection(datasetName + File.separator + "data" + File.separator + cachedRowSetFileTable.getString("DATASET_FILE_NM")));
        }
        // TODO: throw exception and let calling functions (actions) handle this?
    }

    private void insertDatasetDatabaseInformation(int datasetInventoryId, String datasetFileName, String datasetTableName) {
        String inventoryQuery = "insert into CFG_DATASET_INV (DATASET_INV_ID, DATASET_FILE_NM, DATASET_TABLE_NM)" +
                " Values (" + datasetInventoryId + ", \"" + datasetFileName + "\", \"" + datasetTableName + "\")";
        datasetMetadataConnection.executeUpdate(inventoryQuery);
    }

    private void insertDatasetLabelInformation(int datasetInventoryId, List<String> labels) {
        String labelQuery = "insert into CFG_DATASET_LBL (DATASET_INV_ID, DATASET_LBL_VAL) " +
                "Values (" + datasetInventoryId + ", \"{0}\")";
        labels.forEach(label -> datasetMetadataConnection.executeUpdate(MessageFormat.format(labelQuery, label)));
    }

    private Database createNewDatasetDatabase(String datasetName, String filename, String tableName, int inventoryId) throws IOException {
        String filepath = frameworkFolderConfiguration.getFolderAbsolutePath("data") + File.separator + "datasets"
                + File.separator + datasetName + File.separator + "data" + File.separator + filename;
        FileUtils.touch(new File(filepath));
        insertDatasetDatabaseInformation(inventoryId, filename, tableName);
        Database database = new SqliteDatabase(new SqliteDatabaseConnection(filepath));
        String create = "CREATE TABLE " + tableName + " (key TEXT, value TEXT)";
        database.executeUpdate(create);
        return database;
    }

    private Database createNewDataset(String datasetName, List<String> labels) throws IOException {
        int nextInventoryId = getLatestInventoryId() + 1;
        String datasetFilename = UUID.randomUUID().toString() + ".db3";
        tableName = "data";
        insertDatasetLabelInformation(nextInventoryId, labels);
        return createNewDatasetDatabase(datasetName, datasetFilename, "data", nextInventoryId);
    }

    private int getLatestInventoryId() {
        String latestInventoryIdQuery = "select max(DATASET_INV_ID) as LATEST_INVENTORY_ID from CFG_DATASET_INV";
        CachedRowSet cachedRowSet = datasetMetadataConnection.executeQuery(latestInventoryIdQuery);
        if (cachedRowSet.size() == 0) {
            return 0;
        } else {
            try {
                cachedRowSet.next();
                return cachedRowSet.getInt("LATEST_INVENTORY_ID");
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }
    }

    public void clean() {
        // Check if table exists
        String queryTableExists = "select name from sqlite_master where name = '" + tableName + "'";
        CachedRowSet crs = null;
        crs = dataset.executeQuery(queryTableExists);
        String value = "";
        boolean tableExists = false;
        try {
            if (crs.size() >= 1) {
                if (crs.getString("NAME").equalsIgnoreCase(tableName)) {
                    String clean = "delete from " + tableName;
                    dataset.executeUpdate(clean);
                } else {
                    String create = "CREATE TABLE " + tableName + " (key TEXT, value TEXT)";
                    dataset.executeUpdate(create);
                }
            } else {
                String create = "CREATE TABLE " + tableName + " (key TEXT, value TEXT)";
                dataset.executeUpdate(create);
            }
            crs.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
    }

    public void setDataItem(String key, String value) {
        // Store the data
        try {
            String query = "";
            query = "insert into " + tableName + " (key, value) values ('" + key + "','" + value + "')";
            dataset.executeUpdate(query);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
    }

    @Override
    public String toString() {
        return "{{^dataset(" + getNameAsDataType().toString() + ", " + getLabelsAsDataType().toString() + ")}}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Dataset) {
            return getDataItems().equals(((Dataset) obj).getDataItems());
        } else {
            return false;
        }
    }

    public List<String> getLabels() {
        return labels;
    }

    public String getName() {
        return name;
    }
}


