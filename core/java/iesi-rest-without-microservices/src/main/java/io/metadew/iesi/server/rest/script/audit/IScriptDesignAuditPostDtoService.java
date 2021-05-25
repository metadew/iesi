package io.metadew.iesi.server.rest.script.audit;

import io.metadew.iesi.metadata.definition.audit.ScriptDesignAudit;
import io.metadew.iesi.metadata.definition.script.Script;

public interface IScriptDesignAuditPostDto {

    public ScriptDesignAudit convertToEntity(Script script);

}
