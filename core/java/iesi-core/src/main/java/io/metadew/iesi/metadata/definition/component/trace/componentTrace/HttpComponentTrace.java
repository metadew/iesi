package io.metadew.iesi.metadata.definition.component.trace.componentDesign;

import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Data;

@Data
public class HttpComponentDesignTrace extends Metadata<HttpComponentDesignTraceKey> {

    private final String connectionName;
    private final String type;
    private final String endpoint;

    public HttpComponentDesignTrace(HttpComponentDesignTraceKey metadataKey, String connectionName, String type, String endpoint) {
        super(metadataKey);
        this.connectionName = connectionName;
        this.type = type;
        this.endpoint = endpoint;
    }
}
