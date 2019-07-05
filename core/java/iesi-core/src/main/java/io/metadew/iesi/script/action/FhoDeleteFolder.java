package io.metadew.iesi.script.action;

import io.metadew.iesi.connection.FileConnection;
import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.host.ShellCommandResult;
import io.metadew.iesi.connection.host.ShellCommandSettings;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.connection.tools.HostConnectionTools;
import io.metadew.iesi.connection.tools.fho.FileConnectionTools;
import io.metadew.iesi.datatypes.DataType;
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;


/**
 * Action type to delete one or more folders and all of its contents.
 *
 * @author peter.billen
 */
public class FhoDeleteFolder {

    private ActionExecution actionExecution;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation folderPath;
    private ActionParameterOperation folderName;
    private ActionParameterOperation connectionName;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Constructors
    public FhoDeleteFolder() {

    }

    public FhoDeleteFolder(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
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
            String path = convertPath(getFolderPath().getValue());
            String folder = convertFolder(getFolderName().getValue());
            String connectionName = convertConnectionName(getConnectionName().getValue());
            return execute(path, folder, connectionName);

        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private boolean execute(String path, String folder, String connectionName) {
        boolean isOnLocalhost = HostConnectionTools.isOnLocalhost(this.getFrameworkExecution(),
               connectionName, this.getExecutionControl().getEnvName());

        if (isOnLocalhost) {
            if (path.isEmpty()) {
                this.setScope(folder);
                try {
                    FolderTools.deleteFolder(folder, true);
                    this.setSuccess();
                } catch (Exception e) {
                    this.setError(e.getMessage());
                }
            } else {
                List<FileConnection> fileConnections = FolderTools.getFilesInFolder(path, folder);
                for (FileConnection fileConnection : fileConnections) {
                    if (fileConnection.isDirectory()) {
                        this.setScope(fileConnection.getFilePath());
                        try {
                            FolderTools.deleteFolder(fileConnection.getFilePath(), true);
                            this.setSuccess();
                        } catch (Exception e) {
                            this.setError(e.getMessage());
                        }
                    }
                }
            }
        } else {
            ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(
                    this.getFrameworkExecution().getFrameworkInstance());
            Connection connection = connectionConfiguration
                    .getConnection(connectionName, this.getExecutionControl().getEnvName())
                    .get();
            ConnectionOperation connectionOperation = new ConnectionOperation(this.getFrameworkExecution());
            HostConnection hostConnection = connectionOperation.getHostConnection(connection);

            if (path.isEmpty()) {
                this.setScope(folder);
                this.deleteRemoteFolder(hostConnection, folder);
            } else {
                for (FileConnection fileConnection : FileConnectionTools.getFileConnections(hostConnection,
                        path, folder, true)) {
                    this.setScope(fileConnection.getFilePath());
                    this.deleteRemoteFolder(hostConnection, fileConnection.getFilePath());
                }
            }
        }

        return true;
    }

    private String convertConnectionName(DataType connectionName) {
        if (connectionName instanceof Text) {
            return connectionName.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for connectionName",
                    connectionName.getClass()), Level.WARN);
            return connectionName.toString();
        }
    }

    private String convertFolder(DataType folderName) {
        if (folderName instanceof Text) {
            return folderName.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for folderName",
                    folderName.getClass()), Level.WARN);
            return folderName.toString();
        }
    }

    private String convertPath(DataType folderPath) {
        if (folderPath instanceof Text) {
            return folderPath.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for folderPath",
                    folderPath.getClass()), Level.WARN);
            return folderPath.toString();
        }
    }

    private void deleteRemoteFolder(HostConnection hostConnection, String folderFilePath) {
        ShellCommandSettings shellCommandSettings = new ShellCommandSettings();
        ShellCommandResult shellCommandResult = null;
        try {
            shellCommandResult = hostConnection.executeRemoteCommand("", "rm -rf " + folderFilePath,
                    shellCommandSettings);

            if (shellCommandResult.getReturnCode() == 0) {
                this.setSuccess();
            } else {
                this.setError(shellCommandResult.getErrorOutput());
            }
        } catch (Exception e) {
            this.setError(e.getMessage());
        }
    }

    private void setScope(String input) {
        this.getActionExecution().getActionControl().logOutput("folder.delete", input);
    }

    private void setError(String input) {
        this.getActionExecution().getActionControl().logOutput("folder.delete.error", input);
        this.getActionExecution().getActionControl().increaseErrorCount();
    }

    private void setSuccess() {
        this.getActionExecution().getActionControl().logOutput("folder.delete.success", "confirmed");
        this.getActionExecution().getActionControl().increaseSuccessCount();
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
        return this.folderName;
    }

    public void setFolderName(ActionParameterOperation folderName) {
        this.folderName = folderName;
    }

}