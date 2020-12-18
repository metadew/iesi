package io.metadew.iesi.metadata.definition.action.trace;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.action.trace.key.ActionParameterTraceKey;

public class ActionParameterTrace extends Metadata<ActionParameterTraceKey> {

    private final String value;

    public ActionParameterTrace(ActionParameterTraceKey metadataKey, String value) {
        super(metadataKey);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
