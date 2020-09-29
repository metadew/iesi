package io.metadew.iesi.connection.http.entity.json;

import io.metadew.iesi.connection.http.response.HttpResponse;
import io.metadew.iesi.datatypes.dataset.DatasetHandler;
import io.metadew.iesi.datatypes.dataset.keyvalue.KeyValueDataset;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import static org.mockito.Mockito.mock;

class ApplicationJsonHttpResponseEntityServiceTest {

    @Test
    void writeToDatasetTest() {
        DatasetHandler datasetHandler = DatasetHandler.getInstance();
        DatasetHandler datasetHandlerSpy = Mockito.spy(datasetHandler);
        Whitebox.setInternalState(DatasetHandler.class, "INSTANCE", datasetHandlerSpy);

        HttpResponse httpResponse = mock(HttpResponse.class);
        KeyValueDataset dataset = mock(KeyValueDataset.class);
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);


        Whitebox.setInternalState(DatasetHandler.class, "INSTANCE", (DatasetHandler) null);
    }

}
