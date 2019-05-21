package io.metadew.iesi.script.action;

import io.metadew.iesi.framework.configuration.FrameworkStatus;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.metadata.definition.Script;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

public class FwkExecuteScript {

    private ActionExecution actionExecution;

    private ScriptExecution scriptExecution;

    private FrameworkExecution frameworkExecution;

    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation scriptName;

    private ActionParameterOperation scriptVersion;

    private ActionParameterOperation environmentName;

    private ActionParameterOperation paramList;

    private ActionParameterOperation paramFile;

    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Constructors
    public FwkExecuteScript() {

    }

    public FwkExecuteScript(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ScriptExecution scriptExecution,
                            ActionExecution actionExecution) {
        this.init(frameworkExecution, executionControl, scriptExecution, actionExecution);
    }

    public void init(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ScriptExecution scriptExecution,
                     ActionExecution actionExecution) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setScriptExecution(scriptExecution);
        this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
    }

    public void prepare() {
        // Reset Parameters
        this.setScriptName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "script"));
        this.setScriptVersion(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "version"));
        this.setEnvironmentName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "environment"));
        this.setParamList(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "paramList"));
        this.setParamFile(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "paramFile"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("script")) {
                this.getScriptName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("version")) {
                this.getScriptVersion().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("environment")) {
                this.getEnvironmentName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("paramlist")) {
                this.getParamList().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("paramfile")) {
                this.getParamFile().setInputValue(actionParameter.getValue());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("script", this.getScriptName());
        this.getActionParameterOperationMap().put("version", this.getScriptVersion());
        this.getActionParameterOperationMap().put("environment", this.getEnvironmentName());
        this.getActionParameterOperationMap().put("paramList", this.getParamList());
        this.getActionParameterOperationMap().put("paramFile", this.getParamFile());
    }

    public boolean execute() {
        try {
            // Add parameter allow recursive
            // Add reuse options in a script

            // Check on Running a script in a loop
            if (this.getScriptExecution().getScript().getName().equalsIgnoreCase(this.getScriptName().getValue())) {
                throw new RuntimeException("Not allowed to run the script recursively");
            }

            try {
                ScriptConfiguration scriptConfiguration = new ScriptConfiguration(this.getFrameworkExecution());
                // Script script = scriptConfiguration.getScript(this.getScriptName().getValue());
                Script script = null;
                if (this.getScriptVersion().getValue().equalsIgnoreCase("")) {
                    script = scriptConfiguration.getScript(this.getScriptName().getValue());
                } else {
                    script = scriptConfiguration.getScript(this.getScriptName().getValue(),
                            Long.parseLong(this.getScriptVersion().getValue()));
                }
                ScriptExecution scriptExecution = new ScriptExecution(this.getFrameworkExecution(), script);
                scriptExecution.initializeAsNonRootExecution(this.getExecutionControl(), this.getScriptExecution());

                if (!this.getParamList().getValue().equalsIgnoreCase("")) {
                    scriptExecution.setParamList(this.getParamList().getValue());
                }
                if (!this.getParamFile().getValue().equalsIgnoreCase("")) {
                    scriptExecution.setParamFile(this.getParamFile().getValue());
                }

                scriptExecution.execute();

                if (scriptExecution.getResult().equalsIgnoreCase(FrameworkStatus.SUCCESS.value())) {
                    this.getActionExecution().getActionControl().increaseSuccessCount();
                } else if (scriptExecution.getResult()
                        .equalsIgnoreCase(FrameworkStatus.WARNING.value())) {
                    this.getActionExecution().getActionControl().increaseSuccessCount();
                } else if (scriptExecution.getResult()
                        .equalsIgnoreCase(FrameworkStatus.ERROR.value())) {
                    this.getActionExecution().getActionControl().increaseErrorCount();
                } else {
                    this.getActionExecution().getActionControl().increaseErrorCount();
                }

            } catch (Exception e) {
                throw new RuntimeException("Issue setting runtime variables: " + e, e);
            }
            return true;
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
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

    public ScriptExecution getScriptExecution() {
        return scriptExecution;
    }

    public void setScriptExecution(ScriptExecution scriptExecution) {
        this.scriptExecution = scriptExecution;
    }

    public ActionParameterOperation getScriptName() {
        return scriptName;
    }

    public void setScriptName(ActionParameterOperation scriptName) {
        this.scriptName = scriptName;
    }

    public ActionParameterOperation getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(ActionParameterOperation environmentName) {
        this.environmentName = environmentName;
    }

    public ActionParameterOperation getParamList() {
        return paramList;
    }

    public void setParamList(ActionParameterOperation paramList) {
        this.paramList = paramList;
    }

    public ActionParameterOperation getParamFile() {
        return paramFile;
    }

    public void setParamFile(ActionParameterOperation paramFile) {
        this.paramFile = paramFile;
    }

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

    public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        this.actionParameterOperationMap = actionParameterOperationMap;
    }

    public ActionParameterOperation getScriptVersion() {
        return scriptVersion;
    }

    public void setScriptVersion(ActionParameterOperation scriptVersion) {
        this.scriptVersion = scriptVersion;
    }
}