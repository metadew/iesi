package io.metadew.iesi.script.action.fho;

import io.metadew.iesi.SpringContext;
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
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
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

    private static final String FOLDER_PATH_KEY = "path";
    private static final String FOLDER_NAME_KEY = "folder";
    private static final String CONNECTION_NAME_KEY = "connection";
    private static final Logger LOGGER = LogManager.getLogger();

    public FhoDeleteFolder(ExecutionControl executionControl,
                           ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepareAction() { }

    protected boolean executeAction() throws InterruptedException {
        String path = convertPath(getParameterResolvedValue(FOLDER_PATH_KEY));
        String folder = convertFolder(getParameterResolvedValue(FOLDER_NAME_KEY));
        String connectionName = convertConnectionName(getParameterResolvedValue(CONNECTION_NAME_KEY));
        boolean isOnLocalhost = SpringContext.getBean(HostConnectionTools.class).isOnLocalhost(
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
            Connection connection = SpringContext.getBean(ConnectionConfiguration.class)
                    .get(new ConnectionKey(connectionName, this.getExecutionControl().getEnvName()))
                    .get();
            HostConnection hostConnection = SpringContext.getBean(ConnectionOperation.class).getHostConnection(connection);

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

    @Override
    protected String getKeyword() {
        return "fho.deleteFolder";
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
}