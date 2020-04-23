package io.metadew.iesi.connection.http.entity;

import io.metadew.iesi.connection.http.entity.json.ApplicationJsonHttpResponseEntityService;
import io.metadew.iesi.connection.http.entity.plain.TextPlainHttpResponseEntityService;
import io.metadew.iesi.connection.http.entity.plain.TextPlainHttpResponseEntityStrategy;
import io.metadew.iesi.connection.http.response.HttpResponse;
import io.metadew.iesi.datatypes.dataset.keyvalue.KeyValueDataset;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class HttpResponseEntityHandler implements IHttpResponseEntityHandler {

    private static HttpResponseEntityHandler INSTANCE;
    private final List<IHttpResponseEntityService> httpResponseEntityServices;

    public synchronized static HttpResponseEntityHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HttpResponseEntityHandler();
        }
        return INSTANCE;
    }

    private HttpResponseEntityHandler() {
        httpResponseEntityServices = new ArrayList<>();
        httpResponseEntityServices.add(ApplicationJsonHttpResponseEntityService.getInstance());
        httpResponseEntityServices.add(TextPlainHttpResponseEntityService.getInstance());
    }

    @SuppressWarnings("unchecked")
    public void writeToDataset(HttpResponseEntityStrategy httpResponseEntityStrategy, KeyValueDataset dataset, String key, ExecutionRuntime executionRuntime) throws IOException {
        getHttpResponseEntityService(httpResponseEntityStrategy).writeToDataset(httpResponseEntityStrategy, dataset, key, executionRuntime);
    }

    @Override
    public void writeToDataset(HttpResponse httpResponse, KeyValueDataset dataset, String key, ExecutionRuntime executionRuntime) throws IOException {
        if (httpResponse.getHeaders().stream()
                .filter(header -> header.getName().equals(HttpHeaders.CONTENT_TYPE))
                .count() > 1) {
            log.warn("content-type: " + MessageFormat.format("Http response contains multiple headers ({0}) defining the content type", httpResponse.getHeaders().stream()
                    .filter(header -> header.getName().equals(HttpHeaders.CONTENT_TYPE))
                    .count()));
        }
        ContentType contentType = ContentType.get(httpResponse.getHttpEntity());
        if (contentType == null) {
            log.warn("content-type: Http response contains no header defining the content type. Assuming text/plain");
            TextPlainHttpResponseEntityService.getInstance().writeToDataset(new TextPlainHttpResponseEntityStrategy(httpResponse), dataset, key, executionRuntime);
        }
        getHttpResponseEntityService(contentType).writeToDataset(httpResponse, dataset, key, executionRuntime);
    }

    private IHttpResponseEntityService getHttpResponseEntityService(ContentType contentType) {
        return httpResponseEntityServices.stream()
                .filter(iHttpResponseEntityService -> iHttpResponseEntityService.appliesToContentTypes().contains(contentType.getMimeType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unsupported content-type '" + contentType.getMimeType() + "' for http response"));
    }

    private IHttpResponseEntityService getHttpResponseEntityService(HttpResponseEntityStrategy responseEntityStrategy) {
        return httpResponseEntityServices.stream()
                .filter(iHttpResponseEntityService -> iHttpResponseEntityService.appliesToClass().equals(responseEntityStrategy.getClass()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unsupported HttpResponseEntityStrategy '" + httpResponseEntityServices.getClass().getSimpleName() + "' for http response"));
    }

//    public Optional<String> getEntityString(HttpEntity entity) throws IOException {
//        if (entity != null) {
//            String entityString = EntityUtils.toString(entity);
//            EntityUtils.consume(entity);
//            return Optional.ofNullable(entityString);
//        } else {
//            return Optional.empty();
//        }
//    }

}
