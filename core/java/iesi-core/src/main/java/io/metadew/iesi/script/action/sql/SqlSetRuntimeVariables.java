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
import java.sql.SQLException;
import java.text.MessageFormat;

@Log4j2
public class SqlSetRuntimeVariables extends ActionTypeExecution {

    // Parameters
    private static final String QUERY_KEY = "query";
    private static final String CONNECTION_KEY = "connection";

    private final DatabaseHandler databaseHandler = SpringContext.getBean(DatabaseHandler.class);
    private final ConnectionConfiguration connectionConfiguration = SpringContext.getBean(ConnectionConfiguration.class);

    public SqlSetRuntimeVariables(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepareAction() {
    }

    protected boolean executeAction() throws InterruptedException, SQLException {

        String query = convertQuery(getParameterResolvedValue(QUERY_KEY));
        String connectionName = convertConnectionName(getParameterResolvedValue(CONNECTION_KEY));
        // Get Connection
        Connection connection = connectionConfiguration.get(new ConnectionKey(connectionName, this.getExecutionControl().getEnvName()))
                .orElseThrow(() -> new RuntimeException("Could not find connection " + connectionName + " for env " + getExecutionControl().getEnvName()));

        Database database = databaseHandler.getDatabase(connection);
        // Run the action
        CachedRowSet sqlResultSet = databaseHandler.executeQuery(database, query);
        this.getExecutionControl().getExecutionRuntime().setRuntimeVariables(getActionExecution(), sqlResultSet);
        getActionExecution().getActionControl().increaseSuccessCount();
        return true;
    }

    @Override
    protected String getKeyword() {
        return "sql.setRuntimeVariables";
    }

    private String convertConnectionName(DataType connectionName) {
        if (connectionName instanceof Text) {
            return connectionName.toString();
        } else {
            log.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for connection name",
                    connectionName.getClass()));
            return connectionName.toString();
        }
    }

    private String convertQuery(DataType query) {
        if (query instanceof Text) {
            return query.toString();
        } else {
            log.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for query",
                    query.getClass()));
            return query.toString();
        }
    }

}