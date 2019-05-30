package io.metadew.iesi.server.rest.ressource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.hateoas.ResourceSupport;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"_embedded", "_links"})
public class HalMultipleEmbeddedResource<T extends ResourceSupport> extends ResourceSupport {

    private List<T> embeddedResources = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("_embedded")
    public List<T> getEmbeddedResources() {
        return embeddedResources;
    }

    public void embedResource(T embeddedResource) {
        embeddedResources.add(embeddedResource);
    }
}
