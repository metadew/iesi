package io.metadew.iesi.metadata.definition.component.trace.design.http;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.component.trace.design.ComponentDesignTraceKey;
import lombok.Builder;
import lombok.Data;

@Data
public class HttpComponentHeaderDesignTrace extends Metadata<HttpComponentHeaderDesignTraceKey> {

    @Builder
    public HttpComponentHeaderDesignTrace(HttpComponentHeaderDesignTraceKey metadataKey, ComponentDesignTraceKey httpComponentDesignID, String name, String value) {
        super(metadataKey);
        this.httpComponentDesignID = httpComponentDesignID;
        this.name = name;
        this.value = value;
    }

    private final ComponentDesignTraceKey httpComponentDesignID;
    private final String name;
    private final String value;

}
