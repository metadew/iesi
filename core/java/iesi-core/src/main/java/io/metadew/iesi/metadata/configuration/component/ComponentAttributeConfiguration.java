package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.ComponentAttribute;
import io.metadew.iesi.metadata.definition.component.key.ComponentAttributeKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ComponentAttributeConfiguration extends Configuration<ComponentAttribute, ComponentAttributeKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ComponentAttributeConfiguration INSTANCE;

    public synchronized static ComponentAttributeConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComponentAttributeConfiguration();
        }
        return INSTANCE;
    }

    private ComponentAttributeConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<ComponentAttribute> get(ComponentAttributeKey componentAttributeKey) {
        String queryComponentAttribute = "select COMP_ID, COMP_ATT_NM, ENV_NM, COMP_ATT_VAL from " + getMetadataRepository().getTableNameByLabel("ComponentAttributes")
                + " where COMP_ID = " + SQLTools.getStringForSQL(componentAttributeKey.getComponentKey().getId()) +
                " and COMP_ATT_NM = " + SQLTools.getStringForSQL(componentAttributeKey.getComponentAttributeName()) +
                " and COMP_VRS_NB = " + SQLTools.getStringForSQL(componentAttributeKey.getComponentKey().getVersionNumber()) +
                " and ENV_NM = " + SQLTools.getStringForSQL(componentAttributeKey.getEnvironmentKey().getName()) + ";";
        CachedRowSet crsComponentAttribute = getMetadataRepository().executeQuery(queryComponentAttribute, "reader");
        try {
            if (crsComponentAttribute.size() == 0) {
                return Optional.empty();
            }
            crsComponentAttribute.next();
            ComponentAttribute componentAttribute = new ComponentAttribute(componentAttributeKey,
                    crsComponentAttribute.getString("COMP_ATT_VAL"));
            crsComponentAttribute.close();
            return Optional.of(componentAttribute);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ComponentAttribute> getAll() {
        try {
            List<ComponentAttribute> componentBuilds = new ArrayList<>();
            String query = "select * from "
                    + getMetadataRepository().getTableNameByLabel("ComponentAttributes") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                ComponentAttributeKey componentAttributeKey = new ComponentAttributeKey(
                        new ComponentKey(cachedRowSet.getString("COMP_ID"), cachedRowSet.getLong("COMP_VRS_NB")),
                        new EnvironmentKey(cachedRowSet.getString("ENV_NM")),
                        cachedRowSet.getString("COMP_ATT_NM"));
                componentBuilds.add(new ComponentAttribute(
                        componentAttributeKey,
                        cachedRowSet.getString("COMP_ATT_VAL")));
            }
            return componentBuilds;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ComponentAttributeKey metadataKey) {
        LOGGER.trace(MessageFormat.format("Deleting ComponentAttribute {0}.", metadataKey.toString()));
        if (!exists(metadataKey)) {
            throw new MetadataDoesNotExistException(metadataKey);
        }
        String deleteStatement = deleteStatement(metadataKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ComponentAttributeKey componentAttributeKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ComponentAttributes") +
                " WHERE COMP_ID = " + SQLTools.getStringForSQL(componentAttributeKey.getComponentKey().getId()) +
                "AND COMP_VRS_NB = " + SQLTools.getStringForSQL(componentAttributeKey.getComponentKey().getVersionNumber()) +
                "AND ENV_NM = " + SQLTools.getStringForSQL(componentAttributeKey.getEnvironmentKey().getName()) +
                "AND COMP_ATT_NM = " + SQLTools.getStringForSQL(componentAttributeKey.getComponentAttributeName()) + ";";
    }

    @Override
    public void insert(ComponentAttribute metadata) {
        LOGGER.trace(MessageFormat.format("Inserting ComponentAttribute {0}.", metadata.getMetadataKey().toString()));
        if (exists(metadata.getMetadataKey())) {
            throw new MetadataAlreadyExistsException(metadata.getMetadataKey());
        }
        String insertQuery = getInsertStatement(metadata);
        getMetadataRepository().executeUpdate(insertQuery);
    }

    // Insert
    public String getInsertStatement(ComponentAttribute componentAttribute) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ComponentAttributes") +
                " (COMP_ID, COMP_VRS_NB, ENV_NM, COMP_ATT_NM, COMP_ATT_VAL) VALUES (" +
                SQLTools.getStringForSQL(componentAttribute.getMetadataKey().getComponentKey().getId()) + "," +
                SQLTools.getStringForSQL(componentAttribute.getMetadataKey().getComponentKey().getVersionNumber()) + "," +
                SQLTools.getStringForSQL(componentAttribute.getMetadataKey().getEnvironmentKey().getName()) + "," +
                SQLTools.getStringForSQL(componentAttribute.getMetadataKey().getComponentAttributeName()) + "," +
                SQLTools.getStringForSQL(componentAttribute.getValue()) + ");";
    }

    public List<ComponentAttribute> getByComponent(ComponentKey componentKey) {
        try {
            List<ComponentAttribute> componentBuilds = new ArrayList<>();
            String query = "select * from " + getMetadataRepository().getTableNameByLabel("ComponentAttributes") +
                    " WHERE COMP_ID = " + SQLTools.getStringForSQL(componentKey.getId()) +
                    " AND COMP_VRS_NB = " + SQLTools.getStringForSQL(componentKey.getVersionNumber()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                ComponentAttributeKey componentAttributeKey = new ComponentAttributeKey(
                        new ComponentKey(cachedRowSet.getString("COMP_ID"), cachedRowSet.getLong("COMP_VRS_NB")),
                        new EnvironmentKey(cachedRowSet.getString("ENV_NM")),
                        cachedRowSet.getString("COMP_ATT_NM"));
                componentBuilds.add(new ComponentAttribute(
                        componentAttributeKey,
                        cachedRowSet.getString("COMP_ATT_VAL")));
            }
            return componentBuilds;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ComponentAttribute> getByComponentAndEnvironment(ComponentKey componentKey, EnvironmentKey environmentKey) {
        try {
            List<ComponentAttribute> componentBuilds = new ArrayList<>();
            String query = "select * from " + getMetadataRepository().getTableNameByLabel("ComponentAttributes") +
                    " WHERE COMP_ID = " + SQLTools.getStringForSQL(componentKey.getId()) +
                    " AND COMP_VRS_NB = " + SQLTools.getStringForSQL(componentKey.getVersionNumber()) +
                    " AND ENV_NM = " + SQLTools.getStringForSQL(environmentKey.getName()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                ComponentAttributeKey componentAttributeKey = new ComponentAttributeKey(
                        new ComponentKey(cachedRowSet.getString("COMP_ID"), cachedRowSet.getLong("COMP_VRS_NB")),
                        new EnvironmentKey(cachedRowSet.getString("ENV_NM")),
                        cachedRowSet.getString("COMP_ATT_NM"));
                componentBuilds.add(new ComponentAttribute(
                        componentAttributeKey,
                        cachedRowSet.getString("COMP_ATT_VAL")));
            }
            return componentBuilds;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByComponent(ComponentKey componentKey) {
        String query = "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ComponentAttributes") +
                " WHERE COMP_ID = " + SQLTools.getStringForSQL(componentKey.getId()) +
                " AND COMP_VRS_NB = " + SQLTools.getStringForSQL(componentKey.getVersionNumber()) + ";";
        getMetadataRepository().executeUpdate(query);
    }

    public void deleteByComponentId(String componentId) {
        String query = "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ComponentAttributes") +
                " WHERE COMP_ID = " + SQLTools.getStringForSQL(componentId) + ";";
        getMetadataRepository().executeUpdate(query);
    }

    public void deleteByComponentAndEnvironment(ComponentKey componentKey, EnvironmentKey environmentKey) {
        String query = "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ComponentAttributes") +
                " WHERE COMP_ID = " + SQLTools.getStringForSQL(componentKey.getId()) +
                " AND COMP_VRS_NB = " + SQLTools.getStringForSQL(componentKey.getVersionNumber()) +
                " AND ENV_NM = " + SQLTools.getStringForSQL(environmentKey.getName()) + ";";
        getMetadataRepository().executeUpdate(query);
    }

    public void deleteAll() {
        getMetadataRepository().executeUpdate("DELETE FROM " + getMetadataRepository().getTableNameByLabel("ComponentAttributes") + ";");
    }

}