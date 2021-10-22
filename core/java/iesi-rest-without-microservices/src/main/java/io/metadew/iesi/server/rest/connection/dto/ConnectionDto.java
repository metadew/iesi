package io.metadew.iesi.server.rest.connection.dto;


import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConnectionDto extends RepresentationModel<ConnectionDto> {

    private String name;
    private String securityGroupName;
    private String type;
    private String description;
    private Set<ConnectionEnvironmentDto> environments;

}
