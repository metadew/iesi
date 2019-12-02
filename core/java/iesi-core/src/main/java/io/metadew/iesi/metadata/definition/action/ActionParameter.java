package io.metadew.iesi.metadata.definition.action;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterKey;

public class ActionParameter extends Metadata<ActionParameterKey> {

    private String value;

    public ActionParameter(ActionParameterKey actionParameterKey, String value) {
        super(actionParameterKey);
        this.value = value;
    }

    public String getName() {return this.getMetadataKey().getActionName();}

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}