package io.metadew.iesi.server.rest.dataset.implementation.inmemory;

import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKeyValueKey;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
@DirtiesContext
class DatasetImplementationKeyValueDtoModelAssemblerTest {

    @Autowired
    private DatabaseDatasetImplementationKeyValueDtoModelAssembler databaseDatasetImplementationKeyValueDtoModelAssembler;


    @Test
    void toModelTest() {
        UUID keyValueUuid = UUID.randomUUID();
        assertThat(databaseDatasetImplementationKeyValueDtoModelAssembler.toModel(
                DatasetImplementationKeyValue.builder()
                        .metadataKey(new DatasetImplementationKeyValueKey(keyValueUuid))
                        .datasetImplementationKey(new DatasetImplementationKey(UUID.randomUUID()))
                        .key("key")
                        .value("value")
                        .build())
        ).isEqualTo(DatabaseDatasetImplementationKeyValueDto.builder()
                .uuid(keyValueUuid)
                .key("key")
                .value("value")
                .build());
    }

}