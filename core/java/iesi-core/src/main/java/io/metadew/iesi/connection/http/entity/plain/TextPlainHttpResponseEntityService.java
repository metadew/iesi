package io.metadew.iesi.connection.http.entity.plain;

import io.metadew.iesi.connection.http.entity.IHttpResponseEntityService;
import io.metadew.iesi.connection.http.response.HttpResponse;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.DatabaseDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.DatabaseDatasetImplementationService;
import io.metadew.iesi.datatypes.text.Text;
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
public class TextPlainHttpResponseEntityService implements IHttpResponseEntityService<TextPlainHttpResponseEntityStrategy> {

    private static TextPlainHttpResponseEntityService INSTANCE;

    public synchronized static TextPlainHttpResponseEntityService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TextPlainHttpResponseEntityService();
        }
        return INSTANCE;
    }

    @Override
    public void writeToDataset(TextPlainHttpResponseEntityStrategy textPlainHttpResponseEntityStrategy, DatabaseDatasetImplementation dataset,
                               String key, ExecutionRuntime executionRuntime) throws IOException {
        writeToDataset(textPlainHttpResponseEntityStrategy.getHttpResponse(), dataset, key, executionRuntime);
    }

    @Override
    public void writeToDataset(HttpResponse httpResponse, DatabaseDatasetImplementation dataset, String key, ExecutionRuntime executionRuntime) {
        httpResponse.getEntityContent().ifPresent(s -> {
            Charset charset = Optional.ofNullable(ContentType.get(httpResponse.getHttpEntity()))
                    .map(contentType -> Optional.ofNullable(contentType.getCharset())
                            .orElse(Consts.UTF_8))
                    .orElse(Consts.UTF_8);
            DatabaseDatasetImplementationService.getInstance().setDataItem(dataset, key, new Text(new String(s, charset)));
        });
    }

    @Override
    public Class<TextPlainHttpResponseEntityStrategy> appliesToClass() {
        return TextPlainHttpResponseEntityStrategy.class;
    }

    @Override
    public List<String> appliesToContentTypes() {
        return Stream.of(ContentType.TEXT_PLAIN.getMimeType())
                .collect(Collectors.toList());
    }

    @Override
    public void outputResponse(HttpResponse httpResponse, ActionControl actionControl) {
        httpResponse.getEntityContent().ifPresent(s -> {
            Charset charset = Optional.ofNullable(ContentType.get(httpResponse.getHttpEntity()))
                    .map(contentType -> Optional.ofNullable(contentType.getCharset())
                            .orElse(Consts.UTF_8))
                    .orElse(Consts.UTF_8);
            actionControl.logOutput("response.body", new String(s, charset));
        });
    }

}
