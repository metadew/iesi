package io.metadew.iesi.component.http;

import io.metadew.iesi.connection.http.HttpConnection;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HttpComponent {

    private final String referenceName;
    private final Long version;
    private final String description;
    private final HttpConnection httpConnection;
    private final String endpoint;
    private final String type;
    private List<HttpHeader> headers;
    private List<HttpQueryParameter> queryParameters;
}
