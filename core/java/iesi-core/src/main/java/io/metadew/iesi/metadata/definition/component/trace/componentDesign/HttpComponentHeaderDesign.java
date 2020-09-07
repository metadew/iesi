package io.metadew.iesi.metadata.definition.component.trace.componentDesign;

import lombok.Builder;
import lombok.Data;

@Data
public class HttpComponentHeaderDesign {

    @Builder
    public HttpComponentHeaderDesign(String id, HttpComponentDesignTraceKey httpComponentDesignID, String name, String value) {
        this.Id = id;
        this.httpComponentDesignID = httpComponentDesignID;
        this.name = name;
        this.value = value;
    }

    private final String Id;
    private final HttpComponentDesignTraceKey httpComponentDesignID;
    private final String name;
    private final String value;

}
