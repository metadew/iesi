package io.metadew.iesi.script.action.fho;

import io.metadew.iesi.connection.FileConnection;
import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.host.ShellCommandResult;
import io.metadew.iesi.connection.host.ShellCommandSettings;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.connection.tools.FileTools;
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
 * Action type to delete one or more files in a folder.
 *
 * @author peter.billen
 */
public class FhoDeleteFile extends ActionTypeExecution {

    // Parameters
    private ActionParameterOperation filePath;
    private ActionParameterOperation fileName;
    private ActionParameterOperation connectionName;
    private static final Logger LOGGER = LogManager.getLogger();

    public FhoDeleteFile(ExecutionControl executionControl,
                         ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        // Reset Parameters
        this.setFilePath(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "path"));
        this.setFileName(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "file"));
        this.setConnectionName(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "connection"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("path")) {
                this.getFilePath().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("file")) {
                this.getFileName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("connection")) {
                this.getConnectionName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("path", this.getFilePath());
        this.getActionParameterOperationMap().put("file", this.getFileName());
        this.getActionParameterOperationMap().put("connection", this.getConnectionName());
    }

    protected boolean executeAction() throws InterruptedException {
        String path = convertPath(getFilePath().getValue());
        String fileName = convertFile(getFileName().getValue());
        String connectionName = convertConnectionName(getConnectionName().getValue());
        System.out.println("Deleting " + path + " " + fileName + " on " + connectionName);
        boolean isOnLocalhost = HostConnectionTools.isOnLocalhost(
                connectionName, this.getExecutionControl().getEnvName());

        if (isOnLocalhost) {
            if (path.isEmpty()) {
                this.setScope(fileName);
                FileTools.delete(fileName);
                this.setSuccess();
            } else {
                List<FileConnection> fileConnections = FolderTools.getFilesInFolder(path, fileName);
                for (FileConnection fileConnection : fileConnections) {
                    if (!fileConnection.isDirectory()) {
                        this.setScope(fileConnection.getFilePath());
                        FileTools.delete(fileConnection.getFilePath());
                        this.setSuccess();
                    }
                }
            }
        } else {
            ConnectionKey connectionKey = new ConnectionKey(connectionName, this.getExecutionControl().getEnvName());
            Connection connection = ConnectionConfiguration.getInstance()
                    .get(connectionKey)
                    .get();
            HostConnection hostConnection = ConnectionOperation.getInstance().getHostConnection(connection);

            if (path.isEmpty()) {
                this.setScope(fileName);
                this.deleteRemoteFile(hostConnection, fileName);
            } else {
                for (FileConnection fileConnection : FileConnectionTools.getFileConnections(hostConnection,
                        path, fileName, false)) {
                    if (!fileConnection.isDirectory()) {
                        this.setScope(fileConnection.getFilePath());
                        this.deleteRemoteFile(hostConnection, fileConnection.getFilePath());
                    }
                }
            }
        }

        return true;
    }


    private String convertConnectionName(DataType connectionName) {
        System.out.println("converting connection name");
        if (connectionName instanceof Text) {
            return connectionName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for connectionName",
                    connectionName.getClass()));
            return connectionName.toString();
        }
    }

    private String convertFile(DataType folderName) {
        System.out.println("converting folderName");
        if (folderName instanceof Text) {
            return folderName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for folderName",
                    folderName.getClass()));
            return folderName.toString();
        }
    }

    private String convertPath(DataType folderPath) {
        System.out.println("converting folderPath");
        if (folderPath instanceof Text) {
            return folderPath.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for folderPath",
                    folderPath.getClass()));
            return folderPath.toString();
        }
    }

    private void deleteRemoteFile(HostConnection hostConnection, String filePath) {
        ShellCommandSettings shellCommandSettings = new ShellCommandSettings();
        ShellCommandResult shellCommandResult = null;
        shellCommandResult = hostConnection.executeRemoteCommand("", "rm -f " + filePath, shellCommandSettings);

        if (shellCommandResult.getReturnCode() == 0) {
            this.setSuccess();
        } else {
            this.setError(shellCommandResult.getErrorOutput());
        }
    }

    private void setScope(String input) {
        this.getActionExecution().getActionControl().logOutput("file.delete", input);
    }

    private void setError(String input) {
        this.getActionExecution().getActionControl().logOutput("file.delete.error", input);
        this.getActionExecution().getActionControl().increaseErrorCount();
    }

    private void setSuccess() {
        this.getActionExecution().getActionControl().logOutput("file.delete.success", "confirmed");
        this.getActionExecution().getActionControl().increaseSuccessCount();
    }

    public ActionParameterOperation getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(ActionParameterOperation connectionName) {
        this.connectionName = connectionName;
    }

    public ActionParameterOperation getFileName() {
        return fileName;
    }

    public void setFileName(ActionParameterOperation fileName) {
        this.fileName = fileName;
    }

    public ActionParameterOperation getFilePath() {
        return filePath;
    }

    public void setFilePath(ActionParameterOperation filePath) {
        this.filePath = filePath;
    }

}