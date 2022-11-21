package io.metadew.iesi.script.action.fho;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.host.ShellCommandResult;
import io.metadew.iesi.connection.host.ShellCommandSettings;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.connection.tools.HostConnectionTools;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.text.MessageFormat;

/**
 * Action type to create a folder.
 *
 * @author peter.billen
 */
public class FhoCreateFolder extends ActionTypeExecution {

    private static final String FOLDER_PATH_KEY = "path";
    private static final String FOLDER_NAME_KEY = "folder";
    private static final String CONNECTION_NAME_KEY = "connection";
    private static final Logger LOGGER = LogManager.getLogger();

    public FhoCreateFolder(ExecutionControl executionControl,
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
            String subjectFolderPath = "";
            if (path.isEmpty()) {
                subjectFolderPath = FilenameUtils.normalize(folder);
            } else {
                subjectFolderPath = FilenameUtils.normalize(
                        path + File.separator + folder);
            }

            this.setScope(subjectFolderPath);
            FolderTools.createFolder(subjectFolderPath, true);
            this.setSuccess();

        } else {
            Connection connection = SpringContext.getBean(ConnectionConfiguration.class)
                    .get(new ConnectionKey(connectionName, this.getExecutionControl().getEnvName()))
                    .get();
            HostConnection hostConnection = SpringContext.getBean(ConnectionOperation.class).getHostConnection(connection);

            String subjectFolderPath = "";
            if (path.isEmpty()) {
                subjectFolderPath = folder;
            } else {
                subjectFolderPath = path + hostConnection.getFileSeparator() + folder;
            }

            this.setScope(subjectFolderPath);

            ShellCommandSettings shellCommandSettings = new ShellCommandSettings();
            ShellCommandResult shellCommandResult = null;
            shellCommandResult = hostConnection.executeRemoteCommand("", "mkdir " + subjectFolderPath,
                    shellCommandSettings);

            if (shellCommandResult.getReturnCode() == 0) {
                this.setSuccess();
            } else {
                this.setError(shellCommandResult.getErrorOutput());
            }
        }
        return true;
    }

    @Override
    protected String getKeyword() {
        return "fho.createFolder";
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

    private String convertPath(DataType folderName) {
        if (folderName instanceof Text) {
            return folderName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for folderName",
                    folderName.getClass()));
            return folderName.toString();
        }
    }

    private void setScope(String input) {
        this.getActionExecution().getActionControl().logOutput("folder.create", input);
    }

    private void setError(String input) {
        this.getActionExecution().getActionControl().logOutput("folder.create.error", input);
        this.getActionExecution().getActionControl().increaseErrorCount();
    }

    private void setSuccess() {
        this.getActionExecution().getActionControl().logOutput("folder.create.success", "confirmed");
        this.getActionExecution().getActionControl().increaseSuccessCount();
    }
}