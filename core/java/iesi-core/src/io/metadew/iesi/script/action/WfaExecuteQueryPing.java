package io.metadew.iesi.script.action;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.database.connection.DatabaseConnection;
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

public class WfaExecuteQueryPing {

	private ActionExecution actionExecution;
	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;

	// Parameters
	private ActionParameterOperation sqlQuery;
	private ActionParameterOperation expectedResult;
	private ActionParameterOperation setRuntimeVariables;
	private ActionParameterOperation connectionName;
	private int waitInterval;
	private int timeoutInterval;
	private long startTime;
	private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

	// Constructors
	public WfaExecuteQueryPing() {
		
	}
	
	public WfaExecuteQueryPing(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ScriptExecution scriptExecution,  ActionExecution actionExecution) {
		this.init(frameworkExecution, executionControl, scriptExecution, actionExecution);
	}
	
	public void init(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ScriptExecution scriptExecution,  ActionExecution actionExecution) {
		this.setFrameworkExecution(frameworkExecution);
		this.setExecutionControl(executionControl);
		this.setActionExecution(actionExecution);
		this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
	}

	public void prepare() {
		// Set Parameters
		this.setSqlQuery(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(), this.getActionExecution(),
				this.getActionExecution().getAction().getType(), "query"));
		this.setExpectedResult(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(), this.getActionExecution(),
				this.getActionExecution().getAction().getType(), "hasResult"));
		this.setSetRuntimeVariables(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "setRuntimeVariables"));
		this.setConnectionName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(), this.getActionExecution(),
				this.getActionExecution().getAction().getType(), "connection"));
		this.setWaitInterval(1000);
		this.setTimeoutInterval(-1);

		// Get Parameters
		for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
			if (actionParameter.getName().equalsIgnoreCase("query")) {
				this.getSqlQuery().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("hasresult")) {
				this.getExpectedResult().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("setruntimevariables")) {
				this.getSetRuntimeVariables().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("connection")) {
				this.getConnectionName().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("wait")) {
				this.setWaitInterval(Integer.parseInt(actionParameter.getValue()));
			} else if (actionParameter.getName().equalsIgnoreCase("timeout")) {
				this.setTimeoutInterval(Integer.parseInt(actionParameter.getValue()));
			}
		}
		
		//Create parameter list
		this.getActionParameterOperationMap().put("query", this.getSqlQuery());
		this.getActionParameterOperationMap().put("hasResult", this.getExpectedResult());
		this.getActionParameterOperationMap().put("setRuntimeVariables", this.getSetRuntimeVariables());
		this.getActionParameterOperationMap().put("connection", this.getConnectionName());
	}
	
	public void execute() {
		try {
			// Get Connection
			ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(this.getFrameworkExecution());
			Connection connection = connectionConfiguration.getConnection(this.getConnectionName().getValue(),
					this.getExecutionControl().getEnvName()).get();
			ConnectionOperation connectionOperation = new ConnectionOperation(this.getFrameworkExecution());
			DatabaseConnection databaseConnection = connectionOperation.getDatabaseConnection(connection);

			// Run the action
			int i = 1;
			long wait = this.getWaitInterval() * 1000;
			if (wait <= 0)
				wait = 1000;
			boolean checkTimeout = false;
			long timeout = this.getTimeoutInterval() * 1000;
			long timeoutCounter = 0;
			if (timeout > 0)
				checkTimeout = true;

			boolean done = false;
			this.setStartTime(System.currentTimeMillis());
			while (i == 1) {
				if (this.doneWaiting(databaseConnection)) {
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

				this.getActionExecution().getActionControl().logOutput("out","result found");
				this.getActionExecution().getActionControl().logOutput("time",Long.toString(elapsedTime));
			} else {
				this.getActionExecution().getActionControl().increaseErrorCount();

				this.getActionExecution().getActionControl().logOutput("out","time-out");
				this.getActionExecution().getActionControl().logOutput("time",Long.toString(elapsedTime));
			}
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));

			this.getActionExecution().getActionControl().increaseErrorCount();

			this.getActionExecution().getActionControl().logOutput("exception",e.getMessage());
			this.getActionExecution().getActionControl().logOutput("stacktrace",StackTrace.toString());

		}

	}

	private boolean doneWaiting(DatabaseConnection databaseConnection) {
		try {
			CachedRowSet crs = null;
			crs = databaseConnection.executeQuery(this.getSqlQuery().getValue());
			if (SQLTools.getRowCount(crs) > 0) {
				if (this.getExpectedResult().getValue().equalsIgnoreCase("y")) {
					this.setRuntimeVariable(crs);
					return true;
				} else {
					return false;
				}
			} else {
				if (this.getExpectedResult().getValue().equalsIgnoreCase("n")) {
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));

			this.getActionExecution().getActionControl().increaseErrorCount();

			this.getActionExecution().getActionControl().logOutput("exception",e.getMessage());
			this.getActionExecution().getActionControl().logOutput("stacktrace",StackTrace.toString());

			throw new RuntimeException(e.getMessage());
		}

	}

	private void setRuntimeVariable(CachedRowSet crs) {
		if (this.getSetRuntimeVariables().getValue().equalsIgnoreCase("y")) {
			try {
				this.getExecutionControl().getExecutionRuntime().setRuntimeVariables(this.getActionExecution(), crs);
			} catch (Exception e) {
				this.getActionExecution().getActionControl().increaseWarningCount();
				this.getExecutionControl().logExecutionOutput(this.getActionExecution(), "SET_RUN_VAR", e.getMessage());
			}
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

	public int getWaitInterval() {
		return waitInterval;
	}

	public void setWaitInterval(int waitInterval) {
		this.waitInterval = waitInterval;
	}

	public int getTimeoutInterval() {
		return timeoutInterval;
	}

	public void setTimeoutInterval(int timeoutInterval) {
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