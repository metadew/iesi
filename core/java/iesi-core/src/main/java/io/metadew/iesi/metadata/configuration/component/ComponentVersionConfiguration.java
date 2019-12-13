package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ComponentVersionConfiguration extends Configuration<ComponentVersion, ComponentVersionKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ComponentVersionConfiguration INSTANCE;

    public synchronized static ComponentVersionConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComponentVersionConfiguration();
        }
        return INSTANCE;
    }

    private ComponentVersionConfiguration() {}

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
        ComponentBuildConfiguration.getInstance().init(metadataRepository);
    }

    @Override
    public Optional<ComponentVersion> get(ComponentVersionKey metadataKey) {
        return getComponentVersion(metadataKey.getComponentId(), metadataKey.getComponentVersionNumber());
    }

    @Override
    public List<ComponentVersion> getAll() {
        try {
            List<ComponentVersion> componentVersions= new ArrayList<>();
            String query = "select * from "
                    + getMetadataRepository().getTableNameByLabel("ComponentVersions") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                ComponentVersionKey componentVersionKey = new ComponentVersionKey(
                        cachedRowSet.getString("COMP_ID"),
                        cachedRowSet.getLong("COMP_VRS_NB"));
                componentVersions.add(new ComponentVersion(
                        componentVersionKey,
                        cachedRowSet.getString("COMP_VRS_DSC")));
            }
            return componentVersions;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ComponentVersionKey metadataKey) throws MetadataDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting ComponentVersion {0}.", metadataKey.toString()));
        if (!exists(metadataKey)) {
            throw new MetadataDoesNotExistException("ComponentVersion", metadataKey);
        }
        String deleteStatement = deleteStatement(metadataKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ComponentVersionKey componentVersionKey){
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ComponentVersions") +
                " WHERE COMP_ID = " +
                SQLTools.GetStringForSQL(componentVersionKey.getComponentId()) +
                " AND COMP_VRS_NB = " +
                SQLTools.GetStringForSQL(componentVersionKey.getComponentVersionNumber()) + ";";
    }

    @Override
    public void insert(ComponentVersion metadata) throws MetadataAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting ComponentVersion {0}.", metadata.getMetadataKey().toString()));
        if (exists(metadata.getMetadataKey())) {
            throw new MetadataAlreadyExistsException("ComponentVersion", metadata.getMetadataKey());
        }
        String insertQuery = getInsertStatement(metadata);
        getMetadataRepository().executeUpdate(insertQuery);
    }

    // Insert
    public String getInsertStatement(ComponentVersion componentVersion) {
        String sql = "";

        sql += "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ComponentVersions");
        sql += " (COMP_ID, COMP_VRS_NB, COMP_VRS_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(componentVersion.getMetadataKey().getComponentId());
        sql += ",";
        sql += SQLTools.GetStringForSQL(componentVersion.getMetadataKey().getComponentVersionNumber());
        sql += ",";
        sql += SQLTools.GetStringForSQL(componentVersion.getDescription());
        sql += ")";
        sql += ";";

        return sql;
    }


    Optional<ComponentVersion> getComponentVersion(String componentId, long componentVersionNumber) {
        String queryComponentVersion = "select COMP_ID, COMP_VRS_NB, COMP_VRS_DSC from " + getMetadataRepository().getTableNameByLabel("ComponentVersions")
                + " where COMP_ID = " + SQLTools.GetStringForSQL(componentId) + " and COMP_VRS_NB = " + SQLTools.GetStringForSQL(componentVersionNumber);
        CachedRowSet crsComponentVersion = getMetadataRepository().executeQuery(queryComponentVersion, "reader");
        try {
            if (crsComponentVersion.size() == 0) {
                return Optional.empty();
            } else if (crsComponentVersion.size() > 1) {
                LOGGER.warn(MessageFormat.format("component.version=found multiple descriptions for component id {0} version {1}. " + "Returning first implementation.", componentId, componentVersionNumber));
            }
            crsComponentVersion.next();
            ComponentVersionKey componentVersionKey = new ComponentVersionKey(componentId, componentVersionNumber);
            String description = crsComponentVersion.getString("COMP_VRS_DSC");
            ComponentVersion componentVersion = new ComponentVersion(componentVersionKey, description);
            crsComponentVersion.close();
            return Optional.of(componentVersion);
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exeption=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }
        return Optional.empty();
    }

}