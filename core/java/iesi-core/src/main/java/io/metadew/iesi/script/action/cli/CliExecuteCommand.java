package io.metadew.iesi.script.action.cli;

import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.host.ShellCommandResult;
import io.metadew.iesi.connection.host.ShellCommandSettings;
import io.metadew.iesi.connection.operation.ConnectionOperation;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Optional;

/**
 * Action type to execute command line instructions.
 *
 * @author peter.billen
 */
public class CliExecuteCommand {

    private ActionExecution actionExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation shellPath;
    private static final String shellPathKey = "path";
    private ActionParameterOperation shellCommand;
    private final static String shellCommandKey = "command";
    private ActionParameterOperation setRunVar;
    private final static String setRunVarKey = "setRuntimeVariables";
    private ActionParameterOperation setRunVarPrefix;
    private final static String setRunVarPrefixKey = "setRuntimeVariablesPrefix";
    private ActionParameterOperation setRunVarMode;
    private final static String setRunVarModeKey = "setRuntimeVariablesMode";
    private ActionParameterOperation connectionName;
    private final static String connectionNameKey = "connection";
    private ActionParameterOperation systemOutputName;
    private final static String systemOutputNameKey = "output";
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;
    private static final Logger LOGGER = LogManager.getLogger();

    public CliExecuteCommand(ExecutionControl executionControl,
                             ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.executionControl = executionControl;
        this.actionExecution = actionExecution;
        this.actionParameterOperationMap = new HashMap<>();
    }

    public void prepare() {
        // Reset Parameters
        this.shellPath = new ActionParameterOperation(executionControl,
                actionExecution, actionExecution.getAction().getType(), shellPathKey);
        this.shellCommand = new ActionParameterOperation(executionControl,
                actionExecution, actionExecution.getAction().getType(), shellCommandKey);
        this.setRunVar = new ActionParameterOperation(executionControl,
                actionExecution, actionExecution.getAction().getType(), setRunVarKey);
        this.setRunVarPrefix = new ActionParameterOperation(executionControl,
                actionExecution, actionExecution.getAction().getType(),
                setRunVarPrefixKey);
        this.setRunVarMode = new ActionParameterOperation(executionControl,
                actionExecution, actionExecution.getAction().getType(), setRunVarModeKey);
        this.systemOutputName = new ActionParameterOperation(executionControl,
                actionExecution, actionExecution.getAction().getType(), systemOutputNameKey);
        this.connectionName = new ActionParameterOperation(executionControl,
                actionExecution, actionExecution.getAction().getType(), connectionNameKey);

        // Get Parameters
        for (ActionParameter actionParameter : actionExecution.getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(shellPathKey)) {
                shellPath.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(shellCommandKey)) {
                shellCommand.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(setRunVarKey)) {
                setRunVar.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(setRunVarPrefixKey)) {
                setRunVarPrefix.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(setRunVarModeKey)) {
                setRunVarMode.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(systemOutputNameKey)) {
                systemOutputName.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(connectionNameKey)) {
                connectionName.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            }
        }

