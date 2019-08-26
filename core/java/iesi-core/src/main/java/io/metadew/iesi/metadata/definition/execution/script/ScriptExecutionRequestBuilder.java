package io.metadew.iesi.metadata.definition.execution.script;

import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;

import java.text.MessageFormat;
import java.util.*;

public class ScriptExecutionRequestBuilder {

    private ScriptExecutionRequestKey scriptExecutionRequestKey;
    private ExecutionRequestKey executionRequestKey;
    private String mode;
    // File mode
    private String fileName;
    // script mode
    private String scriptName;
    private Long scriptVersion;

    private List<Long> actionSelect;
    private boolean exit;
    private String impersonation;
    private Map<String, String> parameters = new HashMap<>();
    private Map<String, String> impersonations = new HashMap<>();
    private String environment;

    public ScriptExecutionRequestBuilder() {}

    public ScriptExecutionRequestBuilder(String mode) {
        this.mode = mode;
    }

    public ScriptExecutionRequestBuilder executionRequestKey(ExecutionRequestKey executionRequestKey) {
        this.executionRequestKey = executionRequestKey;
        return this;
    }
    public ScriptExecutionRequestBuilder scriptExecutionRequestKey(ScriptExecutionRequestKey scriptExecutionRequestKey) {
        this.scriptExecutionRequestKey = scriptExecutionRequestKey;
        return this;
    }

    public ScriptExecutionRequestBuilder mode(String mode) {
        this.mode = mode;
        return this;
    }

    public ScriptExecutionRequestBuilder fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public ScriptExecutionRequestBuilder impersonation(String impersonation) {
        this.impersonation = impersonation;
        return this;
    }

    public ScriptExecutionRequestBuilder environment(String environment) {
        this.environment = environment;
        return this;
    }

    public ScriptExecutionRequestBuilder impersonations(Map<String, String> impersonations) {
        this.impersonations = impersonations;
        return this;
    }

    public ScriptExecutionRequestBuilder actionSelect(List<Long> actionSelect) {
        this.actionSelect = actionSelect;
        return this;
    }

    public ScriptExecutionRequestBuilder exit(boolean exit) {
        this.exit = exit;
        return this;
    }

    public ScriptExecutionRequestBuilder parameters(Map<String, String> parameters) {
        if (this.parameters == null) {
            this.parameters = parameters;
        } else {
            this.parameters.putAll(parameters);
        }
        return this;
    }

    public ScriptExecutionRequestBuilder scriptName(String scriptName) {
        this.scriptName = scriptName;
        return this;
    }

    public ScriptExecutionRequestBuilder scriptVersion(Long scriptVersion) {
        this.scriptVersion = scriptVersion;
        return this;
    }

    public ScriptExecutionRequest build() throws ScriptExecutionRequestBuilderException {
        verifyMandatoryArguments();
        if (mode.equalsIgnoreCase("file")) {
            return buildFileScriptExecutionRequest();
        } else if (mode.equalsIgnoreCase("script")) {
            return buildScriptNameExecutionRequest();
        } else {
            throw new ScriptExecutionRequestBuilderException(MessageFormat.format("Cannot create ScriptExecutionRequest of mode {0}", mode));
        }
    }

    private void verifyMandatoryArguments() throws ScriptExecutionRequestBuilderException {
        if (mode == null || environment == null || executionRequestKey == null) {
            throw new ScriptExecutionRequestBuilderException(MessageFormat.format("Cannot create ScriptExecutionRequest without mode selection, environment or execution request key", mode));
        }

    }

    private ScriptNameExecutionRequest buildScriptNameExecutionRequest() throws ScriptExecutionRequestBuilderException {
        verifyMandatoryNameArguments();
        return new ScriptNameExecutionRequest(
                getScriptExecutionRequestKey()
                        .orElse(new ScriptExecutionRequestKey(IdentifierTools.getScriptExecutionRequestIdentifier()
                        )),
                executionRequestKey,
                scriptName,
                scriptVersion,
                environment,
                getActionSelect().orElse(new ArrayList<>()),
                exit,
                impersonation,
                impersonations,
                parameters, ScriptExecutionRequestStatus.NEW);
    }

    private void verifyMandatoryNameArguments() throws ScriptExecutionRequestBuilderException {
        if (scriptName == null) {
            throw new ScriptExecutionRequestBuilderException("Cannot create ScriptNameExecutionRequest without scriptName");
        }
    }

    private void verifyMandatoryFileArguments() throws ScriptExecutionRequestBuilderException {
        if (fileName == null) {
            throw new ScriptExecutionRequestBuilderException("Cannot create ScriptFileExecutionRequest without filename");
        }
    }

    private ScriptFileExecutionRequest buildFileScriptExecutionRequest() throws ScriptExecutionRequestBuilderException {
        verifyMandatoryFileArguments();
        return new ScriptFileExecutionRequest(
                getScriptExecutionRequestKey()
                        .orElse(new ScriptExecutionRequestKey(IdentifierTools.getScriptExecutionRequestIdentifier()
                        )),
                executionRequestKey,
                fileName,
                environment,
                getActionSelect().orElse(new ArrayList<>()),
                exit,
                impersonation,
                impersonations,
                parameters, ScriptExecutionRequestStatus.NEW);
    }

    private Optional<List<Long>> getActionSelect() {
        return Optional.ofNullable(actionSelect);
    }

    private Optional<ScriptExecutionRequestKey> getScriptExecutionRequestKey() {
        return Optional.ofNullable(scriptExecutionRequestKey);
    }

}
