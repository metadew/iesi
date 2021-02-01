package io.metadew.iesi.server.rest.dataset.implementation.inmemory;

import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValueKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationLabelDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
class InMemoryDatasetImplementationKeyValueDtoModelAssemblerTest {

    @Autowired
    private InMemoryDatasetImplementationKeyValueDtoModelAssembler inMemoryDatasetImplementationKeyValueDtoModelAssembler;


    @Test
    void toModelTest() {
        UUID keyValueUuid = UUID.randomUUID();
        assertThat(inMemoryDatasetImplementationKeyValueDtoModelAssembler.toModel(
                InMemoryDatasetImplementationKeyValue.builder()
                        .metadataKey(new InMemoryDatasetImplementationKeyValueKey(keyValueUuid))
                        .datasetImplementationKey(new DatasetImplementationKey(UUID.randomUUID()))
                        .key("key")
                        .value("value")
                        .build())
        ).isEqualTo(InMemoryDatasetImplementationKeyValueDto.builder()
                .uuid(keyValueUuid)
                .key("key")
                .value("value")
                .build());
    }

}