package io.metadew.iesi.server.rest.ressource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.hateoas.ResourceSupport;

@JsonPropertyOrder({ "_embedded", "_links" })
public class HalSingleEmbeddedResource<T extends ResourceSupport> extends ResourceSupport {

    private T embeddedResource;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("_embedded")
    public T getEmbeddedResource() {
        return embeddedResource;
    }

    public void setEmbeddedResource(T embeddedResource) {
        this.embeddedResource = embeddedResource;
    }
}
