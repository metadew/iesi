package io.metadew.iesi.server.rest.encrypter.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EncryptPostDto extends RepresentationModel<EncryptPostDto> {

    private String text;
}
