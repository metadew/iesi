package io.metadew.iesi.script.action.sql;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.operation.ConnectionOperation;
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

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;


public class SqlSetRuntimeVariables {

    private ActionExecution actionExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation sqlQuery;
    private ActionParameterOperation connectionName;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;
    private static final Logger LOGGER = LogManager.getLogger();

    public SqlSetRuntimeVariables(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.executionControl = executionControl;
        this.actionExecution = actionExecution;
        this.actionParameterOperationMap = new HashMap<>();
    }

    public void prepare() {
        // Reset Parameters
        this.sqlQuery = new ActionParameterOperation(executionControl, actionExecution, actionExecution.getAction().getType(), "query");
        this.connectionName = new ActionParameterOperation(this.executionControl, actionExecution, actionExecution.getAction().getType(), "connection");

        // Get Parameters
        for (ActionParameter actionParameter : actionExecution.getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("query")) {
                sqlQuery.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("connection")) {
                connectionName.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            }
        }

        //Create parameter list
        actionParameterOperationMap.put("query", sqlQuery);
        actionParameterOperationMap.put("connection", connectionName);
    }


    public boolean execute() throws InterruptedException {
        try {
            return executeOperation();
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

    private boolean executeOperation() throws InterruptedException {

        String query = convertQuery(sqlQuery.getValue());
        String connectionName = convertConnectionName(this.connectionName.getValue());
        // Get Connection
        Connection connection = ConnectionConfiguration.getInstance().get(new ConnectionKey(connectionName, this.executionControl.getEnvName()))
                .orElseThrow(() -> new RuntimeException("Could not find connection " + connectionName));
        ConnectionOperation connectionOperation = new ConnectionOperation();
        Database database = connectionOperation.getDatabase(connection);

        // Run the action
        CachedRowSet sqlResultSet = database.executeQuery(query);
        this.executionControl.getExecutionRuntime().setRuntimeVariables(actionExecution, sqlResultSet);
        actionExecution.getActionControl().increaseSuccessCount();
        return true;
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

    private String convertQuery(DataType query) {
        if (query instanceof Text) {
            return query.toString();
        } else {
            LOGGER.warn(MessageFormat.format(actionExecution.getAction().getType() + " does not accept {0} as type for query",
                    query.getClass()));
            return query.toString();
        }
    }

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }
}