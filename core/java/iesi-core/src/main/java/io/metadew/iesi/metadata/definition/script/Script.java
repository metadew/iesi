package io.metadew.iesi.metadata.definition.script;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.metadew.iesi.metadata.definition.SecuredObject;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

//@JsonSerialize(using = ScriptJsonComponent.Serializer.class)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Script extends SecuredObject<ScriptKey> {

    private String name;
    private String description;
    private String deletedAt;

    @Builder
    public Script(ScriptKey scriptKey, SecurityGroupKey securityGroupKey, String securityGroupName, String name, String description, String deletedAt) {
        super(scriptKey, securityGroupKey, securityGroupName);
        this.name = name;
        this.description = description;
        this.deletedAt = deletedAt;
    }

}