package io.metadew.iesi.script.action;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.metadata.configuration.type.ActionTypeParameterConfiguration;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.type.ActionTypeParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import io.metadew.iesi.script.service.ActionParameterService;
import lombok.Getter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

@Getter
public abstract class ActionTypeExecution {

    private final ExecutionControl executionControl;
    private final ScriptExecution scriptExecution;
    private final ActionExecution actionExecution;
    private final List<ActionParameterResolvement> actionParameterResolvements = new ArrayList<>();
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap = new HashMap<>();

    protected ActionTypeExecution(ExecutionControl executionControl,
                        ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.executionControl = executionControl;
        this.scriptExecution = scriptExecution;
        this.actionExecution = actionExecution;
        for (Map.Entry<String, ActionTypeParameter> actionTypeParameter : ActionTypeParameterConfiguration.getInstance().getActionTypeParameters(getKeyword()).entrySet()) {
            Optional<ActionParameter> actionParameter = getActionExecution().getAction().getParameters().stream()
                    // TODO: go to equals instead of equals ignore case
                    .filter(actionParameterElement -> actionParameterElement.getMetadataKey().getParameterName().equalsIgnoreCase(actionTypeParameter.getKey()))
                    .findFirst();

            if (!actionParameter.isPresent()) {
                if (actionTypeParameter.getValue().isMandatory()) {
                    throw new RuntimeException(String.format("Action parameter %s is mandatory for %s", actionTypeParameter.getKey(), getKeyword()));
                }
            } else {
                getActionParameterResolvements().add(new ActionParameterResolvement(
                        actionParameter.get(),
                        ActionParameterService.getInstance().getValue(actionParameter.get(), getExecutionControl().getExecutionRuntime(), getActionExecution())));
            }
        }
    }

    public abstract void prepare() throws Exception;

    public boolean execute() throws InterruptedException {
        try {
            return executeAction();
        } catch (InterruptedException e) {
            throw (e);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            getActionExecution().getActionControl().increaseErrorCount();

            getActionExecution().getActionControl().logOutput("action.error", e.getMessage());
            getActionExecution().getActionControl().logOutput("action.exception", StackTrace.toString());

            return false;
        }
    }

    protected abstract boolean executeAction() throws Exception;

    protected abstract String getKeyword();

    protected DataType getParameterResolvedValue(String parameterKey) {
        return getActionParameterResolvements().stream()
                .filter(actionParameterResolvement -> actionParameterResolvement.getActionParameter().getMetadataKey().getParameterName().equalsIgnoreCase(parameterKey))
                .findFirst()
                .map(ActionParameterResolvement::getResolvedValue)
                .orElse(null);
    }

}
