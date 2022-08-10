package io.metadew.iesi.script.action.sql;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import lombok.extern.log4j.Log4j2;

import javax.sql.rowset.CachedRowSet;
import java.text.MessageFormat;

@Log4j2
public class SqlSetIterationVariables extends ActionTypeExecution {

    // Parameters
    private static final String LIST_KEY = "list";
    private static final String QUERY_KEY = "query";
    private static final String CONNECTION_KEY = "connection";

    private final DatabaseHandler databaseHandler = SpringContext.getBean(DatabaseHandler.class);


    public SqlSetIterationVariables(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }


    public void prepareAction() {
    }

    protected boolean executeAction() throws InterruptedException {

        String query = convertQuery(getParameterResolvedValue(QUERY_KEY));
        String connectionName = convertConnectionName(getParameterResolvedValue(CONNECTION_KEY));
        String listName = convertListName(getParameterResolvedValue(LIST_KEY));

        // Get Connection
        Connection connection = ConnectionConfiguration.getInstance().get(new ConnectionKey(connectionName, this.getExecutionControl().getEnvName()))
                .get();
        Database database = databaseHandler.getDatabase(connection);
        // Run the action
        CachedRowSet sqlResultSet = databaseHandler.executeQuery(database, query);
        this.getExecutionControl().getExecutionRuntime().setIterationVariables(listName, sqlResultSet);
        this.getActionExecution().getActionControl().increaseSuccessCount();

        return true;
    }

    @Override
    protected String getKeyword() {
        return "sql.setIterationVariables";
    }

    private String convertListName(DataType listName) {
        if (listName instanceof Text) {
            return listName.toString();
        } else {
            log.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for listName",
                    listName.getClass()));
            return listName.toString();
        }
    }

    private String convertConnectionName(DataType connectionName) {
        if (connectionName instanceof Text) {
            return connectionName.toString();
        } else {
            log.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for connection name",
                    connectionName.getClass()));
            return connectionName.toString();
        }
    }

    private String convertQuery(DataType query) {
        if (query instanceof Text) {
            return query.toString();
        } else {
            log.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for query",
                    query.getClass()));
            return query.toString();
        }
    }

}