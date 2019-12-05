package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentAttribute;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentAttributeKey;
import io.metadew.iesi.metadata.execution.MetadataControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import javax.swing.text.html.Option;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ComponentAttributeConfiguration extends Configuration<ComponentAttribute, ComponentAttributeKey> {

    private ComponentVersion componentVersion;
    private ComponentAttribute componentAttribute;

    private static final Logger LOGGER = LogManager.getLogger();
    private static ComponentAttributeConfiguration INSTANCE;

    public synchronized static ComponentAttributeConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComponentAttributeConfiguration();
        }
        return INSTANCE;
    }

    // Constructors
    public ComponentAttributeConfiguration(ComponentVersion componentVersion, ComponentAttribute componentAttribute) {
        this.setComponentVersion(componentVersion);
        this.setComponentAttribute(componentAttribute);
    }

    public ComponentAttributeConfiguration() {
    }

    @Override
    public Optional<ComponentAttribute> get(ComponentAttributeKey metadataKey) {
        return getComponentAttribute(metadataKey.getComponentId(), metadataKey.getComponentAttributeName(),
                metadataKey.getComponentVersionNb());
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
                        cachedRowSet.getString("COMP_ID"),
                        cachedRowSet.getLong("COMP_VRS_NB"),
                        cachedRowSet.getString("COMP_ATT_NM"));
                componentBuilds.add(new ComponentAttribute(
                        componentAttributeKey,
                        cachedRowSet.getString("ENV_NM"),
                        cachedRowSet.getString("COMP_ATT_VAL")));
            }
            return componentBuilds;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ComponentAttributeKey metadataKey) throws MetadataDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting ComponentAttribute {0}.", metadataKey.toString()));
        if (!exists(metadataKey)) {
            throw new MetadataDoesNotExistException("ComponentAttribute", metadataKey);
        }
        String deleteStatement = deleteStatement(metadataKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ComponentAttributeKey attributeKey){
        return "DELETE FROM " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ComponentAttributes") +
                " WHERE COMP_ID = " +
                SQLTools.GetStringForSQL(attributeKey.getComponentId()) +
                "AND WHERE COMP_VRS_NB = " +
                SQLTools.GetStringForSQL(attributeKey.getComponentVersionNb()) +
                "AND WHERE COMP_ATT_NM = " +
                SQLTools.GetStringForSQL(attributeKey.getComponentAttributeName()) + ";";
    }

    @Override
    public void insert(ComponentAttribute metadata) throws MetadataAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting ComponentAttribute {0}.", metadata.getMetadataKey().toString()));
        if (exists(metadata.getMetadataKey())) {
            throw new MetadataAlreadyExistsException("ComponentAttribute", metadata.getMetadataKey());
        }
        String insertQuery = getInsertStatement(metadata);
        getMetadataRepository().executeUpdate(insertQuery);
    }

    // Insert
    public String getInsertStatement(ComponentAttribute componentAttribute) {
        String sql = "";
        ComponentAttributeKey componentAttributeKey = componentAttribute.getMetadataKey();

        sql += "INSERT INTO " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ComponentAttributes");
        sql += " (COMP_ID, COMP_VRS_NB, ENV_NM, COMP_ATT_NM, COMP_ATT_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(componentAttributeKey.getComponentId());
        sql += ",";
        sql += SQLTools.GetStringForSQL(componentAttributeKey.getComponentVersionNb());
        sql += ",";
        sql += SQLTools.GetStringForSQL(componentAttribute.getEnvironment());
        sql += ",";
        sql += SQLTools.GetStringForSQL(componentAttribute.getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(componentAttribute.getValue());
        sql += ")";
        sql += ";";

        return sql;
    }

    public Optional<ComponentAttribute> getComponentAttribute(String componentId, String componentAttributeName, long componentVersionNumber) {
        String queryComponentAttribute = "select COMP_ID, COMP_ATT_NM, ENV_NM, COMP_ATT_VAL from " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ComponentAttributes")
                + " where COMP_ID = " + SQLTools.GetStringForSQL(componentId) + " and COMP_ATT_NM = '" + componentAttributeName + "'" + " and COMP_VRS_NB = " + componentVersionNumber;
        CachedRowSet crsComponentAttribute = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryComponentAttribute, "reader");
        try {
            if (crsComponentAttribute.size()==0){
                return Optional.empty();
            }
            crsComponentAttribute.next();
            ComponentAttributeKey componentAttributeKey = new ComponentAttributeKey(componentId,
                    componentVersionNumber, componentAttributeName);
            ComponentAttribute componentAttribute = new ComponentAttribute(componentAttributeKey,
                    crsComponentAttribute.getString("ENV_NM"),
                    crsComponentAttribute.getString("COMP_ATT_VAL"));
            crsComponentAttribute.close();
            return Optional.of(componentAttribute);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            throw new RuntimeException(e);
        }
    }

    // Getters and Setters
    public ComponentAttribute getComponentAttribute() {
        return componentAttribute;
    }

    public void setComponentAttribute(ComponentAttribute componentAttribute) {
        this.componentAttribute = componentAttribute;
    }

    public ComponentVersion getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(ComponentVersion componentVersion) {
        this.componentVersion = componentVersion;
    }

}