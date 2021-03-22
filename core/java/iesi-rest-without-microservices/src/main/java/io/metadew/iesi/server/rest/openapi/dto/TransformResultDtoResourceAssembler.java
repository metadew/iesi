package io.metadew.iesi.server.rest.openapi.dto;

import io.metadew.iesi.openapi.TransformResult;
import io.metadew.iesi.server.rest.component.dto.ComponentDtoResourceAssembler;
import io.metadew.iesi.server.rest.connection.dto.ConnectionDtoResourceAssembler;
import io.metadew.iesi.server.rest.openapi.OpenAPIController;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TransformResultDtoResourceAssembler extends RepresentationModelAssemblerSupport<TransformResult, TransformResultDto> {

    ComponentDtoResourceAssembler componentDtoResourceAssembler;
    ConnectionDtoResourceAssembler connectionDtoResourceAssembler;

    public TransformResultDtoResourceAssembler(ComponentDtoResourceAssembler componentDtoResourceAssembler, ConnectionDtoResourceAssembler connectionDtoResourceAssembler) {
        super(OpenAPIController.class, TransformResultDto.class);
        this.componentDtoResourceAssembler = componentDtoResourceAssembler;
        this.connectionDtoResourceAssembler = connectionDtoResourceAssembler;
    }

    @Override
    public TransformResultDto toModel(TransformResult transformResult) {
        return convertToDto(transformResult);
    }

    private TransformResultDto convertToDto(TransformResult transformResult) {
        return new TransformResultDto(
                transformResult.getConnections().stream().map(connection -> connectionDtoResourceAssembler.toModel(connection)).collect(Collectors.toList()),
                transformResult.getComponents().stream().map(component -> componentDtoResourceAssembler.toModel(component)).collect(Collectors.toList())
        );
    }
}

