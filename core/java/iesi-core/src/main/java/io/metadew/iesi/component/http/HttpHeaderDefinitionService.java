package io.metadew.iesi.component.http;

import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.script.action.http.KeyValuePairException;
import io.metadew.iesi.script.action.http.QuoteCharException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class HttpHeaderDefinitionService implements IHttpHeaderDefinitionService {

    private static HttpHeaderDefinitionService INSTANCE;

    public synchronized static HttpHeaderDefinitionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HttpHeaderDefinitionService();
        }
        return INSTANCE;
    }

    private HttpHeaderDefinitionService() {
    }

    public HttpHeaderDefinition convert(String httpHeader) {
        log.info(MessageFormat.format("Converting http header {0}", httpHeader));
        List<String> keyValue;
        String key;
        String value;

        if (!httpHeader.contains("=")) {
            throw new KeyValuePairException(String.format("The parameter %s should contain key value pair separated by the equals character < key=\"value\" >.", httpHeader));
        }

        keyValue = Arrays.stream(httpHeader.split("(?<!\".{0,255}[^\"])=|=(?![^\"].*\")")).collect(Collectors.toList());

        if (keyValue.size() > 2) {
            throw new KeyValuePairException(String.format("The parameter %s should contain one key value pair, please remove additional separator character.", httpHeader));
        }

        key = keyValue.get(0);
        value = keyValue.get(1);

        if (!(value.startsWith("\"") && value.endsWith("\""))) {
            throw new QuoteCharException(String.format("The value %s is not provided correctly, please use quotes", value));
        }

        return new HttpHeaderDefinition(key, StringUtils.substringBetween(value, "\"", "\""));
    }

    public HttpHeaderDefinition convert(ComponentParameter componentParameter) {
        return convert(componentParameter.getValue());
    }

    public boolean isHeader(ComponentParameter componentParameter) {
        return componentParameter.getMetadataKey().getParameterName().startsWith("header");
    }

}
