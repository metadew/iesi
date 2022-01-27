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
                .value("content-type, application/json")
                .build())
        ).isTrue();
    }

    @Test
    void isHeaderFalseTest() {
        assertThat(HttpHeaderDefinitionService.getInstance().isHeader(ComponentParameter.builder()
                .componentParameterKey(new ComponentParameterKey(new ComponentKey("id", 1L), "notheader.1"))
                .value("content-type, application/json")
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
    void convertTestNoKeyValueSeparator() {
        Throwable exception = assertThrows(KeyValuePairException.class, () -> HttpHeaderDefinitionService.getInstance().convert(ComponentParameter.builder()
                .componentParameterKey(new ComponentParameterKey(new ComponentKey("id", 1L), "header.1"))
                .value("content-type,\"application/json\"")
                .build()));
        assertThat(exception.getMessage()).isEqualTo("The parameter content-type,\"application/json\" should contain key value pair separated by the equals character < key=\"value\" >.");
    }

    @Test
    void convertTestMultipleKeyValueSeparator() {
        Throwable exception = assertThrows(KeyValuePairException.class, () -> HttpHeaderDefinitionService.getInstance().convert(ComponentParameter.builder()
                .componentParameterKey(new ComponentParameterKey(new ComponentKey("id", 1L), "header.1"))
                .value("content-type===\"application/json\"")
                .build()));
        assertThat(exception.getMessage()).isEqualTo("The parameter content-type===\"application/json\" should contain one key value pair, please remove additional separator character.");
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

}
