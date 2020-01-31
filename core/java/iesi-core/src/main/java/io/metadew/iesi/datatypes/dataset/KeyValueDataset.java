package io.metadew.iesi.datatypes.dataset;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.SqliteDatabase;
import io.metadew.iesi.connection.database.connection.sqlite.SqliteDatabaseConnection;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class KeyValueDataset extends Dataset {

    private static final Logger LOGGER = LogManager.getLogger();

    public KeyValueDataset(DataType name, DataType labels, ExecutionRuntime executionRuntime) throws IOException {
        super(name, labels, executionRuntime);
    }

    public KeyValueDataset(String name, List<String> labels, ExecutionRuntime executionRuntime) throws IOException {
        super(name, labels, executionRuntime);
        LOGGER.trace("datatype.dataset.keyvalue=creating dataset with " + name + " and " + labels.toString());
    }

    public Map<String, DataType> getDataItems(ExecutionRuntime executionRuntime) {
        String query;
        query = "select key, value from " + SQLTools.GetStringForSQLTable(getTableName()) + ";";

        Map<String, DataType> dataItems = new HashMap<>();
        try {
            CachedRowSet crs = getDatasetDatabase().executeQuery(query);
            while (crs.next()) {
                dataItems.put(crs.getString("key"), dataTypeService.resolve(crs.getString("value"), executionRuntime));
            }
            crs.close();
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));

            LOGGER.warn("exception=" + e.getMessage());
            LOGGER.warn("stacktrace=" + stackTrace.toString());
        }
        return dataItems;
    }

    public Optional<DataType> getDataItem(String dataItem, ExecutionRuntime executionRuntime) {
        String query = "select value from " + SQLTools.GetStringForSQLTable(getTableName()) + " where key = " + SQLTools.GetStringForSQL(dataItem) + ";";
        try {
            CachedRowSet crs = getDatasetDatabase().executeQuery(query);
            if (crs.size() == 0) {
                return Optional.empty();
            } else if (crs.size() > 1) {
                LOGGER.warn(MessageFormat.format("Dataset contains multiple items with key ''{0}''. Returning first value", dataItem));
            }
            crs.next();
            DataType value = dataTypeService.resolve(crs.getString("VALUE"), executionRuntime);
            crs.close();
            return Optional.of(value);
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.info("exception=" + e);
            LOGGER.debug("exception.stacktrace=" + stackTrace);
            return Optional.empty();
        }
    }

    public void setDataItem(String key, DataType value) {
        // Store the data
        String query = "insert into " + SQLTools.GetStringForSQLTable(getTableName()) + " (key, value) values ("
                + SQLTools.GetStringForSQL(key) + ", " + SQLTools.GetStringForSQL(value.toString()) + ");";
        getDatasetDatabase().executeUpdate(query);
    }


    protected Database createNewDatasetDatabase(String datasetName, String filename, String tableName, int inventoryId) throws IOException {
        LOGGER.debug(MessageFormat.format("creating dataset {0} for {1} at {2} table {3}", inventoryId, datasetName, filename, tableName));
        String filepath = FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("data") + File.separator + "datasets"
                + File.separator + datasetName + File.separator + "data" + File.separator + filename;
        File file = new File(filepath);
        file.setWritable(true, true);
        FileUtils.touch(file);
        getDatasetMetadata().insertDatasetDatabaseInformation(inventoryId, filename, tableName);
        Database database = new SqliteDatabase(new SqliteDatabaseConnection(filepath));
        String create = "CREATE TABLE " + SQLTools.GetStringForSQLTable(tableName) + " (key TEXT, value TEXT)";
        database.executeUpdate(create);
        return database;
    }

    public void clean(DataType dataType, ExecutionRuntime executionRuntime) {
        if (dataType instanceof Array) {
            for (DataType element : ((Array) dataType).getList()) {
                clean(element, executionRuntime);
            }
        } else if (dataType instanceof Dataset) {
            ((Dataset) dataType).clean(executionRuntime);
        }
    }

    public void clean(ExecutionRuntime executionRuntime) {
        LOGGER.debug(MessageFormat.format("cleaning dataset {0}:{1}", getName(), String.join("-", getLabels())));
        for (DataType dataType : getDataItems(executionRuntime).values()) {
            clean(dataType, executionRuntime);
        }

        // Check if table exists
        String queryTableExists = "select name from sqlite_master where name = " + SQLTools.GetStringForSQLTable(getTableName()) + ";";
        try {
            CachedRowSet crs = getDatasetDatabase().executeQuery(queryTableExists);
            if (crs.size() >= 1) {
                crs.next();
//                if (crs.getString("NAME").equalsIgnoreCase(getTableName())) {
                String clean = "delete from " + SQLTools.GetStringForSQLTable(getTableName()) + ";";
                getDatasetDatabase().executeUpdate(clean);
//                } else {
//                    String create = "CREATE TABLE " + SQLTools.GetStringForSQLTable(getTableName()) + " (key TEXT, value TEXT);";
//                    getDatasetDatabase().executeUpdate(create);
//                }
            } else {
                String create = "CREATE TABLE " + SQLTools.GetStringForSQLTable(getTableName()) + " (key TEXT, value TEXT);";
                getDatasetDatabase().executeUpdate(create);
            }
            crs.close();
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.info("exception=" + e);
            LOGGER.debug("exception.stacktrace=" + stackTrace);
        }
    }


    @Override
    public String toString() {
        return "{{^dataset(" + getNameAsDataType().toString() + ", " + getLabelsAsDataType().toString() + ")}}";
    }

    public boolean equals(Object obj, ExecutionRuntime executionRuntime) {
        if (obj instanceof KeyValueDataset) {
            return getDataItems(executionRuntime).equals(((KeyValueDataset) obj).getDataItems(executionRuntime));
        } else {
            return false;
        }
    }

}
