package io.metadew.iesi.util.harvest;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.connection.operation.DatabaseOperation;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseHarvestExecution {


    public DatabaseHarvestExecution() {

    }

    // Methods
    public void getInsertStatements(String sourceConnectionName, String environmentName, String schemaName,
                                    String tableName, String columns, String filter, String targetFileName) {

        // Compile SQL Statement
        StringBuffer sqlStatement = new StringBuffer();
        sqlStatement.append("select ");
        sqlStatement.append(columns);
        sqlStatement.append(" from ");
        if (schemaName.trim().equalsIgnoreCase("")) {
            sqlStatement.append(tableName);
        } else {
            sqlStatement.append(schemaName);
            sqlStatement.append(".");
            sqlStatement.append(tableName);
        }
        if (!filter.trim().equalsIgnoreCase("")) {
            sqlStatement.append(" where ");
            sqlStatement.append(filter);
        }

        this.getInsertStatements(sourceConnectionName, environmentName, schemaName, tableName, sqlStatement.toString(),
                targetFileName);
    }

    private void getInsertStatements(String sourceConnectionName, String environmentName, String schemaName,
                                     String tableName, String sqlStatement, String targetFileName) {
        // Prepare output file
        String outputFileName = targetFileName;
        // Location for temp harvest
        String outputFilePath = FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("run.tmp");
        boolean insertAppendFlag = false;
        boolean insertOverwriteFlag = false;
        if (!insertAppendFlag) {
            if (!insertOverwriteFlag) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssS");
                Date date = new Date();
                Timestamp timestamp = new Timestamp(date.getTime());
                outputFileName = outputFileName + "." + sdf.format(timestamp);
            } else {
                FileTools
                        .delete(outputFilePath + File.separator + outputFileName);
            }
        }

        // Get Connection
        Connection connection = ConnectionConfiguration.getInstance()
                .get(new ConnectionKey(sourceConnectionName, environmentName))
                .get();
        ConnectionOperation connectionOperation = new ConnectionOperation();
        Database database = connectionOperation.getDatabase(connection);

//		if (databaseConnection.getType().trim().equalsIgnoreCase("oracle")) {
//			this.getFrameworkExecution().getFrameworkLog().log("repository.type=" + databaseConnection.getType(), Level.DEBUG);
//		} else {
//			this.getFrameworkExecution().getFrameworkLog().log("repository.type.notsupported", Level.INFO);
//		}

        // Execute
        try {
            new DatabaseOperation().getScriptBuilder().generateInsertStmts(database, schemaName,
                    tableName, sqlStatement, "file", outputFilePath, outputFileName);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

    }

}