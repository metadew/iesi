package io.metadew.iesi.server.rest.dataset;

import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationPostDto;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DatasetPutDto {

    @NotEmpty
    private UUID uuid;

    @NotBlank
    private String name;

    @NotNull
    private Set<DatasetImplementationPostDto> implementations;

}