        // Create parameter list
        this.actionParameterOperationMap.put(shellPathKey, shellPath);
        this.actionParameterOperationMap.put(shellCommandKey, shellCommand);
        this.actionParameterOperationMap.put(setRunVarKey, setRunVar);
        this.actionParameterOperationMap.put(setRunVarPrefixKey, setRunVarPrefix);
        this.actionParameterOperationMap.put(setRunVarModeKey, setRunVarMode);
        this.actionParameterOperationMap.put(systemOutputNameKey, systemOutputName);
        this.actionParameterOperationMap.put(connectionNameKey, connectionName);
    }

    // Methods
    public boolean execute() throws InterruptedException {
        try {
            String shellPath = convertShellPath(this.shellPath.getValue());
            String shellCommand = convertShellCommand(this.shellCommand.getValue());
            boolean settingRuntimeVariables = convertSetRuntimeVariables(this.setRunVar.getValue());
            String settingRuntimeVariablesPrefix = convertSetRuntimeVariablesPrefix(this.setRunVarPrefix.getValue());
            String settingRuntimeVariablesMode = convertSetRuntimeVariablesMode(this.setRunVarMode.getValue());
            String connectionName = convertConnectionName(this.connectionName.getValue());
            return executeCommand(shellPath, shellCommand, settingRuntimeVariables, settingRuntimeVariablesPrefix, settingRuntimeVariablesMode, connectionName);
        } catch (InterruptedException e) {
            throw (e);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            actionExecution.getActionControl().increaseErrorCount();

            actionExecution.getActionControl().logOutput("exception", e.getMessage());
            actionExecution.getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private boolean executeCommand(String shellPath, String shellCommand, boolean settingRuntimeVariables, String settingRuntimeVariablesPrefix, String settingRuntimeVariablesMode, String connectionName) throws InterruptedException{
        // Get Connection
        boolean isOnLocalhost = HostConnectionTools.isOnLocalhost(
                connectionName, executionControl.getEnvName());
        HostConnection hostConnection;
        if (connectionName.isEmpty() || connectionName.equalsIgnoreCase("localhost")) {
            hostConnection = new HostConnection(HostConnectionTools.getLocalhostType());
        } else {
            Connection connection = ConnectionConfiguration.getInstance().get(new ConnectionKey(connectionName, this.executionControl.getEnvName()))
                    .orElseThrow(() -> new RuntimeException(MessageFormat.format("Cannot find connection definition for {} in environment {}",
                    connectionName, executionControl.getEnvName())));
            ConnectionOperation connectionOperation = new ConnectionOperation();
            hostConnection = connectionOperation.getHostConnection(connection);
        }


        // Run the action
        ShellCommandResult shellCommandResult;
        ShellCommandSettings shellCommandSettings = new ShellCommandSettings();
        shellCommandSettings.setSetRunVar(settingRuntimeVariables);
        shellCommandSettings.setSetRunVarPrefix(settingRuntimeVariablesPrefix);
        shellCommandSettings.setSetRunVarMode(settingRuntimeVariablesMode);
        shellCommandSettings.setEnvironment(executionControl.getEnvName());

        if (isOnLocalhost) {
            shellCommandResult = hostConnection.executeLocalCommand(shellPath,
                    shellCommand);
        } else {
            shellCommandResult = hostConnection.executeRemoteCommand(shellPath,
                    shellCommand, shellCommandSettings);
        }

        // Set runtime variables
        if (settingRuntimeVariables) {
            executionControl.getExecutionRuntime().setRuntimeVariables(actionExecution, shellCommandResult.getRuntimeVariablesOutput());
        }

        if (shellCommandResult.getReturnCode() == 0) {
            actionExecution.getActionControl().increaseSuccessCount();
        } else {
            actionExecution.getActionControl().increaseErrorCount();
        }

        convertName(this.systemOutputName.getValue())
                .ifPresent(systemOutputName -> executionControl.getExecutionRuntime().setRuntimeVariable(actionExecution,
                        systemOutputName, shellCommandResult.getSystemOutput()));

        actionExecution.getActionControl().logOutput("rc",
                Integer.toString(shellCommandResult.getReturnCode()));
        actionExecution.getActionControl().logOutput("sys.out", shellCommandResult.getSystemOutput());
        actionExecution.getActionControl().logOutput("err.out", shellCommandResult.getErrorOutput());

        return true;
    }

    private String convertSetRuntimeVariablesMode(DataType setRuntimeVariablesMode) {
        // TODO: make optional
        if (setRuntimeVariablesMode == null) {
            return "";
        }
        if (setRuntimeVariablesMode instanceof Text) {
            return setRuntimeVariablesMode.toString();
        } else {
            LOGGER.warn(MessageFormat.format(actionExecution.getAction().getType() +  " does not accept {0} as type for setRuntimeVariablesMode",
                    setRuntimeVariablesMode.getClass()));
            return setRuntimeVariablesMode.toString();
        }
    }

    private String convertConnectionName(DataType connectionName) {
        if (connectionName == null) {
            return "localhost";
        } else if (connectionName instanceof Text) {
            return connectionName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(actionExecution.getAction().getType() +  " does not accept {0} as type for connection name",
                    connectionName.getClass()));
            return connectionName.toString();
        }
    }

    private String convertSetRuntimeVariablesPrefix(DataType setRuntimeVariablesPrefix) {
        // TODO: make optional
        if (setRuntimeVariablesPrefix == null) {
            return "";
        }
        if (setRuntimeVariablesPrefix instanceof Text) {
            return setRuntimeVariablesPrefix.toString();
        } else {
            LOGGER.warn(MessageFormat.format(actionExecution.getAction().getType() +  " does not accept {0} as type for setRuntimeVariablesPrefix",
                    setRuntimeVariablesPrefix.getClass()));
            return setRuntimeVariablesPrefix.toString();
        }
    }

    private boolean convertSetRuntimeVariables(DataType setRuntimeVariables) {
        if (setRuntimeVariables == null) {
            return false;
        }
        if (setRuntimeVariables instanceof Text) {
            return setRuntimeVariables.toString().equalsIgnoreCase("y");
        } else {
            LOGGER.warn(MessageFormat.format(actionExecution.getAction().getType() +  " does not accept {0} as type for setRuntimeVariables",
                    setRuntimeVariables.getClass()));
            return false;
        }
    }

    private String convertShellCommand(DataType shellCommand) {
        if (shellCommand instanceof Text) {
            return shellCommand.toString();
        } else {
            LOGGER.warn(MessageFormat.format(actionExecution.getAction().getType() +  " does not accept {0} as type for shellCommand",
                    shellCommand.getClass()));
            return shellCommand.toString();
        }
    }

    private String convertShellPath(DataType ShellPath) {
        if (ShellPath instanceof Text) {
            return ShellPath.toString();
        } else {
            LOGGER.warn(MessageFormat.format(actionExecution.getAction().getType() +   " does not accept {0} as type for ShellPath",
                    ShellPath.getClass()));
            return ShellPath.toString();
        }
    }

    private Optional<String> convertName(DataType name) {
        if (name == null) {
            return Optional.empty();
        } else if (name instanceof Text) {
            return Optional.of(name.toString());
        } else {
            LOGGER.warn(MessageFormat.format(actionExecution.getAction().getType() + " does not accept {0} as type for name",
                    name.getClass()));
            return Optional.of(name.toString());
        }
    }

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

}