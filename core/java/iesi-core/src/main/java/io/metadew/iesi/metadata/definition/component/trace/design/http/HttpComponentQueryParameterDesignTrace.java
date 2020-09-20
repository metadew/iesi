package io.metadew.iesi.metadata.definition.component.trace.design.http;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.component.trace.design.ComponentDesignTraceKey;
import lombok.Builder;
import lombok.Data;

@Data
public class HttpComponentQueryParameterDesignTrace extends Metadata<HttpComponentQueryParameterDesignTraceKey> {
    @Builder
    public HttpComponentQueryParameterDesignTrace(HttpComponentQueryParameterDesignTraceKey metadaKey, ComponentDesignTraceKey httpComponentQueryDesignID, String name, String value) {
        super(metadaKey);
        this.httpComponentQueryDesignID = httpComponentQueryDesignID;
        this.name = name;
        this.value = value;
    }

    private final ComponentDesignTraceKey httpComponentQueryDesignID;
    private final String name;
    private final String value;
}
