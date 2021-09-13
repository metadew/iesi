package io.metadew.iesi.connection.http.entity;

import io.metadew.iesi.connection.http.response.HttpResponse;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.DatabaseDatasetImplementation;
import io.metadew.iesi.script.execution.ActionControl;
import io.metadew.iesi.script.execution.ExecutionRuntime;

import java.io.IOException;

public interface IHttpResponseEntityHandler {

    public void writeToDataset(HttpResponseEntityStrategy httpResponseEntityStrategy, DatabaseDatasetImplementation dataset, String key, ExecutionRuntime executionRuntime) throws IOException;

    public void writeToDataset(HttpResponse httpResponse, DatabaseDatasetImplementation dataset, String key, ExecutionRuntime executionRuntime) throws IOException;

    public void traceOutput(HttpResponse httpResponse, ActionControl actionControl);

}
