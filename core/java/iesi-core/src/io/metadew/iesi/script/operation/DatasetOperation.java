package io.metadew.iesi.script.operation;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.common.json.JsonParsed;
import io.metadew.iesi.common.json.JsonParsedItem;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata_repository.repository.database.connection.DatabaseConnection;
import io.metadew.iesi.metadata_repository.repository.database.connection.SqliteDatabaseConnection;
import io.metadew.iesi.script.execution.ExecutionControl;

/**
 * Operation to manage the datasets that have been defined in the script
 *
 * @author peter.billen
 */
public class DatasetOperation {

    private final Pattern datasetItemPattern = Pattern.compile("(?<table>\\w+)\\.(?<tableField>(\\w+$|\\w+\\.)+)");

    private FrameworkExecution frameworkExecution;

    private ExecutionControl executionControl;
    private String datasetType;

    private DatabaseConnection datasetConnection;

    private DatabaseConnection metadataConnection;

    private String datasetName;

    private String datasetLabels;

    // Constructors
    @SuppressWarnings("unused")
    public DatasetOperation(FrameworkExecution frameworkExecution, ExecutionControl executionControl, String datasetType, String datasetName,
                            String datasetLabels) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setDatasetType(datasetType);
        this.setDatasetName(datasetName);
        this.setDatasetLabels(datasetLabels);

        if (this.getDatasetType().equalsIgnoreCase("stage")) {
            SqliteDatabaseConnection dcSQLiteConnection = new SqliteDatabaseConnection(this.getExecutionControl().getExecutionRuntime().getStageOperation("myStage").getStageFilePath());
            this.setDatasetConnection(dcSQLiteConnection);
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            String datasetFolderName = this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration()
                    .getFolderAbsolutePath("data") + File.separator + "datasets" + File.separator
                    + this.getDatasetName();
            String metadataFileName = datasetFolderName + File.separator + "metadata" + File.separator + "metadata.db3";
            SqliteDatabaseConnection dcSQLiteConnection = new SqliteDatabaseConnection(metadataFileName);
            this.setMetadataConnection(objectMapper.convertValue(dcSQLiteConnection, DatabaseConnection.class));

            // Derive dataset
            String datasetFileName = "";
            CachedRowSet crs = null;

            String query = "select a.DATASET_INV_ID, a.DATASET_FILE_NM from CFG_DATASET_INV a inner join CFG_DATASET_LBL b on a.DATASET_INV_ID = b.DATASET_INV_ID";
            if (!this.getDatasetLabels().trim().equalsIgnoreCase("")) {
                String where = "";
                String[] parts = this.getDatasetLabels().split(",");
                for (int i = 0; i < parts.length; i++) {
                    String innerpart = parts[i];
                    if (where.trim().equalsIgnoreCase("")) {
                        where += " where ";
                    } else {
                        where += " and ";
                    }
                    where += "b.DATASET_LBL_VAL = '";
                    where += innerpart;
                    where += "'";
                }
                query += where;
            }

            crs = this.getMetadataConnection().executeQuery(query);
            try {
                while (crs.next()) {
                    datasetFileName = crs.getString("DATASET_FILE_NM");
                }
                crs.close();
            } catch (Exception e) {
                StringWriter StackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(StackTrace));
                System.out.println(query);
            }

            // New dataset file name
            if (datasetFileName.trim().equalsIgnoreCase("")) {
                datasetFileName = UUID.randomUUID().toString() + ".db3";
                // register in the metadata
                String sql = "insert into CFG_DATASET_INV (DATASET_INV_ID, DATASET_FILE_NM) Values (";
                sql += "";

            }

