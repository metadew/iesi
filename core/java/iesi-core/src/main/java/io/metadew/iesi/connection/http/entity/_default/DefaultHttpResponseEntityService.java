package io.metadew.iesi.connection.http.entity._default;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import io.metadew.iesi.connection.http.entity.IHttpResponseEntityService;
import io.metadew.iesi.connection.http.response.HttpResponse;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationHandler;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ActionControl;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import lombok.extern.log4j.Log4j2;
import org.apache.http.Consts;
import org.apache.http.entity.ContentType;

import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Log4j2
public class DefaultHttpResponseEntityService implements IHttpResponseEntityService<DefaultHttpResponseEntityStrategy> {

    private static DefaultHttpResponseEntityService instance;

    public static synchronized DefaultHttpResponseEntityService getInstance() {
        if (instance == null) {
            instance = new DefaultHttpResponseEntityService();
        }
        return instance;
    }

    @Override
    public void writeToDataset(DefaultHttpResponseEntityStrategy textPlainHttpResponseEntityStrategy, DatasetImplementation dataset,
                               String key, ExecutionRuntime executionRuntime) {
        writeToDataset(textPlainHttpResponseEntityStrategy.getHttpResponse(), dataset, key, executionRuntime);
    }

    @Override
    public void writeToDataset(HttpResponse httpResponse, DatasetImplementation dataset, String key, ExecutionRuntime executionRuntime) {
        if (httpResponse.getEntityContent().isPresent()) {
            Charset charset = Optional.ofNullable(ContentType.get(httpResponse.getHttpEntity()))
                    .map(contentType -> Optional.ofNullable(contentType.getCharset())
                            .orElse(Consts.UTF_8))
                    .orElse(Consts.UTF_8);

            String content = new String(httpResponse.getEntityContent().get(), charset);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
            objectMapper.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);

            log.info(MessageFormat.format("Writing http response {0} with default interpreter", new Text(content)));

            try {

                JsonNode jsonNode = objectMapper.readTree(content);
                if (jsonNode.getNodeType().equals(JsonNodeType.MISSING)) {
                    log.warn("response does not contain a valid JSON message: " + jsonNode.toPrettyString() + ". ");
                } else {
                    DatasetImplementationHandler.getInstance().setDataItem(dataset, key, new Text(jsonNode.toPrettyString()));
                }
            } catch (JsonProcessingException e) {
                DatasetImplementationHandler.getInstance().setDataItem(dataset, key, new Text(content));
            }
        }
    }

    @Override
    public Class<DefaultHttpResponseEntityStrategy> appliesToClass() {
        return DefaultHttpResponseEntityStrategy.class;
    }

    @Override
    public List<String> appliesToContentTypes() {
        return Stream.of(ContentType.WILDCARD.getMimeType())
                .collect(Collectors.toList());
    }

    @Override
    public void outputResponse(HttpResponse httpResponse, ActionControl actionControl) {
        httpResponse.getEntityContent().ifPresent(s -> {
            Charset charset = Optional.ofNullable(ContentType.get(httpResponse.getHttpEntity()))
                    .map(contentType -> Optional.ofNullable(contentType.getCharset())
                            .orElse(Consts.UTF_8))
                    .orElse(Consts.UTF_8);
            log.warn(MessageFormat.format("outputting http response {0} with default interpreter", new String(s, charset)));
            actionControl.logOutput("response.body", new String(s, charset));
        });

    }

}
