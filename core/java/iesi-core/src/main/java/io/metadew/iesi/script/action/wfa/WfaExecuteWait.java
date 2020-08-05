package io.metadew.iesi.script.action.wfa;


import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

public class WfaExecuteWait extends ActionTypeExecution {

    // Parameters
    private ActionParameterOperation waitInterval;
    private final int defaultWaitInterval = 0;
    private ActionParameterOperation waitIntervalInput;
    private long startTime;
    private static final Logger LOGGER = LogManager.getLogger();


    public WfaExecuteWait(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        // Set Parameters
        this.setWaitInterval(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "wait"));


        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("wait")) {
                this.getWaitInterval().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }
        this.getActionParameterOperationMap().put("wait", this.getWaitInterval());

    }

    protected boolean executeAction() throws InterruptedException {
        int waitInterval = convertWaitInterval(getWaitInterval().getValue());
        // Run the action
        long wait = waitInterval * 1000;
        if (wait < 0)
            wait = 1000;
        boolean done = false;

        this.setStartTime(System.currentTimeMillis());
        try {
            Thread.sleep(wait);
            done = true;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        long elapsedTime = System.currentTimeMillis() - this.getStartTime();
        if (done) {
            this.getActionExecution().getActionControl().increaseSuccessCount();

            this.getActionExecution().getActionControl().logOutput("out", "result found");
            this.getActionExecution().getActionControl().logOutput("time", Long.toString(elapsedTime));
        } else {
            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("out", "time-out");
            this.getActionExecution().getActionControl().logOutput("time", Long.toString(elapsedTime));
        }
        return true;
    }

    private int convertWaitInterval(DataType waitInterval) {
        if (waitInterval == null) {
            return defaultWaitInterval;
        }
        if (waitInterval instanceof Text) {
            return Integer.parseInt(waitInterval.toString());
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + "this.getActionExecution().get().getType() +  does not accept {0} as type for wait interval",
                    waitInterval.getClass()));
            return defaultWaitInterval;
        }
    }


    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public ActionParameterOperation getWaitIntervalInput() {
        return waitIntervalInput;
    }

    public void setWaitIntervalInput(ActionParameterOperation waitIntervalInput) {
        this.waitIntervalInput = waitIntervalInput;
    }

    public ActionParameterOperation getWaitInterval() {
        return waitInterval;
    }

    public void setWaitInterval(ActionParameterOperation waitInterval) {
        this.waitInterval = waitInterval;
    }

}