package io.metadew.iesi.server.rest.connectiontypes.actiontypes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionTypeDto {

    private String name;
    private String description;
    @JsonProperty("parameters")
    private List<ConnectionTypeParameterDto> connectionTypeParameterDtos;

}