            datasetFileName = datasetFolderName + File.separator + "data" + File.separator + datasetFileName;
            dcSQLiteConnection = new SqliteDatabaseConnection(datasetFileName);
            this.setDatasetConnection(dcSQLiteConnection);
        }
    }

    public Optional<String> getDataItem(String datasetItem) {
        Matcher matcher = datasetItemPattern.matcher(datasetItem);
        if (!matcher.find()) {
            throw new RuntimeException(MessageFormat.format("Dataset item {0} does not follow the correct syntax of table.table_field", datasetItem));
        }
        CachedRowSet crs;
        String query;
        query = "select ";
        query += "value";
        query += " from ";
        query += matcher.group("table");
        query += " where key = '";
        query += matcher.group("tableField");
        query += "'";

        String value = null;
        crs = this.getDatasetConnection().executeQuery(query);
        try {
            while (crs.next()) {
                value = crs.getString("VALUE");
            }
            crs.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            return Optional.empty();
        }
        return Optional.ofNullable(value);
    }

    public void setDataset(String datasetTableName, JsonParsed jsonParsed) {
        // Check if table exists
        String queryTableExists = "select name from sqlite_master where name = '" + datasetTableName + "'";
        CachedRowSet crs = null;
        crs = this.getDatasetConnection().executeQuery(queryTableExists);
        String value = "";
        boolean tableExists = false;
        try {
            while (crs.next()) {
                value = crs.getString("NAME");
                if (value.trim().equalsIgnoreCase(datasetTableName)) {
                    tableExists = true;
                }
            }
            crs.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

        // Perform necessary initialization actions
        if (tableExists) {
            String clean = "delete from " + datasetTableName;
            this.getDatasetConnection().executeUpdate(clean);
        } else {
            String create = "CREATE TABLE " + datasetTableName + " (key TEXT, value TEXT)";
            this.getDatasetConnection().executeUpdate(create);
        }

        // Store the data
        try {
            for (JsonParsedItem jsonParsedItem : jsonParsed.getJsonParsedItemList()) {
                String query = "";
                query = "insert into " + datasetTableName + " (key, value) values ('";
                query += jsonParsedItem.getPath();
                query += "','";
                query += jsonParsedItem.getValue();
                query += "')";
                this.getDatasetConnection().executeUpdate(query);
            }
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
    }

    public void setDatasetEntry(String datasetTableName, String key, String value) {
        // Store the data
        try {
            String query = "";
            query = "insert into " + datasetTableName + " (key, value) values ('";
            query += key;
            query += "','";
            query += value;
            query += "')";
            this.getDatasetConnection().executeUpdate(query);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
    }

    public void resetDataset(String datasetTableName) {
        // Check if table exists
        String queryTableExists = "select name from sqlite_master where name = '" + datasetTableName + "'";
        CachedRowSet crs = null;
        crs = this.getDatasetConnection().executeQuery(queryTableExists);
        String value = "";
        boolean tableExists = false;
        try {
            while (crs.next()) {
                value = crs.getString("NAME");
                if (value.trim().equalsIgnoreCase(datasetTableName)) {
                    tableExists = true;
                }
            }
            crs.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

        // Perform necessary initialization actions
        if (tableExists) {
            String clean = "delete from " + datasetTableName;
            this.getDatasetConnection().executeUpdate(clean);
        } else {
            String create = "CREATE TABLE " + datasetTableName + " (key TEXT, value TEXT)";
            this.getDatasetConnection().executeUpdate(create);
        }

    }

    // Getters and setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public DatabaseConnection getDatasetConnection() {
        return datasetConnection;
    }

    public void setDatasetConnection(DatabaseConnection datasetConnection) {
        this.datasetConnection = datasetConnection;
    }

    public DatabaseConnection getMetadataConnection() {
        return metadataConnection;
    }

    public void setMetadataConnection(DatabaseConnection metadataConnection) {
        this.metadataConnection = metadataConnection;
    }

    public String getDatasetLabels() {
        return datasetLabels;
    }

    public void setDatasetLabels(String datasetLabels) {
        this.datasetLabels = datasetLabels;
    }


    public String getDatasetType() {
        return datasetType;
    }

    public void setDatasetType(String datasetType) {
        this.datasetType = datasetType;
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

}