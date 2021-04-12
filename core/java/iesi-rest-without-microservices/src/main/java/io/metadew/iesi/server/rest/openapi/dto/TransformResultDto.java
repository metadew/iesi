package io.metadew.iesi.server.rest.openapi.dto;

import io.metadew.iesi.server.rest.component.dto.ComponentDto;
import io.metadew.iesi.server.rest.connection.dto.ConnectionDto;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class TransformResultDto extends RepresentationModel<TransformResultDto> {
    private List<ConnectionDto> connections;
    private List<ComponentDto> components;
    private String title;
    private String version;
}
