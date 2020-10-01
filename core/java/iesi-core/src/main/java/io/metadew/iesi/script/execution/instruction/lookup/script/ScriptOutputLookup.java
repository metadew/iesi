package io.metadew.iesi.script.execution.instruction.lookup.script;

import io.metadew.iesi.metadata.configuration.script.result.ScriptResultOutputConfiguration;
import io.metadew.iesi.metadata.definition.script.result.ScriptResultOutput;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultOutputKey;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.instruction.lookup.LookupInstruction;

import java.text.MessageFormat;
import java.util.Optional;

public class ScriptOutputLookup implements LookupInstruction {
    private final ExecutionControl executionControl;

    public ScriptOutputLookup(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    @Override
    public String getKeyword() {
        return "script.output";
    }

    @Override
    public String generateOutput(String parameters) {
        Optional<ScriptResultOutput> scriptResultOutput = ScriptResultOutputConfiguration.getInstance().get(new ScriptResultOutputKey(executionControl.getRunId(), -1L, parameters.trim()));
        if (!scriptResultOutput.isPresent()) {
            throw new IllegalArgumentException(MessageFormat.format("No script output parameter {0} found for run id {1}", parameters.trim(), executionControl.getRunId()));
        } else {
            return scriptResultOutput.get().getValue();
        }
    }
}
