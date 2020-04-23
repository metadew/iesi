package io.metadew.iesi.connection.http.entity;

import io.metadew.iesi.connection.http.response.HttpResponse;
import lombok.Data;

@Data
public abstract class HttpResponseEntityStrategy {

    private final HttpResponse httpResponse;

}
