package io.metadew.iesi.script.action;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.host.ShellCommandResult;
import io.metadew.iesi.connection.host.ShellCommandSettings;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;

/**
 * Action type to execute command line instructions.
 * 
 * @author peter.billen
 *
 */
public class CliExecuteCommand {

	private ActionExecution actionExecution;
	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;

	// Parameters
	private ActionParameterOperation shellPath;
	private ActionParameterOperation shellCommand;
	private ActionParameterOperation setRunVar;
	private ActionParameterOperation setRunVarPrefix;
	private ActionParameterOperation setRunVarMode;
	private ActionParameterOperation connectionName;
	private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

	// Constructors
	public CliExecuteCommand() {

	}

	public CliExecuteCommand(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
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
		// Reset Parameters
		this.setShellPath(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "path"));
		this.setShellCommand(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "command"));
		this.setSetRunVar(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "setRuntimeVariables"));
		this.setSetRunVarPrefix(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(),
				"setRuntimeVariablesPrefix"));
		this.setSetRunVarMode(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "setRuntimeVariablesMode"));
		this.setConnectionName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "connection"));

		// Get Parameters
		for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
			if (actionParameter.getName().equalsIgnoreCase("path")) {
				this.getShellPath().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("command")) {
				this.getShellCommand().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("setruntimevariables")) {
				this.getSetRunVar().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("setruntimevariablesprefix")) {
				this.getSetRunVarPrefix().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("setruntimevariablesmode")) {
				this.getSetRunVarMode().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("connection")) {
				this.getConnectionName().setInputValue(actionParameter.getValue());
			}
		}

		// Create parameter list
		this.getActionParameterOperationMap().put("path", this.getShellPath());
		this.getActionParameterOperationMap().put("command", this.getShellCommand());
		this.getActionParameterOperationMap().put("setRuntimeVariables", this.getSetRunVar());
		this.getActionParameterOperationMap().put("setRuntimeVariablesPrefix", this.getSetRunVarPrefix());
		this.getActionParameterOperationMap().put("setRuntimeVariablesMode", this.getSetRunVarMode());
		this.getActionParameterOperationMap().put("connection", this.getConnectionName());
	}

	// Methods
	public boolean execute() {
		try {
			// Get Connection
			ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(this.getFrameworkExecution());
			Connection connection = connectionConfiguration.getConnection(this.getConnectionName().getValue(),
					this.getExecutionControl().getEnvName()).get();
			ConnectionOperation connectionOperation = new ConnectionOperation(this.getFrameworkExecution());
			HostConnection hostConnection = connectionOperation.getHostConnection(connection);

			// Check if running on localhost or not
			boolean isOnLocalHost = connectionOperation.isOnLocalConnection(hostConnection);

			// Run the action
			ShellCommandResult shellCommandResult = null;
			ShellCommandSettings shellCommandSettings = new ShellCommandSettings();
			shellCommandSettings.setSetRunVar(this.getSetRunVar().getValue());
			shellCommandSettings.setSetRunVarPrefix(this.getSetRunVarPrefix().getValue());
			shellCommandSettings.setSetRunVarMode(this.getSetRunVarMode().getValue());
			shellCommandSettings.setFrameworkExecution(this.getFrameworkExecution());
			shellCommandSettings.setEnvironment(this.getExecutionControl().getEnvName());
			if (isOnLocalHost) {
				shellCommandResult = hostConnection.executeLocalCommand(this.getShellPath().getValue(),
						this.getShellCommand().getValue(), shellCommandSettings);
			} else {
				shellCommandResult = hostConnection.executeRemoteCommand(this.getShellPath().getValue(),
						this.getShellCommand().getValue(), shellCommandSettings);
			}

			// Set runtime variables
			if (this.getSetRunVar().getValue().trim().equalsIgnoreCase("y")) {
				this.getExecutionControl().getExecutionRuntime()
						.setRuntimeVariables(shellCommandResult.getRuntimeVariablesOutput());
			}

			if (shellCommandResult.getReturnCode() == 0) {
				this.getActionExecution().getActionControl().increaseSuccessCount();
			} else {
				this.getActionExecution().getActionControl().increaseErrorCount();
			}

			this.getActionExecution().getActionControl().logOutput("rc",
					Integer.toString(shellCommandResult.getReturnCode()));
			this.getActionExecution().getActionControl().logOutput("sys.out", shellCommandResult.getSystemOutput());
			this.getActionExecution().getActionControl().logOutput("err.out", shellCommandResult.getErrorOutput());

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

	public ActionParameterOperation getShellPath() {
		return shellPath;
	}

	public void setShellPath(ActionParameterOperation shellPath) {
		this.shellPath = shellPath;
	}

	public ActionParameterOperation getShellCommand() {
		return shellCommand;
	}

	public void setShellCommand(ActionParameterOperation shellCommand) {
		this.shellCommand = shellCommand;
	}

	public ActionParameterOperation getSetRunVar() {
		return setRunVar;
	}

	public void setSetRunVar(ActionParameterOperation setRunVar) {
		this.setRunVar = setRunVar;
	}

	public ActionParameterOperation getSetRunVarPrefix() {
		return setRunVarPrefix;
	}

	public void setSetRunVarPrefix(ActionParameterOperation setRunVarPrefix) {
		this.setRunVarPrefix = setRunVarPrefix;
	}

	public ActionParameterOperation getSetRunVarMode() {
		return setRunVarMode;
	}

	public void setSetRunVarMode(ActionParameterOperation setRunVarMode) {
		this.setRunVarMode = setRunVarMode;
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

}