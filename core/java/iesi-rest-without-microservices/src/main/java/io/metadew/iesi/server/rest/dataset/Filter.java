package io.metadew.iesi.server.rest.dataset;

import lombok.Data;

@Data
public class Filter {

    private final Enum filterOption;
    private final String value;
    private final boolean exactMatch;

}
