package io.metadew.iesi.server.rest.connectiontypes.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionTypeParameterDto {

    private String name;
    private String description;
    private String type;
    private boolean mandatory = false;
    private boolean encrypted = false;

}
