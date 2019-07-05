package io.metadew.iesi.util.harvest;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.execution.FrameworkExecutionContext;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.metadata.definition.Context;

import javax.sql.rowset.CachedRowSet;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DatabaseOffloadExecution {

    private FrameworkExecution frameworkExecution;

    // Constructors
    public DatabaseOffloadExecution() {
        // Create the framework instance
        FrameworkInstance frameworkInstance = new FrameworkInstance();

        // Create the framework execution
        Context context = new Context();
        context.setName("offload");
        context.setScope("");
        this.setFrameworkExecution(new FrameworkExecution(frameworkInstance, new FrameworkExecutionContext(context), null));
    }

    // Methods
    public void offloadData(String sourceConnectionName, String sourceEnvironmentName, String targetConnectionName,
                            String targetEnvironmentName, String sqlStatement, String name, boolean cleanPrevious) throws SQLException {

        // Get Connection
        ConnectionOperation connectionOperation = new ConnectionOperation(this.getFrameworkExecution());
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(this.getFrameworkExecution().getFrameworkInstance());

        Connection sourceConnection = connectionConfiguration.getConnection(sourceConnectionName, sourceEnvironmentName).get();
        Database sourceDatabase = connectionOperation.getDatabase(sourceConnection);
        Connection targetConnection = connectionConfiguration.getConnection(targetConnectionName, targetEnvironmentName).get();
        Database targetDatabase = connectionOperation.getDatabase(targetConnection);

        CachedRowSet crs = null;
        crs = sourceDatabase.executeQuery(sqlStatement);

        String QueryString = "";
        java.sql.Connection liveTargetDatabaseConnection = null;
        try {
            // Get result set meta data
            ResultSetMetaData rsmd = crs.getMetaData();
            int cols = rsmd.getColumnCount();

            // Determine name
            if (name == null || name.isEmpty()) {
                name = rsmd.getTableName(1);
            }

            // Cleaning
            if (cleanPrevious) {
                QueryString = SQLTools.getDropStmt(name, true);
                targetDatabase.executeUpdate(QueryString);
            }

            // create the dataset table if needed
            QueryString = SQLTools.getCreateStmt(rsmd, name, true);
            targetDatabase.executeUpdate(QueryString);

            String temp = "";
            String sql = SQLTools.getInsertPstmt(rsmd, name);
            liveTargetDatabaseConnection = targetDatabase.getLiveConnection();
            PreparedStatement preparedStatement = liveTargetDatabaseConnection.prepareStatement(sql);

            int crsType = crs.getType();
            if (crsType != java.sql.ResultSet.TYPE_FORWARD_ONLY) {
                crs.beforeFirst();
            }

            while (crs.next()) {
                for (int i = 1; i < cols + 1; i++) {
                    temp = crs.getString(i);
                    preparedStatement.setString(i, temp);
                }
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println(QueryString);
            System.out.println("Query Actions Failed");
            e.printStackTrace();
        } finally {
            liveTargetDatabaseConnection.close();
        }

    }

    // Getters and setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }
}