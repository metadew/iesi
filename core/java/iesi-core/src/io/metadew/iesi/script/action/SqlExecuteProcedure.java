package io.metadew.iesi.script.action;

import io.metadew.iesi.connection.database.connection.DatabaseConnection;
import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.connection.tools.sql.SQLDataTransfer;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import io.metadew.iesi.script.operation.DatasetOperation;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

public class SqlExecuteProcedure {

    private ActionExecution actionExecution;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation sqlProcedure;
    private ActionParameterOperation connectionName;
    private ActionParameterOperation sqlParameters;
    private ActionParameterOperation outputDataset;
    private ActionParameterOperation appendOutput;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Constructors
    public SqlExecuteProcedure() {

    }

    public SqlExecuteProcedure(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
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
        this.setSqlProcedure(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "procedure"));
        this.setConnectionName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "connection"));
        this.setSqlParameters(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "parameters"));
        this.setOutputDataset(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "outputDataset"));
        this.setAppendOutput(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "appendOutput"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("procedure")) {
                this.getSqlProcedure().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("connection")) {
                this.getConnectionName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("parameters")) {
                this.getSqlParameters().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("outputdataset")) {
                this.getOutputDataset().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("appendoutput")) {
                this.getAppendOutput().setInputValue(actionParameter.getValue());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("procedure", this.getSqlProcedure());
        this.getActionParameterOperationMap().put("connection", this.getConnectionName());
        this.getActionParameterOperationMap().put("parameters", this.getSqlParameters());
        this.getActionParameterOperationMap().put("outputDataset", this.getOutputDataset());
        this.getActionParameterOperationMap().put("appendOutput", this.getAppendOutput());
    }

    public boolean execute() {
        try {
            // Get Connection
            ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(this.getFrameworkExecution());
            Connection connection = connectionConfiguration
                    .getConnection(this.getConnectionName().getValue(), this.getExecutionControl().getEnvName()).get();
            ConnectionOperation connectionOperation = new ConnectionOperation(this.getFrameworkExecution());
            DatabaseConnection databaseConnection = connectionOperation.getDatabaseConnection(connection);

            if (databaseConnection == null) {
                throw new RuntimeException("Error establishing DB connection");
            }

            SqlScriptResult sqlScriptResult = null;
            CachedRowSet crs = null;
            crs = databaseConnection.executeProcedure(this.getSqlProcedure().getValue(), this.getSqlParameters().getValue());
            // TODO resolve for files and resolve inside
            // TODO Retrieve config from a file

            if (!this.getOutputDataset().getValue().isEmpty()) {
                DatasetOperation datasetOperation = this.getExecutionControl().getExecutionRuntime()
                        .getDatasetOperation(this.getOutputDataset().getValue());
                DatabaseConnection outputDatabaseConnection = datasetOperation.getDatasetConnection();

                // Append logic
                boolean clean = true;
                if (this.getAppendOutput().getValue().equalsIgnoreCase("y")) {
                    clean = false;
                }

                // Perform the action
                SQLDataTransfer.transferData(crs, outputDatabaseConnection, datasetOperation.getDatasetName(), clean);
                sqlScriptResult = new SqlScriptResult(0, "data.transfer.complete", "");

            } else {
                sqlScriptResult = new SqlScriptResult(0, "sql.procedure.complete", "");
            }

            // Evaluate result
            this.getActionExecution().getActionControl().logOutput("sys.out", sqlScriptResult.getSystemOutput());

            if (sqlScriptResult.getReturnCode() != 0) {
                this.getActionExecution().getActionControl().logOutput("err.out", sqlScriptResult.getErrorOutput());
                throw new RuntimeException("Error execting SQL query");
            }

            this.getActionExecution().getActionControl().increaseSuccessCount();
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

    public ActionParameterOperation getSqlProcedure() {
        return sqlProcedure;
    }

    public void setSqlProcedure(ActionParameterOperation sqlProcedure) {
        this.sqlProcedure = sqlProcedure;
    }

    public ActionParameterOperation getSqlParameters() {
        return sqlParameters;
    }

    public void setSqlParameters(ActionParameterOperation sqlParameters) {
        this.sqlParameters = sqlParameters;
    }

}
