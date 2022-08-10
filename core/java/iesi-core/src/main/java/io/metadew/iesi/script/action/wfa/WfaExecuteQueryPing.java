package io.metadew.iesi.script.action.wfa;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class WfaExecuteQueryPing extends ActionTypeExecution {

    // Parameters
    private static final String QUERY_KEY = "query";
    private static final String HAS_RESULT_KEY = "hasResult";
    private static final String SET_RUNTIME_VARIABLES_KEY = "setruntimevariables";
    private static final String CONNECTION_KEY = "connection";
    private static final String WAIT_KEY = "wait";
    private static final String TIMEOUT_KEY = "timeout";

    private long startTime;

    private static final int DEFAULT_WAIT_INTERVAL = 5;
    private static final int DEFAULT_TIMEOUT_INTERVAL = 60;

    private static final Logger LOGGER = LogManager.getLogger();

    private final DatabaseHandler databaseHandler = SpringContext.getBean(DatabaseHandler.class);

    public WfaExecuteQueryPing(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepareAction() {
    }

    private int convertWaitInterval(DataType waitInterval) {
        if (waitInterval == null) {
            return DEFAULT_WAIT_INTERVAL * 1000;
        }
        if (waitInterval instanceof Text) {
            return Integer.parseInt(waitInterval.toString()) * 1000;
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for wait interval",
                    waitInterval.getClass()));
            return DEFAULT_WAIT_INTERVAL * 1000;
        }
    }

    private int convertTimeoutInterval(DataType timeoutInterval) {
        if (timeoutInterval == null) {
            return DEFAULT_TIMEOUT_INTERVAL;
        }
        if (timeoutInterval instanceof Text) {
            return Integer.parseInt(timeoutInterval.toString());
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for timeout interval",
                    timeoutInterval.getClass()));
            return DEFAULT_TIMEOUT_INTERVAL;
        }
    }

    protected boolean executeAction() throws InterruptedException, SQLException {
        // Get Connection
        String query = convertQuery(getParameterResolvedValue(QUERY_KEY));
        String connectionName = convertConnectionName(getParameterResolvedValue(CONNECTION_KEY));
        boolean hasResult = convertHasResult(getParameterResolvedValue(HAS_RESULT_KEY));
        boolean setRuntimeVariables = converSetRuntimeVariable(getParameterResolvedValue(SET_RUNTIME_VARIABLES_KEY));
        int timeoutInterval = convertTimeoutInterval(getParameterResolvedValue(TIMEOUT_KEY));
        int waitInterval = convertWaitInterval(getParameterResolvedValue(WAIT_KEY));

        Connection connection = ConnectionConfiguration.getInstance()
                .get(new ConnectionKey(connectionName, this.getExecutionControl().getEnvName()))
                .orElseThrow(() -> new RuntimeException("Unknown connection name: " + connectionName));

        Database database = databaseHandler.getDatabase(connection);

        // Run the action
        boolean done;
        LocalDateTime start = LocalDateTime.now();
        while (true) {
            if (doneWaiting(database, query, hasResult, setRuntimeVariables)) {
                done = true;
                break;
            } else if (LocalDateTime.now().isAfter(start.plus(timeoutInterval, ChronoUnit.SECONDS))) {
                done = false;
                break;
            }

            try {
                Thread.sleep(waitInterval);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

        }

        long elapsedTime = start.until(LocalDateTime.now(), ChronoUnit.SECONDS);
        if (done) {
            this.getActionExecution().getActionControl().increaseSuccessCount();

            this.getActionExecution().getActionControl().logOutput("out", "result found");
            this.getActionExecution().getActionControl().logOutput("time", Long.toString(elapsedTime));
            return true;
        } else {
            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("action.error", "Query ping timed out");
            this.getActionExecution().getActionControl().logOutput("time", Long.toString(elapsedTime));
            return false;
        }
    }

    @Override
    protected String getKeyword() {
        return "wfa.executeQueryPing";
    }

    private boolean converSetRuntimeVariable(DataType setRuntimeVariables) {
        if (setRuntimeVariables == null) {
            return false;
        }
        if (setRuntimeVariables instanceof Text) {
            return setRuntimeVariables.toString().equalsIgnoreCase("y");
        } else {
            LOGGER.warn(MessageFormat.format("wfa.executeQueryPing does not accept {0} as type for setRuntimeVariables",
                    setRuntimeVariables.getClass()));
            return false;
        }
    }


    private boolean convertHasResult(DataType hasResult) {
        if (hasResult instanceof Text) {
            return hasResult.toString().equalsIgnoreCase("y");
        } else {
            LOGGER.warn(MessageFormat.format("wfa.executeQueryPing does not accept {0} as type for expect result",
                    hasResult.getClass()));
            return false;
        }
    }

    private String convertConnectionName(DataType connectionName) {
        if (connectionName instanceof Text) {
            return connectionName.toString();
        } else {
            LOGGER.warn(MessageFormat.format("wfa.executeQueryPing does not accept {0} as type for connection name",
                    connectionName.getClass()));
            return connectionName.toString();
        }
    }

    private String convertQuery(DataType query) {
        if (query instanceof Text) {
            return query.toString();
        } else {
            LOGGER.warn(MessageFormat.format("wfa.executeQueryPing does not accept {0} as type for query",
                    query.getClass()));
            return query.toString();
        }
    }

    private boolean doneWaiting(Database database, String query, boolean hasResult, boolean setRuntimeVariables) throws SQLException {
        CachedRowSet crs = databaseHandler.executeQuery(database, query);
        if (crs.size() > 0 && hasResult) {
            this.setRuntimeVariable(crs, setRuntimeVariables);
            return true;
        } else if (crs.size() == 0 && !hasResult) {
            return true;
        } else {
            return false;
        }
    }

    private void setRuntimeVariable(CachedRowSet crs, boolean setRuntimeVariables) throws SQLException {
        if (setRuntimeVariables) {
            this.getExecutionControl().getExecutionRuntime().setRuntimeVariables(getActionExecution(), crs);
        }
    }

}