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

import javax.sql.rowset.CachedRowSet;
import java.text.MessageFormat;


public class SqlSetRuntimeVariables extends ActionTypeExecution {

    // Parameters
    private ActionParameterOperation sqlQuery;
    private ActionParameterOperation connectionName;
    private static final Logger LOGGER = LogManager.getLogger();

    public SqlSetRuntimeVariables(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        // Reset Parameters
        this.sqlQuery = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), "query");
        this.connectionName = new ActionParameterOperation(this.getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), "connection");

        // Get Parameters
        for (ActionParameter actionParameter : getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("query")) {
                sqlQuery.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("connection")) {
                connectionName.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        //Create parameter list
        getActionParameterOperationMap().put("query", sqlQuery);
        getActionParameterOperationMap().put("connection", connectionName);
    }

    protected boolean executeAction() throws InterruptedException {

        String query = convertQuery(sqlQuery.getValue());
        String connectionName = convertConnectionName(this.connectionName.getValue());
        // Get Connection
        Connection connection = ConnectionConfiguration.getInstance().get(new ConnectionKey(connectionName, this.getExecutionControl().getEnvName()))
                .orElseThrow(() -> new RuntimeException("Could not find connection " + connectionName));

        Database database = DatabaseHandler.getInstance().getDatabase(connection);
        // Run the action
        CachedRowSet sqlResultSet = DatabaseHandler.getInstance().executeQuery(database, query);
        this.getExecutionControl().getExecutionRuntime().setRuntimeVariables(getActionExecution(), sqlResultSet);
        getActionExecution().getActionControl().increaseSuccessCount();
        return true;
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

    private String convertQuery(DataType query) {
        if (query instanceof Text) {
            return query.toString();
        } else {
            LOGGER.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for query",
                    query.getClass()));
            return query.toString();
        }
    }

}