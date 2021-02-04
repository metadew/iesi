package io.metadew.iesi.server.rest.dataset.implementation.inmemory;


import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationLabelPostDto;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationPostDto;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationType;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class InMemoryDatasetImplementationPostDto extends DatasetImplementationPostDto {

    @NotNull
    private Set<InMemoryDatasetImplementationKeyValuePostDto> keyValues;

    @Builder
    public InMemoryDatasetImplementationPostDto(Set<DatasetImplementationLabelPostDto> labels,
                                                Set<InMemoryDatasetImplementationKeyValuePostDto> keyValues) {
        super(DatasetImplementationType.IN_MEMORY.value(), labels);
        this.keyValues = keyValues;
    }

}
