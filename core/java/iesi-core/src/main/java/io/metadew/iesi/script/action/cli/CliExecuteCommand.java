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
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.Optional;

/**
 * Action type to execute command line instructions.
 *
 * @author peter.billen
 */
public class CliExecuteCommand extends ActionTypeExecution {


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
    private static final Logger LOGGER = LogManager.getLogger();

    public CliExecuteCommand(ExecutionControl executionControl,
                             ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        // Reset Parameters
        this.shellPath = new ActionParameterOperation(getExecutionControl(),
                getActionExecution(), getActionExecution().getAction().getType(), shellPathKey);
        this.shellCommand = new ActionParameterOperation(getExecutionControl(),
                getActionExecution(), getActionExecution().getAction().getType(), shellCommandKey);
        this.setRunVar = new ActionParameterOperation(getExecutionControl(),
                getActionExecution(), getActionExecution().getAction().getType(), setRunVarKey);
        this.setRunVarPrefix = new ActionParameterOperation(getExecutionControl(),
                getActionExecution(), getActionExecution().getAction().getType(),
                setRunVarPrefixKey);
        this.setRunVarMode = new ActionParameterOperation(getExecutionControl(),
                getActionExecution(), getActionExecution().getAction().getType(), setRunVarModeKey);
        this.systemOutputName = new ActionParameterOperation(getExecutionControl(),
                getActionExecution(), getActionExecution().getAction().getType(), systemOutputNameKey);
        this.connectionName = new ActionParameterOperation(getExecutionControl(),
                getActionExecution(), getActionExecution().getAction().getType(), connectionNameKey);

        // Get Parameters
        for (ActionParameter actionParameter : getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(shellPathKey)) {
                shellPath.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(shellCommandKey)) {
                shellCommand.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(setRunVarKey)) {
                setRunVar.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(setRunVarPrefixKey)) {
                setRunVarPrefix.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(setRunVarModeKey)) {
                setRunVarMode.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(systemOutputNameKey)) {
                systemOutputName.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(connectionNameKey)) {
                connectionName.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put(shellPathKey, shellPath);
        this.getActionParameterOperationMap().put(shellCommandKey, shellCommand);
        this.getActionParameterOperationMap().put(setRunVarKey, setRunVar);
        this.getActionParameterOperationMap().put(setRunVarPrefixKey, setRunVarPrefix);
        this.getActionParameterOperationMap().put(setRunVarModeKey, setRunVarMode);
        this.getActionParameterOperationMap().put(systemOutputNameKey, systemOutputName);
        this.getActionParameterOperationMap().put(connectionNameKey, connectionName);
    }

    protected boolean executeAction() throws InterruptedException {
        // Get Connection
        String shellPath = convertShellPath(this.shellPath.getValue());
        String shellCommand = convertShellCommand(this.shellCommand.getValue());
        boolean settingRuntimeVariables = convertSetRuntimeVariables(this.setRunVar.getValue());
        String settingRuntimeVariablesPrefix = convertSetRuntimeVariablesPrefix(this.setRunVarPrefix.getValue());
        String settingRuntimeVariablesMode = convertSetRuntimeVariablesMode(this.setRunVarMode.getValue());
        String connectionName = convertConnectionName(this.connectionName.getValue());
        boolean isOnLocalhost = HostConnectionTools.isOnLocalhost(
                connectionName, getExecutionControl().getEnvName());
        HostConnection hostConnection;
        if (connectionName.isEmpty() || connectionName.equalsIgnoreCase("localhost")) {
            hostConnection = new HostConnection(HostConnectionTools.getLocalhostType());
        } else {
            Connection connection = ConnectionConfiguration.getInstance().get(new ConnectionKey(connectionName, this.getExecutionControl().getEnvName()))
                    .orElseThrow(() -> new RuntimeException(MessageFormat.format("Cannot find connection definition for {} in environment {}",
                            connectionName, getExecutionControl().getEnvName())));
            hostConnection = ConnectionOperation.getInstance().getHostConnection(connection);
        }


        // Run the action
        ShellCommandResult shellCommandResult;
        ShellCommandSettings shellCommandSettings = new ShellCommandSettings();
        shellCommandSettings.setSetRunVar(settingRuntimeVariables);
        shellCommandSettings.setSetRunVarPrefix(settingRuntimeVariablesPrefix);
        shellCommandSettings.setSetRunVarMode(settingRuntimeVariablesMode);
        shellCommandSettings.setEnvironment(getExecutionControl().getEnvName());

        if (isOnLocalhost) {
            shellCommandResult = hostConnection.executeLocalCommand(shellPath,
                    shellCommand);
        } else {
            shellCommandResult = hostConnection.executeRemoteCommand(shellPath,
                    shellCommand, shellCommandSettings);
        }

        // Set runtime variables
        if (settingRuntimeVariables) {
            getExecutionControl().getExecutionRuntime().setRuntimeVariables(getActionExecution(), shellCommandResult.getRuntimeVariablesOutput());
        }

        if (shellCommandResult.getReturnCode() == 0) {
            getActionExecution().getActionControl().increaseSuccessCount();
        } else {
            getActionExecution().getActionControl().logOutput("action.error", "Command executed with return code " + shellCommandResult.getReturnCode());
            getActionExecution().getActionControl().increaseErrorCount();
        }

        convertName(this.systemOutputName.getValue())
                .ifPresent(systemOutputName -> getExecutionControl().getExecutionRuntime().setRuntimeVariable(getActionExecution(),
                        systemOutputName, shellCommandResult.getSystemOutput()));

        getActionExecution().getActionControl().logOutput("rc",
                Integer.toString(shellCommandResult.getReturnCode()));
        getActionExecution().getActionControl().logOutput("sys.out", shellCommandResult.getSystemOutput());
        getActionExecution().getActionControl().logOutput("err.out", shellCommandResult.getErrorOutput());

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
            LOGGER.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for setRuntimeVariablesMode",
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
            LOGGER.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for connection name",
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
            LOGGER.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for setRuntimeVariablesPrefix",
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
            LOGGER.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for setRuntimeVariables",
                    setRuntimeVariables.getClass()));
            return false;
        }
    }

    private String convertShellCommand(DataType shellCommand) {
        if (shellCommand instanceof Text) {
            return shellCommand.toString();
        } else {
            LOGGER.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for shellCommand",
                    shellCommand.getClass()));
            return shellCommand.toString();
        }
    }

    private String convertShellPath(DataType ShellPath) {
        if (ShellPath instanceof Text) {
            return ShellPath.toString();
        } else {
            LOGGER.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for ShellPath",
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
            LOGGER.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for name",
                    name.getClass()));
            return Optional.of(name.toString());
        }
    }

}