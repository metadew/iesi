package io.metadew.iesi.metadata.definition.action.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActionType {

    private String className;
    private String description;
    private Map<String, ActionTypeParameter> parameters;
    private String status = "none";

}