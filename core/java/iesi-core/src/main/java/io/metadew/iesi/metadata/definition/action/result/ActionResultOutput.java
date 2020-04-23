package io.metadew.iesi.metadata.definition.action.result;


import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.action.result.key.ActionResultOutputKey;

public class ActionResultOutput extends Metadata<ActionResultOutputKey> {

    private String value;

    //Constructors

    public ActionResultOutput(ActionResultOutputKey actionResultOutputKey, String value) {
        super(actionResultOutputKey);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}