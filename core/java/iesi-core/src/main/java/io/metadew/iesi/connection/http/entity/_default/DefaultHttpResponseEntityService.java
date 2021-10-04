package io.metadew.iesi.connection.http.entity._default;

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
        httpResponse.getEntityContent().ifPresent(s -> {
            Charset charset = Optional.ofNullable(ContentType.get(httpResponse.getHttpEntity()))
                    .map(contentType -> Optional.ofNullable(contentType.getCharset())
                            .orElse(Consts.UTF_8))
                    .orElse(Consts.UTF_8);
            log.info(MessageFormat.format("Writing http response {0} with default interpreter", new Text(new String(s, charset))));
            DatasetImplementationHandler.getInstance().setDataItem(dataset, key, new Text(new String(s, charset)));
        });
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
