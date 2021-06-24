package io.metadew.iesi.metadata.configuration.connection.trace;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.connection.trace.ConnectionTrace;
import io.metadew.iesi.metadata.definition.connection.trace.ConnectionTraceKey;
import io.metadew.iesi.metadata.definition.connection.trace.http.HttpConnectionTrace;
import lombok.extern.log4j.Log4j2;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
public class ConnectionTraceConfiguration extends Configuration<ConnectionTrace, ConnectionTraceKey> {

    private static final String fetchByIdQuery = "SELECT " +
            "connection_traces.ID as connection_traces_id, connection_traces.RUN_ID as connection_traces_run_id, " +
            "connection_traces.PRC_ID as connection_traces_prc_id, connection_traces.ACTION_PAR_NM as connection_traces_action_par_nm, " +
            "connection_traces.CONN_NM as connection_traces_conn_nm, connection_traces.CONN_TYP_NM as connection_traces_conn_type_nm, " +
            "connection_traces.CONN_DESC as connection_traces_conn_desc, " +
            "http_connection_traces.ID as http_connection_traces_id, http_connection_traces.HOST as http_connection_traces_host, " +
            "http_connection_traces.PORT as http_connection_traces_port, http_connection_traces.BASE_URL as http_connection_traces_base_url, " +
            "http_connection_traces.TLS as http_connection_traces_tls " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ConnectionTraces").getName() + " connection_traces " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("HttpConnectionTraces").getName() + " http_connection_traces " +
            "ON connection_traces.ID=http_connection_traces.ID " +
            "WHERE connection_traces.ID={0};";
    private static final String fetchAllQuery = "SELECT " +
            "connection_traces.ID as connection_traces_id, connection_traces.RUN_ID as connection_traces_run_id, " +
            "connection_traces.PRC_ID as connection_traces_prc_id, connection_traces.ACTION_PAR_NM as connection_traces_action_par_nm, " +
            "connection_traces.CONN_NM as connection_traces_conn_nm, connection_traces.CONN_TYP_NM as connection_traces_conn_type_nm, " +
            "connection_traces.CONN_DESC as connection_traces_conn_desc, " +
            "http_connection_traces.ID as http_connection_traces_id, http_connection_traces.HOST as http_connection_traces_host, " +
            "http_connection_traces.PORT as http_connection_traces_port, http_connection_traces.BASE_URL as http_connection_traces_base_url, " +
            "http_connection_traces.TLS as http_connection_traces_tls " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ConnectionTraces").getName() + " connection_traces " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("HttpConnectionTraces").getName() + " http_connection_traces " +
            "ON connection_traces.ID=http_connection_traces.ID;";
    private static final String insertQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ConnectionTraces").getName() +
            " (ID, RUN_ID, PRC_ID, ACTION_PAR_NM, CONN_NM, CONN_TYP_NM, CONN_DESC) VALUES ({0}, {1}, {2}, {3}, {4}, {5}, {6});";
    private static final String insertHttpQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("HttpConnectionTraces").getName() +
            " (ID, HOST, PORT, BASE_URL, TLS) VALUES ({0}, {1}, {2}, {3}, {4});";
    private static final String deleteByIdQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ConnectionTraces").getName() +
            " WHERE ID={0};";
    private static final String deleteByIdHttpQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("HttpConnectionTraces").getName() +
            " WHERE ID={0};";

    private static ConnectionTraceConfiguration INSTANCE;

