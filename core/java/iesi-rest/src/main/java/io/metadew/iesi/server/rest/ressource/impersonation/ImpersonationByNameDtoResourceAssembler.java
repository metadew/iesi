package io.metadew.iesi.server.rest.ressource.impersonation;

import io.metadew.iesi.metadata.definition.Impersonation;
import io.metadew.iesi.metadata.definition.ImpersonationParameter;
import io.metadew.iesi.server.rest.controller.ImpersonationController;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ImpersonationByNameDtoResourceAssembler extends ResourceAssemblerSupport<List<Impersonation>, ImpersonationByNameDto> {

    private final ModelMapper modelMapper;

    public ImpersonationByNameDtoResourceAssembler() {
        super(ImpersonationController.class, ImpersonationByNameDto.class);
        this.modelMapper = new ModelMapper();
    }


    @Override
    public ImpersonationByNameDto toResource(List<Impersonation> impersonations) {
        ImpersonationByNameDto impersonationByNameDto = convertToDto(impersonations);
        for (ImpersonationParameter impersonation : impersonationByNameDto.getParameters()) {
            impersonationByNameDto.add(linkTo(methodOn(ImpersonationController.class).getByName(impersonationByNameDto.getName()))
                    .withRel("impersonation:"+impersonationByNameDto.getName()+"-"+impersonation));
        }
        return impersonationByNameDto;
    }

    private ImpersonationByNameDto convertToDto(List<Impersonation> impersonations) {
        if (impersonations.isEmpty()) {
            throw new IllegalArgumentException("Impersonations have to be non empty");
        }
        if (!impersonations.stream().allMatch(impersonation -> impersonation.getName().equals(impersonations.get(0).getName()))) {
            throw new IllegalArgumentException(MessageFormat.format("Impersonations ''{0}'' do not define the same name ''{1}''", impersonations, impersonations.get(0).getName()));
        }


        ImpersonationByNameDto impersonationByNameDto = modelMapper.map(impersonations.get(0), ImpersonationByNameDto.class);
//        impersonationByNameDto.setParameters(impersonations.get(0).getParameters());
        return impersonationByNameDto;
    }
}



