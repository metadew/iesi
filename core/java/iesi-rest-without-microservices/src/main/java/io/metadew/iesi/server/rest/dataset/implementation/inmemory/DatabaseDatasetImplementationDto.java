package io.metadew.iesi.server.rest.dataset.implementation.inmemory;


import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationDto;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationLabelDto;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationType;


import lombok.*;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseDatasetImplementationDto extends DatasetImplementationDto {

    private Set<DatabaseDatasetImplementationKeyValueDto> keyValues;

    @Builder
    public DatabaseDatasetImplementationDto(UUID uuid, Set<DatasetImplementationLabelDto> labels, Set<DatabaseDatasetImplementationKeyValueDto> keyValues) {
        super(uuid, DatasetImplementationType.IN_MEMORY.value(), labels);
        this.keyValues = keyValues;
    }

}
