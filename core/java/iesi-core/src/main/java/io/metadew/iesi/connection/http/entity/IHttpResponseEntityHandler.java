package io.metadew.iesi.connection.http.entity;

import io.metadew.iesi.connection.http.response.HttpResponse;
import io.metadew.iesi.datatypes.dataset.keyvalue.KeyValueDataset;
import io.metadew.iesi.script.execution.ExecutionRuntime;

import java.io.IOException;

public interface IHttpResponseEntityHandler {

    public void writeToDataset(HttpResponseEntityStrategy httpResponseEntityStrategy, KeyValueDataset dataset, String key, ExecutionRuntime executionRuntime) throws IOException;

    public void writeToDataset(HttpResponse httpResponse, KeyValueDataset dataset, String key, ExecutionRuntime executionRuntime) throws IOException;

}
