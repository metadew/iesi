package io.metadew.iesi.connection.http.entity;

import io.metadew.iesi.connection.http.response.HttpResponse;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementation;
import io.metadew.iesi.script.execution.ActionControl;
import io.metadew.iesi.script.execution.ExecutionRuntime;

import java.io.IOException;
import java.util.List;

public interface IHttpResponseEntityService<T extends HttpResponseEntityStrategy> {

    public void writeToDataset(T httpResponseEntityStrategy, InMemoryDatasetImplementation dataset, String key, ExecutionRuntime executionRuntime) throws IOException;

    public void writeToDataset(HttpResponse httpResponse, InMemoryDatasetImplementation dataset, String key, ExecutionRuntime executionRuntime) throws IOException;

    public Class<T> appliesToClass();

    public List<String> appliesToContentTypes();

    public void outputResponse(HttpResponse httpResponse, ActionControl actionControl);


}
