package io.metadew.iesi.metadata.configuration.audit;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.audit.ScriptDesignAudit;
import io.metadew.iesi.metadata.definition.audit.ScriptDesignAuditAction;
import io.metadew.iesi.metadata.definition.audit.key.ScriptDesignAuditKey;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Component
public class ScriptDesignAuditConfiguration extends Configuration<ScriptDesignAudit, ScriptDesignAuditKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    public ScriptDesignAuditConfiguration(MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
    }

    @PostConstruct
    private void postConstruct() {
        setMetadataRepository(metadataRepositoryConfiguration.getControlMetadataRepository());
    }

    @Override
    public Optional<ScriptDesignAudit> get(ScriptDesignAuditKey scriptDesignAuditKey) {
        try {
            String query = "SELECT USERNAME, USER_ID, ACTION, SCRIPT_ID, SCRIPT_NAME, SCRIPT_VERSION, SECURITY_GROUP, TIMESTAMP FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptAudit") +
                    " WHERE " +
                    "ID = " + SQLTools.getStringForSQL(scriptDesignAuditKey.getId()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else {
                cachedRowSet.next();
                return Optional.of(new ScriptDesignAudit(scriptDesignAuditKey,
                        cachedRowSet.getString("USERNAME"),
                        cachedRowSet.getString("USER_ID"),
                        ScriptDesignAuditAction.valueOf(cachedRowSet.getString("ACTION")),
                        cachedRowSet.getString("SCRIPT_ID"),
                        cachedRowSet.getString("SCRIPT_NAME"),
                        cachedRowSet.getInt("SCRIPT_VERSION"),
                        cachedRowSet.getString("SECURITY_GROUP"),
                        cachedRowSet.getString("TIMESTAMP")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ScriptDesignAudit> getAll(){
        try {
            List<ScriptDesignAudit> scriptDesignAudits = new ArrayList<>();
            String query = "SELECT ID, USERNAME, USER_ID, ACTION, SCRIPT_ID, SCRIPT_NAME, SCRIPT_VERSION, SECURITY_GROUP, TIMESTAMP FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptAudit") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptDesignAudits.add(new ScriptDesignAudit(new ScriptDesignAuditKey(
                        UUID.fromString(cachedRowSet.getString("ID"))),
                        cachedRowSet.getString("USERNAME"),
                        cachedRowSet.getString("USER_ID"),
                        ScriptDesignAuditAction.valueOf(cachedRowSet.getString("ACTION")),
                        cachedRowSet.getString("SCRIPT_ID"),
                        cachedRowSet.getString("SCRIPT_NAME"),
                        cachedRowSet.getInt("SCRIPT_VERSION"),
                        cachedRowSet.getString("SECURITY_GROUP"),
                        cachedRowSet.getString("TIMESTAMP")));
            }
            return scriptDesignAudits;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ScriptDesignAuditKey scriptDesignAuditKey) {
        LOGGER.trace(MessageFormat.format("Deleting ScriptDesignAudit {0}.", scriptDesignAuditKey.toString()));
        String query = "DELETE FROM "+getMetadataRepository().getTableNameByLabel("ScriptAudit") +
                " WHERE " +
                " ID = " + SQLTools.getStringForSQL(scriptDesignAuditKey.getId()) + ";";
        getMetadataRepository().executeUpdate(query);
    }

    @Override
    public void insert(ScriptDesignAudit scriptDesignAudit) {
        LOGGER.trace(MessageFormat.format("Inserting ScriptDesignParameterTrace {0}.", scriptDesignAudit.toString()));
        String query = "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptAudit") +
                " (ID, USERNAME, USER_ID, ACTION, SCRIPT_ID, SCRIPT_NAME, SCRIPT_VERSION, SECURITY_GROUP, TIMESTAMP) VALUES (" +
                SQLTools.getStringForSQL(scriptDesignAudit.getMetadataKey().getId()) + "," +
                SQLTools.getStringForSQL(scriptDesignAudit.getUsername()) + "," +
                SQLTools.getStringForSQL(scriptDesignAudit.getUserId()) + "," +
                SQLTools.getStringForSQL(scriptDesignAudit.getScriptDesignAuditAction().toString()) + "," +
                SQLTools.getStringForSQL(scriptDesignAudit.getScriptId()) + "," +
                SQLTools.getStringForSQL(scriptDesignAudit.getScriptName()) + "," +
                SQLTools.getStringForSQL(scriptDesignAudit.getScriptVersion()) + "," +
                SQLTools.getStringForSQL(scriptDesignAudit.getSecurityGroup()) + "," +
                SQLTools.getStringForSQL(scriptDesignAudit.getTimeStamp()) + ");";
        getMetadataRepository().executeUpdate(query);
    }
}
