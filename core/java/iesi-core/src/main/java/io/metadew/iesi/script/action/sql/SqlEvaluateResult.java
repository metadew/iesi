package io.metadew.iesi.script.action.sql;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.text.MessageFormat;


public class SqlEvaluateResult extends ActionTypeExecution {

    private static final String SQL_QUERY_KEY = "query";
    private static final String SQL_EXPECTED_RESULT_KEY = "hasResult";
    private static final String CONNECTION_NAME_KEY = "connection";
    private static final Logger LOGGER = LogManager.getLogger();

    public SqlEvaluateResult(ExecutionControl executionControl,
                             ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepareAction() { }

    protected boolean executeAction() throws InterruptedException {
        String query = convertQuery(getParameterResolvedValue(SQL_QUERY_KEY));
        boolean hasResult = convertHasResult(getParameterResolvedValue(SQL_EXPECTED_RESULT_KEY));
        String connectionName = convertConnectionName(getParameterResolvedValue(CONNECTION_NAME_KEY));

        Connection connection = ConnectionConfiguration.getInstance()
                .get(new ConnectionKey(connectionName, this.getExecutionControl().getEnvName()))
                .orElseThrow(() -> new RuntimeException("Unknown connection name: " + connectionName));

        Database database = DatabaseHandler.getInstance().getDatabase(connection);

        // Run the action
        CachedRowSet crs;
        crs = DatabaseHandler.getInstance().executeQuery(database, query);
        int rowCount = SQLTools.getRowCount(crs);
        this.getActionExecution().getActionControl().logOutput("count", Integer.toString(rowCount));

        if (rowCount > 0) {
            if (hasResult) {
                this.getActionExecution().getActionControl().increaseSuccessCount();
                return true;
            } else {
                getActionExecution().getActionControl().logOutput("action.error",
                        "sql query '" + query + "' does not contain any rows");
                this.getActionExecution().getActionControl().increaseErrorCount();
                return false;
            }
        } else {
            if (!hasResult) {
                this.getActionExecution().getActionControl().increaseSuccessCount();
                return true;
            } else {
                getActionExecution().getActionControl().logOutput("action.error",
                        "sql query '" + query + "' contains " + rowCount + " rows");
                this.getActionExecution().getActionControl().increaseErrorCount();
                return false;
            }
        }
    }

    @Override
    protected String getKeyword() {
        return "sql.evaluateResult";
    }

    private boolean convertHasResult(DataType hasResult) {
        if (hasResult instanceof Text) {
            return hasResult.toString().equalsIgnoreCase("y");
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for expect result",
                    hasResult.getClass()));
            return false;
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
}