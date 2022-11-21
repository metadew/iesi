package io.metadew.iesi.metadata.configuration.impersonation;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.impersonation.ImpersonationParameter;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationParameterKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ImpersonationParameterConfiguration extends Configuration<ImpersonationParameter, ImpersonationParameterKey> {

    private ImpersonationParameter impersonationParameter;

    private static final Logger LOGGER = LogManager.getLogger();

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    public ImpersonationParameterConfiguration(MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
    }

    @PostConstruct
    private void postConstruct() {
        setMetadataRepository(metadataRepositoryConfiguration.getControlMetadataRepository());
    }

    @Override
    public Optional<ImpersonationParameter> get(ImpersonationParameterKey impersonationParameterKey) {
        return getImpersonationParameter(impersonationParameterKey.getImpersonationKey().getName(), impersonationParameterKey.getParameterName());
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
                        new ImpersonationKey(cachedRowSet.getString("IMP_NM")),
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
    public void delete(ImpersonationParameterKey metadataKey) {
        LOGGER.trace(MessageFormat.format("Deleting ActionResultOutput {0}.", metadataKey.toString()));
        if (!exists(metadataKey)) {
            throw new MetadataDoesNotExistException(metadataKey);
        }
        String deleteStatement = deleteStatement(metadataKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ImpersonationParameterKey metadataKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ImpersonationParameters") +
                " WHERE " +
                " IMP_NM = " + SQLTools.getStringForSQL(metadataKey.getImpersonationKey().getName()) + " AND " +
                " CONN_NM = " + SQLTools.getStringForSQL(metadataKey.getParameterName()) + ";";
    }

    @Override
    public void insert(ImpersonationParameter metadata) {
        LOGGER.trace(MessageFormat.format("Inserting ImpersonationParameter {0}.", metadata.getMetadataKey().toString()));
        if (exists(metadata.getMetadataKey())) {
            throw new MetadataAlreadyExistsException(metadata.getMetadataKey());
        }
        String insertStatement = getInsertStatement(metadata.getMetadataKey().getImpersonationKey().getName(), metadata);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    public String getInsertStatement(String impersonationName, ImpersonationParameter impersonationParameter) {
        String query =  "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ImpersonationParameters") +
                " (IMP_NM, CONN_NM, CONN_IMP_NM, CONN_IMP_DSC) VALUES (" +
                SQLTools.getStringForSQL(impersonationName) + "," +
                SQLTools.getStringForSQL(impersonationParameter.getMetadataKey().getParameterName()) + "," +
                SQLTools.getStringForSQL(impersonationParameter.getImpersonatedConnection()) +  "," +
                SQLTools.getStringForSQL(impersonationParameter.getDescription()) + ");";
        return query;
    }

    // Insert
    public String getInsertStatement(String impersonationName) {
        String sql = "";

        sql += "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ImpersonationParameters");
        sql += " (IMP_NM, CONN_NM, CONN_IMP_NM, CONN_IMP_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.getStringForSQL(impersonationName);
        sql += ",";
        sql += SQLTools.getStringForSQL(this.getImpersonationParameter().getMetadataKey().getParameterName());
        sql += ",";
        sql += SQLTools.getStringForSQL(this.getImpersonationParameter().getImpersonatedConnection());
        sql += ",";
        sql += SQLTools.getStringForSQL(this.getImpersonationParameter().getDescription());
        sql += ")";
        sql += ";";

        return sql;
    }

    Optional<ImpersonationParameter> getImpersonationParameter(String impersonationName, String impersonationParameterName) {
        try {
            String queryImpersonationParameter = "select IMP_NM, CONN_NM, CONN_IMP_NM, CONN_IMP_DSC from " + getMetadataRepository().getTableNameByLabel("ImpersonationParameters")
                    + " where IMP_NM = '" + impersonationName + "' and CONN_NM = '" + impersonationParameterName + "'";
            CachedRowSet crsImpersonationParameter = getMetadataRepository().executeQuery(queryImpersonationParameter, "reader");
            ImpersonationParameterKey impersonationParameterKey = new ImpersonationParameterKey(
                    new ImpersonationKey(impersonationName), impersonationParameterName);
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