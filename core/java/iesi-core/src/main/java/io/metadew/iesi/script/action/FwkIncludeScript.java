package io.metadew.iesi.script.action;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.Text;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.metadata.definition.Script;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.Level;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Optional;

/**
 * This action includes a script
 *
 * @author peter.billen
 */
public class FwkIncludeScript {

    private ActionExecution actionExecution;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation scriptName;
    private ActionParameterOperation scriptVersion;

    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Exposed Script
    private Script script;

    // Constructors
    public FwkIncludeScript() {

    }

    public FwkIncludeScript(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
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
        this.setScriptName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "script"));
        this.setScriptVersion(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "version"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("script")) {
                this.getScriptName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("version")) {
                this.getScriptVersion().setInputValue(actionParameter.getValue());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("script", this.getScriptName());
        this.getActionParameterOperationMap().put("version", this.getScriptVersion());
    }

    public boolean execute() {
        try {
            String scriptName = convertScriptName(getScriptName().getValue());
            Optional<Long> scriptVersion = convertScriptVersion(getScriptVersion().getValue());
            return includeScript(scriptName, scriptVersion);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }
    }

    private boolean includeScript(String scriptName, Optional<Long> scriptVersion) {
        ScriptConfiguration scriptConfiguration = new ScriptConfiguration(this.getFrameworkExecution().getFrameworkInstance());
        Script script = scriptVersion
                .map(scriptVersion1 -> scriptConfiguration.getScript(scriptName, scriptVersion1))
                .orElse(scriptConfiguration.getScript(scriptName)).get();
        setScript(script);
        this.getActionExecution().getActionControl().increaseSuccessCount();
        return true;
    }

    private Optional<Long> convertScriptVersion(DataType scriptVersion) {
        if (scriptVersion == null) {
            return Optional.empty();
        }
        if (scriptVersion instanceof Text) {
            return Optional.of(Long.parseLong(scriptVersion.toString()));
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for script name",
                    scriptVersion.getClass()), Level.WARN);
            return Optional.empty();
        }
    }


    private String convertScriptName(DataType scriptName) {
        if (scriptName instanceof Text) {
            return scriptName.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for script name",
                    scriptName.getClass()), Level.WARN);
            return scriptName.toString();
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

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

    public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        this.actionParameterOperationMap = actionParameterOperationMap;
    }

    public ActionParameterOperation getScriptName() {
        return scriptName;
    }

    public void setScriptName(ActionParameterOperation scriptName) {
        this.scriptName = scriptName;
    }

    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;
    }

    public ActionParameterOperation getScriptVersion() {
        return scriptVersion;
    }

    public void setScriptVersion(ActionParameterOperation scriptVersion) {
        this.scriptVersion = scriptVersion;
    }

}