package io.metadew.iesi.script.action.fwk;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.Iteration;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;


public class FwkSetIteration extends ActionTypeExecution {

    private ActionParameterOperation iterationName;
    private ActionParameterOperation iterationType;
    private ActionParameterOperation iterationList;
    private ActionParameterOperation iterationValues;
    private ActionParameterOperation iterationFrom;
    private ActionParameterOperation iterationTo;
    private ActionParameterOperation iterationStep;
    private ActionParameterOperation iterationCondition;
    private ActionParameterOperation iterationInterrupt;
    private static final Logger LOGGER = LogManager.getLogger();

    public FwkSetIteration(ExecutionControl executionControl,
                           ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        // TODO: based on type a different class should be defined? e.g. fwk.setIteration.list or fwk.setIteration.range or fwk.setIteration.values
        // Reset Parameters
        this.setIterationName(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "name"));
        this.setIterationType(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "type"));
        this.setIterationList(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "list"));
        this.setIterationValues(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "values"));
        this.setIterationFrom(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "from"));
        this.setIterationTo(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "to"));
        this.setIterationStep(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "step"));
        this.setIterationCondition(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "condition"));
        this.setIterationInterrupt(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "interrupt"));
        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("name")) {
                this.getIterationName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("type")) {
                this.getIterationType().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("list")) {
                this.getIterationList().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("values")) {
                this.getIterationValues().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("from")) {
                this.getIterationFrom().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("to")) {
                this.getIterationTo().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("step")) {
                this.getIterationStep().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("condition")) {
                this.getIterationCondition().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("interrupt")) {
                this.getIterationInterrupt().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("name", this.getIterationName());
        this.getActionParameterOperationMap().put("type", this.getIterationType());
        this.getActionParameterOperationMap().put("list", this.getIterationList());
        this.getActionParameterOperationMap().put("values", this.getIterationValues());
        this.getActionParameterOperationMap().put("from", this.getIterationFrom());
        this.getActionParameterOperationMap().put("to", this.getIterationTo());
        this.getActionParameterOperationMap().put("step", this.getIterationStep());
        this.getActionParameterOperationMap().put("condition", this.getIterationCondition());
        this.getActionParameterOperationMap().put("interrupt", this.getIterationInterrupt());
    }

    protected boolean executeAction() throws InterruptedException {
        String name = convertIterationName(getIterationName().getValue());
        String type = convertIterationType(getIterationType().getValue());
        String list = convertIterationList(getIterationList().getValue());
        String values = convertIterationValues(getIterationValues().getValue());
        String from = convertIterationFrom(getIterationFrom().getValue());
        String to = convertIterationTo(getIterationTo().getValue());
        String step = convertIterationStep(getIterationStep().getValue());
        String condition = convertIterationCondition(getIterationCondition().getValue());
        boolean interrupt = convertIterationInterrupt(getIterationInterrupt().getValue());
        Iteration iteration = new Iteration(name, type, list, values, from, to, step == null ? "1" : step, condition, interrupt ? "y" : "n");
        this.getExecutionControl().getExecutionRuntime().setIteration(iteration);
        this.getActionExecution().getActionControl().increaseSuccessCount();

        return true;
    }


    private boolean convertIterationInterrupt(DataType iterationInterrupt) {
        // TODO: remove if different class for every iteration variable
        if (iterationInterrupt == null) {
            return false;
        } else if (iterationInterrupt instanceof Text) {
            return iterationInterrupt.toString().equalsIgnoreCase("y");
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for iterationInterrupt",
                    iterationInterrupt.getClass()));
            return false;
        }
    }

    private String convertIterationCondition(DataType iterationCondition) {
        // TODO: remove if different class for every iteration variable
        if (iterationCondition == null) {
            return null;
        } else if (iterationCondition instanceof Text) {
            return iterationCondition.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for iterationCondition",
                    iterationCondition.getClass()));
            return iterationCondition.toString();
        }
    }

    private String convertIterationStep(DataType iterationStep) {
        // TODO: remove if different class for every iteration variable
        if (iterationStep == null) {
            return null;
        } else if (iterationStep instanceof Text) {
            return iterationStep.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for iterationStep",
                    iterationStep.getClass()));
            return iterationStep.toString();
        }
    }

    private String convertIterationTo(DataType iterationTo) {
        // TODO: remove if different class for every iteration variable
        if (iterationTo == null) {
            return null;
        } else if (iterationTo instanceof Text) {
            return iterationTo.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for iterationTo",
                    iterationTo.getClass()));
            return iterationTo.toString();
        }
    }

    private String convertIterationFrom(DataType iterationFrom) {
        // TODO: remove if different class for every iteration variable
        if (iterationFrom == null) {
            return null;
        } else if (iterationFrom instanceof Text) {
            return iterationFrom.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for iterationFrom",
                    iterationFrom.getClass()));
            return iterationFrom.toString();
        }
    }

    private String convertIterationValues(DataType iterationValues) {
        // TODO: remove if different class for every iteration variable
        if (iterationValues == null) {
            return null;
        } else if (iterationValues instanceof Text) {
            return iterationValues.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for iterationValues",
                    iterationValues.getClass()));
            return iterationValues.toString();
        }
    }

    private String convertIterationList(DataType iterationList) {
        // TODO: remove if different class for every iteration variable
        if (iterationList == null) {
            return null;
        } else if (iterationList instanceof Text) {
            return iterationList.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for iterationList",
                    iterationList.getClass()));
            return iterationList.toString();
        }
    }

    private String convertIterationType(DataType iterationType) {
        if (iterationType instanceof Text) {
            return iterationType.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for iterationType",
                    iterationType.getClass()));
            return iterationType.toString();
        }
    }

    private String convertIterationName(DataType iterationName) {
        if (iterationName instanceof Text) {
            return iterationName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for iterationName",
                    iterationName.getClass()));
            return iterationName.toString();
        }
    }

    public ActionParameterOperation getIterationName() {
        return iterationName;
    }

    public void setIterationName(ActionParameterOperation iterationName) {
        this.iterationName = iterationName;
    }

    public ActionParameterOperation getIterationType() {
        return iterationType;
    }

    public void setIterationType(ActionParameterOperation iterationType) {
        this.iterationType = iterationType;
    }

    public ActionParameterOperation getIterationList() {
        return iterationList;
    }

    public void setIterationList(ActionParameterOperation iterationList) {
        this.iterationList = iterationList;
    }

    public ActionParameterOperation getIterationValues() {
        return iterationValues;
    }

    public void setIterationValues(ActionParameterOperation iterationValues) {
        this.iterationValues = iterationValues;
    }

    public ActionParameterOperation getIterationFrom() {
        return iterationFrom;
    }

    public void setIterationFrom(ActionParameterOperation iterationFrom) {
        this.iterationFrom = iterationFrom;
    }

    public ActionParameterOperation getIterationTo() {
        return iterationTo;
    }

    public void setIterationTo(ActionParameterOperation iterationTo) {
        this.iterationTo = iterationTo;
    }

    public ActionParameterOperation getIterationStep() {
        return iterationStep;
    }

    public void setIterationStep(ActionParameterOperation iterationStep) {
        this.iterationStep = iterationStep;
    }

    public ActionParameterOperation getIterationInterrupt() {
        return iterationInterrupt;
    }

    public void setIterationInterrupt(ActionParameterOperation iterationInterrupt) {
        this.iterationInterrupt = iterationInterrupt;
    }

    public ActionParameterOperation getIterationCondition() {
        return iterationCondition;
    }

    public void setIterationCondition(ActionParameterOperation iterationCondition) {
        this.iterationCondition = iterationCondition;
    }

}