package io.metadew.iesi.server.rest.actiontypes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ActionTypeParameterDto {

    private String name;
    private String description;
    private String type;
    private boolean mandatory = false;
    private boolean encrypted = false;

}
