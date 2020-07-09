package io.metadew.iesi.script.action.fho;

import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.host.ShellCommandResult;
import io.metadew.iesi.connection.host.ShellCommandSettings;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.connection.tools.HostConnectionTools;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;

/**
 * Action type to create a folder.
 *
 * @author peter.billen
 */
public class FhoCreateFolder {

    private ActionExecution actionExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation folderPath;
    private ActionParameterOperation folderName;
    private ActionParameterOperation connectionName;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;
    private static final Logger LOGGER = LogManager.getLogger();

    // Constructors
    public FhoCreateFolder() {

    }

    public FhoCreateFolder(ExecutionControl executionControl,
                           ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.init(executionControl, scriptExecution, actionExecution);
    }

    public void init(ExecutionControl executionControl,
                     ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
    }

    public void prepare() throws Exception {
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
                this.getFolderPath().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("folder")) {
                this.getFolderName().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("connection")) {
                this.getConnectionName().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("path", this.getFolderPath());
        this.getActionParameterOperationMap().put("folder", this.getFolderName());
        this.getActionParameterOperationMap().put("connection", this.getConnectionName());
    }

    // Methods
    public boolean execute() throws InterruptedException {
        try {
            String path = convertPath(getFolderPath().getValue());
            String folder = convertFolder(getFolderName().getValue());
            String connectionName = convertConnectionName(getConnectionName().getValue());
            return execute(path, folder, connectionName);

        } catch (InterruptedException e) {
            throw (e);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private boolean execute(String path, String folder, String connectionName) throws Exception {

        boolean isOnLocalhost = HostConnectionTools.isOnLocalhost(
                connectionName, this.getExecutionControl().getEnvName());

        if (isOnLocalhost) {
            String subjectFolderPath = "";
            if (path.isEmpty()) {
                subjectFolderPath = FilenameUtils.normalize(folder);
            } else {
                subjectFolderPath = FilenameUtils.normalize(
                        this.getFolderPath().getValue() + File.separator + this.getFolderName().getValue());
            }

            this.setScope(subjectFolderPath);
            FolderTools.createFolder(subjectFolderPath, true);
            this.setSuccess();

        } else {
            Connection connection = ConnectionConfiguration.getInstance()
                    .get(new ConnectionKey(connectionName, this.getExecutionControl().getEnvName()))
                    .get();
            ConnectionOperation connectionOperation = new ConnectionOperation();
            HostConnection hostConnection = connectionOperation.getHostConnection(connection);

            String subjectFolderPath = "";
            if (path.isEmpty()) {
                subjectFolderPath = folder;
            } else {
                subjectFolderPath = this.getFolderPath().getValue() + hostConnection.getFileSeparator()
                        + this.getFolderName().getValue();
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