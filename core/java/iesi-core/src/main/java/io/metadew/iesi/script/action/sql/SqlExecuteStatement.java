package io.metadew.iesi.script.action.sql;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

public class SqlExecuteStatement extends ActionTypeExecution {

    private static final String SQL_STATEMENT_KEY = "statement";
    private static final String CONNECTION_NAME_KEY = "connection";
    private static final Logger LOGGER = LogManager.getLogger();

    public SqlExecuteStatement(ExecutionControl executionControl,
                               ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() { }

    protected boolean executeAction() throws InterruptedException {
        String sqlStatement = convertSqlStatement(getParameterResolvedValue(SQL_STATEMENT_KEY));
        String connectionName = convertConnectionName(getParameterResolvedValue(CONNECTION_NAME_KEY));
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

    @Override
    protected String getKeyword() {
        return "sql.executeStatement";
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
}
