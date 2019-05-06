package io.metadew.iesi.script.action;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import org.apache.commons.io.FilenameUtils;

import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.host.ShellCommandResult;
import io.metadew.iesi.connection.host.ShellCommandSettings;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.connection.tools.HostConnectionTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;

/**
 * Action type to create a folder.
 * 
 * @author peter.billen
 *
 */
public class FhoCreateFolder {

	private ActionExecution actionExecution;
	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;

	// Parameters
	private ActionParameterOperation folderPath;
	private ActionParameterOperation folderName;
	private ActionParameterOperation connectionName;
	private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

	// Constructors
	public FhoCreateFolder() {

	}

	public FhoCreateFolder(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
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
		this.setFolderPath(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "path"));
		this.setFolderName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "folder"));
		this.setConnectionName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "connection"));

		// Get Parameters
		for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
			if (actionParameter.getName().equalsIgnoreCase("path")) {
				this.getFolderPath().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("folder")) {
				this.getFolderName().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("connection")) {
				this.getConnectionName().setInputValue(actionParameter.getValue());
			}
		}

		// Create parameter list
		this.getActionParameterOperationMap().put("path", this.getFolderPath());
		this.getActionParameterOperationMap().put("folder", this.getFolderName());
		this.getActionParameterOperationMap().put("connection", this.getConnectionName());
	}

	// Methods
	public boolean execute() {
		try {
			boolean isOnLocalhost = HostConnectionTools.isOnLocalhost(this.getFrameworkExecution(),
					this.getConnectionName().getValue(), this.getExecutionControl().getEnvName());

			if (isOnLocalhost) {
				String subjectFolderPath = "";
				if (this.getFolderPath().getValue().isEmpty()) {
					subjectFolderPath = FilenameUtils.normalize(this.getFolderName().getValue());
				} else {
					subjectFolderPath = FilenameUtils.normalize(
							this.getFolderPath().getValue() + File.separator + this.getFolderName().getValue());
				}

				this.getActionExecution().getActionControl().logOutput("folder.create", subjectFolderPath);
				try {
					FolderTools.createFolder(subjectFolderPath, true);
					this.getActionExecution().getActionControl().increaseSuccessCount();
					this.getActionExecution().getActionControl().logOutput("folder.create.success", "confirmed");
				} catch (Exception e) {
					this.getActionExecution().getActionControl().logOutput("folder.create.error", e.getMessage());
					this.getActionExecution().getActionControl().increaseErrorCount();
				}

			} else {
				ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(
						this.getFrameworkExecution());
				Connection connection = connectionConfiguration
						.getConnection(this.getConnectionName().getValue(), this.getExecutionControl().getEnvName())
						.get();
				ConnectionOperation connectionOperation = new ConnectionOperation(this.getFrameworkExecution());
				HostConnection hostConnection = connectionOperation.getHostConnection(connection);

				String subjectFolderPath = "";
				if (this.getFolderPath().getValue().isEmpty()) {
					subjectFolderPath = this.getFolderName().getValue();
				} else {
					subjectFolderPath = 
							this.getFolderPath().getValue() + hostConnection.getFileSeparator() + this.getFolderName().getValue();
				}

				this.getActionExecution().getActionControl().logOutput("folder.create", subjectFolderPath);
				
				ShellCommandSettings shellCommandSettings = new ShellCommandSettings();
				ShellCommandResult shellCommandResult = null;
				try {
					shellCommandResult = hostConnection.executeRemoteCommand("",
							"mkdir " + subjectFolderPath, shellCommandSettings);

					if (shellCommandResult.getReturnCode() == 0) {
						this.getActionExecution().getActionControl().increaseSuccessCount();
						this.getActionExecution().getActionControl().logOutput("folder.create.success", "confirmed");
					} else {
						this.getActionExecution().getActionControl().logOutput("folder.create.error",
								shellCommandResult.getErrorOutput());
						this.getActionExecution().getActionControl().increaseErrorCount();
					}
				} catch (Exception e) {
					this.getActionExecution().getActionControl().logOutput("folder.create.error", e.getMessage());
					this.getActionExecution().getActionControl().increaseErrorCount();
				}
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

	public ActionParameterOperation getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(ActionParameterOperation folderPath) {
		this.folderPath = folderPath;
	}

	public ActionParameterOperation getFolderName() {
		return folderName;
	}

	public void setFolderName(ActionParameterOperation folderName) {
		this.folderName = folderName;
	}

}