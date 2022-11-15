package io.metadew.iesi.script.operation;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.Level;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;

/**
 * Operation to manage component attribute when a component
 * has been specific in the action
 *
 * @author peter.billen
 */
public class AttributeOperation {

    private Properties properties;
    private ExecutionControl executionControl;
    private ActionExecution actionExecution;
    private String type;
    private String name;

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration = SpringContext.getBean(MetadataRepositoryConfiguration.class);

    // Constructors
    public AttributeOperation(ExecutionControl executionControl, ActionExecution actionExecution, String type, String name) {
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setType(type);
        this.setName(name);

        // initialize properties
        this.setProperties(new Properties());

        // Get component attributes
        String query = "";
        if (this.getType().equals("component")) {
            query = "select a.comp_id, a.comp_att_nm, a.comp_att_val from "
                    + metadataRepositoryConfiguration.getDesignMetadataRepository().getTableNameByLabel("ComponentAttributes")
                    + " a inner join "
                    + metadataRepositoryConfiguration.getDesignMetadataRepository().getTableNameByLabel("Components")
                    + " b on a.comp_id = b.comp_id where b.comp_nm = '" + this.getName() + "'";
        }

        // Set attribute values
        CachedRowSet crs = null;
        this.getExecutionControl().logMessage("component.name=" + name, Level.DEBUG);
        crs = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                String key = crs.getString("COMP_ATT_NM");
                String value = SQLTools.getStringFromSQLClob(crs, "COMP_ATT_VAL");
                properties.put(key, value);
                this.getExecutionControl().logMessage("attribute.name=" + key, Level.DEBUG);
                this.getExecutionControl().logMessage("attribute.name=" + value, Level.DEBUG);
            }
            crs.close();
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
    }


    public Optional<String> getProperty(String input) {
        return Optional.ofNullable(properties.getProperty(input));
    }

    // Getters and setters

    private void setProperties(Properties properties) {
        this.properties = properties;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type.trim().toLowerCase();
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public ExecutionControl getExecutionControl() {
        return executionControl;
    }


    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }


    public ActionExecution getActionExecution() {
        return actionExecution;
    }


    public void setActionExecution(ActionExecution actionExecution) {
        this.actionExecution = actionExecution;
    }

}