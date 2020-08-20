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


public class SqlSetIterationVariables extends ActionTypeExecution {

    // Parameters
    private ActionParameterOperation listName;
    private ActionParameterOperation sqlQuery;
    private ActionParameterOperation connectionName;
    private static final Logger LOGGER = LogManager.getLogger();


    public SqlSetIterationVariables(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }


    public void prepare() {
        // Reset Parameters
        this.setListName(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "list"));
        this.setSqlQuery(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "query"));
        this.setConnectionName(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "connection"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("list")) {
                this.getListName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("query")) {
                this.getSqlQuery().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("connection")) {
                this.getConnectionName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        //Create parameter list
        this.getActionParameterOperationMap().put("list", this.getListName());
        this.getActionParameterOperationMap().put("query", this.getSqlQuery());
        this.getActionParameterOperationMap().put("connection", this.getConnectionName());
    }

    protected boolean executeAction() throws InterruptedException {

        String query = convertQuery(getSqlQuery().getValue());
        String connectionName = convertConnectionName(getConnectionName().getValue());
        String listName = convertListName(getListName().getValue());

        // Get Connection
        Connection connection = ConnectionConfiguration.getInstance().get(new ConnectionKey(connectionName, this.getExecutionControl().getEnvName()))
                .get();
        Database database = DatabaseHandler.getInstance().getDatabase(connection);
        // Run the action
        CachedRowSet sqlResultSet = DatabaseHandler.getInstance().executeQuery(database, query);
        this.getExecutionControl().getExecutionRuntime().setIterationVariables(listName, sqlResultSet);
        this.getActionExecution().getActionControl().increaseSuccessCount();

        return true;
    }

    private String convertListName(DataType listName) {
        if (listName instanceof Text) {
            return listName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for listName",
                    listName.getClass()));
            return listName.toString();
        }
    }

    private String convertConnectionName(DataType connectionName) {
        if (connectionName instanceof Text) {
            return connectionName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for connection name",
                    connectionName.getClass()));
            return connectionName.toString();
        }
    }

    private String convertQuery(DataType query) {
        if (query instanceof Text) {
            return query.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for query",
                    query.getClass()));
            return query.toString();
        }
    }

    public ActionParameterOperation getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(ActionParameterOperation connectionName) {
        this.connectionName = connectionName;
    }

    public ActionParameterOperation getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(ActionParameterOperation sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public ActionParameterOperation getListName() {
        return listName;
    }

    public void setListName(ActionParameterOperation listName) {
        this.listName = listName;
    }

}