package io.metadew.iesi.server.rest.openapi.dto;

import io.metadew.iesi.server.rest.component.dto.ComponentDto;
import io.metadew.iesi.server.rest.connection.dto.ConnectionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
@AllArgsConstructor
public class TransformResultDto extends RepresentationModel<TransformResultDto> {
    private List<ConnectionDto> connections;
    private List<ComponentDto> components;
    private String title;
    private String version;
}
