package io.metadew.iesi.metadata.definition.component.trace.componentDesign;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
public class HttpComponentHeaderDesign {

    @Builder
    public HttpComponentHeaderDesign(UUID id, HttpComponentDesignTraceKey httpComponentDesignID, String name, String value) {
        this.Id = id;
        this.httpComponentDesignID = httpComponentDesignID;
        this.name = name;
        this.value = value;
    }

    private final UUID Id;
    private final HttpComponentDesignTraceKey httpComponentDesignID;
    private final String name;
    private final String value;

}
