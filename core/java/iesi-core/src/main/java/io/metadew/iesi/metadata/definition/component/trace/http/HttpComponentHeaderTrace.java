package io.metadew.iesi.metadata.definition.component.trace.http;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.component.trace.ComponentTraceKey;
import lombok.Builder;
import lombok.Data;

@Data
public class HttpComponentHeaderTrace extends Metadata<HttpComponentHeaderTraceKey> {

    @Builder
    public HttpComponentHeaderTrace(HttpComponentHeaderTraceKey metadataKey, ComponentTraceKey httpComponentHeaderID, String name, String value) {
        super(metadataKey);
        this.httpComponentHeaderID = httpComponentHeaderID;
        this.name = name;
        this.value = value;
    }

    private final ComponentTraceKey httpComponentHeaderID;
    private final String name;
    private final String value;

}
