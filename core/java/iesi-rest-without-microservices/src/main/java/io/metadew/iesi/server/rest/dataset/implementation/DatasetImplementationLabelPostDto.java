package io.metadew.iesi.server.rest.dataset.implementation;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DatasetImplementationLabelPostDto extends RepresentationModel<DatasetImplementationLabelPostDto> {

    @NotBlank
    private String label;

}