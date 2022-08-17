package io.metadew.iesi.metadata.configuration.component.trace;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.component.trace.design.ComponentDesignTrace;
import io.metadew.iesi.metadata.definition.component.trace.design.ComponentDesignTraceKey;
import io.metadew.iesi.metadata.definition.component.trace.design.http.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Component
public class ComponentDesignTraceConfiguration extends Configuration<HttpComponentDesignTrace, ComponentDesignTraceKey> {

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final MetadataTablesConfiguration metadataTablesConfiguration;

    public ComponentDesignTraceConfiguration(MetadataRepositoryConfiguration metadataRepositoryConfiguration, MetadataTablesConfiguration metadataTablesConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.metadataTablesConfiguration = metadataTablesConfiguration;
    }

    @PostConstruct
    private void postConstruct() {
        setMetadataRepository(metadataRepositoryConfiguration.getTraceMetadataRepository());
    }

    @Override
    public Optional<HttpComponentDesignTrace> get(ComponentDesignTraceKey metadataKey) {
        try {
            Map<String, ComponentHttpDesignTraceBuilder> componentDesignTraceBuilderMap = new LinkedHashMap<>();
            String query = "select TraceComponentDesign.ID as TraceComponentDesign_ID, TraceComponentDesign.RUN_ID as TraceComponentDesign_RUN_ID, " +
                    "TraceComponentDesign.PRC_ID as TraceComponentDesign_PRC_ID, TraceComponentDesign.ACTION_PAR_NM as TraceComponentDesign_ACTION_PAR_NM, " +
                    "TraceComponentDesign.COMP_TYP_NM as TraceComponentDesign_COMP_TYP_NM, TraceComponentDesign.COMP_NM as TraceComponentDesign_COMP_NM, " +
                    "TraceComponentDesign.COMP_DSC as TraceComponentDesign_COMP_DSC, TraceComponentDesign.COMP_VRS_NB as TraceComponentDesign_COMP_VRS_NB, " +
                    " TraceHttpComponentDesign.ID as TraceHttpComponentDesign_ID, TraceHttpComponentDesign.CONN_NM as TraceHttpComponentDesign_CONN_NM, TraceHttpComponentDesign.TYPE as TraceHttpComponentDesign_TYPE, TraceHttpComponentDesign.ENDPOINT as TraceHttpComponentDesign_ENDPOINT, " +
                    " TraceHttpComponentHeaderDesign.ID as TraceHttpComponentHeaderDesign_ID, TraceHttpComponentHeaderDesign.HTTP_COMP_DES_ID as TraceHttpComponentHeaderDesign_HTTP_COMP_DES_ID, TraceHttpComponentHeaderDesign.NAME as TraceHttpComponentHeaderDesign_NAME , TraceHttpComponentHeaderDesign.VALUE as TraceHttpComponentHeaderDesign_VALUE, " +
                    " TraceHttpComponentQueryDesign.ID as TraceHttpComponentQueryDesign_ID, TraceHttpComponentQueryDesign.HTTP_COMP_DES_ID as TraceHttpComponentQueryDesign_HTTP_COMP_DES_ID, TraceHttpComponentQueryDesign.NAME as TraceHttpComponentQueryDesign_NAME, TraceHttpComponentQueryDesign.VALUE as TraceHttpComponentQueryDesign_VALUE" +
                    " FROM " +
                    metadataTablesConfiguration.getMetadataTableNameByLabel("TraceComponentDesign").getName() + " TraceComponentDesign " +
                    " left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("TraceHttpComponentDesign").getName() + " TraceHttpComponentDesign " +
                    "on TraceComponentDesign.ID=TraceHttpComponentDesign.ID " +
                    " left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("TraceHttpComponentHeaderDesign").getName() + " TraceHttpComponentHeaderDesign " +
                    "on TraceHttpComponentDesign.ID=TraceHttpComponentHeaderDesign.HTTP_COMP_DES_ID " +
                    " left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("TraceHttpComponentQueryDesign").getName() + " TraceHttpComponentQueryDesign " +
                    "on TraceHttpComponentDesign.ID=TraceHttpComponentQueryDesign.HTTP_COMP_DES_ID " +
                    " WHERE TraceComponentDesign.ID = " + SQLTools.getStringForSQL(metadataKey.getUuid().toString()) + ";";

            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, componentDesignTraceBuilderMap);
            }
            return componentDesignTraceBuilderMap.values().stream()
                    .findFirst()
                    .map(ComponentHttpDesignTraceBuilder::build);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<HttpComponentDesignTrace> getAll() {
        try {
            Map<String, ComponentHttpDesignTraceBuilder> componentDesignTraceBuilderMap = new LinkedHashMap<>();
            String query = "select TraceComponentDesign.ID as TraceComponentDesign_ID, TraceComponentDesign.RUN_ID as TraceComponentDesign_RUN_ID, TraceComponentDesign.PRC_ID as TraceComponentDesign_PRC_ID, TraceComponentDesign.ACTION_PAR_NM as TraceComponentDesign_ACTION_PAR_NM, " +
                    "TraceComponentDesign.COMP_TYP_NM as TraceComponentDesign_COMP_TYP_NM, TraceComponentDesign.COMP_NM as TraceComponentDesign_COMP_NM, " +
                    "TraceComponentDesign.COMP_DSC as TraceComponentDesign_COMP_DSC, TraceComponentDesign.COMP_VRS_NB as TraceComponentDesign_COMP_VRS_NB, " +
                    " TraceHttpComponentDesign.ID as TraceHttpComponentDesign_ID, TraceHttpComponentDesign.CONN_NM as TraceHttpComponentDesign_CONN_NM, TraceHttpComponentDesign.TYPE as TraceHttpComponentDesign_TYPE, TraceHttpComponentDesign.ENDPOINT as TraceHttpComponentDesign_ENDPOINT, " +
                    " TraceHttpComponentHeaderDesign.ID as TraceHttpComponentHeaderDesign_ID, TraceHttpComponentHeaderDesign.HTTP_COMP_DES_ID as TraceHttpComponentHeaderDesign_HTTP_COMP_DES_ID, TraceHttpComponentHeaderDesign.NAME as TraceHttpComponentHeaderDesign_NAME , TraceHttpComponentHeaderDesign.VALUE as TraceHttpComponentHeaderDesign_VALUE, " +
                    " TraceHttpComponentQueryDesign.ID as TraceHttpComponentQueryDesign_ID, TraceHttpComponentQueryDesign.HTTP_COMP_DES_ID as TraceHttpComponentQueryDesign_HTTP_COMP_DES_ID, TraceHttpComponentQueryDesign.NAME as TraceHttpComponentQueryDesign_NAME, TraceHttpComponentQueryDesign.VALUE as TraceHttpComponentQueryDesign_VALUE" +
                    " FROM " +
                    metadataTablesConfiguration.getMetadataTableNameByLabel("TraceComponentDesign").getName() + " TraceComponentDesign" +
                    " left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("TraceHttpComponentDesign").getName() + " TraceHttpComponentDesign " +
                    "on TraceComponentDesign.ID=TraceHttpComponentDesign.ID " +
                    " left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("TraceHttpComponentHeaderDesign").getName() + " TraceHttpComponentHeaderDesign " +
                    "on TraceHttpComponentDesign.ID=TraceHttpComponentHeaderDesign.HTTP_COMP_DES_ID " +
                    " left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("TraceHttpComponentQueryDesign").getName() + " TraceHttpComponentQueryDesign " +
                    "on TraceHttpComponentDesign.ID=TraceHttpComponentQueryDesign.HTTP_COMP_DES_ID " + ";";

            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, componentDesignTraceBuilderMap);
            }
            return componentDesignTraceBuilderMap.values().stream()
                    .map(ComponentHttpDesignTraceBuilder::build)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ComponentDesignTraceKey metadataKey) {
        log.trace("deleting " + metadataKey.toString());
        String deleteTraceComponentDesign = "DELETE FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("TraceComponentDesign").getName() +
                " WHERE ID = " + SQLTools.getStringForSQL(metadataKey.getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(deleteTraceComponentDesign);
        String deleteTraceHttpComponentDesign = "DELETE FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("TraceHttpComponentDesign").getName() +
                " WHERE ID = " + SQLTools.getStringForSQL(metadataKey.getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(deleteTraceHttpComponentDesign);
        String deleteTraceHttpComponentHeaderDesign = "DELETE FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("TraceHttpComponentHeaderDesign").getName() +
                " WHERE HTTP_COMP_DES_ID = " + SQLTools.getStringForSQL(metadataKey.getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(deleteTraceHttpComponentHeaderDesign);
        String deleteTraceHttpComponentQueryDesign = "DELETE FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("TraceHttpComponentQueryDesign").getName() +
                " WHERE HTTP_COMP_DES_ID = " + SQLTools.getStringForSQL(metadataKey.getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(deleteTraceHttpComponentQueryDesign);
    }


    @Override
    public void insert(HttpComponentDesignTrace metadata) {
        log.trace(MessageFormat.format("Inserting {0}.", metadata.toString()));
        String insertStatementTraceComponentDesign = "INSERT INTO " + metadataTablesConfiguration.getMetadataTableNameByLabel("TraceComponentDesign").getName() +
                " (ID, RUN_ID,  PRC_ID, ACTION_PAR_NM, COMP_TYP_NM, COMP_NM, COMP_DSC, COMP_VRS_NB ) VALUES (" +
                SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()) + ", " +
                SQLTools.getStringForSQL(metadata.getRunId()) + ", " +
                SQLTools.getStringForSQL(metadata.getProcessId()) + ", " +
                SQLTools.getStringForSQL(metadata.getActionParameter()) + ", " +
                SQLTools.getStringForSQL(metadata.getComponentTypeParameter()) + ", " +
                SQLTools.getStringForSQL(metadata.getComponentName()) + ", " +
                SQLTools.getStringForSQL(metadata.getComponentDescription()) + ", " +
                SQLTools.getStringForSQL(metadata.getComponentVersion()) +
                ");";
        getMetadataRepository().executeUpdate(insertStatementTraceComponentDesign);

        String insertStatementHttpTraceComponentDesign = "INSERT INTO " + metadataTablesConfiguration.getMetadataTableNameByLabel("TraceHttpComponentDesign").getName() +
                " (ID, CONN_NM, TYPE, ENDPOINT ) VALUES ( "
                +
                SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid().toString()) + ", " +
                SQLTools.getStringForSQL(metadata.getConnectionName()) + ", " +
                SQLTools.getStringForSQL(metadata.getType()) + ", " +
                SQLTools.getStringForSQL(metadata.getEndpoint()) + ") ";

        getMetadataRepository().executeUpdate(insertStatementHttpTraceComponentDesign);

        metadata.getHttpComponentHeaderDesigns().forEach(httpComponentHeaderTrace ->
                getMetadataRepository().executeUpdate(
                        "INSERT INTO " + metadataTablesConfiguration.getMetadataTableNameByLabel("TraceHttpComponentHeaderDesign").getName() +
                                " (ID, HTTP_COMP_DES_ID, NAME, VALUE ) VALUES " +
                                " ( " +
                                SQLTools.getStringForSQL(httpComponentHeaderTrace.getMetadataKey().getUuid().toString()) + ", " +
                                SQLTools.getStringForSQL(httpComponentHeaderTrace.getHttpComponentDesignID().getUuid().toString()) + ", " +
                                SQLTools.getStringForSQL(httpComponentHeaderTrace.getName()) + ", " +
                                SQLTools.getStringForSQLClob(httpComponentHeaderTrace.getValue(),
                                        getMetadataRepository().getRepositoryCoordinator().getDatabases().values().stream()
                                                .findFirst()
                                                .orElseThrow(RuntimeException::new)) + ") ")
        );

        metadata.getHttpComponentQueryDesigns().forEach(httpComponentQueryTrace ->
                getMetadataRepository().executeUpdate(
                        "INSERT INTO " + metadataTablesConfiguration.getMetadataTableNameByLabel("TraceHttpComponentQueryDesign").getName() +
                                " (ID, HTTP_COMP_DES_ID,  NAME, VALUE )  VALUES " +
                                " ( " +
                                SQLTools.getStringForSQL(httpComponentQueryTrace.getMetadataKey().getUuid().toString()) + ", " +
                                SQLTools.getStringForSQL(httpComponentQueryTrace.getHttpComponentQueryDesignID().getUuid().toString()) + ", " +
                                SQLTools.getStringForSQL(httpComponentQueryTrace.getName()) + ", " +
                                SQLTools.getStringForSQLClob(httpComponentQueryTrace.getValue(),
                                        getMetadataRepository().getRepositoryCoordinator().getDatabases().values().stream()
                                                .findFirst()
                                                .orElseThrow(RuntimeException::new)) + ") "));
    }

    private ComponentHttpDesignTraceBuilder mapHttpComponentDesignTraces(CachedRowSet cachedRowSet) throws SQLException {
        return new ComponentHttpDesignTraceBuilder(
                new ComponentDesignTraceKey(UUID.fromString(cachedRowSet.getString("TraceComponentDesign_ID"))),
                cachedRowSet.getString("TraceComponentDesign_RUN_ID"),
                cachedRowSet.getLong("TraceComponentDesign_PRC_ID"),
                cachedRowSet.getString("TraceComponentDesign_ACTION_PAR_NM"),
                cachedRowSet.getString("TraceComponentDesign_COMP_TYP_NM"),
                cachedRowSet.getString("TraceComponentDesign_COMP_NM"),
                cachedRowSet.getString("TraceComponentDesign_COMP_DSC"),
                cachedRowSet.getLong("TraceComponentDesign_COMP_VRS_NB"),
                cachedRowSet.getString("TraceHttpComponentDesign_CONN_NM"),
                cachedRowSet.getString("TraceHttpComponentDesign_TYPE"),
                cachedRowSet.getString("TraceHttpComponentDesign_ENDPOINT"),
                new LinkedHashMap<>(),
                new LinkedHashMap<>());
    }

    private void mapRow(CachedRowSet cachedRowSet, Map<String, ComponentHttpDesignTraceBuilder> componentDesignTraceBuilderMap) throws SQLException {
        String componentDesignTraceBuilderId = cachedRowSet.getString("TraceComponentDesign_ID");
        ComponentHttpDesignTraceBuilder componentDesignTraceBuilder = componentDesignTraceBuilderMap.get(componentDesignTraceBuilderId);
        if (componentDesignTraceBuilder == null) {
            componentDesignTraceBuilder = mapHttpComponentDesignTraces(cachedRowSet);
            componentDesignTraceBuilderMap.put(componentDesignTraceBuilderId, componentDesignTraceBuilder);
        }
        mapHttpComponentHeaderDesigns(cachedRowSet, componentDesignTraceBuilder);
        mapHttpComponentQueryDesigns(cachedRowSet, componentDesignTraceBuilder);
    }

    private void mapHttpComponentHeaderDesigns(CachedRowSet cachedRowSet, ComponentHttpDesignTraceBuilder componentDesignTraceBuilder) throws SQLException {
        String mapHttpComponentHeaderDesignsId = cachedRowSet.getString("TraceHttpComponentHeaderDesign_ID");
        if (mapHttpComponentHeaderDesignsId != null) {
            componentDesignTraceBuilder.getHttpComponentHeaderDesigns().put(
                    mapHttpComponentHeaderDesignsId,
                    new HttpComponentHeaderDesignTrace(
                            new HttpComponentHeaderDesignTraceKey(UUID.fromString(mapHttpComponentHeaderDesignsId)),
                            new ComponentDesignTraceKey(UUID.fromString(cachedRowSet.getString("TraceHttpComponentHeaderDesign_HTTP_COMP_DES_ID"))),
                            cachedRowSet.getString("TraceHttpComponentHeaderDesign_NAME"),
                            SQLTools.getStringFromSQLClob(cachedRowSet, "TraceHttpComponentHeaderDesign_VALUE")
                    )
            );
        }
    }

    private void mapHttpComponentQueryDesigns(CachedRowSet cachedRowSet, ComponentHttpDesignTraceBuilder componentDesignTraceBuilder) throws SQLException {
        String httpComponentDesignQueryId = cachedRowSet.getString("TraceHttpComponentQueryDesign_ID");
        if (httpComponentDesignQueryId != null) {
            componentDesignTraceBuilder.getHttpComponentQueryDesigns().put(
                    httpComponentDesignQueryId,
                    new HttpComponentQueryParameterDesignTrace(
                        new HttpComponentQueryParameterDesignTraceKey(UUID.fromString(httpComponentDesignQueryId)),
                            new ComponentDesignTraceKey(UUID.fromString(cachedRowSet.getString("TraceHttpComponentQueryDesign_HTTP_COMP_DES_ID"))),
                            cachedRowSet.getString("TraceHttpComponentQueryDesign_NAME"),
                            SQLTools.getStringFromSQLClob(cachedRowSet, "TraceHttpComponentQueryDesign_VALUE")
                    )
            );
        }
    }

    @AllArgsConstructor
    @Getter
    private abstract class ComponentDesignTraceBuilder {
        private final ComponentDesignTraceKey metadataKey;
        private final String runId;
        private final Long processId;
        private final String actionParameter;
        private final String componentTypeParameter;
        private final String componentName;
        private final String componentDescription;
        private final Long componentVersion;

        public abstract ComponentDesignTrace build();
    }

    @Getter
    @ToString
    private class ComponentHttpDesignTraceBuilder extends ComponentDesignTraceBuilder {
        private final String connectionName;
        private final String type;
        private final String endpoint;
        private Map<String, HttpComponentHeaderDesignTrace> httpComponentHeaderDesigns;
        private Map<String, HttpComponentQueryParameterDesignTrace> httpComponentQueryDesigns;

        public ComponentHttpDesignTraceBuilder(ComponentDesignTraceKey metadataKey, String runId,
                                               Long processId, String actionParameter, String componentTypeParameter,
                                               String componentName, String componentDescription, Long componentVersion, String connectionName, String type, String endpoint,
                                               Map<String, HttpComponentHeaderDesignTrace> httpComponentHeaderDesigns, Map<String, HttpComponentQueryParameterDesignTrace> httpComponentQueryDesigns) {
            super(metadataKey, runId, processId, actionParameter, componentTypeParameter, componentName, componentDescription, componentVersion);
            this.connectionName = connectionName;
            this.type = type;
            this.endpoint = endpoint;
            this.httpComponentHeaderDesigns = httpComponentHeaderDesigns;
            this.httpComponentQueryDesigns = httpComponentQueryDesigns;
        }

        public HttpComponentDesignTrace build() {
            return new HttpComponentDesignTrace(
                    getMetadataKey(), getRunId(), getProcessId(), getActionParameter(), getComponentTypeParameter(), getComponentName()
                    , getComponentDescription(), getComponentVersion()
                    , connectionName, type, endpoint,
                    new ArrayList<>(getHttpComponentHeaderDesigns().values()),
                    new ArrayList<>(getHttpComponentQueryDesigns().values())
            );
        }
    }
}
