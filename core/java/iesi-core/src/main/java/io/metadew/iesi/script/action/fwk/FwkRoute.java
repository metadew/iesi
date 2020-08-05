package io.metadew.iesi.script.action.fwk;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import io.metadew.iesi.script.operation.RouteOperation;

import java.util.*;

public class FwkRoute extends ActionTypeExecution {

    private ActionParameterOperation destination;
    private HashMap<String, RouteOperation> routeOperationMap;

    public FwkRoute(ExecutionControl executionControl, ScriptExecution scriptExecution,
                    ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        // Reset Parameters
        this.setDestination(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "destination"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().toLowerCase().startsWith("condition")) {
                ActionParameterOperation condition = new ActionParameterOperation(this.getExecutionControl(),
                        this.getActionExecution(), this.getActionExecution().getAction().getType(), "condition");

                condition.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());

                int id = 0;
                int delim = actionParameter.getMetadataKey().getParameterName().indexOf(".");
                if (delim > 0) {
                    String[] item = actionParameter.getMetadataKey().getParameterName().split(".");
                    id = Integer.parseInt(item[1]);
                }

                RouteOperation routeOperation = this.getRouteOperation(id);
                routeOperation.setId(id);
                this.setRouteOperation(routeOperation);

                this.getActionParameterOperationMap().put(actionParameter.getMetadataKey().getParameterName(), condition);
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("destination")) {
                ActionParameterOperation destination = new ActionParameterOperation(this.getExecutionControl(),
                        this.getActionExecution(), this.getActionExecution().getAction().getType(), "destination");

                destination.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());

                int id = 0;
                int delim = actionParameter.getMetadataKey().getParameterName().indexOf(".");
                if (delim > 0) {
                    String[] item = actionParameter.getMetadataKey().getParameterName().split(".");
                    id = Integer.parseInt(item[1]);
                }

                RouteOperation routeOperation = this.getRouteOperation(id);
                routeOperation.setId(id);
                routeOperation.setDestination(destination);
                this.setRouteOperation(routeOperation);

                this.getActionParameterOperationMap().put(actionParameter.getMetadataKey().getParameterName(), destination);
            }
        }
    }

    protected boolean executeAction() throws InterruptedException {

        // Evaluate conditions

        // Prepare script
        String scriptId = getScriptExecution().getScript().getMetadataKey().getScriptId();
        Long versionNumber = getScriptExecution().getScript().getVersion().getNumber();
        ScriptKey scriptKey = new ScriptKey(scriptId, versionNumber);
        String scriptName = getScriptExecution().getScript().getName();
        String scriptDescription = getScriptExecution().getScript().getDescription();
        ScriptVersion scriptVersion = getScriptExecution().getScript().getVersion();
        List<Action> scriptActions = new ArrayList<>();
        List<ScriptParameter> scriptParameters = getScriptExecution().getScript().getParameters();
        Script script = new Script(scriptKey, scriptName, scriptDescription, scriptVersion,
                scriptParameters, scriptActions, getScriptExecution().getScript().getLabels());

        //Prepare action runtime
        this.getActionExecution().getActionControl().getActionRuntime().setRouteOperations(new ArrayList());

        // Find appropriate actions
        Iterator iterator = null;
        ObjectMapper objectMapper = new ObjectMapper();
        iterator = this.getRouteOperationMap().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            RouteOperation routeOperation = objectMapper.convertValue(pair.getValue(),
                    RouteOperation.class);

            // Evaluate

            // Move to destination
            boolean destinationFound = false;
            List<Action> actions = new ArrayList<>();
            for (Action action : getScriptExecution().getScript().getActions()) {
                if (action.getName().equalsIgnoreCase(routeOperation.getDestination().getValue().toString())) {
                    destinationFound = true;
                }
                if (destinationFound) {
                    actions.add(action);
                }
            }
            script.setActions(actions);

            //Update routeOperation
            routeOperation.setScript(script);

            this.getActionExecution().getActionControl().getActionRuntime().getRouteOperations().add(routeOperation);

            iterator.remove();
        }

        return true;
    }

    private RouteOperation getRouteOperation(int id) {
        if (this.getRouteOperationMap().containsKey(Integer.toString(id))) {
            return this.getRouteOperationMap().get(Integer.toString(id));
        } else {
            return new RouteOperation();
        }
    }

    private void setRouteOperation(RouteOperation routeOperation) {
        this.getRouteOperationMap().put(Integer.toString(routeOperation.getId()), routeOperation);
    }


    public ActionParameterOperation getDestination() {
        return destination;
    }

    public void setDestination(ActionParameterOperation destination) {
        this.destination = destination;
    }

    public HashMap<String, RouteOperation> getRouteOperationMap() {
        return routeOperationMap;
    }

    public void setRouteOperationMap(HashMap<String, RouteOperation> routeOperationMap) {
        this.routeOperationMap = routeOperationMap;
    }
}