package io.metadew.iesi.server.rest.controller;


import io.metadew.iesi.metadata.configuration.exception.ImpersonationAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ImpersonationDoesNotExistException;
import io.metadew.iesi.metadata.configuration.impersonation.ImpersonationConfiguration;
import io.metadew.iesi.metadata.definition.impersonation.Impersonation;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
import io.metadew.iesi.server.rest.error.DataBadRequestException;
import io.metadew.iesi.server.rest.error.DataNotFoundException;
import io.metadew.iesi.server.rest.pagination.ImpersonationCriteria;
import io.metadew.iesi.server.rest.pagination.ImpersonationPagination;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import io.metadew.iesi.server.rest.resource.impersonation.dto.ImpersonationDto;
import io.metadew.iesi.server.rest.resource.impersonation.resource.ImpersonatonDtoResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static io.metadew.iesi.server.rest.helper.Filter.distinctByKey;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/impersonations")
public class ImpersonationController {

    private ImpersonationConfiguration impersonationConfiguration;
    private final ImpersonationPagination impersonationPagination;
    private ImpersonatonDtoResourceAssembler impersonatonDtoResourceAssembler;

    @Autowired
    ImpersonationController(ImpersonationConfiguration impersonationConfiguration, ImpersonationPagination impersonationPagination,
                            ImpersonatonDtoResourceAssembler impersonatonDtoResourceAssembler) {
        this.impersonationConfiguration = impersonationConfiguration;
        this.impersonationPagination = impersonationPagination;
        this.impersonatonDtoResourceAssembler = impersonatonDtoResourceAssembler;
    }


    @GetMapping("")
    public HalMultipleEmbeddedResource<ImpersonationDto> getAll(@Valid ImpersonationCriteria impersonationCriteria) {
        List<Impersonation> impersonations = impersonationConfiguration.getAllImpersonations();
        List<Impersonation> pagination = impersonationPagination.search(impersonations, impersonationCriteria);
        return new HalMultipleEmbeddedResource<>(pagination.stream()
                .filter(distinctByKey(Impersonation::getName))
                .map(impersonation -> impersonatonDtoResourceAssembler.toResource(impersonation))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{name}")
    public ImpersonationDto get(@PathVariable String name) {

        return impersonationConfiguration.getImpersonation(name)
                .map(impersonation -> impersonatonDtoResourceAssembler.toResource(impersonation))
                .orElseThrow(() -> new DataNotFoundException(name));
    }

    @PostMapping("")
    public ImpersonationDto post(@Valid @RequestBody ImpersonationDto impersonationDto) {
        try {
            impersonationConfiguration.insertImpersonation(impersonationDto.convertToEntity());
            return impersonatonDtoResourceAssembler.toResource(impersonationDto.convertToEntity());
        } catch (ImpersonationAlreadyExistsException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Impersonation " + impersonationDto.getName() + " already exists");
        }
    }

    @PutMapping("")
    public HalMultipleEmbeddedResource<ImpersonationDto> putAll(@Valid @RequestBody List<ImpersonationDto> impersonationDtos) {
        HalMultipleEmbeddedResource<ImpersonationDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
        for (ImpersonationDto impersonationDto : impersonationDtos) {
            try {
                impersonationConfiguration.updateImpersonation(impersonationDto.convertToEntity());
                halMultipleEmbeddedResource.embedResource(impersonationDto);
                halMultipleEmbeddedResource.add(linkTo(methodOn(ImpersonationController.class)
                        .get(impersonationDto.getName()))
                        .withRel(impersonationDto.getName()));
            } catch (ImpersonationDoesNotExistException e) {
                e.printStackTrace();
                throw new DataNotFoundException(impersonationDto.getName());
            }
        }

        return halMultipleEmbeddedResource;
    }

    @PutMapping("/{name}")
    public ImpersonationDto put(@PathVariable String name,
                                @RequestBody ImpersonationDto impersonation) {
        if (!impersonation.getName().equals(name)) {
            throw new DataNotFoundException(name);
        } else if (impersonation.getName() == null) {
            throw new DataBadRequestException(name);
        }
        try {
            impersonationConfiguration.updateImpersonation(impersonation.convertToEntity());
            return impersonatonDtoResourceAssembler.toResource(impersonation.convertToEntity());
        } catch (ImpersonationDoesNotExistException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

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
    public ResponseEntity<?> delete(@PathVariable String name) {
        try {
            impersonationConfiguration.deleteImpersonation(new ImpersonationKey(name));
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (ImpersonationDoesNotExistException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}