package io.metadew.iesi.server.rest.script.dto.version;

<<<<<<< HEAD
=======

import io.metadew.iesi.server.rest.script.dto.NoEmptyLinksRepresentationModel;
>>>>>>> master
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;



@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ScriptVersionDto extends NoEmptyLinksRepresentationModel<ScriptVersionDto> {

    private long number;
    private String description;

}