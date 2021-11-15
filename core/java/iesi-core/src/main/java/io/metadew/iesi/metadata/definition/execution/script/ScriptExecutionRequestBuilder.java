package io.metadew.iesi.metadata.definition.execution.script;

import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    private Set<ScriptExecutionRequestParameter> parameters = new HashSet<>();
    private Set<ScriptExecutionRequestImpersonation> impersonations = new HashSet<>();
    private String environment;

    public ScriptExecutionRequestBuilder() {}

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

    public ScriptExecutionRequestBuilder environment(String environment) {
        this.environment = environment;
        return this;
    }

    public ScriptExecutionRequestBuilder impersonations(List<ScriptExecutionRequestImpersonation> impersonations) {
        this.impersonations.addAll(impersonations);
        return this;
    }

    public ScriptExecutionRequestBuilder impersonations(ScriptExecutionRequestImpersonation impersonation) {
        this.impersonations.add(impersonation);
        return this;
    }

   /* public ScriptExecutionRequestBuilder exit(boolean exit) {
        this.exit = exit;
        return this;
    }*/

    public ScriptExecutionRequestBuilder parameters(List<ScriptExecutionRequestParameter> parameters) {
        this.parameters.addAll(parameters);
        return this;
    }

    public ScriptExecutionRequestBuilder parameter(ScriptExecutionRequestParameter parameter) {
        this.parameters.add(parameter);
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
                scriptExecutionRequestKey,
                executionRequestKey,
                environment,
                impersonations,
                parameters,
                ScriptExecutionRequestStatus.NEW,
                scriptName,
                scriptVersion
        );
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
                impersonations,
                parameters,
                ScriptExecutionRequestStatus.NEW);
    }

    private Optional<ScriptExecutionRequestKey> getScriptExecutionRequestKey() {
        return Optional.ofNullable(scriptExecutionRequestKey);
    }

}
