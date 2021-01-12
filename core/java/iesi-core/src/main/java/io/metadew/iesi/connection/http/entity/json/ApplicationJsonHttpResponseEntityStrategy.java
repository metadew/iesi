package io.metadew.iesi.connection.http.entity.json;

import io.metadew.iesi.connection.http.entity.HttpResponseEntityStrategy;
import io.metadew.iesi.connection.http.response.HttpResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ApplicationJsonHttpResponseEntityStrategy extends HttpResponseEntityStrategy {

    public ApplicationJsonHttpResponseEntityStrategy(HttpResponse httpResponse) {
        super(httpResponse);
    }
}
