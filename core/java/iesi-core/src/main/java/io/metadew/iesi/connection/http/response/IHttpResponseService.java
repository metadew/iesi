package io.metadew.iesi.connection.http.response;

import io.metadew.iesi.datatypes.dataset.keyvalue.KeyValueDataset;
import io.metadew.iesi.script.execution.ExecutionRuntime;

import java.io.IOException;

public interface IHttpResponseService {

    public void writeToDataset(HttpResponse httpResponse, KeyValueDataset dataset, ExecutionRuntime executionRuntime) throws IOException;

}
