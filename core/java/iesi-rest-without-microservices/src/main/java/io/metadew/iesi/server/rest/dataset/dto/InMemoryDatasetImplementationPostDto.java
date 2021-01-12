package io.metadew.iesi.server.rest.dataset.dto;

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
        super(labels);
        this.keyValues = keyValues;
    }

}
