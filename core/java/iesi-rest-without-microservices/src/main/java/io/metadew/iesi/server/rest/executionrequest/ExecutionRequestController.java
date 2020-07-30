package io.metadew.iesi.server.rest.executionrequest;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestBuilderException;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDtoRepository;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDtoResourceAssembler;
import io.metadew.iesi.server.rest.executionrequest.dto.TotalPages;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import io.metadew.iesi.server.rest.resource.HalSingleEmbeddedResource;
import io.metadew.iesi.server.rest.script.ScriptController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@CrossOrigin
@Tag(name = "execution requests", description = "Everything about execution requests")
@RequestMapping("/execution-requests")
public class ExecutionRequestController {

    private final ExecutionRequestDtoResourceAssembler executionRequestDtoResourceAssembler;
    private final ExecutionRequestService executionRequestService;

    @Autowired
    ExecutionRequestController(ExecutionRequestService executionRequestService,
                               ExecutionRequestDtoResourceAssembler executionRequestDtoResourceAssembler) {
        this.executionRequestService = executionRequestService;
        this.executionRequestDtoResourceAssembler = executionRequestDtoResourceAssembler;
    }

    @GetMapping("")
    public HalSingleEmbeddedResource<TotalPages> getAll(
            @RequestParam int limit,
            @RequestParam int pageNumber,
            @RequestParam(required = false) List<String> column,
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false) String filterColumn,
            @RequestParam(required = false) String searchParam,
            @RequestParam(required = false) String request_from,
            @RequestParam(required = false) String request_to) {
        List<ExecutionRequestDto> executionRequestDtos = executionRequestService.getAll(limit, pageNumber, column, sort, filterColumn, searchParam, request_from, request_to)
                .stream()
                .parallel()
                .map(executionRequestDtoResourceAssembler::toModel)
                .collect(Collectors.toList());
        TotalPages totalPages = TotalPages.builder()
                .totalPages(executionRequestService.getTotalPages(limit, filterColumn, searchParam, request_from, request_to))
                .payload(executionRequestDtos)
                .build();
        return new HalSingleEmbeddedResource<>(totalPages);
    }

    @GetMapping("/{id}")
    public ExecutionRequestDto getById(@PathVariable String id) {
        return executionRequestService.getById(id)
                .map(executionRequestDtoResourceAssembler::toModel)
                .orElseThrow(() -> new MetadataDoesNotExistException(new ExecutionRequestKey(id)));
    }

    @PostMapping("")
    public ExecutionRequestDto post(@RequestBody ExecutionRequestDto executionRequestDto) throws MetadataAlreadyExistsException {
        try {
            ExecutionRequest executionRequest = executionRequestService.createExecutionRequest(executionRequestDto);
            return executionRequestDtoResourceAssembler.toModel(executionRequest);
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
                    .getByName(executionRequestDto.getName(), null))
                    .withRel(executionRequestDto.getName()));
        }

        return halMultipleEmbeddedResource;
    }

    @PutMapping("/{id}")
    public ExecutionRequestDto put(@PathVariable String id, @RequestBody ExecutionRequestDto executionRequestDto) throws MetadataDoesNotExistException {
        executionRequestService.updateExecutionRequest(executionRequestDto);
        return executionRequestDtoResourceAssembler.toModel(executionRequestDto.convertToEntity());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteByName(@PathVariable String id) throws MetadataDoesNotExistException {
        executionRequestService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}