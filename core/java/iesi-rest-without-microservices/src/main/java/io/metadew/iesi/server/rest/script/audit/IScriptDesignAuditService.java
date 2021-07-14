package io.metadew.iesi.server.rest.script.audit;

import io.metadew.iesi.metadata.definition.audit.ScriptDesignAudit;
import io.metadew.iesi.metadata.definition.audit.ScriptDesignAuditAction;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;

public interface IScriptDesignAuditService {

    ScriptDesignAudit convertToScriptAudit(ScriptVersion scriptVersion, ScriptDesignAuditAction scriptDesignAuditAction);

}
