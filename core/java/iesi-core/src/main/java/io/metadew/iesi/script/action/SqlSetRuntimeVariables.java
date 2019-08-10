package io.metadew.iesi.script.action;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.connection.Connection;
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

    // Constructors
    public SqlSetRuntimeVariables() {

    }

    public SqlSetRuntimeVariables(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.init(executionControl, scriptExecution, actionExecution);
    }

    public void init(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
    }

    public void prepare() {
        // Reset Parameters
        this.setSqlQuery(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "query"));
        this.setConnectionName(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "connection"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("query")) {
                this.getSqlQuery().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("connection")) {
                this.getConnectionName().setInputValue(actionParameter.getValue());
            }
        }

        //Create parameter list
        this.getActionParameterOperationMap().put("query", this.getSqlQuery());
        this.getActionParameterOperationMap().put("connection", this.getConnectionName());
    }


    public boolean execute() {
        try {
            String query = convertQuery(getSqlQuery().getValue());
            String connectionName = convertConnectionName(getConnectionName().getValue());
            // Get Connection
            ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration();
            Connection connection = connectionConfiguration.getConnection(connectionName,
                    this.getExecutionControl().getEnvName()).get();
            ConnectionOperation connectionOperation = new ConnectionOperation();
            Database database = connectionOperation.getDatabase(connection);
            
            // Run the action
            CachedRowSet sqlResultSet = database.executeQuery(query);
            try {
                this.getExecutionControl().getExecutionRuntime().setRuntimeVariables(actionExecution, sqlResultSet);
                this.getActionExecution().getActionControl().increaseSuccessCount();
            } catch (Exception e) {
                throw new RuntimeException("Issue setting runtime variables: " + e, e);
            }

            return true;
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

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

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    public ActionExecution getActionExecution() {
        return actionExecution;
    }

    public void setActionExecution(ActionExecution actionExecution) {
        this.actionExecution = actionExecution;
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

}