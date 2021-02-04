package io.metadew.iesi.server.rest.dataset.implementation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public abstract class DatasetImplementationPostDto {

    @NotBlank
    private String type;

    @NotEmpty
    private Set<DatasetImplementationLabelPostDto> labels;

}
