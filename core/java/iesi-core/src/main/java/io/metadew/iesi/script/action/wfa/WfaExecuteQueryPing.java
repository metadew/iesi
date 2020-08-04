package io.metadew.iesi.script.action.wfa;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.connection.tools.SQLTools;
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

public class WfaExecuteQueryPing {

    // Parameters
    private ActionParameterOperation sqlQuery;
    private ActionParameterOperation expectedResult;
    private ActionParameterOperation setRuntimeVariables;
    private ActionParameterOperation connectionName;
    private ActionParameterOperation waitInterval;
    private ActionParameterOperation timeoutInterval;
    private long startTime;

    private final int defaultWaitInterval = 1000;
    private final int defaultTimeoutInterval = -1;

    private static final Logger LOGGER = LogManager.getLogger();

    // Constructors
    public WfaExecuteQueryPing() {

    }

    public WfaExecuteQueryPing(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.init(executionControl, scriptExecution, actionExecution);
    }

    public void init(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setActionParameterOperationMap(new HashMap<>());
    }

    public void prepare() {
        // Set Parameters
        this.setSqlQuery(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "query"));
        this.setExpectedResult(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "hasResult"));
        this.setSetRuntimeVariables(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "setRuntimeVariables"));
        this.setConnectionName(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "connection"));
        this.setWaitInterval(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "wait"));
        this.setTimeoutInterval(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "timeout"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("query")) {
                this.getSqlQuery().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("hasresult")) {
                this.getExpectedResult().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("setruntimevariables")) {
                this.getSetRuntimeVariables().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("connection")) {
                this.getConnectionName().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("wait")) {
                this.getWaitInterval().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("timeout")) {
                this.getTimeoutInterval().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            }
        }

        //Create parameter list
        this.getActionParameterOperationMap().put("query", this.getSqlQuery());
        this.getActionParameterOperationMap().put("hasResult", this.getExpectedResult());
        this.getActionParameterOperationMap().put("setRuntimeVariables", this.getSetRuntimeVariables());
        this.getActionParameterOperationMap().put("connection", this.getConnectionName());
        this.getActionParameterOperationMap().put("wait", this.getWaitInterval());
        this.getActionParameterOperationMap().put("timeout", this.getTimeoutInterval());
    }

    public boolean execute() throws InterruptedException {
        try {
            String query = convertQuery(getSqlQuery().getValue());
            String connectionName = convertConnectionName(getConnectionName().getValue());
            boolean hasResult = convertHasResult(getExpectedResult().getValue());
            boolean setRuntimeVariables = converSetRuntimeVariable(getSetRuntimeVariables().getValue());
            int timeoutInterval = convertTimeoutInterval(getTimeoutInterval().getValue());
            int waitInterval = convertWaitInterval(getWaitInterval().getValue());
            return executeQueryPing(query, connectionName, hasResult, setRuntimeVariables, waitInterval, timeoutInterval);

        } catch (InterruptedException e) {
            throw (e);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());
            return false;
        }

    }

    private int convertWaitInterval(DataType waitInterval) {
        if (waitInterval == null) {
            return defaultWaitInterval;
        }
        if (waitInterval instanceof Text) {
            return Integer.parseInt(waitInterval.toString());
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for wait interval",
                    waitInterval.getClass()));
            return defaultWaitInterval;
        }
    }

    private int convertTimeoutInterval(DataType timeoutInterval) {
        if (timeoutInterval == null) {
            return defaultTimeoutInterval;
        }
        if (timeoutInterval instanceof Text) {
            return Integer.parseInt(timeoutInterval.toString());
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for timeout interval",
                    timeoutInterval.getClass()));
            return defaultTimeoutInterval;
        }
    }

    private boolean executeQueryPing(String query, String connectionName, boolean hasResult, boolean setRuntimeVariables, int waitInterval, int timeoutInterval) throws InterruptedException {
        // Get Connection
        Connection connection = ConnectionConfiguration.getInstance()
                .get(new ConnectionKey(connectionName, this.getExecutionControl().getEnvName()))
                .get();
        ConnectionOperation connectionOperation = new ConnectionOperation();
        Database database = DatabaseHandler.getInstance().getDatabase(connection);

        // Run the action
        int i = 1;
        long wait = waitInterval * 1000;
        if (wait <= 0)
            wait = 1000;
        boolean checkTimeout = false;
        long timeout = timeoutInterval * 1000;
        long timeoutCounter = 0;
        if (timeout > 0)
            checkTimeout = true;

        boolean done = false;
        this.setStartTime(System.currentTimeMillis());
        while (i == 1) {
            if (this.doneWaiting(database, query, hasResult, setRuntimeVariables)) {
                done = true;
                break;
            }

            if (checkTimeout) {
                timeoutCounter += wait;
                if (timeoutCounter >= timeout)
                    break;
            }

            try {
                Thread.sleep(wait);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

        }

        long elapsedTime = System.currentTimeMillis() - this.getStartTime();
        if (done) {
            this.getActionExecution().getActionControl().increaseSuccessCount();

            this.getActionExecution().getActionControl().logOutput("out", "result found");
            this.getActionExecution().getActionControl().logOutput("time", Long.toString(elapsedTime));
            return true;
        } else {
            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("out", "time-out");
            this.getActionExecution().getActionControl().logOutput("time", Long.toString(elapsedTime));
            return false;
        }
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

    private boolean doneWaiting(Database database, String query, boolean hasResult, boolean setRuntimeVariables) {
        CachedRowSet crs;
        crs = DatabaseHandler.getInstance().executeQuery(database, query);
        if (SQLTools.getRowCount(crs) > 0) {
            if (hasResult) {
                this.setRuntimeVariable(crs, setRuntimeVariables);
                return true;
            } else {
                return false;
            }
        } else {
            if (!hasResult) {
                return true;
            } else {
                return false;
            }
        }
    }

    private void setRuntimeVariable(CachedRowSet crs, boolean setRuntimeVariables) {
        if (setRuntimeVariables) {
            this.getExecutionControl().getExecutionRuntime().setRuntimeVariables(actionExecution, crs);
        }
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    public ActionParameterOperation getWaitInterval() {
        return waitInterval;
    }

    public void setWaitInterval(ActionParameterOperation waitInterval) {
        this.waitInterval = waitInterval;
    }

    public ActionParameterOperation getTimeoutInterval() {
        return timeoutInterval;
    }

    public void setTimeoutInterval(ActionParameterOperation timeoutInterval) {
        this.timeoutInterval = timeoutInterval;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public ActionExecution getActionExecution() {
        return actionExecution;
    }

    public void setActionExecution(ActionExecution actionExecution) {
        this.actionExecution = actionExecution;
    }

    public ActionParameterOperation getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(ActionParameterOperation expectedResult) {
        this.expectedResult = expectedResult;
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

    public ActionParameterOperation getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(ActionParameterOperation sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public ActionParameterOperation getSetRuntimeVariables() {
        return setRuntimeVariables;
    }

    public void setSetRuntimeVariables(ActionParameterOperation setRuntimeVariables) {
        this.setRuntimeVariables = setRuntimeVariables;
    }

}