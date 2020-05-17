package io.metadew.iesi.server.rest.actiontypes;

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
public class ActionTypeDto {

    private String name;
    private String description;
    private String status;
    @JsonProperty("parameters")
    private List<ActionTypeParameterDto> actionTypeParameterDtos;

}
