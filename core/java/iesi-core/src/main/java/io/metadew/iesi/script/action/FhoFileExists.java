package io.metadew.iesi.script.action;

import io.metadew.iesi.connection.FileConnection;
import io.metadew.iesi.connection.HostConnection;
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
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;

/**
 * Action type to verify if a file exists.
 *
 * @author peter.billen
 */
public class FhoFileExists {

    private ActionExecution actionExecution;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation filePath;
    private ActionParameterOperation fileName;
    private ActionParameterOperation connectionName;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Constructors
    public FhoFileExists() {

    }

    public FhoFileExists(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
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
        this.setFilePath(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "path"));
        this.setFileName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "file"));
        this.setConnectionName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "connection"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("path")) {
                this.getFilePath().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("file")) {
                this.getFileName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("connection")) {
                this.getConnectionName().setInputValue(actionParameter.getValue());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("path", this.getFilePath());
        this.getActionParameterOperationMap().put("file", this.getFileName());
        this.getActionParameterOperationMap().put("connection", this.getConnectionName());
    }

    // Methods
    public boolean execute() {
        try {
            String path = convertPath(getFilePath().getValue());
            String fileName = convertFile(getFileName().getValue());
            String connectionName = convertConnectionName(getConnectionName().getValue());
            return execute(path, fileName, connectionName);

        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private boolean execute(String path, String fileName, String connectionName) {
        boolean isOnLocalhost = HostConnectionTools.isOnLocalhost(this.getFrameworkExecution(),
                connectionName, this.getExecutionControl().getEnvName());

        String subjectFilePath = "";
        if (path.isEmpty()) {
            if (isOnLocalhost) {
                subjectFilePath = FilenameUtils.normalize(fileName);
            } else {
                subjectFilePath = fileName;
            }
        } else {
            if (isOnLocalhost) {
                subjectFilePath = FilenameUtils.normalize(
                        this.getFilePath().getValue() + File.separator + this.getFileName().getValue());
            } else {
                subjectFilePath = this.getFilePath().getValue() + "/"
                        + this.getFileName().getValue();
            }
        }
        File file = new File(subjectFilePath);

        boolean result = false;
        if (isOnLocalhost) {
            List<FileConnection> fileConnections = FolderTools.getFilesInFolder(file.getParent(), file.getName());
            for (FileConnection fileConnection : fileConnections) {
                if (!fileConnection.isDirectory()
                        && fileConnection.getFilePath().equalsIgnoreCase(subjectFilePath)) {
                    this.setScope(fileConnection.getFilePath());
                    result = true;
                    this.setSuccess();
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

            for (FileConnection fileConnection : FileConnectionTools.getFileConnections(hostConnection,
                    FilenameUtils.separatorsToUnix(file.getParent()), FilenameUtils.separatorsToUnix(file.getName()), false)) {
                if (!fileConnection.isDirectory()
                        && fileConnection.getFilePath().equalsIgnoreCase(subjectFilePath)) {
                    this.setScope(fileConnection.getFilePath());
                    result = true;
                    this.setSuccess();
                }
            }
        }

        if (!result) {
            this.setError("notfound");
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

    private String convertFile(DataType folderName) {
        if (folderName instanceof Text) {
            return folderName.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for folderName",
                    folderName.getClass()), Level.WARN);
            return folderName.toString();
        }
    }

    private String convertPath(DataType folderName) {
        if (folderName instanceof Text) {
            return folderName.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for folderName",
                    folderName.getClass()), Level.WARN);
            return folderName.toString();
        }
    }

    private void setScope(String input) {
        this.getActionExecution().getActionControl().logOutput("file.exists", input);
    }

    private void setError(String input) {
        this.getActionExecution().getActionControl().logOutput("file.exists.error", input);
        this.getActionExecution().getActionControl().increaseErrorCount();
    }

    private void setSuccess() {
        this.getActionExecution().getActionControl().logOutput("file.exists.success", "confirmed");
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

    public ActionParameterOperation getFilePath() {
        return filePath;
    }

    public void setFilePath(ActionParameterOperation filePath) {
        this.filePath = filePath;
    }

    public ActionParameterOperation getFileName() {
        return fileName;
    }

    public void setFileName(ActionParameterOperation fileName) {
        this.fileName = fileName;
    }

}