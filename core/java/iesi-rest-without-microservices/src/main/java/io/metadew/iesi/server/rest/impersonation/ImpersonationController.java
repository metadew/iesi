package io.metadew.iesi.server.rest.impersonation;


import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.impersonation.Impersonation;
import io.metadew.iesi.server.rest.error.DataBadRequestException;
import io.metadew.iesi.server.rest.impersonation.dto.ImpersonationDto;
import io.metadew.iesi.server.rest.impersonation.resource.ImpersonatonDtoResourceAssembler;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "impersonations", description = "Everything about impersonations")
@RequestMapping("/impersonations")
public class ImpersonationController {

    private ImpersonationService impersonationService;
    private ImpersonatonDtoResourceAssembler impersonatonDtoResourceAssembler;

    @Autowired
    ImpersonationController(ImpersonationService impersonationService,
                            ImpersonatonDtoResourceAssembler impersonatonDtoResourceAssembler) {
        this.impersonationService = impersonationService;
        this.impersonatonDtoResourceAssembler = impersonatonDtoResourceAssembler;
    }


    @GetMapping("")
    public HalMultipleEmbeddedResource<ImpersonationDto> getAll() {
        List<Impersonation> impersonations = impersonationService.getAll();
        return new HalMultipleEmbeddedResource<>(impersonations.stream()
                .filter(distinctByKey(impersonation -> impersonation.getMetadataKey().getName()))
                .map(impersonation -> impersonatonDtoResourceAssembler.toModel(impersonation))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{name}")
    public ImpersonationDto get(@PathVariable String name) throws MetadataDoesNotExistException {
        return impersonatonDtoResourceAssembler.toModel(impersonationService.getByName(name));
    }

    @PostMapping("")
    public ImpersonationDto post(@Valid @RequestBody ImpersonationDto impersonationDto) throws MetadataAlreadyExistsException {
        impersonationService.createImpersonation(impersonationDto);
        return impersonatonDtoResourceAssembler.toModel(impersonationDto.convertToEntity());
    }

    @PutMapping("")
    public HalMultipleEmbeddedResource<ImpersonationDto> putAll(@Valid @RequestBody List<ImpersonationDto> impersonationDtos) throws MetadataDoesNotExistException {
        impersonationService.updateImpersonations(impersonationDtos);
        HalMultipleEmbeddedResource<ImpersonationDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
        for (ImpersonationDto impersonationDto : impersonationDtos) {
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
        impersonationService.updateImpersonation(impersonation);
        return impersonatonDtoResourceAssembler.toModel(impersonation.convertToEntity());

    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteAll() {
        impersonationService.deleteAll();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<?> delete(@PathVariable String name) throws MetadataDoesNotExistException {
        impersonationService.deleteByName(name);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}