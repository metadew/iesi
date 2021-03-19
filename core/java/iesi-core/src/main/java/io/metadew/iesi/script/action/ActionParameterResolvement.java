package io.metadew.iesi.script.action;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import lombok.Data;

@Data
public class ActionParameterResolvement {

    private final ActionParameter actionParameter;
    private final DataType resolvedValue;

}
