package io.metadew.iesi.metadata.definition.component.trace.componentTrace;

import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Builder;
import lombok.Data;

@Data
public class HttpComponentQueryParameterTrace extends Metadata<HttpComponentQueryParameterTraceKey> {

    @Builder
    public HttpComponentQueryParameterTrace(HttpComponentQueryParameterTraceKey metadataKey, ComponentTraceKey httpComponentQueryID, String name, String value) {
        super(metadataKey);
        this.httpComponentQueryID = httpComponentQueryID;
        this.name = name;
        this.value = value;
    }

    private final ComponentTraceKey httpComponentQueryID;
    private final String name;
    private final String value;

}
