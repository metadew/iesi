package io.metadew.iesi.script.action.fwk;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import io.metadew.iesi.script.operation.RouteOperation;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
public class FwkRoute {

    private ActionExecution actionExecution;

    private ScriptExecution scriptExecution;

    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation destination;
    private HashMap<String, RouteOperation> routeOperationMap;

    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Constructors
    public FwkRoute() {

    }

    public FwkRoute(ExecutionControl executionControl, ScriptExecution scriptExecution,
                    ActionExecution actionExecution) {
        this.init(executionControl, scriptExecution, actionExecution);
    }

    public void init(ExecutionControl executionControl, ScriptExecution scriptExecution,
                     ActionExecution actionExecution) {
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setScriptExecution(scriptExecution);
        this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
        this.setRouteOperationMap(new HashMap<String, RouteOperation>());
    }

    public void prepare()  {
        // Reset Parameters
        this.setDestination(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "destination"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().toLowerCase().startsWith("condition")) {
                ActionParameterOperation condition = new ActionParameterOperation(this.getExecutionControl(),
                        this.getActionExecution(), this.getActionExecution().getAction().getType(), "condition");

                condition.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());

                int id = 0;
                int delim = actionParameter.getName().indexOf(".");
                if (delim > 0) {
                    String[] item = actionParameter.getName().split(".");
                    id = Integer.parseInt(item[1]);
                }

                RouteOperation routeOperation = this.getRouteOperation(id);
                routeOperation.setId(id);
                routeOperation.setCondition(condition);
                this.setRouteOperation(routeOperation);

                this.getActionParameterOperationMap().put(actionParameter.getName(), condition);
            } else if (actionParameter.getName().equalsIgnoreCase("destination")) {
                ActionParameterOperation destination = new ActionParameterOperation(this.getExecutionControl(),
                        this.getActionExecution(), this.getActionExecution().getAction().getType(), "destination");

                destination.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());

                int id = 0;
                int delim = actionParameter.getName().indexOf(".");
                if (delim > 0) {
                    String[] item = actionParameter.getName().split(".");
                    id = Integer.parseInt(item[1]);
                }

                RouteOperation routeOperation = this.getRouteOperation(id);
                routeOperation.setId(id);
                routeOperation.setDestination(destination);
                this.setRouteOperation(routeOperation);

                this.getActionParameterOperationMap().put(actionParameter.getName(), destination);
            }

        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean execute() {
        try {

            // Evaluate conditions

            // Prepare script
            String scriptId = this.getScriptExecution().getScript().getId();
            Long versionNumber = this.getScriptExecution().getScript().getVersion().getNumber();
            ScriptKey scriptKey = new ScriptKey(scriptId, versionNumber);
            String scriptType = this.getScriptExecution().getScript().getType();
            String scriptName = this.getScriptExecution().getScript().getName();
            String scriptDescription = this.getScriptExecution().getScript().getDescription();
            ScriptVersion scriptVersion = this.getScriptExecution().getScript().getVersion();
            List<Action> scriptActions = new ArrayList<>();
            List<ScriptParameter> scriptParameters = this.getScriptExecution().getScript().getParameters();
            Script script = new Script(scriptKey, scriptType, scriptName, scriptDescription, scriptVersion,
                    scriptParameters, scriptActions);


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
                List<Action> actions = new ArrayList();
                for (Action action : this.getScriptExecution().getScript().getActions()) {
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
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

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

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

    public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        this.actionParameterOperationMap = actionParameterOperationMap;
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