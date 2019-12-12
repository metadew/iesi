package io.metadew.iesi.metadata.configuration.impersonation;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.impersonation.ImpersonationParameter;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationParameterKey;
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

public class ImpersonationParameterConfiguration extends Configuration<ImpersonationParameter, ImpersonationParameterKey> {

    private ImpersonationParameter impersonationParameter;

    private static final Logger LOGGER = LogManager.getLogger();
    private static ImpersonationParameterConfiguration INSTANCE;

    public synchronized static ImpersonationParameterConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ImpersonationParameterConfiguration();
        }
        return INSTANCE;
    }

    public void init(MetadataRepository metadataRepository){
        setMetadataRepository(metadataRepository);
    }

    private ImpersonationParameterConfiguration() {
    }

    @Override
    public Optional<ImpersonationParameter> get(ImpersonationParameterKey metadataKey) {
        return getImpersonationParameter(metadataKey.getImpersonationName(), metadataKey.getImpersonationParameterName());
    }

    @Override
    public List<ImpersonationParameter> getAll() {
        try {
            List<ImpersonationParameter> impersonationParameters = new ArrayList<>();
            String query = "select IMP_NM, CONN_NM, CONN_IMP_NM, CONN_IMP_DSC from "
                    + getMetadataRepository().getTableNameByLabel("ImpersonationParameters") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                impersonationParameters.add(new ImpersonationParameter(new ImpersonationParameterKey(
                        cachedRowSet.getString("IMP_NM"),
                        cachedRowSet.getString("CONN_NM")),
                        cachedRowSet.getString("CONN_IMP_NM"),
                        cachedRowSet.getString("CONN_IMP_DSC")));
            }
            return impersonationParameters;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ImpersonationParameterKey metadataKey) throws MetadataDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting ActionResultOutput {0}.", metadataKey.toString()));
        if (!exists(metadataKey)) {
            throw new MetadataDoesNotExistException("ImpersonationParameter", metadataKey);
        }
        String deleteStatement = deleteStatement(metadataKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ImpersonationParameterKey metadataKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ImpersonationParameters") +
                " WHERE " +
                " IMP_NM = " + SQLTools.GetStringForSQL(metadataKey.getImpersonationName()) + " AND " +
                " CONN_NM = " + SQLTools.GetStringForSQL(metadataKey.getImpersonationParameterName()) + ";";
    }

    @Override
    public void insert(ImpersonationParameter metadata) throws MetadataAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting ImpersonationParameter {0}.", metadata.getMetadataKey().toString()));
        if (exists(metadata.getMetadataKey())) {
            throw new MetadataAlreadyExistsException("ImpersonationParameter", metadata.getMetadataKey());
        }
        String insertStatement = getInsertStatement(metadata.getMetadataKey().getImpersonationName(), metadata);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    public String getInsertStatement(String impersonationName, ImpersonationParameter impersonationParameter) {
        String query =  "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ImpersonationParameters") +
                " (IMP_NM, CONN_NM, CONN_IMP_NM, CONN_IMP_DSC) VALUES (" +
                SQLTools.GetStringForSQL(impersonationName) + "," +
                SQLTools.GetStringForSQL(impersonationParameter.getConnection()) + "," +
                SQLTools.GetStringForSQL(impersonationParameter.getImpersonatedConnection()) +  "," +
                SQLTools.GetStringForSQL(impersonationParameter.getDescription()) + ");";
        return query;
    }

    // Insert
    public String getInsertStatement(String impersonationName) {
        String sql = "";

        sql += "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ImpersonationParameters");
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

    Optional<ImpersonationParameter> getImpersonationParameter(String impersonationName, String impersonationParameterName) {
        try {
            String queryImpersonationParameter = "select IMP_NM, CONN_NM, CONN_IMP_NM, CONN_IMP_DSC from " + getMetadataRepository().getTableNameByLabel("ImpersonationParameters")
                    + " where IMP_NM = '" + impersonationName + "' and CONN_NM = '" + impersonationParameterName + "'";
            CachedRowSet crsImpersonationParameter = getMetadataRepository().executeQuery(queryImpersonationParameter, "reader");
            ImpersonationParameterKey impersonationParameterKey = new ImpersonationParameterKey(impersonationName, impersonationParameterName);
            if (crsImpersonationParameter.size() == 0) {
                return Optional.empty();
            } else if (crsImpersonationParameter.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for ActionResultOutput {0}. Returning first implementation", impersonationParameterKey.toString()));
            }
            crsImpersonationParameter.next();
            return Optional.of(new ImpersonationParameter(impersonationParameterKey,
                    crsImpersonationParameter.getString("CONN_IMP_NM"),
                    crsImpersonationParameter.getString("CONN_IMP_DSC")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // Getters and Setters
    public ImpersonationParameter getImpersonationParameter() {
        return impersonationParameter;
    }

    public void setImpersonationParameter(ImpersonationParameter impersonationParameter) {
        this.impersonationParameter = impersonationParameter;
    }

}