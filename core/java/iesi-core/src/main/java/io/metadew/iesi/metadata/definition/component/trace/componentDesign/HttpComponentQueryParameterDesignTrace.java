package io.metadew.iesi.metadata.definition.component.trace.componentDesign;

import io.metadew.iesi.metadata.definition.Metadata;
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
