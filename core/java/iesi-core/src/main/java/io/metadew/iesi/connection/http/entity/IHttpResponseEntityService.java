package io.metadew.iesi.connection.http.entity;

import io.metadew.iesi.connection.http.response.HttpResponse;
import io.metadew.iesi.datatypes.dataset.keyvalue.KeyValueDataset;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.util.List;

public interface IHttpResponseEntityService<T extends HttpResponseEntityStrategy> {

    public void writeToDataset(T httpResponseEntityStrategy, KeyValueDataset dataset, String key, ExecutionRuntime executionRuntime) throws IOException;

    public void writeToDataset(HttpResponse httpResponse, KeyValueDataset dataset, String key, ExecutionRuntime executionRuntime) throws IOException;

    public Class<T> appliesToClass();

    public List<String> appliesToContentTypes();

}
