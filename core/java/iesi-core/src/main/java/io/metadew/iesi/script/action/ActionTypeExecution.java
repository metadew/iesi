package io.metadew.iesi.script.action;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.type.ActionTypeParameterConfiguration;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.type.ActionTypeParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.service.ActionParameterService;
import lombok.Getter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
public abstract class ActionTypeExecution {

    private final ExecutionControl executionControl;
    private final ScriptExecution scriptExecution;
    private final ActionExecution actionExecution;
    private final List<ActionParameterResolvement> actionParameterResolvements = new ArrayList<>();
    private final ActionTypeParameterConfiguration actionTypeParameterConfiguration = SpringContext.getBean(ActionTypeParameterConfiguration.class);
    private final ActionParameterService actionParameterService = SpringContext.getBean(ActionParameterService.class);

    protected ActionTypeExecution(ExecutionControl executionControl,
                        ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.executionControl = executionControl;
        this.scriptExecution = scriptExecution;
        this.actionExecution = actionExecution;
    }

    public void resolveParameters() {
        for (Map.Entry<String, ActionTypeParameter> actionTypeParameter : actionTypeParameterConfiguration.getActionTypeParameters(getKeyword()).entrySet()) {
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
                        actionParameterService.getValue(actionParameter.get(), getExecutionControl().getExecutionRuntime(), getActionExecution())));
            }
        }
    }

    public void prepare() throws Exception {
        resolveParameters();
        prepareAction();
    }

    public abstract void prepareAction() throws Exception;

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

    public void replaceParameterResolvedValue(ActionParameter actionParameter, String newValue) {
        actionParameterResolvements.removeIf(o -> o.getActionParameter().getMetadataKey().getParameterName().equals(actionParameter.getMetadataKey().getParameterName()));
        actionParameterResolvements.add(
                new ActionParameterResolvement(
                        actionParameter,
                        new Text(newValue)
                )
        );
    }

}
