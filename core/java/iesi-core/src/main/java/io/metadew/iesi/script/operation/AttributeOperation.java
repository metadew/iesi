package io.metadew.iesi.script.operation;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.Level;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

/**
 * Operation to manage component attribute when a component
 * has been specific in the action
 *
 * @author peter.billen
 */
public class AttributeOperation {

    private Properties properties;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;
    private ActionExecution actionExecution;
    private String type;
    private String name;

    // Constructors
    public AttributeOperation(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ActionExecution actionExecution, String type, String name) {
        this.setFrameworkExecution(frameworkExecution);
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
                    + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentAttributes")
                    + " a inner join "
                    + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Components")
                    + " b on a.comp_id = b.comp_id where b.comp_nm = '" + this.getName() + "'";
        }

        // Set attribute values
        CachedRowSet crs = null;
        this.getExecutionControl().logMessage(this.getActionExecution(), "component.name=" + name, Level.DEBUG);
        crs = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                String key = crs.getString("COMP_ATT_NM");
                String value = crs.getString("COMP_ATT_VAL");
                this.getProperties().put(key, value);
                this.getExecutionControl().logMessage(this.getActionExecution(), "attribute.name=" + key, Level.DEBUG);
                this.getExecutionControl().logMessage(this.getActionExecution(), "attribute.name=" + value, Level.DEBUG);
            }
            crs.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
    }


    public String getProperty(String input) {
        String output = this.getProperties().getProperty(input);
        if (output == null) {
            throw new RuntimeException("Unknown value lookup requested: " + input);
        }
        return output;
    }

    // Getters and setters
    private Properties getProperties() {
        return properties;
    }

    private void setProperties(Properties properties) {
        this.properties = properties;
    }


    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }


    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
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