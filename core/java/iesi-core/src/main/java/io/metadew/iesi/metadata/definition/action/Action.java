package io.metadew.iesi.metadata.definition.action;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Action extends Metadata<ActionKey> {

    private Long number;
    private String type;
    private String name;
    private String description;
    private String component = "";
    private String condition = "";
    private String iteration = "";
    private String errorExpected;
    private String errorStop;
    private int retries = 0;
    private List<ActionParameter> parameters = new ArrayList<>();

    // TODO: make optional Paramaters of type Optional instead of ""

    @Builder
    public Action(ActionKey actionKey, long number, String type, String name, String description, String component,
                  String condition, String iteration, String errorExpected, String errorStop, String retries, List<ActionParameter> parameters) {
        super(actionKey);
        this.number = number;
        this.type = type;
        this.name = name;
        this.description = description;
        this.component = component;
        this.condition = condition;
        this.iteration = iteration;
        this.errorExpected = errorExpected;
        this.errorStop = errorStop;
        this.retries = Integer.parseInt(retries);
        this.parameters = parameters;
    }


    public boolean getErrorExpected() {
        return errorExpected.equalsIgnoreCase("y");
    }

    public boolean getErrorStop() {
        return errorStop.equalsIgnoreCase("y");
    }

}