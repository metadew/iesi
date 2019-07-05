package io.metadew.iesi.script.action;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.connection.tools.sql.SQLDataTransfer;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.Text;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.Level;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Optional;


public class SqlExecuteQuery {

    private ActionExecution actionExecution;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation sqlQuery;
    private ActionParameterOperation connectionName;
    private ActionParameterOperation outputDataset;
    private ActionParameterOperation appendOutput;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Constructors
    public SqlExecuteQuery() {

    }

    public SqlExecuteQuery(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                           ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.init(frameworkExecution, executionControl, scriptExecution, actionExecution);
    }

    public void init(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                     ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
    }

    public void prepare() {
        // Set Parameters
        this.setSqlQuery(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "query"));
        this.setConnectionName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "connection"));
        this.setOutputDataset(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "outputDataset"));
        this.setAppendOutput(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "appendOutput"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("query")) {
                this.getSqlQuery().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("connection")) {
                this.getConnectionName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("outputdataset")) {
                this.getOutputDataset().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("appendoutput")) {
                this.getAppendOutput().setInputValue(actionParameter.getValue());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("query", this.getSqlQuery());
        this.getActionParameterOperationMap().put("connection", this.getConnectionName());
        this.getActionParameterOperationMap().put("outputdataset", this.getOutputDataset());
        this.getActionParameterOperationMap().put("appendoutput", this.getAppendOutput());
    }


    public boolean execute() {
        try {
            String query = convertQuery(getSqlQuery().getValue());
            String connection = convertConnectionName(getConnectionName().getValue());
            String outputDatasetReferenceName = convertDatasetReferenceName(getSqlQuery().getValue());
            boolean appendOutput = convertAppendOutput(getConnectionName().getValue());
            return executeQuery(query, connection, outputDatasetReferenceName, appendOutput);
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
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for connection name",
                    connectionName.getClass()), Level.WARN);
            return connectionName.toString();
        }
    }

    private String convertQuery(DataType query) {
        if (query instanceof Text) {
            return query.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() +  " does not accept {0} as type for query",
                    query.getClass()), Level.WARN);
            return query.toString();
        }
    }

    private boolean convertAppendOutput(DataType appendOutput) {
        if (appendOutput instanceof Text) {
            return appendOutput.toString().equalsIgnoreCase("y");
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for appendOutput",
                    appendOutput.getClass()), Level.WARN);
            return false;
        }
    }

    private String convertDatasetReferenceName(DataType datasetReferenceName) {
        if (datasetReferenceName instanceof Text) {
            return datasetReferenceName.toString();
        } else {
            frameworkExecution.getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for dataset reference name",
                    datasetReferenceName.getClass()), Level.WARN);
            return datasetReferenceName.toString();
        }
    }

    private boolean executeQuery(String query, String connectionName, String outputDatasetReferenceName, boolean appendOutput) throws SQLException {

        // Get Connection
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(this.getFrameworkExecution().getFrameworkInstance());
        Connection connection = connectionConfiguration
                .getConnection(connectionName, this.getExecutionControl().getEnvName()).get();
        ConnectionOperation connectionOperation = new ConnectionOperation(this.getFrameworkExecution());
        Database database = connectionOperation.getDatabase(connection);

        if (database == null) {
            throw new RuntimeException("Error establishing DB connection");
        }

        // Run the action
        // Make sure the SQL statement is ended with a ;
        if (!query.trim().endsWith(";")) {
            query = query + ";";
        }

        SqlScriptResult sqlScriptResult;

        Optional<Dataset> dataset = this.getExecutionControl().getExecutionRuntime()
                .getDataset(outputDatasetReferenceName);
        CachedRowSet crs = database.executeQuery(query);
        this.getActionExecution().getActionControl().logOutput("sql.execute.size", Integer.toString(crs.size()));
        // TODO resolve for files and resolve inside
        if (dataset.isPresent()) {
            SQLDataTransfer.transferData(crs, dataset.get().getDatasetDatabase(), dataset.get().getName(), appendOutput);
            sqlScriptResult = new SqlScriptResult(0, "data.transfer.complete", "");
        }

        sqlScriptResult = new SqlScriptResult(0, "sql.execute.complete", "");

        // Evaluate result
        this.getActionExecution().getActionControl().logOutput("sys.out", sqlScriptResult.getSystemOutput());

        if (sqlScriptResult.getReturnCode() != 0) {
            this.getActionExecution().getActionControl().logOutput("err.out", sqlScriptResult.getErrorOutput());
            throw new RuntimeException("Error execting SQL query");
        }

        this.getActionExecution().getActionControl().increaseSuccessCount();
        return true;
    }

    // Getters and Setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
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

    public ActionParameterOperation getActionParameterOperation(String key) {
        return this.getActionParameterOperationMap().get(key);
    }

    public ActionParameterOperation getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(ActionParameterOperation sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public ActionParameterOperation getOutputDataset() {
        return outputDataset;
    }

    public void setOutputDataset(ActionParameterOperation outputDataset) {
        this.outputDataset = outputDataset;
    }

    public ActionParameterOperation getAppendOutput() {
        return appendOutput;
    }

    public void setAppendOutput(ActionParameterOperation appendOutput) {
        this.appendOutput = appendOutput;
    }

}
