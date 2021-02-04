package io.metadew.iesi.server.rest.dataset;

import java.util.HashSet;
import java.util.Set;

public class DatasetFiltersBuilder {

    private String name;

    public DatasetFiltersBuilder name(String name) {
        this.name = name;
        return this;
    }

    public Set<DatasetFilter> build() {
        Set<DatasetFilter> datasetFilters = new HashSet<>();
        if (name != null) {
            datasetFilters.add(new DatasetFilter(DatasetFilterOption.NAME, name, false));
        }
        return datasetFilters;
    }

}
