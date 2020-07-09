package io.metadew.iesi.script.action.sql;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;

public class SqlExecuteStatement {

    private ActionExecution actionExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation sqlStatement;
    private ActionParameterOperation connectionName;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;
    private static final Logger LOGGER = LogManager.getLogger();

    public SqlExecuteStatement(ExecutionControl executionControl,
                               ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.executionControl = executionControl;
        this.actionExecution = actionExecution;
        this.actionParameterOperationMap = new HashMap<>();
    }

    public void prepare()  throws Exception {
        // Set Parameters
        this.sqlStatement = new ActionParameterOperation(executionControl,
                actionExecution, actionExecution.getAction().getType(), "statement");
        this.connectionName = new ActionParameterOperation(executionControl,
                actionExecution, actionExecution.getAction().getType(), "connection");

        // Get Parameters
        for (ActionParameter actionParameter : actionExecution.getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("statement")) {
                this.getSqlStatement().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("connection")) {
                this.getConnectionName().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            }
        }

        // Create parameter list
        actionParameterOperationMap.put("statement", this.getSqlStatement());
        actionParameterOperationMap.put("connection", this.getConnectionName());
    }

    public boolean execute() throws InterruptedException {
        try {
            String sqlStatement = convertSqlStatement(getSqlStatement().getValue());
            String connectionName = convertConnectionName(getConnectionName().getValue());
            return execute(sqlStatement, connectionName);
        } catch (InterruptedException e) {
            throw (e);
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exception=" + e);
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());

            actionExecution.getActionControl().increaseErrorCount();

            actionExecution.getActionControl().logOutput("exception", e.getMessage());
            actionExecution.getActionControl().logOutput("stacktrace", stackTrace.toString());

            return false;
        }

    }

    private boolean execute(String sqlStatement, String connectionName) throws InterruptedException {
        // Get Connection
        Connection connection = ConnectionConfiguration.getInstance().get(new ConnectionKey(connectionName, executionControl.getEnvName()))
                .orElseThrow(() -> new RuntimeException("Cannot find connection " + connectionName));
        Database database = DatabaseHandler.getInstance().getDatabase(connection);
        if (database == null) {
            throw new RuntimeException("Error establishing DB connection");
        }

        // Run the action
        // Make sure the SQL statement is ended with a ;
        if (!sqlStatement.trim().endsWith(";")) {
            sqlStatement = sqlStatement + ";";
        }

        DatabaseHandler.getInstance().executeUpdate(database, sqlStatement);

        // Evaluate result
//        actionExecution.getActionControl().logOutput("sys.out", sqlScriptResult.getSystemOutput());
//
//        if (sqlScriptResult.getReturnCode() != 0) {
//            actionExecution.getActionControl().logOutput("err.out", sqlScriptResult.getErrorOutput());
//            throw new RuntimeException("Error executing SQL query");
//        }

        actionExecution.getActionControl().increaseSuccessCount();
        return true;
    }

    private String convertSqlStatement(DataType sqlStatement) {
        if (sqlStatement instanceof Text) {
            return sqlStatement.toString();
        } else {
            LOGGER.warn(MessageFormat.format(actionExecution.getAction().getType() + " does not accept {0} as type for sql statement",
                    sqlStatement.getClass()));
            return sqlStatement.toString();
        }
    }


    private String convertConnectionName(DataType connectionName) {
        if (connectionName instanceof Text) {
            return connectionName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(actionExecution.getAction().getType() + " does not accept {0} as type for connection name",
                    connectionName.getClass()));
            return connectionName.toString();
        }
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    public ActionExecution getActionExecution() {
        return actionExecution;
    }

    public void setActionExecution(ActionExecution actionExecution) {
        this.actionExecution = actionExecution;
    }

    public ActionParameterOperation getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(ActionParameterOperation connectionName) {
        this.connectionName = connectionName;
    }

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

    public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        this.actionParameterOperationMap = actionParameterOperationMap;
    }

    public ActionParameterOperation getActionParameterOperation(String key) {
        return actionParameterOperationMap.get(key);
    }

    public ActionParameterOperation getSqlStatement() {
        return sqlStatement;
    }

    public void setSqlStatement(ActionParameterOperation sqlStatement) {
        this.sqlStatement = sqlStatement;
    }

}
