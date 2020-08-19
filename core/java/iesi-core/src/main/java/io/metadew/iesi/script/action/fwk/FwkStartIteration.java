package io.metadew.iesi.script.action.fwk;

import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;

public class FwkStartIteration extends ActionTypeExecution {

    private ActionParameterOperation typeName;
    private ActionParameterOperation listName;
    private ActionParameterOperation listValues;
    private ActionParameterOperation numberFrom;
    private ActionParameterOperation numberTo;
    private ActionParameterOperation numberAction;
    private ActionParameterOperation breakOnError;

    // Constructors
    public FwkStartIteration(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
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
                this.getTypeName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("list_nm")) {
                this.getListName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("list_val")) {
                this.getListValues().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("number_from")) {
                this.getNumberFrom().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("number_to")) {
                this.getNumberTo().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("number_action")) {
                this.getNumberAction().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("break_on_error")) {
                this.getBreakOnError().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
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

    protected boolean executeAction() throws InterruptedException {
        this.getActionExecution().getActionControl().increaseSuccessCount();

        return true;
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


}