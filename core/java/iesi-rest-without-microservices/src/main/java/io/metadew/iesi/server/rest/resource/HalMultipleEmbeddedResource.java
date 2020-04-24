package io.metadew.iesi.server.rest.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.hateoas.RepresentationModel;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"_embedded", "_links"})
public class HalMultipleEmbeddedResource<T extends RepresentationModel> extends RepresentationModel {

    private List<T> embeddedResources;

    public HalMultipleEmbeddedResource (List<T> embeddedResources) {
        this.embeddedResources = embeddedResources;
    }

    public HalMultipleEmbeddedResource () {
        this.embeddedResources = new ArrayList<>();
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("_embedded")
    public List<T> getEmbeddedResources() {
        return embeddedResources;
    }

    public void embedResource(T embeddedResource) {
        embeddedResources.add(embeddedResource);
    }
}
