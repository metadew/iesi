package io.metadew.iesi.datatypes.dataset;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.SqliteDatabase;
import io.metadew.iesi.connection.database.connection.SqliteDatabaseConnection;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.apache.logging.log4j.Level;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DatasetMetadata {

    private final Database database;
    private final ExecutionRuntime executionRuntime;
    private final String datasetName;
    private final FrameworkFolderConfiguration frameworkFolderConfiguration;

    public DatasetMetadata(String datasetName, ExecutionRuntime executionRuntime, FrameworkFolderConfiguration frameworkFolderConfiguration) {
        this.datasetName = datasetName;
        this.database =  new SqliteDatabase(new SqliteDatabaseConnection(frameworkFolderConfiguration.getFolderAbsolutePath("data") + File.separator + "datasets"
                + File.separator + datasetName + File.separator + "metadata" + File.separator + "metadata.db3"));
        this.executionRuntime = executionRuntime;
        this.frameworkFolderConfiguration = frameworkFolderConfiguration;
    }

    public Optional<Long> getId(List<String> labels) throws SQLException {
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

        long datasetInventoryId;
        CachedRowSet cachedRowSetLabels = database.executeQuery(strictLabelMatchQuery);

        if (cachedRowSetLabels.size() == 0) {
            return Optional.empty();
        } else if (cachedRowSetLabels.size() == 1) {
            cachedRowSetLabels.next();
            datasetInventoryId = cachedRowSetLabels.getLong("DATASET_INV_ID");
            executionRuntime.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format("Found dataset id {0} for labels {1}-{2}.", Long.toString(datasetInventoryId), datasetName, String.join(", ", labels)), Level.TRACE);
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
        return Optional.of(datasetInventoryId);
    }

    public Database getDatasetDatabase(long id) throws SQLException {
        String query = "select DATASET_FILE_NM, DATASET_TABLE_NM from CFG_DATASET_INV where DATASET_INV_ID = " + id;
        CachedRowSet cachedRowSetFileTable = database.executeQuery(query);


        if (cachedRowSetFileTable.size() == 0) {
            throw new RuntimeException(MessageFormat.format("dataset id {0} is does not have an implementation. " +
                    "Please implement this dataset", datasetName));
        } else if (cachedRowSetFileTable.size() > 1) {
            executionRuntime.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format("Found more than implementation for dataset id {0}. " +
                            "Returning first occurrence.",
                    id), Level.WARN);
        }
        cachedRowSetFileTable.next();
        Database database = new SqliteDatabase(new SqliteDatabaseConnection(frameworkFolderConfiguration.getFolderAbsolutePath("data") + File.separator + "datasets"
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
            executionRuntime.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format("Found more than implementation for dataset id {0}. " +
                            "Returning first occurrence.",
                    id), Level.WARN);
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
