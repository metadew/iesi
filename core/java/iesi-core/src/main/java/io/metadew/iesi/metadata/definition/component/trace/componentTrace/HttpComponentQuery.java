package io.metadew.iesi.metadata.definition.component.trace.componentTrace;

import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Builder;
import lombok.Data;

@Data
public class HttpComponentQuery {

    @Builder
    public HttpComponentQuery(String id, HttpComponentQueryKey httpComponentQueryID, String name, String value) {
        Id = id;
        this.httpComponentQueryID = httpComponentQueryID;
        this.name = name;
        this.value = value;
    }

    private final String Id;
    private final HttpComponentQueryKey httpComponentQueryID;

    private final String name;
    private final String value;

}
