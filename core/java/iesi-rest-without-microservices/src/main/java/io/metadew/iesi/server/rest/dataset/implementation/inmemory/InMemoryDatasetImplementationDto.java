package io.metadew.iesi.server.rest.dataset.implementation.inmemory;


import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationDto;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationLabelDto;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationType;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class InMemoryDatasetImplementationDto extends DatasetImplementationDto {

    private Set<InMemoryDatasetImplementationKeyValueDto> keyValues;

    @Builder
    public InMemoryDatasetImplementationDto(UUID uuid, Set<DatasetImplementationLabelDto> labels, Set<InMemoryDatasetImplementationKeyValueDto> keyValues) {
        super(uuid, DatasetImplementationType.IN_MEMORY.value(), labels);
        this.keyValues = keyValues;
    }

}
