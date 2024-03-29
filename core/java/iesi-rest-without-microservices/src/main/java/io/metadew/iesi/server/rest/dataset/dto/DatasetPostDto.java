package io.metadew.iesi.server.rest.dataset.dto;

import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationPostDto;
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
    @NotBlank
    private String securityGroupName;

    @NotNull
    private Set<DatasetImplementationPostDto> implementations;


}
