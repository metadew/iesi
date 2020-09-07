package io.metadew.iesi.metadata.definition.component.trace;

import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Data;

@Data
public class HttpComponentHeaderDesign extends Metadata<HttpComponentHeaderDesignKey> {

    private final String httpComponentHeaderDesignID;
    private final String name;
    private final String value;

    public HttpComponentHeaderDesign(HttpComponentHeaderDesignKey metadataKey, String httpComponentHeaderDesignID, String name, String value) {
        super(metadataKey);
        this.httpComponentHeaderDesignID = httpComponentHeaderDesignID;
        this.name = name;
        this.value = value;
    }
}
