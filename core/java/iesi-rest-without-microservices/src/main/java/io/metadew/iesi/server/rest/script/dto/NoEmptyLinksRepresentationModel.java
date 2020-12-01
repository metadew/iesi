package io.metadew.iesi.server.rest.script.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.lang.NonNull;

public class NoEmptyLinksRepresentationModel<T extends NoEmptyLinksRepresentationModel<? extends T>> extends RepresentationModel<T> {


    @JsonProperty("_links")
    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = NonEmptyLinksFilter.class)
    @NonNull
    @Override
    public Links getLinks() {
        return super.getLinks();
    }

    static class NonEmptyLinksFilter {

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Links)) {
                return false;
            }
            Links links = (Links) obj;
            return links.isEmpty();
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
}
