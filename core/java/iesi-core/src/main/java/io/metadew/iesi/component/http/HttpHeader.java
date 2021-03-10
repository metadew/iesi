package io.metadew.iesi.component.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class HttpHeader {

    private final String name;
    private String value;

}
