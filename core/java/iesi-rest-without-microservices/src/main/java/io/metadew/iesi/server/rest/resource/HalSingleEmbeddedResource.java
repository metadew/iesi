package io.metadew.iesi.server.rest.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.hateoas.RepresentationModel;

@JsonPropertyOrder({ "_embedded", "_links" })
public class HalSingleEmbeddedResource<T extends RepresentationModel> extends RepresentationModel {

    private T embeddedResource;

    public HalSingleEmbeddedResource(T embeddedResource) {
        this.embeddedResource = embeddedResource;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("_embedded")
    public T getEmbeddedResource() {
        return embeddedResource;
    }

    public void embedResource(T embeddedResource) {
        this.embeddedResource = embeddedResource;
    }
}
