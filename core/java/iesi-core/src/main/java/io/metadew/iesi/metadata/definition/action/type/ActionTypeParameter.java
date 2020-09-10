package io.metadew.iesi.metadata.definition.action.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionTypeParameter {

    private String description;
    private String type;
    private boolean mandatory = false;
    private boolean encrypted = false;
    private String subroutine = "";
    private boolean impersonate = false;

}