package io.metadew.iesi.server.rest.executionrequest;


import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestBuilderException;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDtoModelAssembler;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import io.metadew.iesi.server.rest.script.ScriptsController;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
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

    @SuppressWarnings("unchecked")
    @GetMapping("")
    @PreAuthorize("hasPrivilege('EXECUTION_REQUESTS_READ')")
    public PagedModel<ExecutionRequestDto> getAll(Pageable pageable,
                                                  @RequestParam(required = false, name = "script") String script,
                                                  @RequestParam(required = false, name = "version") String version,
                                                  @RequestParam(required = false, name = "environment") String environment,
                                                  @RequestParam(required = false, name = "label") String labelKeyCombination) {
        List<ExecutionRequestFilter> executionRequestFilters = extractScriptFilterOptions(script, version, environment, labelKeyCombination);
        Page<ExecutionRequestDto> executionRequestDtoPage = executionRequestService.getAll(pageable, executionRequestFilters);
        if (executionRequestDtoPage.hasContent())
            return executionRequestDtoResourceAssemblerPage.toModel(executionRequestDtoPage, executionRequestDtoModelAssembler::toModel);
        return (PagedModel<ExecutionRequestDto>) executionRequestDtoResourceAssemblerPage.toEmptyModel(executionRequestDtoPage, ExecutionRequestDto.class);
    }

    private List<ExecutionRequestFilter> extractScriptFilterOptions(String name, String version, String environment, String labelKeyCombination) {
        List<ExecutionRequestFilter> executionRequestFilters = new ArrayList<>();
        if (name != null) {
            executionRequestFilters.add(new ExecutionRequestFilter(ExecutionRequestFilterOption.NAME, name, false));
        }
        if (labelKeyCombination != null) {
            executionRequestFilters.add(new ExecutionRequestFilter(ExecutionRequestFilterOption.LABEL, labelKeyCombination, false));
        }
        if (environment != null) {
            executionRequestFilters.add(new ExecutionRequestFilter(ExecutionRequestFilterOption.ENVIRONMENT, environment, false));
        }
        if (version != null) {
            executionRequestFilters.add(new ExecutionRequestFilter(ExecutionRequestFilterOption.VERSION, version, true));
        }
        return executionRequestFilters;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPrivilege('EXECUTION_REQUESTS_READ')")
    public ExecutionRequestDto getById(@PathVariable String id) {
        return executionRequestService.getById(id)
                .orElseThrow(() -> new MetadataDoesNotExistException(new ExecutionRequestKey(id)));
    }

    @PostMapping("")
    @PreAuthorize("hasPrivilege('EXECUTION_REQUESTS_WRITE')")
    public ExecutionRequestDto post(@RequestBody ExecutionRequestDto executionRequestDto) throws MetadataAlreadyExistsException {
        try {
            ExecutionRequest executionRequest = executionRequestService.createExecutionRequest(executionRequestDto);
            return executionRequestDtoModelAssembler.toModel(executionRequest);
        } catch (ExecutionRequestBuilderException e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("")
    @PreAuthorize("hasPrivilege('EXECUTION_REQUESTS_WRITE')")
    public HalMultipleEmbeddedResource<ExecutionRequestDto> putAll(@RequestBody List<ExecutionRequestDto> executionRequestDtos) throws MetadataDoesNotExistException {
        executionRequestService.updateExecutionRequests(executionRequestDtos);
        HalMultipleEmbeddedResource<ExecutionRequestDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
        for (ExecutionRequestDto executionRequestDto : executionRequestDtos) {
            halMultipleEmbeddedResource.embedResource(executionRequestDto);
            halMultipleEmbeddedResource.add(WebMvcLinkBuilder.linkTo(methodOn(ScriptsController.class)
                    .getByName(PageRequest.of(0, 20), executionRequestDto.getName(), null, ""))
                    .withRel(executionRequestDto.getName()));
        }

        return halMultipleEmbeddedResource;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPrivilege('EXECUTION_REQUESTS_WRITE')")
    public ExecutionRequestDto put(@PathVariable String id, @RequestBody ExecutionRequestDto executionRequestDto) throws MetadataDoesNotExistException {
        executionRequestService.updateExecutionRequest(executionRequestDto);
        return executionRequestDtoModelAssembler.toModel(executionRequestDto.convertToEntity());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPrivilege('EXECUTION_REQUESTS_WRITE')")
    public ResponseEntity<Object> deleteByName(@PathVariable String id) throws MetadataDoesNotExistException {
        executionRequestService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}