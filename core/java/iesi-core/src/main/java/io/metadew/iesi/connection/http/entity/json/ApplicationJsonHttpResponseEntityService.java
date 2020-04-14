package io.metadew.iesi.connection.http.entity.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.connection.http.entity.IHttpResponseEntityService;
import io.metadew.iesi.connection.http.response.HttpResponse;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.dataset.DatasetHandler;
import io.metadew.iesi.datatypes.dataset.keyvalue.KeyValueDataset;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import lombok.extern.log4j.Log4j2;
import org.apache.http.Consts;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class ApplicationJsonHttpResponseEntityService implements IHttpResponseEntityService<ApplicationJsonHttpResponseEntityStrategy> {

    private static ApplicationJsonHttpResponseEntityService INSTANCE;

    public synchronized static ApplicationJsonHttpResponseEntityService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ApplicationJsonHttpResponseEntityService();
        }
        return INSTANCE;
    }

    @Override
    public void writeToDataset(ApplicationJsonHttpResponseEntityStrategy applicationJsonHttpResponseEntityStrategy, KeyValueDataset dataset,
                               String key, ExecutionRuntime executionRuntime) throws IOException {
        writeToDataset(applicationJsonHttpResponseEntityStrategy.getHttpResponse(), dataset, key, executionRuntime);
    }

    @Override
    public void writeToDataset(HttpResponse httpResponse, KeyValueDataset dataset, String key, ExecutionRuntime executionRuntime) throws IOException {
        if (httpResponse.getEntityContent().isPresent()) {
            Charset charset = Optional.ofNullable(ContentType.get(httpResponse.getHttpEntity()))
                    .map(contentType -> Optional.ofNullable(contentType.getCharset())
                            .orElse(Consts.UTF_8))
                    .orElse(Consts.UTF_8);
            JsonNode jsonNode = new ObjectMapper().readTree(new String(httpResponse.getEntityContent().get(), charset));
            //DatasetHandler.getInstance().clean(dataset, executionRuntime);
            DatasetHandler.getInstance().setDataItem(dataset, key, DataTypeHandler.getInstance().resolve(dataset, key, jsonNode, executionRuntime));
        }
    }

    @Override
    public Class<ApplicationJsonHttpResponseEntityStrategy> appliesToClass() {
        return ApplicationJsonHttpResponseEntityStrategy.class;
    }

    @Override
    public List<String> appliesToContentTypes() {
        return Stream.of(ContentType.APPLICATION_JSON.getMimeType())
                .collect(Collectors.toList());
    }

}
