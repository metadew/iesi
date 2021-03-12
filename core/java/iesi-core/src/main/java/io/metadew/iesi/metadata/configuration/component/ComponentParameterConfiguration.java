package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
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

public class ComponentParameterConfiguration extends Configuration<ComponentParameter, ComponentParameterKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ComponentParameterConfiguration INSTANCE;

    public synchronized static ComponentParameterConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComponentParameterConfiguration();
        }
        return INSTANCE;
    }

    // Constructors
    private ComponentParameterConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository());
    }

    @Override
    public Optional<ComponentParameter> get(ComponentParameterKey componentParameterKey) {
        String queryComponentParameter = "select COMP_ID, COMP_PAR_NM, COMP_PAR_VAL from " + getMetadataRepository().getTableNameByLabel("ComponentParameters")
                + " where COMP_ID = " + SQLTools.getStringForSQL(componentParameterKey.getComponentKey().getId()) +
                " and COMP_PAR_NM = " + SQLTools.getStringForSQL(componentParameterKey.getParameterName()) +
                " and COMP_VRS_NB = " + SQLTools.getStringForSQL(componentParameterKey.getComponentKey().getVersionNumber()) + ";";
        CachedRowSet crsComponentParameter = getMetadataRepository().executeQuery(queryComponentParameter, "reader");
        try {
            if (crsComponentParameter.size()==0){
                return Optional.empty();
            }
            crsComponentParameter.next();
            ComponentParameter componentParameter = new ComponentParameter(componentParameterKey,
                    crsComponentParameter.getString("COMP_PAR_VAL"));
            crsComponentParameter.close();
            return Optional.of(componentParameter);
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            throw new RuntimeException();
        }
    }

    @Override
    public List<ComponentParameter> getAll() {
        try {
            List<ComponentParameter> componentParameters = new ArrayList<>();
            String query = "select COMP_ID, COMP_VRS_NB, COMP_PAR_NM, COMP_PAR_VAL from "
                    + getMetadataRepository().getTableNameByLabel("ComponentParameters") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                ComponentParameterKey componentParameterKey = new ComponentParameterKey(
                        cachedRowSet.getString("COMP_ID"),
                        cachedRowSet.getLong("COMP_VRS_NB"),
                        cachedRowSet.getString("COMP_PAR_NM"));
                componentParameters.add(new ComponentParameter(
                        componentParameterKey,
                        cachedRowSet.getString("COMP_PAR_VAL")));
            }
            return componentParameters;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ComponentParameterKey metadataKey) {
        LOGGER.trace(MessageFormat.format("Deleting ComponentParameter {0}.", metadataKey.toString()));
        if (!exists(metadataKey)) {
            throw new MetadataDoesNotExistException(metadataKey);
        }
        String deleteStatement = deleteStatement(metadataKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ComponentParameterKey componentParameterKey){
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ComponentParameters") +
                " WHERE COMP_ID = " + SQLTools.getStringForSQL(componentParameterKey.getComponentKey().getId()) +
                " AND COMP_VRS_NB = " + SQLTools.getStringForSQL(componentParameterKey.getComponentKey().getVersionNumber()) +
                " AND COMP_PAR_NM = " + SQLTools.getStringForSQL(componentParameterKey.getParameterName()) + ";";
    }

    @Override
    public void insert(ComponentParameter metadata) {
        LOGGER.trace(MessageFormat.format("Inserting ComponentParameter {0}.", metadata.getMetadataKey().toString()));
        if (exists(metadata.getMetadataKey())) {
            throw new MetadataAlreadyExistsException(metadata.getMetadataKey());
        }
        String insertQuery = getInsertStatement(metadata);
        getMetadataRepository().executeUpdate(insertQuery);
    }

    // Insert
    public String getInsertStatement(ComponentParameter componentParameter) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ComponentParameters") +
                " (COMP_ID, COMP_VRS_NB, COMP_PAR_NM, COMP_PAR_VAL) VALUES (" +
                SQLTools.getStringForSQL(componentParameter.getMetadataKey().getComponentKey().getId()) + "," +
                SQLTools.getStringForSQL(componentParameter.getMetadataKey().getComponentKey().getVersionNumber()) + "," +
                SQLTools.getStringForSQL(componentParameter.getMetadataKey().getParameterName()) + "," +
                SQLTools.getStringForSQL(componentParameter.getValue()) + ");";
    }

    public List<ComponentParameter> getByComponent(ComponentKey componentKey) {
        try {
            List<ComponentParameter> componentParameters = new ArrayList<>();
            String query = "select COMP_ID, COMP_VRS_NB, COMP_PAR_NM, COMP_PAR_VAL from "
                    + getMetadataRepository().getTableNameByLabel("ComponentParameters") +
                    " WHERE COMP_ID = " + SQLTools.getStringForSQL(componentKey.getId()) +
                    " AND COMP_VRS_NB = " + SQLTools.getStringForSQL(componentKey.getVersionNumber()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                componentParameters.add(new ComponentParameter(
                        new ComponentParameterKey(componentKey, cachedRowSet.getString("COMP_PAR_NM")),
                        cachedRowSet.getString("COMP_PAR_VAL")));
            }
            return componentParameters;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByComponent(ComponentKey componentKey) {
        String deleteStatement = "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ComponentParameters") +
                " WHERE COMP_ID = " + SQLTools.getStringForSQL(componentKey.getId()) +
                " AND COMP_VRS_NB = " + SQLTools.getStringForSQL(componentKey.getVersionNumber()) + ";";
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    public void deleteByComponentId(String componentId) {
        String deleteStatement = "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ComponentParameters") +
                " WHERE COMP_ID = " + SQLTools.getStringForSQL(componentId) + ";";
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    public void deleteAll() {
        getMetadataRepository().executeUpdate("DELETE FROM " + getMetadataRepository().getTableNameByLabel("ComponentParameters") + ";");
    }

}