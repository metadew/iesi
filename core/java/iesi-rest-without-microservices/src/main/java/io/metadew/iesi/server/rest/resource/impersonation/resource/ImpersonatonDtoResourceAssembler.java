package io.metadew.iesi.server.rest.resource.impersonation.resource;

import io.metadew.iesi.metadata.definition.impersonation.Impersonation;
import io.metadew.iesi.metadata.definition.impersonation.ImpersonationParameter;
import io.metadew.iesi.server.rest.controller.ImpersonationController;
import io.metadew.iesi.server.rest.resource.impersonation.dto.ImpersonationDto;
import io.metadew.iesi.server.rest.resource.impersonation.dto.ImpersonationParameterDto;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ImpersonatonDtoResourceAssembler  extends ResourceAssemblerSupport<Impersonation, ImpersonationDto> {

    private final ModelMapper modelMapper;

    public ImpersonatonDtoResourceAssembler() {
        super(ImpersonationController.class, ImpersonationDto.class);
        this.modelMapper = new ModelMapper();
    }


    @Override
    public ImpersonationDto toResource(Impersonation impersonation) {
        ImpersonationDto impersonationDto = convertToDto(impersonation);
        Link selfLink = linkTo(methodOn(ImpersonationController.class).get(impersonationDto.getName()))
                .withSelfRel();
        impersonationDto.add(selfLink);
        return impersonationDto;
    }

    private ImpersonationDto convertToDto(Impersonation impersonation) {
        if (impersonation == null) {
            throw new IllegalArgumentException("Impersonations have to be non empty");
        }
        return new ImpersonationDto(impersonation.getName(), impersonation.getDescription(), impersonation.getParameters().stream().map(this::convertToDto).collect(Collectors.toList()));
    }

    private ImpersonationParameterDto convertToDto(ImpersonationParameter impersonationParameter) {
        return new ImpersonationParameterDto(impersonationParameter.getConnection(), impersonationParameter.getImpersonatedConnection(),
                impersonationParameter.getDescription());
    }
}