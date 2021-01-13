package io.metadew.iesi.server.rest.dataset.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DatasetPostDto {

    @NotBlank
    private String name;

    @NotNull
    private Set<DatasetImplementationPostDto> implementations;


}
