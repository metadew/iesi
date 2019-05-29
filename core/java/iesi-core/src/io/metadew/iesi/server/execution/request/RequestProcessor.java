package io.metadew.iesi.server.execution.request;

import io.metadew.iesi.framework.execution.FrameworkExecution;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

public class RequestProcessor {

    private FrameworkExecution frameworkExecution;
    public CachedRowSet crs;
    // fields
    public int executionId;
    public int que_id;
    public String request_type;
    public String eng_cfg;
    public String env_nm;
    public String script_nm;
    public int context_id;
    public int scope_id;

    public RequestProcessor(FrameworkExecution frameworkExecution, int executionId, int que_id) {
        this.setFrameworkExecution(frameworkExecution);
        this.executionId = executionId;
        this.que_id = que_id;
        this.setProcessor();
        this.getFields();
    }

    public void setProcessor() {
        String QueryString = "update " + "PRC_CTL"
                + " set request_id = " + this.que_id + " where exe_id = " + this.executionId;
        this.getFrameworkExecution().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);

        QueryString = "update " + "PRC_REQ"
                + " set exe_id = " + this.executionId + " where request_id = " + this.que_id;
        this.getFrameworkExecution().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);
    }

    public void clearProcessor() {
        String QueryString = "update " + "PRC_CTL"
                + " set request_id = -1 where exe_id = " + this.executionId;
        this.getFrameworkExecution().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);
    }

    public void removeFromQueue() {
        String QueryString = "delete from " + "PRC_REQ"
                + " where request_id = " + this.que_id;
        this.getFrameworkExecution().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);
    }

    public void getFields() {
        String QueryString = "";
        CachedRowSet crs = null;
        QueryString = "select request_id, request_type, script_nm, env_nm, prc_id from "
                + "PRC_REQ" + " where request_id = "
                + this.que_id;
        crs = this.getFrameworkExecution().getExecutionServerRepositoryConfiguration().executeQuery(QueryString, "reader");

        try {
            while (crs.next()) {
                this.request_type = crs.getString("REQUEST_TYPE");
                this.env_nm = crs.getString("ENV_NM");
                this.script_nm = crs.getString("SCRIPT_NM");
            }

            crs.close();
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
    }

    public void execute() {
        // Execution logic
        System.out.println("Execution logic here");
        this.removeFromQueue();
        this.clearProcessor();

    }

    // Getters and setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

}
