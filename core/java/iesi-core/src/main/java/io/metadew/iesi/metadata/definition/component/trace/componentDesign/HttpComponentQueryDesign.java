package io.metadew.iesi.metadata.definition.component.trace.componentDesign;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
public class HttpComponentQueryDesign  {
    @Builder
    public HttpComponentQueryDesign(UUID id,HttpComponentQueryDesignKey httpComponentQueryDesignID, String name, String value) {
        this.Id = id;
        this.httpComponentQueryDesignID = httpComponentQueryDesignID;
        this.name = name;
        this.value = value;
    }
    private final UUID Id;
    private final HttpComponentQueryDesignKey httpComponentQueryDesignID;
    private final String name;
    private final String value;
}
