package io.metadew.iesi.util.harvest;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.connection.operation.DatabaseOperation;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.execution.FrameworkExecutionContext;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.metadata.definition.Context;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseHarvestExecution {

    private FrameworkExecution frameworkExecution;

    // Constructors

    public DatabaseHarvestExecution() {
        // Create the framework instance
        FrameworkInstance frameworkInstance = new FrameworkInstance();

        // Create the framework execution
        Context context = new Context();
        context.setName("harvest");
        context.setScope("");
        this.setFrameworkExecution(new FrameworkExecution(frameworkInstance, new FrameworkExecutionContext(context), null));
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
        String outputFilePath = this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("run.tmp");
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
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(this.getFrameworkExecution().getFrameworkInstance());
        Connection connection = connectionConfiguration.getConnection(sourceConnectionName, environmentName).get();
        ConnectionOperation connectionOperation = new ConnectionOperation(this.getFrameworkExecution());
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

    // Getters and setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }
}