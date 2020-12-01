package io.metadew.iesi.metadata.configuration.impersonation;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.impersonation.Impersonation;
import io.metadew.iesi.metadata.definition.impersonation.ImpersonationParameter;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationParameterKey;
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

public class ImpersonationConfiguration extends Configuration<Impersonation, ImpersonationKey> {

    private Impersonation impersonation;

    private static final Logger LOGGER = LogManager.getLogger();
    private static ImpersonationConfiguration INSTANCE;

    public synchronized static ImpersonationConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ImpersonationConfiguration();
        }
        return INSTANCE;
    }

    // Constructors
    private ImpersonationConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
        ImpersonationParameterConfiguration.getInstance().init(metadataRepository);
    }

    @Override
    public Optional<Impersonation> get(ImpersonationKey metadataKey) {
        return getImpersonation(metadataKey.getName());
    }

    @Override
    public List<Impersonation> getAll() {
        return getAllImpersonations();
    }

    @Override
    public void delete(ImpersonationKey metadataKey) {
        LOGGER.trace(MessageFormat.format("Deleting Impersonation {0}.", metadataKey.toString()));
        if (!exists(metadataKey)) {
            throw new MetadataDoesNotExistException(metadataKey);
        }
        List<String> deleteStatement = getDeleteStatement(metadataKey.getName());
        getMetadataRepository().executeBatch(deleteStatement);
    }

    @Override
    public void insert(Impersonation metadata) {
        insertImpersonation(metadata);
    }

    public ImpersonationConfiguration(Impersonation impersonation) {
        this.setImpersonation(impersonation);
    }

    // Methods
    public Optional<Impersonation> getImpersonation(String impersonationName) {
        Impersonation impersonation = null;
        String queryImpersonation = "select IMP_NM, IMP_DSC from "
                + getMetadataRepository().getTableNameByLabel("Impersonations")
                + " where IMP_NM = '" + impersonationName + "'";
        CachedRowSet crsImpersonation = getMetadataRepository().executeQuery(queryImpersonation, "reader");
        try {
            while (crsImpersonation.next()) {
                String description = crsImpersonation.getString("IMP_DSC");

                // Get parameters
                String queryImpersonationParameters = "select IMP_NM, CONN_NM from "
                        + getMetadataRepository().getTableNameByLabel("ImpersonationParameters")
                        + " where IMP_NM = '" + impersonationName + "'";
                CachedRowSet crsImpersonationParameters = getMetadataRepository()
                        .executeQuery(queryImpersonationParameters, "reader");
                List<ImpersonationParameter> impersonationParameterList = getAllLinkedImpersonationParameters(impersonationName);
                crsImpersonationParameters.close();
                impersonation = new Impersonation(new ImpersonationKey(impersonationName), description, impersonationParameterList);
            }
            crsImpersonation.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(impersonation);
    }

    public List<ImpersonationParameter> getAllLinkedImpersonationParameters(String impersonationName) {
        List<ImpersonationParameter> impersonationParameters = new ArrayList<>();
        String selectQuery = "select * from "
                + getMetadataRepository().getTableNameByLabel("ImpersonationParameters")
                + " where IMP_NM = '" + impersonationName + "'";
        CachedRowSet crsImpersonationParameters = getMetadataRepository().executeQuery(selectQuery, "reader");
        try {
            while (crsImpersonationParameters.next()) {
                ImpersonationParameterKey impersonationParameterKey = new ImpersonationParameterKey(
                        new ImpersonationKey(impersonationName),
                        crsImpersonationParameters.getString("CONN_NM"));
                impersonationParameters.add(new ImpersonationParameter(impersonationParameterKey,
                        crsImpersonationParameters.getString("CONN_IMP_NM"),
                        crsImpersonationParameters.getString("CONN_IMP_DSC")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return impersonationParameters;


    }

    public List<Impersonation> getAllImpersonations() {
        //TODO fix logging
        //frameworkExecution.getFrameworkLog().log("Getting all impersonations {0}.", Level.TRACE);
        List<Impersonation> impersonations = new ArrayList<>();
        String query = "select IMP_NM from " + getMetadataRepository().getTableNameByLabel("Impersonations")
                + " order by IMP_NM ASC";
        CachedRowSet crs = getMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                String impersonationName = crs.getString("IMP_NM");
                getImpersonation(impersonationName).ifPresent(impersonations::add);
            }
            crs.close();
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return impersonations;
    }

    public boolean exists(Impersonation impersonation) {
        String queryImpersonation = "select * from "
                + getMetadataRepository().getTableNameByLabel("Impersonations")
                + " where IMP_NM = '"
                + impersonation.getMetadataKey().getName() + "'";

        CachedRowSet crsEnvironment = getMetadataRepository().executeQuery(queryImpersonation, "reader");
        return crsEnvironment.size() == 1;
    }

    public void deleteImpersonation(Impersonation impersonation) {
        deleteImpersonation(impersonation.getMetadataKey());
    }

    public void deleteImpersonation(ImpersonationKey impersonationKey) {
        //TODO fix logging
        //frameworkExecution.getFrameworkLog().log(MessageFormat.format("Deleting impersonation {0}.", impersonation.getScriptName()), Level.TRACE);
        if (!exists(impersonationKey)) {
            throw new MetadataDoesNotExistException(impersonationKey);

        }
        List<String> query = getDeleteStatement(impersonationKey.getName());
        getMetadataRepository().executeBatch(query);
    }

    public void deleteAllImpersonations() {
        //TODO fix logging
        //frameworkExecution.getFrameworkLog().log("Deleting all impersonations", Level.TRACE);
        List<String> query = getDeleteAllStatement();
        getMetadataRepository().executeBatch(query);
    }

    private List<String> getDeleteAllStatement() {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("Impersonations") + ";");
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("ImpersonationParameters") + ";");
        return queries;
    }

    public void insertImpersonation(Impersonation impersonation) {
        //TODO fix logging
        //frameworkExecution.getFrameworkLog().log(MessageFormat.format("Inserting impersonation {0}.", impersonation.getScriptName()), Level.TRACE);
        if (exists(impersonation)) {
            throw new MetadataAlreadyExistsException(impersonation);
        }

        for (ImpersonationParameter impersonationParameter : impersonation.getParameters()) {
            ImpersonationParameterConfiguration.getInstance().insert(impersonationParameter);
        }
        String query = "INSERT INTO " + getMetadataRepository().getTableNameByLabel("Impersonations") + " (IMP_NM, IMP_DSC) VALUES (" +
                SQLTools.getStringForSQL(impersonation.getMetadataKey().getName()) + "," +
                SQLTools.getStringForSQL(impersonation.getDescription()) + ");";
        getMetadataRepository().executeUpdate(query);
    }

    public void updateImpersonation(Impersonation impersonation) {
        //TODO fix logging
        //frameworkExecution.getFrameworkLog().log(MessageFormat.format("Updating impersonation {0}.", impersonation.getScriptName()), Level.TRACE);
        deleteImpersonation(impersonation);
        insertImpersonation(impersonation);
    }

    public List<String> getDeleteStatement(String impersonationName) {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("Impersonations") +
                " WHERE IMP_NM = " + SQLTools.getStringForSQL(impersonationName) + ";");
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("ImpersonationParameters") +
                " WHERE IMP_NM = "
                + SQLTools.getStringForSQL(impersonationName) + ";");
        return queries;
    }

    public List<String> getInsertStatement(Impersonation impersonation) {
        ImpersonationParameterConfiguration impersonationParameterConfiguration = ImpersonationParameterConfiguration.getInstance();
        List<String> queries = new ArrayList<>();
        queries.add("INSERT INTO " + getMetadataRepository().getTableNameByLabel("Impersonations") + " (IMP_NM, IMP_DSC) VALUES (" +
                SQLTools.getStringForSQL(impersonation.getMetadataKey().getName()) + "," +
                SQLTools.getStringForSQL(impersonation.getDescription()) + ");");
        for (ImpersonationParameter impersonationParameter : impersonation.getParameters()) {
            queries.add(impersonationParameterConfiguration.getInsertStatement(impersonation.getMetadataKey().getName(), impersonationParameter));
        }
        return queries;
    }

    private String getParameterInsertStatements(Impersonation impersonation) {
        String result = "";

        // Catch null parameters
        if (impersonation.getParameters() == null)
            return result;

        for (ImpersonationParameter impersonationParameter : impersonation.getParameters()) {
            ImpersonationParameterConfiguration impersonationParameterConfiguration = ImpersonationParameterConfiguration.getInstance();
            if (!result.equalsIgnoreCase(""))
                result += "\n";

            result += impersonationParameterConfiguration.getInsertStatement(impersonation.getMetadataKey().getName(), impersonationParameter);
        }

        return result;
    }


    // Delete

    public String getDeleteStatement() {
        String sql = "";

        sql += "DELETE FROM " + getMetadataRepository().getTableNameByLabel("Impersonations");
        sql += " WHERE IMP_NM = "
                + SQLTools.getStringForSQL(this.getImpersonation().getMetadataKey().getName());
        sql += ";";
        sql += "\n";
        sql += "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ImpersonationParameters");
        sql += " WHERE IMP_NM = "
                + SQLTools.getStringForSQL(this.getImpersonation().getMetadataKey().getName());
        sql += ";";
        sql += "\n";

        return sql;

    }
    // Insert

    public String getInsertStatement() {
        String sql = "";

        if (this.exists()) {
            sql += this.getDeleteStatement();
        }

        sql += "INSERT INTO " + getMetadataRepository().getTableNameByLabel("Impersonations");
        sql += " (IMP_NM, IMP_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.getStringForSQL(this.getImpersonation().getMetadataKey().getName());
        sql += ",";
        sql += SQLTools.getStringForSQL(this.getImpersonation().getDescription());
        sql += ")";
        sql += ";";

        // add Parameters
        String sqlParameters = this.getParameterInsertStatements(this.getImpersonation().getMetadataKey().getName());
        if (!sqlParameters.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlParameters;
        }

        return sql;
    }

    private String getParameterInsertStatements(String impersonationName) {
        String result = "";

        // Catch null parameters
        if (this.getImpersonation().getParameters() == null)
            return result;

        for (ImpersonationParameter impersonationParameter : this.getImpersonation().getParameters()) {
            ImpersonationParameterConfiguration impersonationParameterConfiguration = ImpersonationParameterConfiguration.getInstance();
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += impersonationParameterConfiguration.getInsertStatement(impersonationName);
        }

        return result;
    }

    // Exists
    public boolean exists() {
        return true;
    }

    // Getters and Setters
    public Impersonation getImpersonation() {
        return impersonation;
    }

    public void setImpersonation(Impersonation impersonation) {
        this.impersonation = impersonation;
    }

}