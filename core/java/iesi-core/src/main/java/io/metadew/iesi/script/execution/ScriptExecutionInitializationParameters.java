package io.metadew.iesi.script.execution;

import io.metadew.iesi.metadata.definition.script.Script;
import lombok.Data;

import java.util.Map;

@Data
public class ScriptExecutionInitializationParameters {

    private final Script script;
    private final Map<String, String> scriptExecutionParameters;
    private final String environment;

}
