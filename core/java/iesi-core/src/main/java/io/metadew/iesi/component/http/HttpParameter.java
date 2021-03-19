package io.metadew.iesi.component.http;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HttpParameter {
    protected final String name;
    protected final String value;
}
