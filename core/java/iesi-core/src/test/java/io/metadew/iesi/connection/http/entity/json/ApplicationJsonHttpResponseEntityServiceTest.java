package io.metadew.iesi.connection.http.entity.json;

import io.metadew.iesi.connection.http.response.HttpResponse;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.dataset.DatasetHandler;
import io.metadew.iesi.datatypes.dataset.keyvalue.KeyValueDataset;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;
import org.junit.jupiter.api.Test;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ApplicationJsonHttpResponseEntityServiceTest {

    @Test
    void writeToDatasetTest() throws IOException {
        // Setup
        DatasetHandler datasetHandler = mock(DatasetHandler.class);
        Whitebox.setInternalState(DatasetHandler.class, "INSTANCE", datasetHandler);
        DataTypeHandler dataTypeHandler = mock(DataTypeHandler.class);
        Whitebox.setInternalState(DataTypeHandler.class, "INSTANCE", dataTypeHandler);


        HttpResponse httpResponse = mock(HttpResponse.class);
        HttpEntity httpEntity = mock(HttpEntity.class);
        when(httpResponse.getEntityContent())
                .thenReturn(Optional.of(new String("{\"test\":\"test\"}".getBytes(Consts.ASCII), Consts.ASCII).getBytes(Consts.ASCII)));
        when(httpEntity.getContentType())
                .thenReturn(new BasicHeader("Content-Type", Consts.ASCII.toString()));
        KeyValueDataset dataset = mock(KeyValueDataset.class);
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);

        when(dataTypeHandler
                .resolve(eq(dataset), eq("key"), any(), eq(executionRuntime)))
                .thenReturn(new Text("test"));
        // Test
        ApplicationJsonHttpResponseEntityService.getInstance().writeToDataset(httpResponse, dataset, "key", executionRuntime);
        verify(datasetHandler, times(1)).setDataItem(dataset, "key", new Text("test"));

        // Clean up
        Whitebox.setInternalState(DatasetHandler.class, "INSTANCE", (DatasetHandler) null);
        Whitebox.setInternalState(DataTypeHandler.class, "INSTANCE", (DataTypeHandler) null);
    }

    @Test
    void writeToDatasetNoCharsetTest() throws IOException {
        // Setup
        DatasetHandler datasetHandler = mock(DatasetHandler.class);
        Whitebox.setInternalState(DatasetHandler.class, "INSTANCE", datasetHandler);
        DataTypeHandler dataTypeHandler = mock(DataTypeHandler.class);
        Whitebox.setInternalState(DataTypeHandler.class, "INSTANCE", dataTypeHandler);


        HttpResponse httpResponse = mock(HttpResponse.class);
        when(httpResponse.getEntityContent())
                .thenReturn(Optional.of(new String("{\"test\":\"test\"}".getBytes(Consts.UTF_8), Consts.UTF_8).getBytes(Consts.UTF_8)));
        KeyValueDataset dataset = mock(KeyValueDataset.class);
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);

        when(dataTypeHandler
                .resolve(eq(dataset), eq("key"), any(), eq(executionRuntime)))
                .thenReturn(new Text("test"));
        // Test
        ApplicationJsonHttpResponseEntityService.getInstance().writeToDataset(httpResponse, dataset, "key", executionRuntime);
        verify(datasetHandler, times(1)).setDataItem(dataset, "key", new Text("test"));

        // Clean up
        Whitebox.setInternalState(DatasetHandler.class, "INSTANCE", (DatasetHandler) null);
        Whitebox.setInternalState(DataTypeHandler.class, "INSTANCE", (DataTypeHandler) null);
    }

    @Test
    void writeToDatasetInvalidJsonTest() throws IOException {
        // Setup
        DatasetHandler datasetHandler = mock(DatasetHandler.class);
        Whitebox.setInternalState(DatasetHandler.class, "INSTANCE", datasetHandler);
        DataTypeHandler dataTypeHandler = mock(DataTypeHandler.class);
        Whitebox.setInternalState(DataTypeHandler.class, "INSTANCE", dataTypeHandler);


        HttpResponse httpResponse = mock(HttpResponse.class);
        HttpEntity httpEntity = mock(HttpEntity.class);
        when(httpResponse.getEntityContent())
                .thenReturn(Optional.of(new String("".getBytes(Consts.UTF_8), Consts.UTF_8).getBytes(Consts.UTF_8)));
        when(httpEntity.getContentType())
                .thenReturn(new BasicHeader("Content-Type", Consts.UTF_8.toString()));
        KeyValueDataset dataset = mock(KeyValueDataset.class);
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);

        // Test
        ApplicationJsonHttpResponseEntityService.getInstance().writeToDataset(httpResponse, dataset, "key", executionRuntime);
        verify(dataTypeHandler, times(0)).resolve(eq(dataset), eq("key"), any(), eq(executionRuntime));
        verify(datasetHandler, times(0)).setDataItem(dataset, "key", new Text("test"));
        // Clean up
        Whitebox.setInternalState(DatasetHandler.class, "INSTANCE", (DatasetHandler) null);
        Whitebox.setInternalState(DataTypeHandler.class, "INSTANCE", (DataTypeHandler) null);
    }

}
