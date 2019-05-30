package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.ImpersonationParameter;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ImpersonationParameterConfiguration {

    private ImpersonationParameter impersonationParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public ImpersonationParameterConfiguration(ImpersonationParameter impersonationParameter, FrameworkInstance frameworkInstance) {
        this.setImpersonationParameter(impersonationParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public ImpersonationParameterConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public String getInsertStatement(String impersonationName, ImpersonationParameter impersonationParameter) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("ImpersonationParameters");
        sql += " (IMP_NM, CONN_NM, CONN_IMP_NM, CONN_IMP_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(impersonationName);
        sql += ",";
        sql += SQLTools.GetStringForSQL(impersonationParameter.getConnection());
        sql += ",";
        sql += SQLTools.GetStringForSQL(impersonationParameter.getImpersonatedConnection());
        sql += ",";
        sql += SQLTools.GetStringForSQL(impersonationParameter.getDescription());
        sql += ")";
        sql += ";";

        return sql;
    }

    // Insert
    public String getInsertStatement(String impersonationName) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("ImpersonationParameters");
        sql += " (IMP_NM, CONN_NM, CONN_IMP_NM, CONN_IMP_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(impersonationName);
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getImpersonationParameter().getConnection());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getImpersonationParameter().getImpersonatedConnection());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getImpersonationParameter().getDescription());
        sql += ")";
        sql += ";";

        return sql;
    }

    public ImpersonationParameter getImpersonationParameter(String impersonationName, String impersonationParameterName) {
        ImpersonationParameter impersonationParameter = new ImpersonationParameter();
        CachedRowSet crsImpersonationParameter = null;
        String queryImpersonationParameter = "select IMP_NM, CONN_NM, CONN_IMP_NM, CONN_IMP_DSC from " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("ImpersonationParameters")
                + " where IMP_NM = '" + impersonationName + "' and CONN_NM = '" + impersonationParameterName + "'";
        crsImpersonationParameter = this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().executeQuery(queryImpersonationParameter, "reader");
        try {
            while (crsImpersonationParameter.next()) {
                impersonationParameter.setConnection(impersonationParameterName);
                impersonationParameter.setImpersonatedConnection(crsImpersonationParameter.getString("CONN_IMP_NM"));
                impersonationParameter.setDescription(crsImpersonationParameter.getString("CONN_IMP_DSC"));
            }
            crsImpersonationParameter.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return impersonationParameter;
    }


    // Getters and Setters
    public ImpersonationParameter getImpersonationParameter() {
        return impersonationParameter;
    }

    public void setImpersonationParameter(ImpersonationParameter impersonationParameter) {
        this.impersonationParameter = impersonationParameter;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}