package io.metadew.iesi.script.action.wfa;


import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

public class WfaExecuteWait extends ActionTypeExecution {

    private static final String WAIT_KEY = "wait";
    private final int defaultWaitInterval = 0;
    private long startTime;
    private static final Logger LOGGER = LogManager.getLogger();


    public WfaExecuteWait(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepareAction() {

    }

    protected boolean executeAction() throws InterruptedException {
        int waitInterval = convertWaitInterval(getParameterResolvedValue(WAIT_KEY));
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

            this.getActionExecution().getActionControl().logOutput("action.error", "Waiting period interrupted");
            this.getActionExecution().getActionControl().logOutput("time", Long.toString(elapsedTime));
        }
        return true;
    }

    @Override
    protected String getKeyword() {
        return "wfa.executeWait";
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

}