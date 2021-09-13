package io.metadew.iesi.server.rest.dataset.implementation.inmemory;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DatabaseDatasetImplementationKeyValuePostDto {

    @NotBlank
    private String key;

    @NotNull
    private String value;

}
