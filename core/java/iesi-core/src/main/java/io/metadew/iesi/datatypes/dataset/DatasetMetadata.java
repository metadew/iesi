package io.metadew.iesi.datatypes.dataset;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.SqliteDatabase;
import io.metadew.iesi.connection.database.connection.sqlite.SqliteDatabaseConnection;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.framework.execution.IESIMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DatasetMetadata {

    private final Database database;
    private final String datasetName;

    private static final Logger LOGGER = LogManager.getLogger();

    public DatasetMetadata(String datasetName) {
        this.datasetName = datasetName;
        this.database = new SqliteDatabase(new SqliteDatabaseConnection(FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("data") + File.separator + "datasets"
                + File.separator + datasetName + File.separator + "metadata" + File.separator + "metadata.db3"));
    }

    public Optional<Long> getId(List<String> labels) {
        try {
            String query = "SELECT DATASET_INV_ID FROM CFG_DATASET_LBL WHERE DATASET_LBL_VAL in (" + labels.stream().map(SQLTools::GetStringForSQL).collect(Collectors.joining(",")) +
                    ") GROUP BY DATASET_INV_ID HAVING COUNT(DISTINCT DATASET_LBL_VAL) = " + labels.size() + ";";
            CachedRowSet cachedRowSet = database.executeQuery(query);
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.trace(new IESIMessage(MessageFormat.format("Found multiple dataset ids ({0}) for labels {1}-{2}. Returning first occurence",
                        cachedRowSet.size(), datasetName, String.join(", ", labels))));
            }
            cachedRowSet.next();
            long datasetInventoryId = cachedRowSet.getLong("DATASET_INV_ID");
            LOGGER.trace(new IESIMessage(MessageFormat.format("Found dataset id {0} for labels {1}-{2}.", Long.toString(datasetInventoryId), datasetName, String.join(", ", labels))));

            return Optional.of(datasetInventoryId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Database getDatasetDatabase(long id) {
        try {
            String query = "select DATASET_FILE_NM, DATASET_TABLE_NM from CFG_DATASET_INV where DATASET_INV_ID = " + id;
            CachedRowSet cachedRowSetFileTable = database.executeQuery(query);


            if (cachedRowSetFileTable.size() == 0) {
                throw new RuntimeException(MessageFormat.format("dataset id {0} is does not have an implementation. " +
                        "Please implement this dataset", datasetName));
            } else if (cachedRowSetFileTable.size() > 1) {
                LOGGER.warn(new IESIMessage(MessageFormat.format("Found more than implementation for dataset id {0}. " +
                        "Returning first occurrence.", id)));
            }
            cachedRowSetFileTable.next();
            Database database = new SqliteDatabase(new SqliteDatabaseConnection(FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("data") + File.separator + "datasets"
                    + File.separator + datasetName + File.separator + "data" + File.separator + cachedRowSetFileTable.getString("DATASET_FILE_NM")));
            cachedRowSetFileTable.close();
            return database;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getTableName(long id) {
        try {
            String query = "select DATASET_FILE_NM, DATASET_TABLE_NM from CFG_DATASET_INV where DATASET_INV_ID = " + id;
            CachedRowSet cachedRowSetFileTable = database.executeQuery(query);


            if (cachedRowSetFileTable.size() == 0) {
                throw new RuntimeException(MessageFormat.format("dataset id {0} is does not have an implementation. " +
                        "Please implement this dataset", datasetName));
            } else if (cachedRowSetFileTable.size() > 1) {

                LOGGER.warn(new IESIMessage(MessageFormat.format("Found more than implementation for dataset id {0}. " +
                        "Returning first occurrence.", id)));

            }
            cachedRowSetFileTable.next();
            String tableName = cachedRowSetFileTable.getString("DATASET_TABLE_NM");
            cachedRowSetFileTable.close();
            return tableName;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void insertDatasetLabelInformation(int datasetInventoryId, List<String> labels) {
        String labelQuery = "insert into CFG_DATASET_LBL (DATASET_INV_ID, DATASET_LBL_VAL) VALUES ("
                + SQLTools.GetStringForSQL(datasetInventoryId) + ", {0})";
        labels.forEach(label -> database.executeUpdate(MessageFormat.format(labelQuery, SQLTools.GetStringForSQL(label))));
    }

    protected synchronized int getLatestInventoryId() {
        try {
            String latestInventoryIdQuery = "select max(DATASET_INV_ID) as LATEST_INVENTORY_ID from (SELECT DATASET_INV_ID FROM CFG_DATASET_INV " +
                    "UNION ALL SELECT DATASET_INV_ID FROM CFG_DATASET_LBL);";
            CachedRowSet cachedRowSet = database.executeQuery(latestInventoryIdQuery);
            int inventoryId;
            if (cachedRowSet.size() == 0) {
                inventoryId = 0;
            } else {
                cachedRowSet.next();
                inventoryId = cachedRowSet.getInt("LATEST_INVENTORY_ID");
            }
            cachedRowSet.close();
            return inventoryId;
//            int inventoryIdLbl;
//            // TODO: robustness to lbl
//            String latestInventoryIdQueryLbl = "select max(DATASET_INV_ID) as LATEST_INVENTORY_ID from CFG_DATASET_LBL";
//            CachedRowSet cachedRowSetLbl = database.executeQuery(latestInventoryIdQueryLbl);
//            if (cachedRowSetLbl.size() == 0) {
//                inventoryIdLbl = 0;
//            } else {
//                cachedRowSetLbl.next();
//                inventoryIdLbl = cachedRowSetLbl.getInt("LATEST_INVENTORY_ID");
//            }
//            cachedRowSetLbl.close();
//            return Math.max(inventoryId, inventoryIdLbl);
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.info("exception=" + e);
            LOGGER.debug("exception.stacktrace=" + stackTrace.toString());
            throw new RuntimeException(e);
        }
    }

    public void insertDatasetDatabaseInformation(int inventoryId, String filename, String tableName) {
        String inventoryQuery = "insert into CFG_DATASET_INV (DATASET_INV_ID, DATASET_FILE_NM, DATASET_TABLE_NM)" +
                " Values (" + inventoryId + ", \"" + filename + "\", \"" + tableName + "\")";
        database.executeUpdate(inventoryQuery);
    }
}
