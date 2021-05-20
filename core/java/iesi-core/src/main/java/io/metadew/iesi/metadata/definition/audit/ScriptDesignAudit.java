package io.metadew.iesi.metadata.definition.audit;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.audit.key.ScriptDesignAuditKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ScriptDesignAudit extends Metadata<ScriptDesignAuditKey> {

    private String username;
    private String userId;
    private Enum<ScriptDesignAuditAction> scriptDesignAuditAction;
    private String scriptId;
    private String scriptName;
    private long scriptVersion;
    private String securityGroup;
    private String timeStamp;

    @Builder
    public ScriptDesignAudit(ScriptDesignAuditKey scriptDesignAuditKey, String username, String userId, Enum<ScriptDesignAuditAction> scriptDesignAuditAction,
                             String scriptId, String scriptName, long scriptVersion, String securityGroup, String timeStamp) {
        super(scriptDesignAuditKey);
        this.username = username;
        this.userId = userId;
        this.scriptDesignAuditAction = scriptDesignAuditAction;
        this.scriptId = scriptId;
        this.scriptName = scriptName;
        this.scriptVersion = scriptVersion;
        this.securityGroup = securityGroup;
        this.timeStamp = timeStamp;
    }
}
