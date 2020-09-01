package io.metadew.iesi.server.rest.impersonation.dto;

import io.metadew.iesi.metadata.definition.impersonation.Impersonation;
import io.metadew.iesi.metadata.definition.impersonation.ImpersonationParameter;
import io.metadew.iesi.server.rest.impersonation.ImpersonationController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ImpersonatonDtoResourceAssembler extends RepresentationModelAssemblerSupport<Impersonation, ImpersonationDto> {

    public ImpersonatonDtoResourceAssembler() {
        super(ImpersonationController.class, ImpersonationDto.class);
    }

    @Override
    public ImpersonationDto toModel(Impersonation impersonation) {
        ImpersonationDto impersonationDto = convertToDto(impersonation);
        Link selfLink = linkTo(methodOn(ImpersonationController.class)
                .get(impersonationDto.getName()))
                .withSelfRel();
        impersonationDto.add(selfLink);
        return impersonationDto;
    }

    private ImpersonationDto convertToDto(Impersonation impersonation) {
        if (impersonation == null) {
            throw new IllegalArgumentException("Impersonations have to be non empty");
        }
        return new ImpersonationDto(impersonation.getMetadataKey().getName(), impersonation.getDescription(), impersonation.getParameters().stream().map(this::convertToDto).collect(Collectors.toList()));
    }

    private ImpersonationParameterDto convertToDto(ImpersonationParameter impersonationParameter) {
        return new ImpersonationParameterDto(impersonationParameter.getMetadataKey().getParameterName(), impersonationParameter.getImpersonatedConnection(),
                impersonationParameter.getDescription());
    }
}