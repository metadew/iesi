package io.metadew.iesi.server.rest.executionrequest;


import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestBuilderException;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDtoModelAssembler;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import io.metadew.iesi.server.rest.script.ScriptController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@CrossOrigin
@Tag(name = "execution requests", description = "Everything about execution requests")
@RequestMapping("/execution-requests")
public class ExecutionRequestController {

    private final ExecutionRequestDtoModelAssembler executionRequestDtoModelAssembler;
    private final IExecutionRequestService executionRequestService;
    private final PagedResourcesAssembler<ExecutionRequestDto> executionRequestDtoResourceAssemblerPage;

    @Autowired
    ExecutionRequestController(ExecutionRequestService executionRequestService,
                               ExecutionRequestDtoModelAssembler executionRequestDtoModelAssembler,
                               PagedResourcesAssembler<ExecutionRequestDto> executionRequestDtoResourceAssemblerPage) {
        this.executionRequestService = executionRequestService;
        this.executionRequestDtoModelAssembler = executionRequestDtoModelAssembler;
        this.executionRequestDtoResourceAssemblerPage = executionRequestDtoResourceAssemblerPage;
    }

    @GetMapping("")
    public PagedModel<ExecutionRequestDto> getAll(Pageable pageable) {
        Page<ExecutionRequestDto> executionRequestDtoPage = executionRequestService.getAll(pageable);
        if (executionRequestDtoPage.hasContent())
            return executionRequestDtoResourceAssemblerPage.toModel(executionRequestDtoPage, executionRequestDtoModelAssembler::toModel);
        return (PagedModel<ExecutionRequestDto>) executionRequestDtoResourceAssemblerPage.toEmptyModel(executionRequestDtoPage, ExecutionRequestDto.class);
    }

    @GetMapping("/{id}")
    public ExecutionRequestDto getById(@PathVariable String id) {
        return executionRequestService.getById(id)
                .orElseThrow(() -> new MetadataDoesNotExistException(new ExecutionRequestKey(id)));
    }

    @PostMapping("")
    public ExecutionRequestDto post(@RequestBody ExecutionRequestDto executionRequestDto) throws MetadataAlreadyExistsException {
        try {
            ExecutionRequest executionRequest = executionRequestService.createExecutionRequest(executionRequestDto);
            return executionRequestDtoModelAssembler.toModel(executionRequest);
        } catch (ExecutionRequestBuilderException e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("")
    public HalMultipleEmbeddedResource<ExecutionRequestDto> putAll(@RequestBody List<ExecutionRequestDto> executionRequestDtos) throws MetadataDoesNotExistException {
        executionRequestService.updateExecutionRequests(executionRequestDtos);
        HalMultipleEmbeddedResource<ExecutionRequestDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
        for (ExecutionRequestDto executionRequestDto : executionRequestDtos) {
            halMultipleEmbeddedResource.embedResource(executionRequestDto);
            halMultipleEmbeddedResource.add(WebMvcLinkBuilder.linkTo(methodOn(ScriptController.class)
                    .getByName(PageRequest.of(0, 20), executionRequestDto.getName(), null, ""))
                    .withRel(executionRequestDto.getName()));
        }

        return halMultipleEmbeddedResource;
    }

    @PutMapping("/{id}")
    public ExecutionRequestDto put(@PathVariable String id, @RequestBody ExecutionRequestDto executionRequestDto) throws MetadataDoesNotExistException {
        executionRequestService.updateExecutionRequest(executionRequestDto);
        return executionRequestDtoModelAssembler.toModel(executionRequestDto.convertToEntity());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteByName(@PathVariable String id) throws MetadataDoesNotExistException {
        executionRequestService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}