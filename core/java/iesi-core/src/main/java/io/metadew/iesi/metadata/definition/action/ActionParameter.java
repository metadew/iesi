package io.metadew.iesi.metadata.definition.action;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ActionParameter extends Metadata<ActionParameterKey> {

    private String value;

    @Builder
    public ActionParameter(ActionParameterKey actionParameterKey, String value) {
        super(actionParameterKey);
        this.value = value;
    }
}