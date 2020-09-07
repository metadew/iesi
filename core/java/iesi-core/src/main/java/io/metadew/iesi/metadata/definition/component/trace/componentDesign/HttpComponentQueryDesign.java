package io.metadew.iesi.metadata.definition.component.trace.componentDesign;

import lombok.Builder;
import lombok.Data;

@Data
public class HttpComponentQueryDesign  {
    @Builder
    public HttpComponentQueryDesign(String id,HttpComponentQueryDesignKey httpComponentQueryDesignID, String name, String value) {
        this.Id = id;
        this.httpComponentQueryDesignID = httpComponentQueryDesignID;
        this.name = name;
        this.value = value;
    }
    private final String Id;
    private final HttpComponentQueryDesignKey httpComponentQueryDesignID;
    private final String name;
    private final String value;

}
