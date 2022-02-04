package io.metadew.iesi.component.http;

import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.script.action.http.KeyValuePairException;
import io.metadew.iesi.script.action.http.QuoteCharException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HttpHeaderDefinitionServiceTest {

    @Test
    void isHeaderTrueTest() {
        assertThat(HttpHeaderDefinitionService.getInstance().isHeader(ComponentParameter.builder()
                .componentParameterKey(new ComponentParameterKey(new ComponentKey("id", 1L), "header.1"))
                .value("content-type=\"application/json\"")
                .build())
        ).isTrue();
    }

    @Test
    void isHeaderFalseTest() {
        assertThat(HttpHeaderDefinitionService.getInstance().isHeader(ComponentParameter.builder()
                .componentParameterKey(new ComponentParameterKey(new ComponentKey("id", 1L), "notheader.1"))
                .value("content-type=\"application/json\"")
                .build())
        ).isFalse();
    }

    @Test
    void convertTest() {
        assertThat(HttpHeaderDefinitionService.getInstance().convert(ComponentParameter.builder()
                .componentParameterKey(new ComponentParameterKey(new ComponentKey("id", 1L), "header.1"))
                .value("content-type=\"application/json\"")
                .build())
        ).isEqualTo(new HttpHeaderDefinition("content-type", "application/json"));
    }

    @Test
    void convertTestPreferences() {
        assertThat(HttpHeaderDefinitionService.getInstance().convert(ComponentParameter.builder()
                .componentParameterKey(new ComponentParameterKey(new ComponentKey("id", 1L), "header.1"))
                .value("content-type=\"application/json, application/xml;version=2\"")
                .build())
        ).isEqualTo(new HttpHeaderDefinition("content-type", "application/json, application/xml;version=2"));
    }

    @Test
    void convertTestSimpleSeparatorInsideQuotes() {
        assertThat(HttpHeaderDefinitionService.getInstance().convert(ComponentParameter.builder()
                .componentParameterKey(new ComponentParameterKey(new ComponentKey("id", 1L), "header.1"))
                .value("Authorization=\"BasicdGVzdF9hcGk6dGVzdF9hcGkwMDE=\"")
                .build())
        ).isEqualTo(new HttpHeaderDefinition("Authorization", "BasicdGVzdF9hcGk6dGVzdF9hcGkwMDE="));
    }

    @Test
    void convertWithSpaces() {
        assertThat(HttpHeaderDefinitionService.getInstance().convert(ComponentParameter.builder()
                .componentParameterKey(new ComponentParameterKey(new ComponentKey("id", 1L), "header.1"))
                .value("Authorization=\"Basic dGVzdF9hcGk6dGVzdF9hcGkwMDE=\"")
                .build())
        ).isEqualTo(new HttpHeaderDefinition("Authorization", "Basic dGVzdF9hcGk6dGVzdF9hcGkwMDE="));
    }

    @Test
    void convertTestMultipleSeparatorInsideQuotes() {
        assertThat(HttpHeaderDefinitionService.getInstance().convert(ComponentParameter.builder()
                .componentParameterKey(new ComponentParameterKey(new ComponentKey("id", 1L), "header.1"))
                .value("Authorization=\"=BasicdGVzdF9hcG==k6dGVzdF9=hcGkwMDE=\"")
                .build())
        ).isEqualTo(new HttpHeaderDefinition("Authorization", "=BasicdGVzdF9hcG==k6dGVzdF9=hcGkwMDE="));
    }

    @Test
    void convertTestNoKeyValueSeparator() {
        Throwable exception = assertThrows(KeyValuePairException.class, () -> HttpHeaderDefinitionService.getInstance().convert(ComponentParameter.builder()
                .componentParameterKey(new ComponentParameterKey(new ComponentKey("id", 1L), "header.1"))
                .value("content-type,\"application/json\"")
                .build()));
        assertThat(exception.getMessage()).isEqualTo("The parameter content-type,\"application/json\" should contain key value pair separated by the equals character < key=\"value\" >.");
    }

    @Test
    void convertTestMultipleKeyValueSeparatorBegin() {
        Throwable exception = assertThrows(KeyValuePairException.class, () -> HttpHeaderDefinitionService.getInstance().convert(ComponentParameter.builder()
                .componentParameterKey(new ComponentParameterKey(new ComponentKey("id", 1L), "header.1"))
                .value("content-type===\"application/json\"")
                .build()));
        assertThat(exception.getMessage())
                .isEqualTo("The parameter content-type===\"application/json\" should contain only one key-value separator, please remove additional separators");
    }

    @Test
    void convertTestMultipleKeyValueSeparatorEnd() {
        Throwable exception = assertThrows(KeyValuePairException.class, () -> HttpHeaderDefinitionService.getInstance().convert(ComponentParameter.builder()
                .componentParameterKey(new ComponentParameterKey(new ComponentKey("id", 1L), "header.1"))
                .value("content-type=\"application/json\"Accept=\"application/xml\"")
                .build()));
        assertThat(exception.getMessage())
                .isEqualTo("The parameter content-type=\"application/json\"Accept=\"application/xml\" should contain only one key-value separator, please remove additional separators");
    }

    @Test
    void convertTestNoStartQuote() {
        Throwable exception = assertThrows(QuoteCharException.class, () -> HttpHeaderDefinitionService.getInstance().convert(ComponentParameter.builder()
                .componentParameterKey(new ComponentParameterKey(new ComponentKey("id", 1L), "header.1"))
                .value("content-type=application/json\"")
                .build()));
        assertThat(exception.getMessage()).isEqualTo("The value application/json\" is not provided correctly, please use quotes");
    }

    @Test
    void convertTestNoEndtQuote() {
        Throwable exception = assertThrows(QuoteCharException.class, () -> HttpHeaderDefinitionService.getInstance().convert(ComponentParameter.builder()
                .componentParameterKey(new ComponentParameterKey(new ComponentKey("id", 1L), "header.1"))
                .value("content-type=\"application/json")
                .build()));
        assertThat(exception.getMessage()).isEqualTo("The value \"application/json is not provided correctly, please use quotes");
    }

    @Test
    void convertTestMultipleHeaderDefinition() {
        Throwable exception = assertThrows(KeyValuePairException.class, () -> HttpHeaderDefinitionService.getInstance().convert(ComponentParameter.builder()
                .componentParameterKey(new ComponentParameterKey(new ComponentKey("id", 1L), "header.1"))
                .value("content-type=\"application/json\", accept=\"application/json;version=2.0\"")
                .build()));
        assertThat(exception.getMessage())
                .isEqualTo("The parameter content-type=\"application/json\", accept=\"application/json;version=2.0\" should not contains comma outside header value");
    }

}
