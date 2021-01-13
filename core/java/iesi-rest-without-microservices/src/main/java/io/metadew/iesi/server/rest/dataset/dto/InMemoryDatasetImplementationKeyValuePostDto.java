package io.metadew.iesi.server.rest.dataset.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InMemoryDatasetImplementationKeyValuePostDto {

    @NotBlank
    private String key;

    @NotNull
    private String value;

}
