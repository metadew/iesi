package io.metadew.iesi.component.http;

import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.script.action.http.KeyValuePairException;
import io.metadew.iesi.script.action.http.QuoteCharException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

        Pattern patternComma = Pattern.compile("(,)(?=(?:[^\"]|\"[^\"]*\")*$)"); //Checks if comma exists outside quotes
        Matcher matcher = patternComma.matcher(httpHeader);
        Pattern patternEquals = Pattern.compile("(=)(?=(?:[^\"]|\"[^\"]*\")*$)");

        if (!httpHeader.contains("=")) {
            throw new KeyValuePairException(String.format("The parameter %s should contain key value pair separated by the equals character < key=\"value\" >.", httpHeader));
        }

        if (matcher.find()) {
            throw new KeyValuePairException(String.format("The parameter %s should not contains comma outside header value", httpHeader));
        }

        keyValue = Arrays.stream(httpHeader.split("=", 2)).collect(Collectors.toList());

        key = keyValue.get(0);
        value = keyValue.get(1);

        Matcher matcherEquals = patternEquals.matcher(value);
        if (matcherEquals.find()) {
            throw new KeyValuePairException(String.format("The parameter %s should contain only one key-value separator, please remove additional separators", httpHeader));
        }

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
