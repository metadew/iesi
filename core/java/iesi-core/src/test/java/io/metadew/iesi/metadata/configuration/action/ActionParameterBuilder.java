package io.metadew.iesi.metadata.configuration.action;

import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterKey;

import java.util.Optional;

public class ActionParameterBuilder {

    private final String scriptId;
    private final long scriptVersionNumber;
    private final String actionId;
    private final String actionParameterName;
    private String value;

    public ActionParameterBuilder(String scriptId, long scriptVersionNumber, String actionId, String actionParameterName) {
        this.scriptId = scriptId;
        this.scriptVersionNumber = scriptVersionNumber;
        this.actionId = actionId;
        this.actionParameterName = actionParameterName;
    }

    public ActionParameterBuilder value(String value) {
        this.value = value;
        return this;
    }

    public ActionParameter build() {
        return new ActionParameter(new ActionParameterKey(scriptId, scriptVersionNumber, actionId, actionParameterName), getValue().orElse("dummy"));
    }

    public Optional<String> getValue() {
        return Optional.ofNullable(value);
    }



}
