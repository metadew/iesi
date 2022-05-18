package io.metadew.iesi.server.rest.environment;

import io.metadew.iesi.server.rest.dataset.Filter;

public class EnvironmentFilter extends Filter {
    public EnvironmentFilter(EnvironmentFilterOption environmentFilterOption, String value, boolean exactMatch) {
        super(environmentFilterOption, value, exactMatch);
    }
}
