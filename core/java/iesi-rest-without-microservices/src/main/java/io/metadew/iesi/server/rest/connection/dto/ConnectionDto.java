package io.metadew.iesi.server.rest.connection.dto;


import io.metadew.iesi.metadata.definition.connection.Connection;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
