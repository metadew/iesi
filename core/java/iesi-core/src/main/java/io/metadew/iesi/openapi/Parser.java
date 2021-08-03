package io.metadew.iesi.openapi;

import io.swagger.v3.oas.models.OpenAPI;

import java.util.List;

public interface Parser<T> {

    List<T> parse(OpenAPI openAPI);
}
