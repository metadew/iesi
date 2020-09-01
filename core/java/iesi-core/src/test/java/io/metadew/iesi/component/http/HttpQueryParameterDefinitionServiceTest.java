package io.metadew.iesi.component.http;

import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpQueryParameterDefinitionServiceTest {

    @Test
    void isHeaderTrueTest() {
        assertThat(HttpQueryParameterDefinitionService.getInstance().isQueryParameter(ComponentParameter.builder()
                .componentParameterKey(new ComponentParameterKey(new ComponentKey("id", 1L), "queryparam.1"))
                .value("param,value")
                .build())
        ).isTrue();
    }

    @Test
    void isHeaderFalseTest() {
        assertThat(HttpQueryParameterDefinitionService.getInstance().isQueryParameter(ComponentParameter.builder()
                .componentParameterKey(new ComponentParameterKey(new ComponentKey("id", 1L), "notqueryparam.1"))
                .value("param,value")
                .build())
        ).isFalse();
    }

    @Test
    void convertTest() {
        assertThat(HttpQueryParameterDefinitionService.getInstance().convert(ComponentParameter.builder()
                .componentParameterKey(new ComponentParameterKey(new ComponentKey("id", 1L), "queryparam.1"))
                .value("param,value")
                .build())
        ).isEqualTo(new HttpQueryParameterDefinition("param", "value"));
    }

}
