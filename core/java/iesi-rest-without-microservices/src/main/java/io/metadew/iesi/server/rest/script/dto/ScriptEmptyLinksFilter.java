package io.metadew.iesi.server.rest.script.dto;

import org.springframework.hateoas.Links;

public class ScriptEmptyLinksFilter {

    @Override
    public boolean equals(Object obj) {

        if (obj == null || !(obj instanceof Links)) {
            return false;
        }
        Links links = (Links) obj;
        return links.isEmpty();
    }

}

