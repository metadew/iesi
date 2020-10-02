package io.metadew.iesi.script.action.sql;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.tools.SQLTools;
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


public class SqlEvaluateResult extends ActionTypeExecution {

    // Parameters
    private ActionParameterOperation sqlQuery;
    private ActionParameterOperation expectedResult;
    private ActionParameterOperation connectionName;
    private static final Logger LOGGER = LogManager.getLogger();


    public SqlEvaluateResult(ExecutionControl executionControl,
                             ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        // Reset Parameters
        this.setSqlQuery(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "query"));
        this.setExpectedResult(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "hasResult"));
        this.setConnectionName(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "connection"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("query")) {
                this.getSqlQuery().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("hasresult")) {
                this.getExpectedResult().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("connection")) {
                this.getConnectionName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("query", this.getSqlQuery());
        this.getActionParameterOperationMap().put("hasResult", this.getExpectedResult());
        this.getActionParameterOperationMap().put("connection", this.getConnectionName());
    }

    protected boolean executeAction() throws InterruptedException {
        String query = convertQuery(getSqlQuery().getValue());
        boolean hasResult = convertHasResult(getExpectedResult().getValue());
        String connectionName = convertConnectionName(getConnectionName().getValue());

        Connection connection = ConnectionConfiguration.getInstance()
                .get(new ConnectionKey(connectionName, this.getExecutionControl().getEnvName()))
                .get();
        Database database = DatabaseHandler.getInstance().getDatabase(connection);
        // Run the action
        CachedRowSet crs;
        crs = DatabaseHandler.getInstance().executeQueryLimitRows(database, query, 10);
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

    public ActionParameterOperation getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(ActionParameterOperation sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

}