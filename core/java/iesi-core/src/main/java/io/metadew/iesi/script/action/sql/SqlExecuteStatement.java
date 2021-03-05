package io.metadew.iesi.script.action.sql;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

public class SqlExecuteStatement extends ActionTypeExecution {

    // Parameters
    private ActionParameterOperation sqlStatement;
    private ActionParameterOperation connectionName;
    private static final Logger LOGGER = LogManager.getLogger();

    public SqlExecuteStatement(ExecutionControl executionControl,
                               ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        // Set Parameters
        this.sqlStatement = new ActionParameterOperation(getExecutionControl(),
                getActionExecution(), getActionExecution().getAction().getType(), "statement");
        this.connectionName = new ActionParameterOperation(getExecutionControl(),
                getActionExecution(), getActionExecution().getAction().getType(), "connection");

        // Get Parameters
        for (ActionParameter actionParameter : getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("statement")) {
                this.getSqlStatement().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("connection")) {
                this.getConnectionName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Create parameter list
        getActionParameterOperationMap().put("statement", this.getSqlStatement());
        getActionParameterOperationMap().put("connection", this.getConnectionName());
    }

    protected boolean executeAction() throws InterruptedException {
        String sqlStatement = convertSqlStatement(getSqlStatement().getValue());
        String connectionName = convertConnectionName(getConnectionName().getValue());
        // Get Connection
        Connection connection = ConnectionConfiguration.getInstance()
                .get(new ConnectionKey(connectionName, this.getExecutionControl().getEnvName()))
                .orElseThrow(() -> new RuntimeException("Unknown connection name: " + connectionName));

        Database database = DatabaseHandler.getInstance().getDatabase(connection);

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

        getActionExecution().getActionControl().increaseSuccessCount();
        return true;
    }

    private String convertSqlStatement(DataType sqlStatement) {
        if (sqlStatement instanceof Text) {
            return sqlStatement.toString();
        } else {
            LOGGER.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for sql statement",
                    sqlStatement.getClass()));
            return sqlStatement.toString();
        }
    }


    private String convertConnectionName(DataType connectionName) {
        if (connectionName instanceof Text) {
            return connectionName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for connection name",
                    connectionName.getClass()));
            return connectionName.toString();
        }
    }


    public ActionParameterOperation getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(ActionParameterOperation connectionName) {
        this.connectionName = connectionName;
    }

    public ActionParameterOperation getSqlStatement() {
        return sqlStatement;
    }

    public void setSqlStatement(ActionParameterOperation sqlStatement) {
        this.sqlStatement = sqlStatement;
    }

}
