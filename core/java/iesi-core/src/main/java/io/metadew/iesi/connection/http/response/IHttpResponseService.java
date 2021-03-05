package io.metadew.iesi.connection.http.response;

import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementation;
import io.metadew.iesi.script.execution.ExecutionRuntime;

import java.io.IOException;

public interface IHttpResponseService {

    public void writeToDataset(HttpResponse httpResponse, InMemoryDatasetImplementation dataset, ExecutionRuntime executionRuntime) throws IOException;

}
