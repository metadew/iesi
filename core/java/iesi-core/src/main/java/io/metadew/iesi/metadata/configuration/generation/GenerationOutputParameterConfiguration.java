//package io.metadew.iesi.metadata.configuration.generation;
//
//import io.metadew.iesi.connection.tools.SQLTools;
//import io.metadew.iesi.framework.configuration.metadata.repository.MetadataRepositoryConfiguration;
//import io.metadew.iesi.metadata.definition.generation.GenerationOutputParameter;
//
//import javax.sql.rowset.CachedRowSet;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.sql.SQLException;
//
//public class GenerationOutputParameterConfiguration {
//
//    private GenerationOutputParameter generationOutputParameter;
//
//    // Constructors
//    public GenerationOutputParameterConfiguration(GenerationOutputParameter generationOutputParameter) {
//        this.setgenerationOutputParameter(generationOutputParameter);
//    }
//
//    public GenerationOutputParameterConfiguration() {
//    }
//
//    // Insert
//    public String getInsertStatement(String generationName, String generationOutputName) {
//        String sql = "";
//
//        sql += "INSERT INTO "
//                + MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository().getTableNameByLabel("GenerationOutputParameters");
//        sql += " (GEN_OUT_ID, GEN_OUT_PAR_NM, GEN_OUT_PAR_VAL) ";
//        sql += "VALUES ";
//        sql += "(";
//        sql += "(" + SQLTools.GetLookupIdStatement(MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository().getTableNameByLabel("GenerationOutputs"),
//                "GEN_OUT_ID",
//                "where GEN_OUT_NM = '" + generationOutputName + "' and GEN_ID = ("
//                        + SQLTools.GetLookupIdStatement(MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository().getTableNameByLabel("Generations"),
//                        "GEN_ID",
//                        "GEN_NM",
//                        generationName))
//                + "))";
//        sql += ",";
//        sql += SQLTools.GetStringForSQL(this.getgenerationOutputParameter().getName());
//        sql += ",";
//        sql += SQLTools.GetStringForSQL(this.getgenerationOutputParameter().getValue());
//        sql += ")";
//        sql += ";";
//
//        return sql;
//    }
//
//    public GenerationOutputParameter getGenerationOutputParameter(long generationOutputId, String generationOutputParameterName) {
//        GenerationOutputParameter generationOutputParameter = new GenerationOutputParameter();
//        CachedRowSet crsGenerationOutputParameter = null;
//        String queryGenerationOutputParameter = "select GEN_OUT_ID, GEN_OUT_PAR_NM, GEN_OUT_PAR_VAL from " + MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository().getTableNameByLabel("GenerationOutputParameters")
//                + " where GEN_OUT_ID = " + generationOutputId + " and GEN_OUT_PAR_NM = '" + generationOutputParameterName + "'";
//        crsGenerationOutputParameter = MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository().executeQuery(queryGenerationOutputParameter, "reader");
//        try {
//            while (crsGenerationOutputParameter.next()) {
//                generationOutputParameter.setName(generationOutputParameterName);
//                generationOutputParameter.setValue(crsGenerationOutputParameter.getString("GEN_OUT_PAR_VAL"));
//            }
//            crsGenerationOutputParameter.close();
//        } catch (SQLException e) {
//            StringWriter StackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(StackTrace));
//        }
//        return generationOutputParameter;
//    }
//
//    // Getters and Setters
//    public GenerationOutputParameter getgenerationOutputParameter() {
//        return generationOutputParameter;
//    }
//
//    public void setgenerationOutputParameter(GenerationOutputParameter generationOutputParameter) {
//        this.generationOutputParameter = generationOutputParameter;
//    }
//
//}