    public synchronized static ConnectionTraceConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConnectionTraceConfiguration();
        }
        return INSTANCE;
    }

    private ConnectionTraceConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getTraceMetadataRepository());
    }

    @Override
    public Optional<ConnectionTrace> get(ConnectionTraceKey metadataKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchByIdQuery,
                            SQLTools.getStringForSQL(metadataKey.getUuid())
                    ),
                    "reader");
            List<ConnectionTrace> connectionTraces = new ArrayList<>();
            while (cachedRowSet.next()) {
                connectionTraces.add(mapRow(cachedRowSet));
            }
            return connectionTraces.stream()
                    .findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ConnectionTrace> getAll() {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    fetchAllQuery,
                    "reader");
            List<ConnectionTrace> connectionTraces = new ArrayList<>();
            while (cachedRowSet.next()) {
                connectionTraces.add(mapRow(cachedRowSet));
            }
            return connectionTraces;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ConnectionTraceKey metadataKey) {
        log.trace("deleting " + metadataKey.toString());
        getMetadataRepository().executeUpdate(
                MessageFormat.format(deleteByIdQuery,
                        SQLTools.getStringForSQL(metadataKey.getUuid()))
        );
        getMetadataRepository().executeUpdate(
                MessageFormat.format(deleteByIdHttpQuery,
                        SQLTools.getStringForSQL(metadataKey.getUuid())
                ));
    }

    @Override
    public void insert(ConnectionTrace metadata) {
        log.info("inserting " + metadata.toString());
        getMetadataRepository().executeUpdate(
                MessageFormat.format(insertQuery,
                        SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()),
                        SQLTools.getStringForSQL(metadata.getRunId()),
                        SQLTools.getStringForSQL(metadata.getProcessId()),
                        SQLTools.getStringForSQL(metadata.getActionParameter()),
                        SQLTools.getStringForSQL(metadata.getName()),
                        SQLTools.getStringForSQL(metadata.getType()),
                        SQLTools.getStringForSQL(metadata.getDescription())
                ));
        if (metadata instanceof HttpConnectionTrace) {
            log.info("inserting http connection" + metadata.toString());
            getMetadataRepository().executeUpdate(
                    MessageFormat.format(insertHttpQuery,
                            SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()),
                            SQLTools.getStringForSQLClob(((HttpConnectionTrace) metadata).getHost(),
                                    getMetadataRepository().getRepositoryCoordinator().getDatabases().values().stream()
                                            .findFirst()
                                            .orElseThrow(RuntimeException::new)),
                            SQLTools.getStringForSQL(((HttpConnectionTrace) metadata).getPort()),
                            SQLTools.getStringForSQLClob(((HttpConnectionTrace) metadata).getBaseUrl(),
                                    getMetadataRepository().getRepositoryCoordinator().getDatabases().values().stream()
                                            .findFirst()
                                            .orElseThrow(RuntimeException::new)),
                            SQLTools.getStringForSQL(((HttpConnectionTrace) metadata).isTls())
                    ));
        }

    }


    private ConnectionTrace mapRow(CachedRowSet cachedRowSet) throws SQLException {
        // connection_traces_id, connection_traces_run_id, connection_traces_prc_id,
        // connection_traces_action_par_nm, connection_traces_conn_nm, connection_traces_conn_type_nm, " +
        // connection_traces_conn_desc, http_connection_traces_id, http_connection_traces_host,
        // http_connection_traces_port, http_connection_traces_base_url, http_connection_traces_tls " +
        if (cachedRowSet.getString("http_connection_traces_id") != null) {
            return HttpConnectionTrace.builder()
                    .metadataKey(new ConnectionTraceKey(UUID.fromString(cachedRowSet.getString("http_connection_traces_id"))))
                    .runId(cachedRowSet.getString("connection_traces_run_id"))
                    .processId(cachedRowSet.getLong("connection_traces_prc_id"))
                    .actionParameter(cachedRowSet.getString("connection_traces_action_par_nm"))
                    .name(cachedRowSet.getString("connection_traces_conn_nm"))
                    .type(cachedRowSet.getString("connection_traces_conn_type_nm"))
                    .description(cachedRowSet.getString("connection_traces_conn_desc"))
                    .host(SQLTools.getStringFromSQLClob(cachedRowSet, "http_connection_traces_host"))
                    .port(cachedRowSet.getInt("http_connection_traces_port"))
                    .baseUrl(SQLTools.getStringFromSQLClob(cachedRowSet, "http_connection_traces_base_url"))
                    .tls(SQLTools.getBooleanFromSql(cachedRowSet.getString("http_connection_traces_tls")))
                    .build();
        } else {
            throw new RuntimeException("Cannot find type of Connection Trace");
        }

    }
}
