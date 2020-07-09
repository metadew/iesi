package io.metadew.iesi.script.action.fwk;

import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.operation.ActionParameterOperation;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

public class FwkStartIteration {

    private ActionExecution actionExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation typeName;
    private ActionParameterOperation listName;
    private ActionParameterOperation listValues;
    private ActionParameterOperation numberFrom;
    private ActionParameterOperation numberTo;
    private ActionParameterOperation numberAction;
    private ActionParameterOperation breakOnError;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Constructors
    public FwkStartIteration(ExecutionControl executionControl, ActionExecution actionExecution) {
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setActionParameterOperationMap(new HashMap<>());
    }

    public void prepare() throws Exception {
        // Reset Parameters
        this.setTypeName(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "TYPE_NM"));
        this.setListName(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "LIST_NM"));
        this.setListValues(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "LIST_VAL"));
        this.setNumberFrom(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "NUMBER_FROM"));
        this.setNumberTo(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "NUMBER_TO"));
        this.setNumberAction(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "NUMBER_STEP"));
        this.setBreakOnError(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "BREAK_ON_ERROR"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("type_nm")) {
                this.getTypeName().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("list_nm")) {
                this.getListName().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("list_val")) {
                this.getListValues().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("number_from")) {
                this.getNumberFrom().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("number_to")) {
                this.getNumberTo().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("number_action")) {
                this.getNumberAction().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("break_on_error")) {
                this.getBreakOnError().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            }
        }

        //Create parameter list
        this.getActionParameterOperationMap().put("TYPE_NM", this.getTypeName());
        this.getActionParameterOperationMap().put("LIST_NM", this.getListName());
        this.getActionParameterOperationMap().put("LIST_VAL", this.getListValues());
        this.getActionParameterOperationMap().put("NUMBER_FROM", this.getNumberFrom());
        this.getActionParameterOperationMap().put("NUMBER_TO", this.getNumberTo());
        this.getActionParameterOperationMap().put("NUMBER_STEP", this.getNumberAction());
        this.getActionParameterOperationMap().put("BREAK_ON_ERROR", this.getBreakOnError());
    }

    public boolean execute() throws InterruptedException {
        try {
            return executeOperation();
        } catch (InterruptedException e) {
            throw (e);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private boolean executeOperation() throws InterruptedException {
        this.getActionExecution().getActionControl().increaseSuccessCount();

        return true;
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

    public ActionParameterOperation getTypeName() {
        return typeName;
    }

    public void setTypeName(ActionParameterOperation typeName) {
        this.typeName = typeName;
    }

    public ActionParameterOperation getListName() {
        return listName;
    }

    public void setListName(ActionParameterOperation listName) {
        this.listName = listName;
    }

    public ActionParameterOperation getListValues() {
        return listValues;
    }

    public void setListValues(ActionParameterOperation listValues) {
        this.listValues = listValues;
    }

    public ActionParameterOperation getNumberFrom() {
        return numberFrom;
    }

    public void setNumberFrom(ActionParameterOperation numberFrom) {
        this.numberFrom = numberFrom;
    }

    public ActionParameterOperation getNumberTo() {
        return numberTo;
    }

    public void setNumberTo(ActionParameterOperation numberTo) {
        this.numberTo = numberTo;
    }

    public ActionParameterOperation getNumberAction() {
        return numberAction;
    }

    public void setNumberAction(ActionParameterOperation numberAction) {
        this.numberAction = numberAction;
    }

    public ActionParameterOperation getBreakOnError() {
        return breakOnError;
    }

    public void setBreakOnError(ActionParameterOperation breakOnError) {
        this.breakOnError = breakOnError;
    }

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

    public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        this.actionParameterOperationMap = actionParameterOperationMap;
    }

}