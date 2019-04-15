package io.metadew.iesi.script.action;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import io.metadew.iesi.connection.DatabaseConnection;
import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;

public class SqlExecuteQuery {

	private ActionExecution actionExecution;
	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;

	// Parameters
	private ActionParameterOperation sqlQuery;
	private ActionParameterOperation connectionName;
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

		// Get Parameters
		for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
			if (actionParameter.getName().equalsIgnoreCase("query")) {
				this.getSqlQuery().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("connection")) {
				this.getConnectionName().setInputValue(actionParameter.getValue());
			}
		}

		// Create parameter list
		this.getActionParameterOperationMap().put("query", this.getSqlQuery());
		this.getActionParameterOperationMap().put("connection", this.getConnectionName());
	}

	public boolean execute() {
		try {
			// Get Connection
			ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(this.getFrameworkExecution());
			Connection connection = connectionConfiguration.getConnection(this.getConnectionName().getValue(),
					this.getExecutionControl().getEnvName());
			ConnectionOperation connectionOperation = new ConnectionOperation(this.getFrameworkExecution());
			DatabaseConnection databaseConnection = connectionOperation.getDatabaseConnection(connection);

			if (databaseConnection == null) {
				throw new RuntimeException("Error establishing DB connection");
			}

			// Run the action
			// Make sure the SQL statement is ended with a ;
			if (!this.getSqlQuery().getValue().trim().endsWith(";")) {
				this.getSqlQuery().setValue(this.getSqlQuery().getValue() + ";");
			}

			// Convert to inputstream
			InputStream inputStream = FileTools.convertToInputStream(this.getSqlQuery().getValue(),
					this.getFrameworkExecution().getFrameworkControl());
			SqlScriptResult dcSQLScriptResult = databaseConnection.executeScript(inputStream);

			// Evaluate result
			this.getActionExecution().getActionControl().logOutput("sys.out", dcSQLScriptResult.getSystemOutput());

			if (dcSQLScriptResult.getReturnCode() != 0) {
				this.getActionExecution().getActionControl().logOutput("err.out", dcSQLScriptResult.getErrorOutput());
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

	public ActionParameterOperation getSqlQuery() {
		return sqlQuery;
	}

	public void setSqlQuery(ActionParameterOperation sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

}
