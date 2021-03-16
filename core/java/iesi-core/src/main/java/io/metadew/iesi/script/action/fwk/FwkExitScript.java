package io.metadew.iesi.script.action.fwk;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.Optional;

/**
 * This action exits a script
 *
 * @author peter.billen
 */
public class FwkExitScript extends ActionTypeExecution {

    // Parameters
    private static final String STATUS_KEY = "status";
    private static final Logger LOGGER = LogManager.getLogger();

    public FwkExitScript(ExecutionControl executionControl,
                         ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() { }

    protected boolean executeAction() throws InterruptedException {
        Optional<String> status = convertStatus(getParameterResolvedValue(STATUS_KEY));
        // Verify if the status is empty
        if (status.map(status1 -> status1.trim().isEmpty()).orElse(false)) {
            status = Optional.of(ScriptRunStatus.SUCCESS.value());
        }
        return true;
    }

    @Override
    protected String getKeyword() {
        return "fwk.exitScript";
    }

    private Optional<String> convertStatus(DataType status) {
        if (status == null) {
            return Optional.empty();
        }
        if (status instanceof Text) {
            return Optional.of(status.toString());
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for status",
                    status.getClass()));
            return Optional.of(status.toString());
        }
    }
}