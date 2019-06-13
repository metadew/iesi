package io.metadew.iesi.server.rest.ressource.impersonation;

import io.metadew.iesi.metadata.definition.Impersonation;
import io.metadew.iesi.metadata.definition.ImpersonationParameter;
import io.metadew.iesi.server.rest.controller.ImpersonationController;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

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
        Link selfLink = linkTo(methodOn(ImpersonationController.class).getByName(impersonationDto.getName()))
                .withSelfRel();
        impersonationDto.add(selfLink);
        return impersonationDto;
    }

    private ImpersonationDto convertToDto(Impersonation impersonation) {
        if (impersonation == null) {
            throw new IllegalArgumentException("Impersonations have to be non empty");
        }

        return modelMapper.map(impersonation, ImpersonationDto.class);
    }
}