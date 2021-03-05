package io.metadew.iesi.metadata.definition.action.result;


import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.action.result.key.ActionResultOutputKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ActionResultOutput extends Metadata<ActionResultOutputKey> {

    private String value;

    //Constructors

    @Builder
    public ActionResultOutput(ActionResultOutputKey actionResultOutputKey, String value) {
        super(actionResultOutputKey);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}