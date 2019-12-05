package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.ComponentBuild;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.key.ComponentBuildKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.metadata.execution.MetadataControl;
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

public class ComponentBuildConfiguration extends Configuration<ComponentBuild, ComponentBuildKey> {

    private ComponentBuild componentBuild;

    private static final Logger LOGGER = LogManager.getLogger();
    private static ComponentBuildConfiguration INSTANCE;

    public synchronized static ComponentBuildConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComponentBuildConfiguration();
        }
        return INSTANCE;
    }

    // Constructors
    public ComponentBuildConfiguration(ComponentBuild componentBuild) {
        this.setComponentBuild(componentBuild);
    }

    public ComponentBuildConfiguration() {
    }

    @Override
    public Optional<ComponentBuild> get(ComponentBuildKey metadataKey) {
        return getComponentBuild(metadataKey.getComponentId(),
                metadataKey.getComponentVersionNb(), metadataKey.getComponentBuildName());
    }

    @Override
    public List<ComponentBuild> getAll() {
        try {
            List<ComponentBuild> componentBuilds = new ArrayList<>();
            String query = "select * from "
                    + getMetadataRepository().getTableNameByLabel("ComponentVersionBuilds") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                ComponentBuildKey componentBuildKey = new ComponentBuildKey(
                        cachedRowSet.getString("COMP_ID"),
                        cachedRowSet.getLong("COMP_VRS_NB"),
                        cachedRowSet.getString("COMP_BLD_NM"));
                componentBuilds.add(new ComponentBuild(
                        componentBuildKey,
                        cachedRowSet.getString("COMP_BLD_DSC")));
            }
            return componentBuilds;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ComponentBuildKey metadataKey) throws MetadataDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting Component {0}.", metadataKey.toString()));
        if (!exists(metadataKey)) {
            throw new MetadataDoesNotExistException("Component", metadataKey);
        }
        String deleteStatement = deleteStatement(metadataKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ComponentBuildKey componentBuildKey){
        return "DELETE FROM " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ComponentVersionBuilds") +
                " WHERE COMP_ID = " +
                SQLTools.GetStringForSQL(componentBuildKey.getComponentId()) +
                "AND WHERE COMP_VRS_NB = " +
                SQLTools.GetStringForSQL(componentBuildKey.getComponentVersionNb()) +
                "AND WHERE COMP_BLD_NM = " +
                SQLTools.GetStringForSQL(componentBuildKey.getComponentBuildName()) + ";";
    }

    @Override
    public void insert(ComponentBuild metadata) throws MetadataAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting ComponentBuild {0}.", metadata.getMetadataKey().toString()));
        if (exists(metadata.getMetadataKey())) {
            throw new MetadataAlreadyExistsException("ComponentBuild", metadata.getMetadataKey());
        }
        String insertQuery = getInsertStatement(metadata);
        getMetadataRepository().executeUpdate(insertQuery);
    }

    // Insert
    public String getInsertStatement(ComponentBuild componentBuild) {
        String sql = "";
        ComponentBuildKey componentBuildKey = componentBuild.getMetadataKey();

        sql += "INSERT INTO " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ComponentVersionBuilds");
        sql += " (COMP_ID, COMP_VRS_NB, COMP_BLD_NM, COMP_BLD_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(componentBuildKey.getComponentId());
        sql += ",";
        sql += SQLTools.GetStringForSQL(componentBuildKey.getComponentVersionNb());
        sql += ",";
        sql += SQLTools.GetStringForSQL(componentBuild.getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(componentBuild.getDescription());
        sql += ")";
        sql += ";";

        return sql;
    }

    public Optional<ComponentBuild> getComponentBuild(String componentId, long componentVersionName, String componentBuildName) {
        String queryComponentBuild = "select COMP_ID, COMP_VRS_NB, COMP_BLD_NM, COMP_BLD_DSC from " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ComponentVersionBuilds")
                + " where COMP_ID = " + SQLTools.GetStringForSQL(componentId) + " and COMP_VRS_NM = '" + componentVersionName + "'" + " and COMP_BLD_NM = '" + componentBuildName + "'";
        CachedRowSet crsComponentBuild = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryComponentBuild, "reader");
        try {
            if (crsComponentBuild.size()==0){
                return Optional.empty();
            }
            crsComponentBuild.next();
            ComponentBuildKey componentBuildKey = new ComponentBuildKey(
                    crsComponentBuild.getString("COMP_ID"),
                    crsComponentBuild.getLong("COMP_VRS_NB"),
                    crsComponentBuild.getString("COMP_BLD_NM")
            );
            ComponentBuild componentBuild = new ComponentBuild(
                    componentBuildKey,
                    crsComponentBuild.getString("COMP_BLD_DSC")
            );
            crsComponentBuild.close();
            return Optional.of(componentBuild);
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            throw new RuntimeException(e);
        }
    }

    // Getters and Setters
    public ComponentBuild getComponentBuild() {
        return componentBuild;
    }

    public void setComponentBuild(ComponentBuild componentBuild) {
        this.componentBuild = componentBuild;
    }

}