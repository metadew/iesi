package io.metadew.iesi.connection.http.response;

import io.metadew.iesi.connection.http.entity.HttpResponseEntityHandler;
import io.metadew.iesi.connection.http.entity._default.DefaultHttpResponseEntityService;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationService;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ActionControl;
import io.metadew.iesi.script.execution.ExecutionRuntime;

import java.io.IOException;
import java.util.stream.Collectors;


public class HttpResponseService implements IHttpResponseService {

    private static HttpResponseService instance;

    public static synchronized HttpResponseService getInstance() {
        if (instance == null) {
            instance = new HttpResponseService();
        }
        return instance;
    }

    public void writeToDataset(HttpResponse httpResponse, InMemoryDatasetImplementation dataset, ExecutionRuntime executionRuntime) throws IOException {
        InMemoryDatasetImplementationService.getInstance().setDataItem(dataset, "protocol", new Text(httpResponse.getProtocolVersion().getProtocol()));
        InMemoryDatasetImplementationService.getInstance().setDataItem(dataset, "protocol.version.major", new Text(String.valueOf(httpResponse.getProtocolVersion().getMajor())));
        InMemoryDatasetImplementationService.getInstance().setDataItem(dataset, "protocol.version.minor", new Text(String.valueOf(httpResponse.getProtocolVersion().getMinor())));
        InMemoryDatasetImplementationService.getInstance().setDataItem(dataset, "status.code", new Text(String.valueOf(httpResponse.getStatusLine().getStatusCode())));
        InMemoryDatasetImplementationService.getInstance().setDataItem(dataset, "status.reason", new Text(String.valueOf(httpResponse.getStatusLine().getReasonPhrase())));
        InMemoryDatasetImplementationService.getInstance().setDataItem(dataset, "headers", new Array(
                httpResponse.getHeaders().stream()
                        .map(header -> new Text(header.getName() + ":" + header.getValue()))
                        .collect(Collectors.toList())));
        HttpResponseEntityHandler.getInstance().writeToDataset(httpResponse, dataset, "body", executionRuntime);
        DefaultHttpResponseEntityService.getInstance().writeToDataset(httpResponse, dataset, "rawbody", executionRuntime);
    }

    public void traceOutput(HttpResponse httpResponse, ActionControl actionControl) {
        actionControl.logOutput("response.protocol", httpResponse.getProtocolVersion().getProtocol());
        actionControl.logOutput("response.protocol.version.major", String.valueOf(httpResponse.getProtocolVersion().getMajor()));
        actionControl.logOutput("response.protocol.version.minor", String.valueOf(httpResponse.getProtocolVersion().getMinor()));
        actionControl.logOutput("response.status.code", String.valueOf(httpResponse.getStatusLine().getStatusCode()));
        actionControl.logOutput("response.status.reason", String.valueOf(httpResponse.getStatusLine().getReasonPhrase()));
        for (int index = 0; index < httpResponse.getHeaders().size(); index++) {
            actionControl.logOutput("response.header." + index, httpResponse.getHeaders().get(index).getName() + ":" + httpResponse.getHeaders().get(index).getValue());
        }
        HttpResponseEntityHandler.getInstance().traceOutput(httpResponse, actionControl);
    }

}
