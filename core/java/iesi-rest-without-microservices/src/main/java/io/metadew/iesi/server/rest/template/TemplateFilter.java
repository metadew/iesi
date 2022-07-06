package io.metadew.iesi.server.rest.template;

import io.metadew.iesi.server.rest.dataset.Filter;

public class TemplateFilter extends Filter {

    public TemplateFilter(TemplateFilterOption filterOption, String value, boolean exactMatch) {
        super(filterOption, value, exactMatch);
    }
}
