package io.metadew.iesi.script.action;

import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.host.ShellCommandResult;
import io.metadew.iesi.connection.host.ShellCommandSettings;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.Text;
import io.metadew.iesi.connection.tools.HostConnectionTools;
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

/**
 * Action type to execute command line instructions.
 *
 * @author peter.billen
 */
public class CliExecuteCommand {

    private ActionExecution actionExecution;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation shellPath;
    private ActionParameterOperation shellCommand;
    private ActionParameterOperation setRunVar;
    private ActionParameterOperation setRunVarPrefix;
    private ActionParameterOperation setRunVarMode;
    private ActionParameterOperation connectionName;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Constructors
    public CliExecuteCommand() {

    }

    public CliExecuteCommand(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
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
        this.setShellPath(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "path"));
        this.setShellCommand(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "command"));
        this.setSetRunVar(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "setRuntimeVariables"));
        this.setSetRunVarPrefix(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(),
                "setRuntimeVariablesPrefix"));
        this.setSetRunVarMode(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "setRuntimeVariablesMode"));
        this.setConnectionName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "connection"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("path")) {
                this.getShellPath().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("command")) {
                this.getShellCommand().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("setruntimevariables")) {
                this.getSetRunVar().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("setruntimevariablesprefix")) {
                this.getSetRunVarPrefix().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("setruntimevariablesmode")) {
                this.getSetRunVarMode().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("connection")) {
                this.getConnectionName().setInputValue(actionParameter.getValue());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("path", this.getShellPath());
        this.getActionParameterOperationMap().put("command", this.getShellCommand());
        this.getActionParameterOperationMap().put("setRuntimeVariables", this.getSetRunVar());
        this.getActionParameterOperationMap().put("setRuntimeVariablesPrefix", this.getSetRunVarPrefix());
        this.getActionParameterOperationMap().put("setRuntimeVariablesMode", this.getSetRunVarMode());
        this.getActionParameterOperationMap().put("connection", this.getConnectionName());
    }

    // Methods
    public boolean execute() {
        try {
            String shellPath = convertShellPath(getShellPath().getValue());
            String shellCommand = convertShellCommand(getShellCommand().getValue());
            boolean settingRuntimeVariables = convertSetRuntimeVariables(getSetRunVar().getValue());
            String settingRuntimeVariablesPrefix = convertSetRuntimeVariablesPrefix(getSetRunVarPrefix().getValue());
            String settingRuntimeVariablesMode = convertSetRuntimeVariablesMode(getSetRunVarMode().getValue());
            String connectionName = convertConnectionName(getConnectionName().getValue());
            return executeCommand(shellPath, shellCommand, settingRuntimeVariables, settingRuntimeVariablesPrefix, settingRuntimeVariablesMode, connectionName);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private boolean executeCommand(String shellPath, String shellCommand, boolean settingRuntimeVariables, String settingRuntimeVariablesPrefix, String settingRuntimeVariablesMode, String connectionName) {
        // Get Connection
        boolean isOnLocalhost = HostConnectionTools.isOnLocalhost(this.getFrameworkExecution(),
                connectionName, this.getExecutionControl().getEnvName());

        HostConnection hostConnection;
        if (connectionName.isEmpty() || connectionName.equalsIgnoreCase("localhost")) {
            hostConnection = new HostConnection(HostConnectionTools.getLocalhostType());
        } else {
            ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(this.getFrameworkExecution().getFrameworkInstance());
            Connection connection = connectionConfiguration.getConnection(connectionName,
                    this.getExecutionControl().getEnvName()).get();
            ConnectionOperation connectionOperation = new ConnectionOperation(this.getFrameworkExecution());
            hostConnection = connectionOperation.getHostConnection(connection);
        }


        // Run the action
        ShellCommandResult shellCommandResult;
        ShellCommandSettings shellCommandSettings = new ShellCommandSettings();
        shellCommandSettings.setSetRunVar(settingRuntimeVariables);
        shellCommandSettings.setSetRunVarPrefix(settingRuntimeVariablesPrefix);
        shellCommandSettings.setSetRunVarMode(settingRuntimeVariablesMode);
        shellCommandSettings.setFrameworkExecution(this.getFrameworkExecution());
        shellCommandSettings.setEnvironment(this.getExecutionControl().getEnvName());

        if (isOnLocalhost) {
            shellCommandResult = hostConnection.executeLocalCommand(shellPath,
                    shellCommand, shellCommandSettings);
        } else {
            shellCommandResult = hostConnection.executeRemoteCommand(shellPath,
                    shellCommand, shellCommandSettings);
        }

        // Set runtime variables
        if (settingRuntimeVariables) {
            this.getExecutionControl().getExecutionRuntime()
                    .setRuntimeVariables(this.getActionExecution(), shellCommandResult.getRuntimeVariablesOutput());
        }

        if (shellCommandResult.getReturnCode() == 0) {
            this.getActionExecution().getActionControl().increaseSuccessCount();
        } else {
            this.getActionExecution().getActionControl().increaseErrorCount();
        }

        this.getActionExecution().getActionControl().logOutput("rc",
                Integer.toString(shellCommandResult.getReturnCode()));
        this.getActionExecution().getActionControl().logOutput("sys.out", shellCommandResult.getSystemOutput());
        this.getActionExecution().getActionControl().logOutput("err.out", shellCommandResult.getErrorOutput());

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
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() +  " does not accept {0} as type for setRuntimeVariablesMode",
                    setRuntimeVariablesMode.getClass()), Level.WARN);
            return setRuntimeVariablesMode.toString();
        }
    }

    private String convertConnectionName(DataType connectionName) {
        if (connectionName == null) {
            return "localhost";
        }
        if (connectionName instanceof Text) {
            return connectionName.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() +  " does not accept {0} as type for connection name",
                    connectionName.getClass()), Level.WARN);
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
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() +  " does not accept {0} as type for setRuntimeVariablesPrefix",
                    setRuntimeVariablesPrefix.getClass()), Level.WARN);
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
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() +  " does not accept {0} as type for setRuntimeVariables",
                    setRuntimeVariables.getClass()), Level.WARN);
            return false;
        }
    }

    private String convertShellCommand(DataType shellCommand) {
        if (shellCommand instanceof Text) {
            return shellCommand.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() +  " does not accept {0} as type for shellCommand",
                    shellCommand.getClass()), Level.WARN);
            return shellCommand.toString();
        }
    }

    private String convertShellPath(DataType ShellPath) {
        if (ShellPath instanceof Text) {
            return ShellPath.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() +   " does not accept {0} as type for ShellPath",
                    ShellPath.getClass()), Level.WARN);
            return ShellPath.toString();
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

    public ActionParameterOperation getShellPath() {
        return shellPath;
    }

    public void setShellPath(ActionParameterOperation shellPath) {
        this.shellPath = shellPath;
    }

    public ActionParameterOperation getShellCommand() {
        return shellCommand;
    }

    public void setShellCommand(ActionParameterOperation shellCommand) {
        this.shellCommand = shellCommand;
    }

    public ActionParameterOperation getSetRunVar() {
        return setRunVar;
    }

    public void setSetRunVar(ActionParameterOperation setRunVar) {
        this.setRunVar = setRunVar;
    }

    public ActionParameterOperation getSetRunVarPrefix() {
        return setRunVarPrefix;
    }

    public void setSetRunVarPrefix(ActionParameterOperation setRunVarPrefix) {
        this.setRunVarPrefix = setRunVarPrefix;
    }

    public ActionParameterOperation getSetRunVarMode() {
        return setRunVarMode;
    }

    public void setSetRunVarMode(ActionParameterOperation setRunVarMode) {
        this.setRunVarMode = setRunVarMode;
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

}