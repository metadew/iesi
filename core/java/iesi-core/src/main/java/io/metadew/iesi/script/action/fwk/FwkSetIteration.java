package io.metadew.iesi.script.action.fwk;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.Iteration;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;


public class FwkSetIteration extends ActionTypeExecution {

    private static final String ITERATION_NAME_KEY = "name";
    private static final String ITERATION_TYPE_KEY = "type";
    private static final String ITERATION_LIST_KEY = "list";
    private static final String ITERATION_VALUES_KEY = "values";
    private static final String ITERATION_FROM_KEY = "from";
    private static final String ITERATION_TO_KEY = "to";
    private static final String ITERATION_STEP_KEY = "step";
    private static final String ITERATION_CONDITION_KEY = "condition";
    private static final String ITERATION_INTERRUPT_KEY = "interrupt";
    private static final Logger LOGGER = LogManager.getLogger();

    public FwkSetIteration(ExecutionControl executionControl,
                           ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        // TODO: based on type a different class should be defined? e.g. fwk.setIteration.list or fwk.setIteration.range or fwk.setIteration.values
    }

    protected boolean executeAction() throws InterruptedException {
        String name = convertIterationName(getParameterResolvedValue(ITERATION_NAME_KEY));
        String type = convertIterationType(getParameterResolvedValue(ITERATION_TYPE_KEY));
        String list = convertIterationList(getParameterResolvedValue(ITERATION_LIST_KEY));
        String values = convertIterationValues(getParameterResolvedValue(ITERATION_VALUES_KEY));
        String from = convertIterationFrom(getParameterResolvedValue(ITERATION_FROM_KEY));
        String to = convertIterationTo(getParameterResolvedValue(ITERATION_TO_KEY));
        String step = convertIterationStep(getParameterResolvedValue(ITERATION_STEP_KEY));
        String condition = convertIterationCondition(getParameterResolvedValue(ITERATION_CONDITION_KEY));
        boolean interrupt = convertIterationInterrupt(getParameterResolvedValue(ITERATION_INTERRUPT_KEY));
        Iteration iteration = new Iteration(name, type, list, values, from, to, step == null ? "1" : step, condition, interrupt ? "y" : "n");
        this.getExecutionControl().getExecutionRuntime().setIteration(iteration);
        this.getActionExecution().getActionControl().increaseSuccessCount();

        return true;
    }

    @Override
    protected String getKeyword() {
        return "fwk.setIteration";
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
}