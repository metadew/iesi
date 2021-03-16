package io.metadew.iesi.script.action.fwk;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.Optional;

/**
 * This action includes a script
 *
 * @author peter.billen
 */
public class FwkIncludeScript extends ActionTypeExecution {

    private static final String SCRIPT_NAME_KEY = "script";
    private static final String SCRIPT_VERSION_KEY = "version";

    // Exposed Script
    private Script script;

    private static final Logger LOGGER = LogManager.getLogger();


    public FwkIncludeScript(ExecutionControl executionControl,
                            ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() { }

    protected boolean executeAction() throws InterruptedException {
        String scriptName = convertScriptName(getParameterResolvedValue(SCRIPT_NAME_KEY));
        Optional<Long> scriptVersion = convertScriptVersion(getParameterResolvedValue(SCRIPT_VERSION_KEY));
        Script script = scriptVersion
                .map(scriptVersion1 -> ScriptConfiguration.getInstance().get(new ScriptKey(IdentifierTools.getScriptIdentifier(scriptName), scriptVersion1)))
                .orElse(ScriptConfiguration.getInstance().getLatestVersion(scriptName)).get();
        setScript(script);
        this.getActionExecution().getActionControl().increaseSuccessCount();
        return true;
    }

    @Override
    protected String getKeyword() {
        return "fwk.includeScript";
    }

    private Optional<Long> convertScriptVersion(DataType scriptVersion) {
        if (scriptVersion == null) {
            return Optional.empty();
        }
        if (scriptVersion instanceof Text) {
            return Optional.of(Long.parseLong(scriptVersion.toString()));
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for script name",
                    scriptVersion.getClass()));
            return Optional.empty();
        }
    }


    private String convertScriptName(DataType scriptName) {
        if (scriptName instanceof Text) {
            return scriptName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for script name",
                    scriptName.getClass()));
            return scriptName.toString();
        }
    }

    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;
    }

}