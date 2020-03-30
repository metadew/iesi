package io.metadew.iesi.server.rest.controller;


import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.impersonation.ImpersonationConfiguration;
import io.metadew.iesi.metadata.definition.impersonation.Impersonation;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
import io.metadew.iesi.server.rest.error.DataBadRequestException;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import io.metadew.iesi.server.rest.resource.impersonation.dto.ImpersonationDto;
import io.metadew.iesi.server.rest.resource.impersonation.resource.ImpersonatonDtoResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static io.metadew.iesi.server.rest.helper.Filter.distinctByKey;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/impersonations")
public class ImpersonationController {

    private ImpersonationConfiguration impersonationConfiguration;
    private ImpersonatonDtoResourceAssembler impersonatonDtoResourceAssembler;

    @Autowired
    ImpersonationController(ImpersonationConfiguration impersonationConfiguration,
                            ImpersonatonDtoResourceAssembler impersonatonDtoResourceAssembler) {
        this.impersonationConfiguration = impersonationConfiguration;
        this.impersonatonDtoResourceAssembler = impersonatonDtoResourceAssembler;
    }


    @GetMapping("")
    public HalMultipleEmbeddedResource<ImpersonationDto> getAll() {
        List<Impersonation> impersonations = impersonationConfiguration.getAllImpersonations();
        return new HalMultipleEmbeddedResource<>(impersonations.stream()
                .filter(distinctByKey(Impersonation::getName))
                .map(impersonation -> impersonatonDtoResourceAssembler.toModel(impersonation))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{name}")
    public ImpersonationDto get(@PathVariable String name) throws MetadataDoesNotExistException {
        return impersonationConfiguration.getImpersonation(name)
                .map(impersonation -> impersonatonDtoResourceAssembler.toModel(impersonation))
                .orElseThrow(() -> new MetadataDoesNotExistException(new ImpersonationKey(name)));
    }

    @PostMapping("")
    public ImpersonationDto post(@Valid @RequestBody ImpersonationDto impersonationDto) throws MetadataAlreadyExistsException {
        impersonationConfiguration.insertImpersonation(impersonationDto.convertToEntity());
        return impersonatonDtoResourceAssembler.toModel(impersonationDto.convertToEntity());
    }

    @PutMapping("")
    public HalMultipleEmbeddedResource<ImpersonationDto> putAll(@Valid @RequestBody List<ImpersonationDto> impersonationDtos) throws MetadataDoesNotExistException {
        HalMultipleEmbeddedResource<ImpersonationDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
        for (ImpersonationDto impersonationDto : impersonationDtos) {
            impersonationConfiguration.updateImpersonation(impersonationDto.convertToEntity());
            halMultipleEmbeddedResource.embedResource(impersonationDto);
            halMultipleEmbeddedResource.add(linkTo(methodOn(ImpersonationController.class)
                    .get(impersonationDto.getName()))
                    .withRel(impersonationDto.getName()));
        }

        return halMultipleEmbeddedResource;
    }

    @PutMapping("/{name}")
    public ImpersonationDto put(@PathVariable String name,
                                @RequestBody ImpersonationDto impersonation) throws MetadataDoesNotExistException {
        if (!impersonation.getName().equals(name)) {
            throw new DataBadRequestException(name);
        } else if (impersonation.getName() == null) {
            throw new DataBadRequestException(name);
        }
        impersonationConfiguration.updateImpersonation(impersonation.convertToEntity());
        return impersonatonDtoResourceAssembler.toModel(impersonation.convertToEntity());

    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteAll() {
        List<Impersonation> impersonation = impersonationConfiguration.getAllImpersonations();
        if (!impersonation.isEmpty()) {
            impersonationConfiguration.deleteAllImpersonations();
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<?> delete(@PathVariable String name) throws MetadataDoesNotExistException {
        impersonationConfiguration.deleteImpersonation(new ImpersonationKey(name));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}