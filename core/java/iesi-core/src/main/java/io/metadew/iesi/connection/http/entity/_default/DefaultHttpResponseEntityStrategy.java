package io.metadew.iesi.connection.http.entity._default;

import io.metadew.iesi.connection.http.entity.HttpResponseEntityStrategy;
import io.metadew.iesi.connection.http.response.HttpResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DefaultHttpResponseEntityStrategy extends HttpResponseEntityStrategy {

    public DefaultHttpResponseEntityStrategy(HttpResponse httpResponse) {
        super(httpResponse);
    }
}
