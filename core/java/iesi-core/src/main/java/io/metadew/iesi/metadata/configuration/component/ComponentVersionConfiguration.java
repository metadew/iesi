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

    private ComponentVersionConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
        ComponentBuildConfiguration.getInstance().init(metadataRepository);
    }

    @Override
    public Optional<ComponentVersion> get(ComponentVersionKey componentVersionKey) {
        String queryComponentVersion = "select COMP_ID, COMP_VRS_NB, COMP_VRS_DSC from " + getMetadataRepository().getTableNameByLabel("ComponentVersions")
                + " where COMP_ID = " + SQLTools.GetStringForSQL(componentVersionKey.getComponentKey().getId()) +
                " and COMP_VRS_NB = " + SQLTools.GetStringForSQL(componentVersionKey.getComponentKey().getVersionNumber());
        CachedRowSet crsComponentVersion = getMetadataRepository().executeQuery(queryComponentVersion, "reader");
        try {
            if (crsComponentVersion.size() == 0) {
                return Optional.empty();
            } else if (crsComponentVersion.size() > 1) {
                LOGGER.warn(MessageFormat.format("component.version=found multiple descriptions for component {0}. Returning first implementation.", componentVersionKey.toString()));
            }
            crsComponentVersion.next();
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

    @Override
    public List<ComponentVersion> getAll() {
        try {
            List<ComponentVersion> componentVersions = new ArrayList<>();
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
    public void delete(ComponentVersionKey metadataKey) {
        LOGGER.trace(MessageFormat.format("Deleting ComponentVersion {0}.", metadataKey.toString()));
        if (!exists(metadataKey)) {
            throw new MetadataDoesNotExistException(metadataKey);
        }
        String deleteStatement = deleteStatement(metadataKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ComponentVersionKey componentVersionKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ComponentVersions") +
                " WHERE COMP_ID = " + SQLTools.GetStringForSQL(componentVersionKey.getComponentKey().getId()) +
                " AND COMP_VRS_NB = " + SQLTools.GetStringForSQL(componentVersionKey.getComponentKey().getVersionNumber()) + ";";
    }

    @Override
    public void insert(ComponentVersion metadata) {
        LOGGER.trace(MessageFormat.format("Inserting ComponentVersion {0}.", metadata.getMetadataKey().toString()));
        if (exists(metadata.getMetadataKey())) {
            throw new MetadataAlreadyExistsException(metadata.getMetadataKey());
        }
        String insertQuery = getInsertStatement(metadata);
        getMetadataRepository().executeUpdate(insertQuery);
    }

    // Insert
    public String getInsertStatement(ComponentVersion componentVersion) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ComponentVersions")
                + " (COMP_ID, COMP_VRS_NB, COMP_VRS_DSC) VALUES (" +
                SQLTools.GetStringForSQL(componentVersion.getMetadataKey().getComponentKey().getId()) + "," +
                SQLTools.GetStringForSQL(componentVersion.getMetadataKey().getComponentKey().getVersionNumber()) + "," +
                SQLTools.GetStringForSQL(componentVersion.getDescription()) + ");";
    }

    public List<ComponentVersion> getByComponent(String componentId) {
        try {
            List<ComponentVersion> componentVersions = new ArrayList<>();
            String query = "select * from "
                    + getMetadataRepository().getTableNameByLabel("ComponentVersions") +
                    " WHERE COMP_ID = " + SQLTools.GetStringForSQL(componentId) + ";";
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

    public void deleteByComponentId(String componentId) {
        String deleteStatement = "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ComponentVersions") +
                " WHERE COMP_ID = " + SQLTools.GetStringForSQL(componentId) + ";";
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    public long getLatestVersionByComponentId(String componentId) {
        String queryComponentVersion = "select max(COMP_VRS_NB) as \"MAX_VRS_NB\" from "
                + getMetadataRepository().getTableNameByLabel("ComponentVersions") +
                " where COMP_ID = " + SQLTools.GetStringForSQL(componentId) + ";";
        CachedRowSet crsComponentVersion = getMetadataRepository().executeQuery(queryComponentVersion, "reader");
        try {
            if (crsComponentVersion.size() == 0) {
                throw new RuntimeException(MessageFormat.format("Component with ID {0} does not exeist, cannot find latest version", componentId));
            } else {
                crsComponentVersion.next();
                long componentVersionNumber = crsComponentVersion.getLong("MAX_VRS_NB");
                crsComponentVersion.close();
                return componentVersionNumber;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAll() {
        getMetadataRepository().executeUpdate("DELETE FROM " + getMetadataRepository().getTableNameByLabel("ComponentVersions") + ";");
    }

}