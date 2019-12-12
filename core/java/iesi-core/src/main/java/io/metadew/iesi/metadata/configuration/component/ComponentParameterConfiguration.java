package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.metadata.execution.MetadataControl;
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

    private ComponentVersion componentVersion;
    private ComponentParameter componentParameter;

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
    }

    public void init(MetadataRepository metadataRepository){
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<ComponentParameter> get(ComponentParameterKey metadataKey) {
        return getComponentParameter(metadataKey.getComponentId(), metadataKey.getComponentParameterName(), metadataKey.getComponentVersionNb());
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
    public void delete(ComponentParameterKey metadataKey) throws MetadataDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting ComponentParameter {0}.", metadataKey.toString()));
        if (!exists(metadataKey)) {
            throw new MetadataDoesNotExistException("ComponentParameter", metadataKey);
        }
        String deleteStatement = deleteStatement(metadataKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ComponentParameterKey componentParameterKey){
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ComponentParameters") +
                " WHERE COMP_ID = " +
                SQLTools.GetStringForSQL(componentParameterKey.getComponentId()) +
                "AND COMP_VRS_NB = " +
                SQLTools.GetStringForSQL(componentParameterKey.getComponentVersionNb()) +
                "AND COMP_PAR_NM = " +
                SQLTools.GetStringForSQL(componentParameterKey.getComponentParameterName()) + ";";
    }

    @Override
    public void insert(ComponentParameter metadata) throws MetadataAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting ComponentParameter {0}.", metadata.getMetadataKey().toString()));
        if (exists(metadata.getMetadataKey())) {
            throw new MetadataAlreadyExistsException("ComponentParameter", metadata.getMetadataKey());
        }
        String insertQuery = getInsertStatement(metadata);
        getMetadataRepository().executeUpdate(insertQuery);
    }

    // Insert
    public String getInsertStatement(ComponentParameter componentParameter) {
        String sql = "";

        sql += "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ComponentParameters");
        sql += " (COMP_ID, COMP_VRS_NB, COMP_PAR_NM, COMP_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(componentParameter.getMetadataKey().getComponentId());
        sql += ",";
        sql += SQLTools.GetStringForSQL(componentParameter.getMetadataKey().getComponentVersionNb());
        sql += ",";
        sql += SQLTools.GetStringForSQL(componentParameter.getMetadataKey().getComponentParameterName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(componentParameter.getValue());
        sql += ")";
        sql += ";";

        return sql;
    }

    private Optional<ComponentParameter> getComponentParameter(String componentId, String componentParameterName, long componentVersionNumber) {
        CachedRowSet crsComponentParameter = null;
        String queryComponentParameter = "select COMP_ID, COMP_PAR_NM, COMP_PAR_VAL from " + getMetadataRepository().getTableNameByLabel("ComponentParameters")
                + " where COMP_ID = " + SQLTools.GetStringForSQL(componentId) + " and COMP_PAR_NM = '" + componentParameterName + "'" + " and COMP_VRS_NB = " + componentVersionNumber;
        crsComponentParameter = getMetadataRepository().executeQuery(queryComponentParameter, "reader");
        try {
            if (crsComponentParameter.size()==0){
                return Optional.empty();
            }
            crsComponentParameter.next();
            ComponentParameterKey componentParameterKey = new ComponentParameterKey(componentId,
                    componentVersionNumber, componentParameterName);
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

    // Getters and Setters
    public ComponentParameter getComponentParameter() {
        return componentParameter;
    }

    public void setComponentParameter(ComponentParameter componentParameter) {
        this.componentParameter = componentParameter;
    }

    public ComponentVersion getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(ComponentVersion componentVersion) {
        this.componentVersion = componentVersion;
    }

}