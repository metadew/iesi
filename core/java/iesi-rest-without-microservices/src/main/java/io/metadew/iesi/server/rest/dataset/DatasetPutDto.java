package io.metadew.iesi.server.rest.dataset;

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
public class DatasetPutDto {

    @NotBlank
    private String securityGroupName;

    @NotBlank
    private String name;

    @NotNull
    private Set<DatasetImplementationPostDto> implementations;

}
