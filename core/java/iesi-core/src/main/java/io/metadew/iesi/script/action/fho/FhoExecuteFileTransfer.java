package io.metadew.iesi.script.action.fho;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.connection.operation.FileTransferService;
import io.metadew.iesi.connection.operation.filetransfer.FileTransferResult;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

public class FhoExecuteFileTransfer extends ActionTypeExecution {

    private static final String SOURCE_FILE_PATH_KEY = "sourceFilePath";
    private static final String SOURCE_FILE_NAME_KEY = "sourceFileName";
    private static final String SOURCE_CONNECTION_NAME_KEY = "sourceConnection";
    private static final String TARGET_FILE_PATH_KEY = "targetFilePath";
    private static final String TARGET_FILE_NAME_KEY = "targetFileName";
    private static final String TARGET_CONNECTION_NAME = "targetConnection";
    private static final Logger LOGGER = LogManager.getLogger();

    private final HostConnectionTools hostConnectionTools = SpringContext.getBean(HostConnectionTools.class);
    private final FileTransferService fileTransferService = SpringContext.getBean(FileTransferService.class);
    private final ConnectionConfiguration connectionConfiguration = SpringContext.getBean(ConnectionConfiguration.class);

    public FhoExecuteFileTransfer(ExecutionControl executionControl,
                                  ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepareAction() { }

    protected boolean executeAction() throws InterruptedException {
        String sourceFilePath = convertSourceFilePath(getParameterResolvedValue(SOURCE_FILE_PATH_KEY));
        String sourceFileName = convertSourceFileName(getParameterResolvedValue(SOURCE_FILE_NAME_KEY));
        String sourceConnectionName = convertSourceConnectionName(getParameterResolvedValue(SOURCE_CONNECTION_NAME_KEY));
        String targetFilePath = convertTargetFilePath(getParameterResolvedValue(TARGET_FILE_PATH_KEY));
        String targetFileName = convertTargetFileName(getParameterResolvedValue(TARGET_FILE_NAME_KEY));
        String targetConnectionName = convertTargetConnection(getParameterResolvedValue(TARGET_CONNECTION_NAME));
        // Check if source or target are localhost
        // TODO check the creation of the sourceConnections
        boolean sourceIsOnLocalHost = hostConnectionTools.isOnLocalhost(
                sourceConnectionName, this.getExecutionControl().getEnvName());
        boolean targetIsOnLocalHost = hostConnectionTools.isOnLocalhost(
                targetConnectionName, this.getExecutionControl().getEnvName());

        // Run the action
        FileTransferResult fileTransferResult;
        if (sourceIsOnLocalHost && !targetIsOnLocalHost) {
            Connection targetConnection = connectionConfiguration
                    .get(new ConnectionKey(targetConnectionName, this.getExecutionControl().getEnvName()))
                    .orElseThrow(() -> new RuntimeException(String.format("Unable to find %s", new ConnectionKey(targetConnectionName, this.getExecutionControl().getEnvName()))));
            fileTransferResult = fileTransferService.transferLocalToRemote(sourceFilePath,
                    sourceFileName, targetFilePath, targetFileName, targetConnection);
        } else if (!sourceIsOnLocalHost && targetIsOnLocalHost) {
            Connection sourceConnection = connectionConfiguration
                    .get(new ConnectionKey(sourceConnectionName, this.getExecutionControl().getEnvName()))
                    .orElseThrow(() -> new RuntimeException(String.format("Unable to find %s", new ConnectionKey(sourceConnectionName, this.getExecutionControl().getEnvName()))));

            fileTransferResult = fileTransferService.transferRemoteToLocal(sourceFilePath,
                    sourceFileName, sourceConnection, targetFilePath,
                    targetFileName);
        } else if (sourceIsOnLocalHost && targetIsOnLocalHost) {

            fileTransferResult = fileTransferService.transferLocalToLocal(sourceFilePath,
                    sourceFileName, targetFilePath, targetFileName);
        } else {
            throw new RuntimeException("Method not supported yet");
        }

        if (fileTransferResult.getReturnCode() == 0) {
            this.getActionExecution().getActionControl().increaseSuccessCount();
        } else {
            getActionExecution().getActionControl().logOutput("action.error",
                    "file transfer ended in return code " + fileTransferResult.getReturnCode());
            this.getActionExecution().getActionControl().increaseErrorCount();
        }

        this.getActionExecution().getActionControl().logOutput("rc",
                Integer.toString(fileTransferResult.getReturnCode()));
        this.getActionExecution().getActionControl().logOutput("files",
                Integer.toString(fileTransferResult.getDcFileTransferedList().size()));

        return true;
    }

    @Override
    protected String getKeyword() {
        return "fho.executeFileTransfer";
    }

    private String convertTargetConnection(DataType targetConnection) {
        if (targetConnection instanceof Text) {
            return targetConnection.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for targetConnection",
                    targetConnection.getClass()));
            return targetConnection.toString();
        }
    }

    private String convertTargetFileName(DataType targetFileName) {
        if (targetFileName instanceof Text) {
            return targetFileName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for targetFileName",
                    targetFileName.getClass()));
            return targetFileName.toString();
        }
    }

    private String convertTargetFilePath(DataType targetFilePath) {
        if (targetFilePath instanceof Text) {
            return targetFilePath.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for targetFilePath",
                    targetFilePath.getClass()));
            return targetFilePath.toString();
        }
    }

    private String convertSourceConnectionName(DataType sourceConnectionName) {
        if (sourceConnectionName instanceof Text) {
            return sourceConnectionName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for sourceConnectionName",
                    sourceConnectionName.getClass()));
            return sourceConnectionName.toString();
        }
    }

    private String convertSourceFileName(DataType sourceFileName) {
        if (sourceFileName instanceof Text) {
            return sourceFileName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for sourceFileName",
                    sourceFileName.getClass()));
            return sourceFileName.toString();
        }
    }

    private String convertSourceFilePath(DataType sourceFilePath) {
        if (sourceFilePath instanceof Text) {
            return sourceFilePath.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for sourceFilePath",
                    sourceFilePath.getClass()));
            return sourceFilePath.toString();
        }
    }
}