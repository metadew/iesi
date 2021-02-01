package io.metadew.iesi.server.rest.dataset.implementation;

import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.useDefaultDateFormatsOnly;

@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
class DatasetImplementationLabelDtoModelAssemblerTest {

    @Autowired
    private DatasetImplementationLabelDtoModelAssembler datasetImplementationLabelDtoModelAssembler;


    @Test
    void toModelTest() {
        UUID labelUuid = UUID.randomUUID();
        assertThat(datasetImplementationLabelDtoModelAssembler.toModel(
                DatasetImplementationLabel.builder()
                        .metadataKey(new DatasetImplementationLabelKey(labelUuid))
                        .datasetImplementationKey(new DatasetImplementationKey(UUID.randomUUID()))
                        .value("label")
                        .build())
        ).isEqualTo(DatasetImplementationLabelDto.builder()
                .uuid(labelUuid)
                .label("label")
                .build());

    }

}