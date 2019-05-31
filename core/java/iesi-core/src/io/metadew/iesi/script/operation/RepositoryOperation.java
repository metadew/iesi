package io.metadew.iesi.script.operation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.common.json.JsonParsed;
import io.metadew.iesi.common.json.JsonParsedItem;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.connection.DatabaseConnection;
import io.metadew.iesi.connection.database.connection.SqliteDatabaseConnection;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.RepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.RepositoryInstanceConfiguration;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.metadata.definition.Repository;
import io.metadew.iesi.metadata.definition.RepositoryInstance;
import io.metadew.iesi.metadata.definition.RepositoryInstanceParameter;
import io.metadew.iesi.script.execution.ExecutionControl;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

/**
 * Operation to manage the repositories that have been defined in the script
 *
 * @author peter.billen
 */
public class RepositoryOperation {

    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;
    private DatabaseConnection datasetConnection;

    private DatabaseConnection metadataConnection;

    private String repositoryName;
    private String repositoryInstanceName;
    private String repositoryInstanceLabels;
    private Repository repository;
    private RepositoryInstance repositoryInstance;
    private RepositoryParameterOperation repositoryInstanceConnectionName;
    private Database repositoryDatabaseInstance;

    // Constructors
    @SuppressWarnings("unused")
    public RepositoryOperation(FrameworkExecution frameworkExecution, ExecutionControl executionControl, String repositoryName,
                               String repositoryInstanceName, String repositoryInstanceLabels) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setRepositoryName(repositoryName);
        this.setRepositoryInstanceName(repositoryInstanceName);
        this.setRepositoryInstanceLabels(repositoryInstanceLabels);

        RepositoryConfiguration repositoryConfiguration = new RepositoryConfiguration(this.getFrameworkExecution().getFrameworkInstance());
        this.setRepository(repositoryConfiguration.getRepository(this.getRepositoryName()));
        RepositoryInstanceConfiguration repositoryInstanceConfiguration = new RepositoryInstanceConfiguration(this.getFrameworkExecution().getFrameworkInstance());
        this.setRepositoryInstance(repositoryInstanceConfiguration.getRepositoryInstance(this.getRepository(), this.getRepositoryInstanceName()));

        this.setRepositoryInstanceConnectionName(new RepositoryParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(), "connection"));
        for (RepositoryInstanceParameter repositoryInstanceParameter : this.getRepositoryInstance().getParameters()) {
            if (repositoryInstanceParameter.getName().equalsIgnoreCase("connection")) {
                this.getRepositoryInstanceConnectionName().setInputValue(repositoryInstanceParameter.getValue());
            }
        }

        // Get Connection
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(this.getFrameworkExecution().getFrameworkInstance());
        Connection connection = connectionConfiguration.getConnection(this.getRepositoryInstanceConnectionName().getValue(),
                this.getExecutionControl().getEnvName()).get();
        ConnectionOperation connectionOperation = new ConnectionOperation(this.getFrameworkExecution());
        this.setRepositoryDatabaseInstance(connectionOperation
                .getDatabase(connection));


        ObjectMapper objectMapper = new ObjectMapper();
        String datasetFolderName = this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration()
                .getFolderAbsolutePath("data") + File.separator + "datasets" + File.separator + "";
        String metadataFileName = datasetFolderName + File.separator + "metadata" + File.separator + "metadata.db3";
        SqliteDatabaseConnection dcSQLiteConnection = new SqliteDatabaseConnection(metadataFileName);
        this.setMetadataConnection(objectMapper.convertValue(dcSQLiteConnection, DatabaseConnection.class));

        // Derive dataset
        String datasetFileName = "";
        CachedRowSet crs = null;

        String query = "select a.DATASET_INV_ID, a.DATASET_FILE_NM from CFG_DATASET_INV a inner join CFG_DATASET_LBL b on a.DATASET_INV_ID = b.DATASET_INV_ID";
        if (!this.getRepositoryInstanceLabels().trim().equalsIgnoreCase("")) {
            String where = "";
            String[] parts = this.getRepositoryInstanceLabels().split(",");
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
        this.setDatasetConnection(objectMapper.convertValue(dcSQLiteConnection, DatabaseConnection.class));
    }

    public String getDataItem(String datasetItem) {
        CachedRowSet crs = null;
        String query = "";
        if (!datasetItem.trim().equalsIgnoreCase("")) {
            query = "select ";
            String[] parts = datasetItem.split("\\.");
            query += "value";
            query += " from ";
            query += parts[0];
            query += " where key = '";
            query += parts[1];
            query += "'";
        }

        String value = "";
        crs = this.getDatasetConnection().executeQuery(query);
        try {
            while (crs.next()) {
                value = crs.getString("VALUE");
            }
            crs.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

        return value;
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

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getRepositoryInstanceName() {
        return repositoryInstanceName;
    }

    public void setRepositoryInstanceName(String repositoryInstanceName) {
        this.repositoryInstanceName = repositoryInstanceName;
    }

    public String getRepositoryInstanceLabels() {
        return repositoryInstanceLabels;
    }

    public void setRepositoryInstanceLabels(String repositoryInstanceLabels) {
        this.repositoryInstanceLabels = repositoryInstanceLabels;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public RepositoryInstance getRepositoryInstance() {
        return repositoryInstance;
    }

    public void setRepositoryInstance(RepositoryInstance repositoryInstance) {
        this.repositoryInstance = repositoryInstance;
    }

    public RepositoryParameterOperation getRepositoryInstanceConnectionName() {
        return repositoryInstanceConnectionName;
    }

    public void setRepositoryInstanceConnectionName(RepositoryParameterOperation repositoryInstanceConnectionName) {
        this.repositoryInstanceConnectionName = repositoryInstanceConnectionName;
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

	public Database getRepositoryDatabaseInstance() {
		return repositoryDatabaseInstance;
	}

	public void setRepositoryDatabaseInstance(Database repositoryDatabaseInstance) {
		this.repositoryDatabaseInstance = repositoryDatabaseInstance;
	}

}