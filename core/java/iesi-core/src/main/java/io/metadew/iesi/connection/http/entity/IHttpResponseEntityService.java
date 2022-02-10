package io.metadew.iesi.connection.http.entity;

import io.metadew.iesi.connection.http.response.HttpResponse;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.script.execution.ActionControl;
import io.metadew.iesi.script.execution.ExecutionRuntime;

import java.io.IOException;
import java.util.List;

public interface IHttpResponseEntityService<T extends HttpResponseEntityStrategy> {

    void writeToDataset(T httpResponseEntityStrategy, DatasetImplementation dataset, String key, ExecutionRuntime executionRuntime) throws IOException;

    void writeToDataset(HttpResponse httpResponse, DatasetImplementation dataset, String key, ExecutionRuntime executionRuntime) throws IOException;

    Class<T> appliesToClass();

    List<String> appliesToContentTypes();

    void outputResponse(HttpResponse httpResponse, ActionControl actionControl);

}
