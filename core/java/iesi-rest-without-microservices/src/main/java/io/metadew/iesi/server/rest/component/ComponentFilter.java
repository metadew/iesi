package io.metadew.iesi.server.rest.component;

import io.metadew.iesi.server.rest.dataset.Filter;

public class ComponentFilter extends Filter {
    public ComponentFilter(ComponentFilterOption scriptFilterOption, String value, boolean exactMatch) {
        super(scriptFilterOption, value, exactMatch);
    }
}
