package io.metadew.iesi.server.rest.ressource.impersonation;


import io.metadew.iesi.metadata.definition.Impersonation;
import io.metadew.iesi.server.rest.controller.ImpersonationController;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.metadew.iesi.server.rest.helper.Filter.distinctByKey;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ImpersonationGlobalDtoResourceAssembler extends ResourceAssemblerSupport<List<Impersonation>, ImpersonationGlobalDto> {

    private final ModelMapper modelMapper;

    public ImpersonationGlobalDtoResourceAssembler() {
        super(ImpersonationController.class, ImpersonationGlobalDto.class);
        this.modelMapper = new ModelMapper();
    }

    @Override
    public ImpersonationGlobalDto toResource(List<Impersonation> impersonations) {
        ImpersonationGlobalDto impersonationGlobalDto = convertToDto(impersonations);
        impersonationGlobalDto.add(linkTo(methodOn(ImpersonationController.class)
                .getByName(impersonations.get(0).getName()))
                .withSelfRel());
        return impersonationGlobalDto;
    }

    private ImpersonationGlobalDto convertToDto(List<Impersonation> impersonations) {
        if (impersonations.isEmpty()) {
            throw new IllegalArgumentException("Cannot create Impersonation global DTO from empty list");
        }
        if (impersonations.stream().filter(distinctByKey(Impersonation::getName)).count() > 1) {
            throw new IllegalArgumentException("Cannot create Impersonation global DTO from list with multiple impersonation names");
        }
        return modelMapper.map(impersonations.get(0), ImpersonationGlobalDto.class);


    }
}