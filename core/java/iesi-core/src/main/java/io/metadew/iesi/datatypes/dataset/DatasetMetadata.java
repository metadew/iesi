package io.metadew.iesi.datatypes.dataset;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.SqliteDatabase;
import io.metadew.iesi.connection.database.connection.sqlite.SqliteDatabaseConnection;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.framework.execution.IESIMessage;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.apache.logging.log4j.Level;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DatasetMetadata {

    private final Database database;
    private final ExecutionRuntime executionRuntime;
    private final String datasetName;

    public DatasetMetadata(String datasetName, ExecutionRuntime executionRuntime) {
        this.datasetName = datasetName;
        this.database =  new SqliteDatabase(new SqliteDatabaseConnection(FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("data") + File.separator + "datasets"
                + File.separator + datasetName + File.separator + "metadata" + File.separator + "metadata.db3"));
        this.executionRuntime = executionRuntime;
    }

    public Optional<Long> getId(List<String> labels) throws SQLException {
        // TODO: specific SQLite behaviour!!
        String query = "SELECT DATASET_INV_ID FROM CFG_DATASET_LBL WHERE DATASET_LBL_VAL in (" + labels.stream().map(SQLTools::GetStringForSQL).collect(Collectors.joining(",")) +
                ") GROUP BY DATASET_INV_ID HAVING COUNT(DISTINCT DATASET_LBL_VAL) = " + labels.size() + ";";
        CachedRowSet cachedRowSet = database.executeQuery(query);
        if (cachedRowSet.size() == 0) {
            return Optional.empty();
        } else if (cachedRowSet.size() > 1) {
            executionRuntime.getExecutionControl().logMessage(new IESIMessage(MessageFormat.format("Found multiple dataset ids ({0}) for labels {1}-{2}. Returning first occurence",
                    cachedRowSet.size(), datasetName, String.join(", ", labels))), Level.TRACE);
        }
        cachedRowSet.next();
        long datasetInventoryId = cachedRowSet.getLong("DATASET_INV_ID");
        executionRuntime.getExecutionControl().logMessage(new IESIMessage(MessageFormat.format("Found dataset id {0} for labels {1}-{2}.", Long.toString(datasetInventoryId), datasetName, String.join(", ", labels))), Level.TRACE);

        return Optional.of(datasetInventoryId);
    }

    public Database getDatasetDatabase(long id) throws SQLException {
        String query = "select DATASET_FILE_NM, DATASET_TABLE_NM from CFG_DATASET_INV where DATASET_INV_ID = " + id;
        CachedRowSet cachedRowSetFileTable = database.executeQuery(query);


        if (cachedRowSetFileTable.size() == 0) {
            throw new RuntimeException(MessageFormat.format("dataset id {0} is does not have an implementation. " +
                    "Please implement this dataset", datasetName));
        } else if (cachedRowSetFileTable.size() > 1) {
            executionRuntime.getExecutionControl().logMessage(new IESIMessage(MessageFormat.format("Found more than implementation for dataset id {0}. " +
                    "Returning first occurrence.", id)), Level.WARN);
        }
        cachedRowSetFileTable.next();
        Database database = new SqliteDatabase(new SqliteDatabaseConnection(FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("data") + File.separator + "datasets"
                + File.separator  + datasetName + File.separator + "data" + File.separator + cachedRowSetFileTable.getString("DATASET_FILE_NM")));
        cachedRowSetFileTable.close();
        return database;
    }

    public String getTableName(long id) throws SQLException {
        String query = "select DATASET_FILE_NM, DATASET_TABLE_NM from CFG_DATASET_INV where DATASET_INV_ID = " + id;
        CachedRowSet cachedRowSetFileTable = database.executeQuery(query);


        if (cachedRowSetFileTable.size() == 0) {
            throw new RuntimeException(MessageFormat.format("dataset id {0} is does not have an implementation. " +
                    "Please implement this dataset", datasetName));
        } else if (cachedRowSetFileTable.size() > 1) {

            executionRuntime.getExecutionControl().logMessage(new IESIMessage(MessageFormat.format("Found more than implementation for dataset id {0}. " +
                    "Returning first occurrence.",id)), Level.WARN);

        }
        cachedRowSetFileTable.next();
        String tableName = cachedRowSetFileTable.getString("DATASET_TABLE_NM");
        cachedRowSetFileTable.close();
        return tableName;
    }

    protected void insertDatasetLabelInformation(int datasetInventoryId, List<String> labels) {
        String labelQuery = "insert into CFG_DATASET_LBL (DATASET_INV_ID, DATASET_LBL_VAL) " +
                "Values (" + datasetInventoryId + ", \"{0}\")";
        labels.forEach(label -> database.executeUpdate(MessageFormat.format(labelQuery, label)));
    }

    protected int getLatestInventoryId() {
        String latestInventoryIdQuery = "select max(DATASET_INV_ID) as LATEST_INVENTORY_ID from CFG_DATASET_INV";
        CachedRowSet cachedRowSet = database.executeQuery(latestInventoryIdQuery);
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

    public void insertDatasetDatabaseInformation(int inventoryId, String filename, String tableName) {
        String inventoryQuery = "insert into CFG_DATASET_INV (DATASET_INV_ID, DATASET_FILE_NM, DATASET_TABLE_NM)" +
                " Values (" + inventoryId + ", \"" + filename + "\", \"" + tableName + "\")";
        database.executeUpdate(inventoryQuery);
    }
}
