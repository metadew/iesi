package io.metadew.iesi.script.action.fho;

import io.metadew.iesi.connection.FileConnection;
import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.host.ShellCommandResult;
import io.metadew.iesi.connection.host.ShellCommandSettings;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.connection.tools.HostConnectionTools;
import io.metadew.iesi.connection.tools.fho.FileConnectionTools;
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

import java.text.MessageFormat;
import java.util.List;


/**
 * Action type to delete one or more folders and all of its contents.
 *
 * @author peter.billen
 */
public class FhoDeleteFolder extends ActionTypeExecution {

    // Parameters
    private ActionParameterOperation folderPath;
    private ActionParameterOperation folderName;
    private ActionParameterOperation connectionName;
    private static final Logger LOGGER = LogManager.getLogger();

    public FhoDeleteFolder(ExecutionControl executionControl,
                           ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        // Reset Parameters
        this.setFolderPath(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "path"));
        this.setFolderName(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "folder"));
        this.setConnectionName(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "connection"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("path")) {
                this.getFolderPath().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("folder")) {
                this.getFolderName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("connection")) {
                this.getConnectionName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("path", this.getFolderPath());
        this.getActionParameterOperationMap().put("folder", this.getFolderName());
        this.getActionParameterOperationMap().put("connection", this.getConnectionName());
    }

    protected boolean executeAction() throws InterruptedException {
        String path = convertPath(getFolderPath().getValue());
        String folder = convertFolder(getFolderName().getValue());
        String connectionName = convertConnectionName(getConnectionName().getValue());
        boolean isOnLocalhost = HostConnectionTools.isOnLocalhost(
                connectionName, this.getExecutionControl().getEnvName());

        if (isOnLocalhost) {
            if (path.isEmpty()) {
                this.setScope(folder);
                FolderTools.deleteFolder(folder, true);
                this.setSuccess();
            } else {
                List<FileConnection> fileConnections = FolderTools.getFilesInFolder(path, folder);
                for (FileConnection fileConnection : fileConnections) {
                    if (fileConnection.isDirectory()) {
                        this.setScope(fileConnection.getFilePath());
                        FolderTools.deleteFolder(fileConnection.getFilePath(), true);
                        this.setSuccess();
                    }
                }
            }
        } else {
            Connection connection = ConnectionConfiguration.getInstance()
                    .get(new ConnectionKey(connectionName, this.getExecutionControl().getEnvName()))
                    .get();
            HostConnection hostConnection = ConnectionOperation.getInstance().getHostConnection(connection);

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
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for connectionName",
                    connectionName.getClass()));
            return connectionName.toString();
        }
    }

    private String convertFolder(DataType folderName) {
        if (folderName instanceof Text) {
            return folderName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for folderName",
                    folderName.getClass()));
            return folderName.toString();
        }
    }

    private String convertPath(DataType folderPath) {
        if (folderPath instanceof Text) {
            return folderPath.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for folderPath",
                    folderPath.getClass()));
            return folderPath.toString();
        }
    }

    private void deleteRemoteFolder(HostConnection hostConnection, String folderFilePath) {
        ShellCommandSettings shellCommandSettings = new ShellCommandSettings();
        ShellCommandResult shellCommandResult = null;
        shellCommandResult = hostConnection.executeRemoteCommand("", "rm -rf " + folderFilePath,
                shellCommandSettings);

        if (shellCommandResult.getReturnCode() == 0) {
            this.setSuccess();
        } else {
            this.setError(shellCommandResult.getErrorOutput());
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

    public ActionParameterOperation getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(ActionParameterOperation connectionName) {
        this.connectionName = connectionName;
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