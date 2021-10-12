package io.metadew.iesi.server.rest.openapi;

import io.metadew.iesi.openapi.TransformResult;
import io.metadew.iesi.server.rest.openapi.dto.TransformResultDto;
import io.metadew.iesi.server.rest.openapi.dto.TransformResultDtoResourceAssembler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/openapi")
@ConditionalOnWebApplication
public class OpenAPIController {

    private final IOpenAPIService openAPIService;
    private final TransformResultDtoResourceAssembler transformResultDtoResourceAssembler;

    public OpenAPIController(OpenAPIService openAPIService, TransformResultDtoResourceAssembler transformResultDtoResourceAssembler) {
        this.openAPIService = openAPIService;
        this.transformResultDtoResourceAssembler = transformResultDtoResourceAssembler;
    }

    @PostMapping(value = "/transform", consumes = "multipart/form-data")
    public TransformResultDto post(@RequestParam(value = "file") MultipartFile multipartFile) throws IOException {
        TransformResult transformResult = openAPIService.transform(multipartFile);
        return transformResultDtoResourceAssembler.toModel(transformResult);
    }

    @PostMapping(value = "/transform", consumes = "application/json")
    public TransformResultDto post(@RequestBody String jsonObject) {
        TransformResult transformResult = openAPIService.transform(jsonObject);
        return transformResultDtoResourceAssembler.toModel(transformResult);
    }
}
