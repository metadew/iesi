package io.metadew.iesi.server.rest.dataset.implementation.database;

import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationType;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationLabelPostDto;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationPostDto;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseDatasetImplementationPostDto extends DatasetImplementationPostDto {

    @NotNull
    private Set<DatabaseDatasetImplementationKeyValuePostDto> keyValues;

    @Builder
    public DatabaseDatasetImplementationPostDto(Set<DatasetImplementationLabelPostDto> labels,
                                                Set<DatabaseDatasetImplementationKeyValuePostDto> keyValues) {
        super(DatasetImplementationType.DATABASE.value(), labels);
        this.keyValues = keyValues;
    }

}
