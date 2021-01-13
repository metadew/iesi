package io.metadew.iesi.server.rest.dataset.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public abstract class DatasetImplementationPostDto {

    @NotEmpty
    private Set<DatasetImplementationLabelPostDto> labels;

}
