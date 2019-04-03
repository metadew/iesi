package io.metadew.iesi.script.action;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.DatabaseConnection;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;

public class SqlEvaluateResult {

	private ActionExecution actionExecution;
	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;

	// Parameters
	private ActionParameterOperation sqlQuery;
	private ActionParameterOperation expectedResult;
	private ActionParameterOperation connectionName;
	private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

	// Constructors
	public SqlEvaluateResult() {
		
	}
	
	public SqlEvaluateResult(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
		this.init(frameworkExecution, executionControl, scriptExecution, actionExecution);
	}
	
	public void init(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
		this.setFrameworkExecution(frameworkExecution);
		this.setExecutionControl(executionControl);
		this.setActionExecution(actionExecution);
		this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
	}

	public void prepare() {
		// Reset Parameters
		this.setSqlQuery(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(), this.getActionExecution(),
				this.getActionExecution().getAction().getType(), "query"));
		this.setExpectedResult(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(), this.getActionExecution(),
				this.getActionExecution().getAction().getType(), "hasResult"));
		this.setConnectionName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(), this.getActionExecution(),
				this.getActionExecution().getAction().getType(), "connection"));

		// Get Parameters
		for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
			if (actionParameter.getName().equalsIgnoreCase("query")) {
				this.getSqlQuery().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("hasresult")) {
				this.getExpectedResult().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("connection")) {
				this.getConnectionName().setInputValue(actionParameter.getValue());
			}
		}

		//Create parameter list
		this.getActionParameterOperationMap().put("query", this.getSqlQuery());
		this.getActionParameterOperationMap().put("hasResult", this.getExpectedResult());
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
			
			// Run the action
			CachedRowSet crs = null;
			// String query = this.getExecutionControl().getExecutionRuntime().resolveVariables(this.getFrameworkExecution().getSqlTools().getFirstSQLStmt(this.getFilePath().getValue(), this.getFileName().getValue()));
			crs = databaseConnection.executeQueryLimitRows(this.getSqlQuery().getValue(),10);
			int rowCount = SQLTools.getRowCount(crs);
			this.getActionExecution().getActionControl().logOutput("count",Integer.toString(rowCount));

			if (rowCount > 0) {
				if (this.getExpectedResult().getValue().equalsIgnoreCase("y")) {
					if (this.getActionExecution().getAction().getErrorExpected().equalsIgnoreCase("y")) {
						this.getActionExecution().getActionControl().increaseErrorCount();
					} else {
						this.getActionExecution().getActionControl().increaseSuccessCount();
					}
					return true;
				} else {
					if (this.getActionExecution().getAction().getErrorExpected().equalsIgnoreCase("n")) {
						this.getActionExecution().getActionControl().increaseErrorCount();
					} else {
						this.getActionExecution().getActionControl().increaseSuccessCount();
					}
					return false;
				}
			} else {
				if (this.getExpectedResult().getValue().equalsIgnoreCase("n")) {
					if (this.getActionExecution().getAction().getErrorExpected().equalsIgnoreCase("y")) {
						this.getActionExecution().getActionControl().increaseErrorCount();
					} else {
						this.getActionExecution().getActionControl().increaseSuccessCount();
					}
					return true;
				} else {
					if (this.getActionExecution().getAction().getErrorExpected().equalsIgnoreCase("n")) {
						this.getActionExecution().getActionControl().increaseErrorCount();
					} else {
						this.getActionExecution().getActionControl().increaseSuccessCount();
					}
					return false;
				}
			}
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));

			this.getActionExecution().getActionControl().increaseErrorCount();

			this.getActionExecution().getActionControl().logOutput("exception",e.getMessage());
			this.getActionExecution().getActionControl().logOutput("stacktrace",StackTrace.toString());

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

}