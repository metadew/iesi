package io.metadew.iesi.metadata.definition.component.trace.componentTrace;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
public class HttpComponentHeader {

    @Builder
    public HttpComponentHeader(UUID id, HttpComponentHeaderKey httpComponentHeaderID, String name, String value) {
        Id = id;
        this.httpComponentHeaderID = httpComponentHeaderID;
        this.name = name;
        this.value = value;
    }

    private final UUID Id;
    private final HttpComponentHeaderKey httpComponentHeaderID;
    private final String name;
    private final String value;

}
