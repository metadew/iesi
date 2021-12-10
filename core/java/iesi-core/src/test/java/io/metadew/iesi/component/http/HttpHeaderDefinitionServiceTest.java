package io.metadew.iesi.component.http;

import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
                .value("content-type,application/json")
                .build())
        ).isEqualTo(new HttpHeaderDefinition("content-type", "application/json"));
    }

}
