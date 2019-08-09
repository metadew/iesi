package io.metadew.iesi.datatypes.dataset;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.SqliteDatabase;
import io.metadew.iesi.connection.database.connection.SqliteDatabaseConnection;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.datatypes.DataType;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class KeyValueDataset extends Dataset {

    private static final Logger LOGGER = LogManager.getLogger();

    public KeyValueDataset(DataType name, DataType labels, ExecutionRuntime executionRuntime) throws IOException, SQLException {
        super(name, labels, executionRuntime);
    }

    public KeyValueDataset(String name, List<String> labels, ExecutionRuntime executionRuntime) throws IOException, SQLException {
        super(name, labels, executionRuntime);
        LOGGER.trace("Creating dataset with " + name + " and " + labels.toString());
    }

    public Map<String, DataType> getDataItems() {
        String query;
        query = "select key, value from " + SQLTools.GetStringForSQLTable(getTableName()) + ";";

        Map<String, DataType> dataItems = new HashMap<>();
        CachedRowSet crs = getDatasetDatabase().executeQuery(query);
        try {
            while (crs.next()) {
                dataItems.put(crs.getString("key"), dataTypeService.resolve(crs.getString("value")));
            }
            crs.close();
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));

            LOGGER.warn("exception=" + e.getMessage());
            LOGGER.warn("stacktrace=" + stackTrace.toString());
        }
        return dataItems;
    }

    public Optional<DataType> getDataItem(String dataItem) {
        CachedRowSet crs;
        String query;
        query = "select value from " + SQLTools.GetStringForSQLTable(getTableName()) + " where key = " + SQLTools.GetStringForSQL(dataItem) + ";";

        DataType value = null;
        crs = getDatasetDatabase().executeQuery(query);
        try {
            while (crs.next()) {
                value = dataTypeService.resolve(crs.getString("VALUE"));
            }
            crs.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            return Optional.empty();
        }
        return Optional.ofNullable(value);
    }

    public void setDataItem(String key, DataType value) {
        // Store the data
        try {
            String query = "insert into " + SQLTools.GetStringForSQLTable(getTableName()) + " (key, value) values ("
                    + SQLTools.GetStringForSQL(key) + ", " + SQLTools.GetStringForSQL(value.toString()) + ");";
            getDatasetDatabase().executeUpdate(query);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
    }


    protected Database createNewDatasetDatabase(String datasetName, String filename, String tableName, int inventoryId) throws IOException {
        String filepath = FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("data") + File.separator + "datasets"
                + File.separator + datasetName + File.separator + "data" + File.separator + filename;
        FileUtils.touch(new File(filepath));
        getDatasetMetadata().insertDatasetDatabaseInformation(inventoryId, filename, tableName);
        Database database = new SqliteDatabase(new SqliteDatabaseConnection(filepath));
        String create = "CREATE TABLE " + SQLTools.GetStringForSQLTable(tableName) + " (key TEXT, value TEXT)";
        database.executeUpdate(create);
        return database;
    }

    @SuppressWarnings("unused")
	public void clean() {
        // Check if table exists
        String queryTableExists = "select name from sqlite_master where name = " + SQLTools.GetStringForSQL(getTableName()) + ";";
        CachedRowSet crs = getDatasetDatabase().executeQuery(queryTableExists);
        try {
            if (crs.size() >= 1) {
                if (crs.getString("NAME").equalsIgnoreCase(getTableName())) {
                    String clean = "delete from " + SQLTools.GetStringForSQLTable(getTableName()) + ";";
                    getDatasetDatabase().executeUpdate(clean);
                } else {
                    String create = "CREATE TABLE " + SQLTools.GetStringForSQLTable(getTableName()) + " (key TEXT, value TEXT);";
                    getDatasetDatabase().executeUpdate(create);
                }
            } else {
                String create = "CREATE TABLE " + SQLTools.GetStringForSQLTable(getTableName()) + " (key TEXT, value TEXT);";
                getDatasetDatabase().executeUpdate(create);
            }
            crs.close();
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
        if (obj instanceof KeyValueDataset) {
            return getDataItems().equals(((KeyValueDataset) obj).getDataItems());
        } else {
            return false;
        }
    }

}
