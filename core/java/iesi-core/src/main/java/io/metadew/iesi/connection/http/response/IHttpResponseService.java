package io.metadew.iesi.connection.http.response;

import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.script.execution.ExecutionRuntime;

import java.io.IOException;

public interface IHttpResponseService {

    void writeToDataset(HttpResponse httpResponse, DatasetImplementation dataset, ExecutionRuntime executionRuntime) throws IOException;

}
