package io.metadew.iesi.connection.http.entity;

import io.metadew.iesi.connection.http.entity._default.DefaultHttpResponseEntityService;
import io.metadew.iesi.connection.http.entity._default.DefaultHttpResponseEntityStrategy;
import io.metadew.iesi.connection.http.entity.json.ApplicationJsonHttpResponseEntityService;
import io.metadew.iesi.connection.http.entity.plain.TextPlainHttpResponseEntityService;
import io.metadew.iesi.connection.http.response.HttpResponse;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.script.execution.ActionControl;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class HttpResponseEntityHandler implements IHttpResponseEntityHandler {

    private static HttpResponseEntityHandler instance;
    private final List<IHttpResponseEntityService> httpResponseEntityServices;

    private HttpResponseEntityHandler() {
        httpResponseEntityServices = new ArrayList<>();
        httpResponseEntityServices.add(ApplicationJsonHttpResponseEntityService.getInstance());
        httpResponseEntityServices.add(TextPlainHttpResponseEntityService.getInstance());
        httpResponseEntityServices.add(DefaultHttpResponseEntityService.getInstance());
    }

    public static synchronized HttpResponseEntityHandler getInstance() {
        if (instance == null) {
            instance = new HttpResponseEntityHandler();
        }
        return instance;
    }

    @Override
    public void writeToDataset(HttpResponseEntityStrategy httpResponseEntityStrategy, DatasetImplementation dataset, String key, ExecutionRuntime executionRuntime) throws IOException {
        getHttpResponseEntityService(httpResponseEntityStrategy).writeToDataset(httpResponseEntityStrategy, dataset, key, executionRuntime);
    }

    @Override
    public void writeToDataset(HttpResponse httpResponse, DatasetImplementation dataset, String key, ExecutionRuntime executionRuntime) throws IOException {
        if (httpResponse.getHeaders().stream()
                .filter(header -> header.getName().equals(HttpHeaders.CONTENT_TYPE))
                .count() > 1) {
            log.warn("content-type: " + MessageFormat.format("Http response contains multiple headers ({0}) defining the content type", httpResponse.getHeaders().stream()
                    .filter(header -> header.getName().equals(HttpHeaders.CONTENT_TYPE))
                    .count()));
        }
        ContentType contentType = ContentType.get(httpResponse.getHttpEntity());
        if (contentType == null) {
            log.warn("content-type: Http response contains no header defining the content type.");
            DefaultHttpResponseEntityService.getInstance().writeToDataset(new DefaultHttpResponseEntityStrategy(httpResponse), dataset, key, executionRuntime);
        } else {
            getHttpResponseEntityService(contentType).writeToDataset(httpResponse, dataset, key, executionRuntime);
        }
    }

    @Override
    public void traceOutput(HttpResponse httpResponse, ActionControl actionControl) {
        if (httpResponse.getHeaders().stream()
                .filter(header -> header.getName().equals(HttpHeaders.CONTENT_TYPE))
                .count() > 1) {
            log.warn("content-type: " + MessageFormat.format("Http response contains multiple headers ({0}) defining the content type", httpResponse.getHeaders().stream()
                    .filter(header -> header.getName().equals(HttpHeaders.CONTENT_TYPE))
                    .count()));
        }
        ContentType contentType = ContentType.get(httpResponse.getHttpEntity());
        if (contentType == null) {
            log.warn("content-type: Http response contains no header defining the content type.");
            DefaultHttpResponseEntityService.getInstance().outputResponse(httpResponse, actionControl);
        } else {
            getHttpResponseEntityService(contentType).outputResponse(httpResponse, actionControl);
        }
    }

    private IHttpResponseEntityService getHttpResponseEntityService(ContentType contentType) {
        return httpResponseEntityServices.stream()
                .filter(iHttpResponseEntityService -> iHttpResponseEntityService.appliesToContentTypes().contains(contentType.getMimeType()))
                .findFirst()
                .orElseGet(DefaultHttpResponseEntityService::getInstance);
    }

    private IHttpResponseEntityService getHttpResponseEntityService(HttpResponseEntityStrategy responseEntityStrategy) {
        return httpResponseEntityServices.stream()
                .filter(iHttpResponseEntityService -> iHttpResponseEntityService.appliesToClass().equals(responseEntityStrategy.getClass()))
                .findFirst()
                .orElseGet(DefaultHttpResponseEntityService::getInstance);
    }

}
