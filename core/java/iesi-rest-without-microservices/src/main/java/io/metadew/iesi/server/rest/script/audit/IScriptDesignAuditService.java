package io.metadew.iesi.server.rest.script.audit;

import io.metadew.iesi.metadata.definition.audit.ScriptDesignAudit;
import io.metadew.iesi.metadata.definition.audit.ScriptDesignAuditAction;
import io.metadew.iesi.metadata.definition.script.Script;

public interface IScriptDesignAuditService {

    public ScriptDesignAudit convertToScriptAudit(Script script, ScriptDesignAuditAction scriptDesignAuditAction);

}
