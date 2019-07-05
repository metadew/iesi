package io.metadew.iesi.datatypes.dataset;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.SqliteDatabase;
import io.metadew.iesi.connection.database.connection.SqliteDatabaseConnection;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeResolver;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.apache.commons.io.FileUtils;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.*;

public class KeyValueDataset extends Dataset {

    public KeyValueDataset(DataType name, DataType labels, FrameworkFolderConfiguration frameworkFolderConfiguration, ExecutionRuntime executionRuntime) throws IOException, SQLException {
        super(name, labels, frameworkFolderConfiguration, executionRuntime);
    }

    public KeyValueDataset(String name, List<String> labels, FrameworkFolderConfiguration frameworkFolderConfiguration, ExecutionRuntime executionRuntime) throws IOException, SQLException {
        super(name, labels, frameworkFolderConfiguration, executionRuntime);
    }

    public Map<String, DataType> getDataItems() {
        CachedRowSet crs;
        String query;
        query = "select key, value from " +
                getTableName() + ";";

        Map<String, DataType> dataItems = new HashMap<>();
        crs = getDatasetDatabase().executeQuery(query);
        try {
            while (crs.next()) {
                dataItems.put(crs.getString("key"), DataTypeResolver.resolveToDataType(crs.getString("value"), getFrameworkFolderConfiguration(), getExecutionRuntime()));
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
                getTableName() +
                "\" where key = '" + dataItem + "'";

        DataType value = null;
        crs = getDatasetDatabase().executeQuery(query);
        try {
            while (crs.next()) {
                value = DataTypeResolver.resolveToDataType(crs.getString("VALUE"), getFrameworkFolderConfiguration(), getExecutionRuntime());
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
            String query = "";
            query = "insert into " + getTableName() + " (key, value) values ('" + key + "','" + value.toString() + "')";
            getDatasetDatabase().executeUpdate(query);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
    }


    protected Database createNewDatasetDatabase(String datasetName, String filename, String tableName, int inventoryId) throws IOException {
        String filepath = getFrameworkFolderConfiguration().getFolderAbsolutePath("data") + File.separator + "datasets"
                + File.separator + datasetName + File.separator + "data" + File.separator + filename;
        FileUtils.touch(new File(filepath));
        getDatasetMetadata().insertDatasetDatabaseInformation(inventoryId, filename, tableName);
        Database database = new SqliteDatabase(new SqliteDatabaseConnection(filepath));
        String create = "CREATE TABLE " + tableName + " (key TEXT, value TEXT)";
        database.executeUpdate(create);
        return database;
    }

    @SuppressWarnings("unused")
	public void clean() {
        // Check if table exists
        String queryTableExists = "select name from sqlite_master where name = '" + getTableName() + "'";
        CachedRowSet crs = null;
        crs = getDatasetDatabase().executeQuery(queryTableExists);
        String value = "";
        boolean tableExists = false;
        try {
            if (crs.size() >= 1) {
                if (crs.getString("NAME").equalsIgnoreCase(getTableName())) {
                    String clean = "delete from " + getTableName();
                    getDatasetDatabase().executeUpdate(clean);
                } else {
                    String create = "CREATE TABLE " + getTableName() + " (key TEXT, value TEXT)";
                    getDatasetDatabase().executeUpdate(create);
                }
            } else {
                String create = "CREATE TABLE " + getTableName() + " (key TEXT, value TEXT)";
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
