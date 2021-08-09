package io.metadew.iesi.script.action.cli;

import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.host.ShellCommandResult;
import io.metadew.iesi.connection.host.ShellCommandSettings;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.connection.tools.HostConnectionTools;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes._null.Null;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import lombok.extern.log4j.Log4j2;

import java.text.MessageFormat;
import java.util.Optional;

@Log4j2
public class CliExecuteCommand extends ActionTypeExecution {


    // Parameters
    private static final String SHELL_PATH_KEY = "path";
    private static  final String SHELL_COMMAND_KEY = "command";
    private static final String SET_RUN_VAR_KEY = "setRuntimeVariables";
    private static final String SET_RUN_VAR_PREFIX_KEY = "setRuntimeVariablesPrefix";
    private static final String SET_RUN_VAR_MODE_KEY = "setRuntimeVariablesMode";
    private static final String CONNECTION_NAME_KEY = "connection";
    private static final String SYSTEM_OUTPUT_NAME_KEY = "output";

    public CliExecuteCommand(ExecutionControl executionControl,
                             ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepareAction() {
        // Reset Parameters
    }

    protected boolean executeAction() throws InterruptedException {
        // Get Connection
        String shellPath = convertShellPath(getParameterResolvedValue(SHELL_PATH_KEY));
        String shellCommand = convertShellCommand(getParameterResolvedValue(SHELL_COMMAND_KEY));
        boolean settingRuntimeVariables = convertSetRuntimeVariables(getParameterResolvedValue(SET_RUN_VAR_KEY));
        String settingRuntimeVariablesPrefix = convertSetRuntimeVariablesPrefix(getParameterResolvedValue(SET_RUN_VAR_PREFIX_KEY));
        String settingRuntimeVariablesMode = convertSetRuntimeVariablesMode(getParameterResolvedValue(SET_RUN_VAR_MODE_KEY));
        String connectionName = convertConnectionName(getParameterResolvedValue(CONNECTION_NAME_KEY));
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

        convertName(getParameterResolvedValue(SYSTEM_OUTPUT_NAME_KEY))
                .ifPresent(systemOutputName -> getExecutionControl().getExecutionRuntime().setRuntimeVariable(getActionExecution(),
                        systemOutputName, shellCommandResult.getSystemOutput()));

        getActionExecution().getActionControl().logOutput("rc",
                Integer.toString(shellCommandResult.getReturnCode()));
        getActionExecution().getActionControl().logOutput("sys.out", shellCommandResult.getSystemOutput());
        getActionExecution().getActionControl().logOutput("err.out", shellCommandResult.getErrorOutput());

        return true;
    }

    @Override
    protected String getKeyword() {
        return "cli.executeCommand";
    }

    private String convertSetRuntimeVariablesMode(DataType setRuntimeVariablesMode) {
        // TODO: make optional
        if (setRuntimeVariablesMode == null || setRuntimeVariablesMode instanceof Null) {
            return "";
        }
        if (setRuntimeVariablesMode instanceof Text) {
            return setRuntimeVariablesMode.toString();
        } else {
            log.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for setRuntimeVariablesMode",
                    setRuntimeVariablesMode.getClass()));
            return setRuntimeVariablesMode.toString();
        }
    }

    private String convertConnectionName(DataType connectionName) {
        if (connectionName == null || connectionName instanceof Null) {
            return "localhost";
        } else if (connectionName instanceof Text) {
            return connectionName.toString();
        } else {
            log.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for connection name",
                    connectionName.getClass()));
            return connectionName.toString();
        }
    }

    private String convertSetRuntimeVariablesPrefix(DataType setRuntimeVariablesPrefix) {
        // TODO: make optional
        if (setRuntimeVariablesPrefix == null || setRuntimeVariablesPrefix instanceof Null) {
            return "";
        }
        if (setRuntimeVariablesPrefix instanceof Text) {
            return setRuntimeVariablesPrefix.toString();
        } else {
            log.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for setRuntimeVariablesPrefix",
                    setRuntimeVariablesPrefix.getClass()));
            return setRuntimeVariablesPrefix.toString();
        }
    }

    private boolean convertSetRuntimeVariables(DataType setRuntimeVariables) {
        if (setRuntimeVariables == null || setRuntimeVariables instanceof Null) {
            return false;
        }
        if (setRuntimeVariables instanceof Text) {
            return setRuntimeVariables.toString().equalsIgnoreCase("y");
        } else {
            log.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for setRuntimeVariables",
                    setRuntimeVariables.getClass()));
            return false;
        }
    }

    private String convertShellCommand(DataType shellCommand) {
        if (shellCommand instanceof Text) {
            return shellCommand.toString();
        } else {
            log.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for shellCommand",
                    shellCommand.getClass()));
            return shellCommand.toString();
        }
    }

    private String convertShellPath(DataType ShellPath) {
        if (ShellPath instanceof Text) {
            return ShellPath.toString();
        } else {
            log.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for ShellPath",
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
            log.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for name",
                    name.getClass()));
            return Optional.of(name.toString());
        }
    }

}