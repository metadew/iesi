package io.metadew.iesi.script.action;

import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.connection.operation.FileTransferOperation;
import io.metadew.iesi.connection.operation.filetransfer.FileTransferResult;
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

public class FhoExecuteFileTransfer {

    private ActionExecution actionExecution;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation sourceFilePath;
    private ActionParameterOperation sourceFileName;
    private ActionParameterOperation sourceConnectionName;
    private ActionParameterOperation targetFilePath;
    private ActionParameterOperation targetFileName;
    private ActionParameterOperation targetConnectionName;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Constructors
    public FhoExecuteFileTransfer() {

    }

    public FhoExecuteFileTransfer(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
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
        // Set Parameters
        this.setSourceFilePath(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "sourceFilePath"));
        this.setSourceFileName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "sourceFileName"));
        this.setSourceConnectionName(new ActionParameterOperation(this.getFrameworkExecution(),
                this.getExecutionControl(), this.getActionExecution(), this.getActionExecution().getAction().getType(),
                "sourceConnection"));
        this.setTargetFilePath(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "targetFilePath"));
        this.setTargetFileName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "targetFileName"));
        this.setTargetConnectionName(new ActionParameterOperation(this.getFrameworkExecution(),
                this.getExecutionControl(), this.getActionExecution(), this.getActionExecution().getAction().getType(),
                "targetConnection"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("sourcefilepath")) {
                this.getSourceFilePath().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("sourcefilename")) {
                this.getSourceFileName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("sourceconnection")) {
                this.getSourceConnectionName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("targetfilepath")) {
                this.getTargetFilePath().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("targetfilename")) {
                this.getTargetFileName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("targetconnection")) {
                this.getTargetConnectionName().setInputValue(actionParameter.getValue());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("sourceFilePath", this.getSourceFilePath());
        this.getActionParameterOperationMap().put("sourceFileName", this.getSourceFileName());
        this.getActionParameterOperationMap().put("sourceConnection", this.getSourceConnectionName());
        this.getActionParameterOperationMap().put("targetFilePath", this.getTargetFilePath());
        this.getActionParameterOperationMap().put("targetFileName", this.getTargetFileName());
        this.getActionParameterOperationMap().put("targetConnection", this.getTargetConnectionName());
    }

    // Methods
    public boolean execute() {
        try {
            String sourceFilePath = convertSourceFilePath(getSourceFilePath().getValue());
            String sourceFileName = convertSourceFileName(getSourceFileName().getValue());
            String sourceConnectionName = convertSourceConnectionName(getSourceConnectionName().getValue());
            String targetFilePath = convertTargetFilePath(getTargetFilePath().getValue());
            String targetFileName = convertTargetFileName(getTargetFileName().getValue());
            String targetConnectionName = convertTargetConnection(getTargetConnectionName().getValue());
            return execute(sourceFilePath, sourceFileName, sourceConnectionName, targetFilePath, targetFileName, targetConnectionName);

        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private boolean execute(String sourceFilePath, String sourceFileName, String sourceConnectionName, String targetFilePath, String targetFileName, String targetConnectionName) {
        // Get Connections
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(this.getFrameworkExecution().getFrameworkInstance());
        Connection sourceConnection = connectionConfiguration
                .getConnection(sourceConnectionName, this.getExecutionControl().getEnvName()).get();
        ConnectionOperation connectionOperation = new ConnectionOperation(this.getFrameworkExecution());
        HostConnection sourceHostConnection = connectionOperation.getHostConnection(sourceConnection);
        Connection targetConnection = connectionConfiguration
                .getConnection(targetConnectionName, this.getExecutionControl().getEnvName()).get();
        HostConnection targetHostConnection = connectionOperation.getHostConnection(targetConnection);

        // Check if source or target are localhost
        boolean sourceIsOnLocalHost = connectionOperation.isOnLocalConnection(sourceHostConnection);
        boolean targetIsOnLocalHost = connectionOperation.isOnLocalConnection(targetHostConnection);

        // Run the action
        FileTransferOperation fileTransferOperation = new FileTransferOperation(this.getFrameworkExecution());
        FileTransferResult fileTransferResult = null;
        if (sourceIsOnLocalHost && !targetIsOnLocalHost) {
            fileTransferResult = fileTransferOperation.transferLocalToRemote(sourceFilePath,
                    sourceFileName, sourceConnection, targetFilePath, targetFileName, targetConnection);
        } else if (!sourceIsOnLocalHost && targetIsOnLocalHost) {
            fileTransferResult = fileTransferOperation.transferRemoteToLocal(sourceFilePath,
                    sourceFileName, sourceConnection, targetFilePath,
                    targetFileName, targetConnection);
        } else if (sourceIsOnLocalHost && targetIsOnLocalHost) {
            fileTransferResult = fileTransferOperation.transferLocalToLocal(sourceFilePath,
                    sourceFileName, sourceConnection, targetFilePath,
                    targetFileName, targetConnection);
        } else if (!sourceIsOnLocalHost && !targetIsOnLocalHost) {
            throw new RuntimeException("Method not supported yet");
        }

        if (fileTransferResult.getReturnCode() == 0) {
            this.getActionExecution().getActionControl().increaseSuccessCount();
        } else {
            this.getActionExecution().getActionControl().increaseErrorCount();
        }

        this.getActionExecution().getActionControl().logOutput("rc",
                Integer.toString(fileTransferResult.getReturnCode()));
        this.getActionExecution().getActionControl().logOutput("files",
                Integer.toString(fileTransferResult.getDcFileTransferedList().size()));

        return true;
    }

    private String convertTargetConnection(DataType targetConnection) {
        if (targetConnection instanceof Text) {
            return targetConnection.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for targetConnection",
                    targetConnection.getClass()), Level.WARN);
            return targetConnection.toString();
        }
    }

    private String convertTargetFileName(DataType targetFileName) {
        if (targetFileName instanceof Text) {
            return targetFileName.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for targetFileName",
                    targetFileName.getClass()), Level.WARN);
            return targetFileName.toString();
        }
    }

    private String convertTargetFilePath(DataType targetFilePath) {
        if (targetFilePath instanceof Text) {
            return targetFilePath.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for targetFilePath",
                    targetFilePath.getClass()), Level.WARN);
            return targetFilePath.toString();
        }
    }

    private String convertSourceConnectionName(DataType sourceConnectionName) {
        if (sourceConnectionName instanceof Text) {
            return sourceConnectionName.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for sourceConnectionName",
                    sourceConnectionName.getClass()), Level.WARN);
            return sourceConnectionName.toString();
        }
    }

    private String convertSourceFileName(DataType sourceFileName) {
        if (sourceFileName instanceof Text) {
            return sourceFileName.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for sourceFileName",
                    sourceFileName.getClass()), Level.WARN);
            return sourceFileName.toString();
        }
    }

    private String convertSourceFilePath(DataType sourceFilePath) {
        if (sourceFilePath instanceof Text) {
            return sourceFilePath.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for sourceFilePath",
                    sourceFilePath.getClass()), Level.WARN);
            return sourceFilePath.toString();
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

    public ActionParameterOperation getSourceFilePath() {
        return sourceFilePath;
    }

    public void setSourceFilePath(ActionParameterOperation sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }

    public ActionParameterOperation getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(ActionParameterOperation sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public ActionParameterOperation getSourceConnectionName() {
        return sourceConnectionName;
    }

    public void setSourceConnectionName(ActionParameterOperation sourceConnectionName) {
        this.sourceConnectionName = sourceConnectionName;
    }

    public ActionParameterOperation getTargetFilePath() {
        return targetFilePath;
    }

    public void setTargetFilePath(ActionParameterOperation targetFilePath) {
        this.targetFilePath = targetFilePath;
    }

    public ActionParameterOperation getTargetFileName() {
        return targetFileName;
    }

    public void setTargetFileName(ActionParameterOperation targetFileName) {
        this.targetFileName = targetFileName;
    }

    public ActionParameterOperation getTargetConnectionName() {
        return targetConnectionName;
    }

    public void setTargetConnectionName(ActionParameterOperation targetConnectionName) {
        this.targetConnectionName = targetConnectionName;
    }

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

    public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        this.actionParameterOperationMap = actionParameterOperationMap;
    }

}