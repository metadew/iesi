package io.metadew.iesi.connection.http.entity.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.NullNode;
import io.metadew.iesi.SpringContext;
import io.metadew.iesi.connection.http.entity.IHttpResponseEntityService;
import io.metadew.iesi.connection.http.response.HttpResponse;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationHandler;
import io.metadew.iesi.script.execution.ActionControl;
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

    private static ApplicationJsonHttpResponseEntityService instance;

    private final ObjectMapper objectMapper;

    public static synchronized ApplicationJsonHttpResponseEntityService getInstance() {
        if (instance == null) {
            instance = new ApplicationJsonHttpResponseEntityService();
        }
        return instance;
    }

    private ApplicationJsonHttpResponseEntityService() {
        this.objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        objectMapper.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
    }

    @Override
    public void writeToDataset(ApplicationJsonHttpResponseEntityStrategy applicationJsonHttpResponseEntityStrategy, DatasetImplementation dataset,
                               String key, ExecutionRuntime executionRuntime) throws IOException {
        writeToDataset(applicationJsonHttpResponseEntityStrategy.getHttpResponse(), dataset, key, executionRuntime);
    }

    @Override
    public void writeToDataset(HttpResponse httpResponse, DatasetImplementation dataset, String key, ExecutionRuntime executionRuntime) throws IOException {
        if (httpResponse.getEntityContent().isPresent()) {
            Charset charset = Optional.ofNullable(ContentType.get(httpResponse.getHttpEntity()))
                    .map(contentType -> Optional.ofNullable(contentType.getCharset())
                            .orElse(Consts.UTF_8))
                    .orElse(Consts.UTF_8);

            String jsonContent = new String(httpResponse.getEntityContent().get(), charset);
            JsonNode jsonNode;

            try {
                jsonNode = objectMapper.readTree(jsonContent);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e.getMessage());
            }

            if (jsonNode.getNodeType().equals(JsonNodeType.MISSING)) {
                log.warn("response does not contain a valid JSON message: " + jsonNode.toPrettyString() + ". ");
            } else {
                DatasetImplementationHandler.getInstance().setDataItem(dataset, key, SpringContext.getBean(DataTypeHandler.class).resolve(dataset, key, jsonNode, executionRuntime));
            }
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

    @Override
    public void outputResponse(HttpResponse httpResponse, ActionControl actionControl) {
        httpResponse.getEntityContent().ifPresent(s -> {
            Charset charset = Optional.ofNullable(ContentType.get(httpResponse.getHttpEntity()))
                    .map(contentType -> Optional.ofNullable(contentType.getCharset())
                            .orElse(Consts.UTF_8))
                    .orElse(Consts.UTF_8);

            JsonNode jsonNode = NullNode.getInstance();
            try {
                jsonNode = objectMapper.readTree(new String(s, charset));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            actionControl.logOutput("response.body", jsonNode.toPrettyString());
        });
    }

}
