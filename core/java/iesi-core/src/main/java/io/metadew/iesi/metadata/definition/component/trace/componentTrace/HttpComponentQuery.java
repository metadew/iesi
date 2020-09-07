package io.metadew.iesi.metadata.definition.component.trace.componentDesign;

import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Data;

@Data
public class HttpComponentQueryDesign extends Metadata<HttpComponentQueryDesignKey> {

    private final String httpComponentQueryDesignID;
    private final String name;
    private final String value;

    public HttpComponentQueryDesign(HttpComponentQueryDesignKey metadataKey, String httpComponentQueryDesignID, String name, String value) {
        super(metadataKey);
        this.httpComponentQueryDesignID = httpComponentQueryDesignID;
        this.name = name;
        this.value = value;
    }
}
