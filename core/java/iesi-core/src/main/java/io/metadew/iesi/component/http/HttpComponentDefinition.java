package io.metadew.iesi.component.http;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HttpComponentDefinition {


    private final String referenceName;
    private final Long version;
    private final String description;
    private final String httpConnectionReferenceName;
    private final String endpoint;
    private final String type;
    private List<String> headers;
    private List<String> queryParameters;
}
