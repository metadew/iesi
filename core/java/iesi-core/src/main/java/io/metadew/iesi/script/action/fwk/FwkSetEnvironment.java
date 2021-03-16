package io.metadew.iesi.script.action.fwk;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.environment.EnvironmentConfiguration;
import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;


public class FwkSetEnvironment extends ActionTypeExecution {

    private static final String ENVIRONMENT_NAME_KEY = "environment";
    private static final Logger LOGGER = LogManager.getLogger();

    public FwkSetEnvironment(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() { }

    protected boolean executeAction() throws InterruptedException {
        String environmentName = convertEnvironmentName(getParameterResolvedValue(ENVIRONMENT_NAME_KEY));

        //Check if environment exists
        Environment environment = EnvironmentConfiguration.getInstance()
                .get(new EnvironmentKey(environmentName))
                .orElseThrow(() -> new RuntimeException("Could not find environment " + environmentName));
        this.getExecutionControl().setEnvironment(getActionExecution(), environmentName);
        this.getActionExecution().getActionControl().increaseSuccessCount();
        return true;
    }

    @Override
    protected String getKeyword() {
        return "fwk.setEnvironment";
    }

    private String convertEnvironmentName(DataType environmentName) {
        if (environmentName instanceof Text) {
            return environmentName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for expect environmentName",
                    environmentName.getClass()));
            return environmentName.toString();
        }
    }
}