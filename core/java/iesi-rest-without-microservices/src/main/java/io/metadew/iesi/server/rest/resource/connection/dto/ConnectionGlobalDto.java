package io.metadew.iesi.server.rest.resource.connection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionGlobalDto extends RepresentationModel<ConnectionGlobalDto> {

    private String name;
    private String type;
    private String description;

}
