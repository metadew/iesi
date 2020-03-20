//package io.metadew.iesi.metadata.configuration.repository;
//
//import io.metadew.iesi.connection.tools.SQLTools;
//import io.metadew.iesi.framework.configuration.metadata.repository.MetadataRepositoryConfiguration;
//import io.metadew.iesi.metadata.definition.repository.RepositoryInstanceParameter;
//
//import javax.sql.rowset.CachedRowSet;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.sql.SQLException;
//
//public class RepositoryInstanceParameterConfiguration {
//
//    private RepositoryInstanceParameter repositoryParameter;
//
//    // Constructors
//    public RepositoryInstanceParameterConfiguration(RepositoryInstanceParameter repositoryParameter) {
//        this.setRepositoryInstanceParameter(repositoryParameter);
//    }
//
//    public RepositoryInstanceParameterConfiguration() {
//    }
//
//    // Insert
//    public String getInsertStatement(String repositoryName, String repositoryInstanceName) {
//        String sql = "";
//
//        sql += "INSERT INTO "
//                + MetadataRepositoryConfiguration.getInstance().getConnectivityMetadataRepository()
//                .getTableNameByLabel("RepositoryInstanceParameters");
//        sql += " (REPO_ID, REPO_INST_ID, REPO_INST_PAR_NM, REPO_INST_PAR_VAL) ";
//        sql += "VALUES ";
//        sql += "(";
//        sql += "(" + SQLTools.GetLookupIdStatement(
//                MetadataRepositoryConfiguration.getInstance().getConnectivityMetadataRepository()
//                        .getTableNameByLabel("Repositories"),
//                "REPO_ID", "where REPO_NM = '" + repositoryName) + "')";
//        sql += ",";
//        sql += "(" + SQLTools.GetLookupIdStatement(
//                MetadataRepositoryConfiguration.getInstance().getConnectivityMetadataRepository()
//                        .getTableNameByLabel("RepositoryInstances"),
//                "REPO_INST_ID",
//                "where REPO_ID = (" + SQLTools.GetLookupIdStatement(
//                        MetadataRepositoryConfiguration.getInstance().getConnectivityMetadataRepository()
//                                .getTableNameByLabel("Repositories"),
//                        "REPO_ID", "where REPO_NM = '" + repositoryName) + "') and REPO_INST_NM = '"
//                        + repositoryInstanceName)
//                + "')";
//        sql += ",";
//        sql += SQLTools.GetStringForSQL(this.getRepositoryInstanceParameter().getName());
//        sql += ",";
//        sql += SQLTools.GetStringForSQL(this.getRepositoryInstanceParameter().getValue());
//        sql += ")";
//        sql += ";";
//
//        return sql;
//    }
//
//    public RepositoryInstanceParameter getRepositoryInstanceParameter(long repositoryId, long repositoryInstanceId,
//                                                                      String repositoryInstanceParameterName) {
//        RepositoryInstanceParameter repositoryInstanceParameter = new RepositoryInstanceParameter();
//        CachedRowSet crsRepositoryInstanceParameter = null;
//        String queryRepositoryInstanceParameter = "select REPO_ID, REPO_INST_ID, REPO_INST_PAR_NM, REPO_INST_PAR_VAL from "
//                + MetadataRepositoryConfiguration.getInstance().getConnectivityMetadataRepository()
//                .getTableNameByLabel("RepositoryInstanceParameters")
//                + " where REPO_ID = " + repositoryId + " and REPO_INST_ID = " + repositoryInstanceId + " and REPO_INST_PAR_NM = '" + repositoryInstanceParameterName + "'";
//        crsRepositoryInstanceParameter = MetadataRepositoryConfiguration.getInstance()
//                .getConnectivityMetadataRepository().executeQuery(queryRepositoryInstanceParameter, "reader");
//        try {
//            while (crsRepositoryInstanceParameter.next()) {
//                repositoryInstanceParameter.setName(repositoryInstanceParameterName);
//                repositoryInstanceParameter.setValue(crsRepositoryInstanceParameter.getString("REPO_INST_PAR_VAL"));
//            }
//            crsRepositoryInstanceParameter.close();
//        } catch (SQLException e) {
//            StringWriter StackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(StackTrace));
//        }
//        return repositoryInstanceParameter;
//    }
//
//    // Getters and Setters
//    public RepositoryInstanceParameter getRepositoryInstanceParameter() {
//        return repositoryParameter;
//    }
//
//    public void setRepositoryInstanceParameter(RepositoryInstanceParameter repositoryParameter) {
//        this.repositoryParameter = repositoryParameter;
//    }
//
//}