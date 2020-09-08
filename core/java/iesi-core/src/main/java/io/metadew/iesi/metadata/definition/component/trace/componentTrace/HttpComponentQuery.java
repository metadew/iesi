package io.metadew.iesi.metadata.definition.component.trace.componentTrace;

import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
public class HttpComponentQuery {

    @Builder
    public HttpComponentQuery(UUID id, HttpComponentQueryKey httpComponentQueryID, String name, String value) {
        Id = id;
        this.httpComponentQueryID = httpComponentQueryID;
        this.name = name;
        this.value = value;
    }

    private final UUID Id;
    private final HttpComponentQueryKey httpComponentQueryID;

    private final String name;
    private final String value;

}
