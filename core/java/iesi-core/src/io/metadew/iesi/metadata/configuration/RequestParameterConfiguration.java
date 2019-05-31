package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.RequestParameter;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class RequestParameterConfiguration {

    private RequestParameter requestParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public RequestParameterConfiguration(RequestParameter requestParameter, FrameworkInstance frameworkInstance) {
        this.setRequestParameter(requestParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public RequestParameterConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public String getInsertStatement(String requestId, RequestParameter requestParameter) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkInstance().getExecutionServerRepositoryConfiguration()
                .getTableNameByLabel("RequestParameters");
        sql += " (REQUEST_ID, REQUEST_PAR_TYP_NM, REQUEST_PAR_NM, REQUEST_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(requestId);
        sql += ",";
        sql += SQLTools.GetStringForSQL(requestParameter.getType());
        sql += ",";
        sql += SQLTools.GetStringForSQL(requestParameter.getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(requestParameter.getValue());
        sql += ")";
        sql += ";";

        return sql;
    }


    // Insert
    public String getInsertStatement(String requestId) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkInstance().getExecutionServerRepositoryConfiguration()
                .getTableNameByLabel("RequestParameters");
        sql += " (REQUEST_ID, REQUEST_PAR_TYP_NM, REQUEST_PAR_NM, REQUEST_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(requestId);
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getRequestParameter().getType());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getRequestParameter().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getRequestParameter().getValue());
        sql += ")";
        sql += ";";

        return sql;
    }

    public RequestParameter getRequestParameter(String requestId, String requestParameterType, String requestParameterName) {
        RequestParameter requestParameter = new RequestParameter();
        CachedRowSet crsRequestParameter = null;
        String queryRequestParameter = "select REQUEST_ID, REQUEST_PAR_TYP_NM, REQUEST_PAR_NM, REQUEST_PAR_VAL from "
                + this.getFrameworkInstance().getExecutionServerRepositoryConfiguration()
                .getTableNameByLabel("RequestParameters")
                + " where REQUEST_ID = '" + requestId + "' and REQUEST_PAR_TYP_NM = '" + requestParameterType + "' and REQUEST_PAR_NM = '" + requestParameterName + "'";
        crsRequestParameter = this.getFrameworkInstance().getExecutionServerRepositoryConfiguration()
                .executeQuery(queryRequestParameter, "reader");
        try {
            while (crsRequestParameter.next()) {
            	requestParameter.setType(requestParameterType);
                requestParameter.setName(requestParameterName);
                requestParameter.setValue(crsRequestParameter.getString("REQUEST_PAR_VAL"));
            }
            crsRequestParameter.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return requestParameter;
    }

    public Optional<String> getRequestParameterValue(String requestId, String requestParameterType, String requestParameterName) {
        String output = null;
        CachedRowSet crsRequestParameter = null;
        String queryRequestParameter = "select REQUEST_ID, REQUEST_PAR_TYP_NM, REQUEST_PAR_NM, REQUEST_PAR_VAL from "
                + this.getFrameworkInstance().getExecutionServerRepositoryConfiguration()
                .getTableNameByLabel("RequestParameters")
                + " where REQUEST_ID = '" + requestId + "' and REQUEST_PAR_TYP_NM = '" + requestParameterType + "'" + "' and REQUEST_PAR_NM = '" + requestParameterName + "'";
        crsRequestParameter = this.getFrameworkInstance().getExecutionServerRepositoryConfiguration()
                .executeQuery(queryRequestParameter, "reader");
        try {
            while (crsRequestParameter.next()) {
                output = crsRequestParameter.getString("REQUEST_PAR_VAL");
            }
            crsRequestParameter.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            return Optional.empty();
        }

        return Optional.ofNullable(output);
    }

    // Getters and Setters
    public RequestParameter getRequestParameter() {
        return requestParameter;
    }

    public void setRequestParameter(RequestParameter requestParameter) {
        this.requestParameter = requestParameter;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}