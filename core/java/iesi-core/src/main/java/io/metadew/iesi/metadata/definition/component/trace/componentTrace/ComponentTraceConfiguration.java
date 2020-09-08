package io.metadew.iesi.metadata.definition.component.trace.componentTrace;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class ComponentTraceConfiguration extends Configuration<HttpComponentTrace, ComponentTraceKey> {


    private static ComponentTraceConfiguration INSTANCE;

    public synchronized static ComponentTraceConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComponentTraceConfiguration();
        }
        return INSTANCE;
    }

    private ComponentTraceConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository());
    }


    @Override
    public Optional<HttpComponentTrace> get(ComponentTraceKey metadataKey) {
        try {
            Map<String, ComponentHttpTraceBuilder> componentTraceBuilderMap = new LinkedHashMap<>();
            String query = "select TraceComponent.ID as TraceComponent_ID, TraceComponent.RUN_ID as TraceComponent_RUN_ID, TraceComponent.PRC_ID as TraceComponent_PRC_ID," +
                    " TraceComponent.ACTION_PAR_NM as TraceComponent_ACTION_PAR_NM, " +
                    " TraceComponent.COMP_TYP_NM as TraceComponent_COMP_TYP_NM, TraceComponent.COMP_NM as TraceComponent_COMP_NM, " +
                    "TraceComponent.COMP_DSC as TraceComponent_COMP_DSC, TraceComponent.COMP_VRS_NB as TraceComponent_COMP_VRS_NB, " +
                    " TraceHttpComponent.ID as TraceHttpComponent_ID, TraceHttpComponent.CONN_NM as TraceHttpComponent_CONN_NM, TraceHttpComponent.TYPE as TraceHttpComponent_TYPE, TraceHttpComponent.ENDPOINT as  TraceHttpComponent_ENDPOINT," +
                    " TraceHttpComponentHeader.ID as TraceHttpComponentHeader_ID, TraceHttpComponentHeader.HTTP_COMP_ID as TraceHttpComponentHeader_HTTP_COMP_ID, TraceHttpComponentHeader.NAME as TraceHttpComponentHeader_NAME , TraceHttpComponentHeader.VALUE as TraceHttpComponentHeader_VALUE," +
                    " TraceHttpComponentQuery.ID as TraceHttpComponentQuery_ID, TraceHttpComponentQuery.HTTP_COMP_ID as TraceHttpComponentQuery_HTTP_COMP_ID, TraceHttpComponentQuery.NAME as TraceHttpComponentQuery_NAME, TraceHttpComponentQuery.VALUE as TraceHttpComponentQuery_VALUE" +
                    " FROM " +
                    getMetadataRepository().getTableNameByLabel("TraceComponent") + " TraceComponent " +
                    "left outer join " + getMetadataRepository().getTableNameByLabel("TraceHttpComponent") + " TraceHttpComponent " +
                    "on TraceComponent.ID=TraceHttpComponent.ID " +
                    "left outer join " + getMetadataRepository().getTableNameByLabel("TraceHttpComponentHeader") + " TraceHttpComponentHeader " +
                    "on TraceHttpComponent.ID=TraceHttpComponentHeader.HTTP_COMP_ID " +
                    "left outer join " + getMetadataRepository().getTableNameByLabel("TraceHttpComponentQuery") + " TraceHttpComponentQuery " +
                    "on TraceHttpComponent.ID=TraceHttpComponentQuery.HTTP_COMP_ID " +
                    " WHERE TraceComponent.ID = " + SQLTools.GetStringForSQL(metadataKey.getUuid().toString()) + ";";

            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, componentTraceBuilderMap);
            }
            return componentTraceBuilderMap.values().stream()
                    .findFirst()
                    .map(ComponentHttpTraceBuilder::build);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<HttpComponentTrace> getAll() {
        try {
            Map<String, ComponentHttpTraceBuilder> componentTraceBuilderMap = new LinkedHashMap<>();
            String query = "select TraceComponent.ID as TraceComponent_ID, TraceComponent.RUN_ID as TraceComponent_RUN_ID, TraceComponent.PRC_ID as TraceComponent_PRC_ID," +
                    " TraceComponent.ACTION_PAR_NM as TraceComponent_ACTION_PAR_NM, " +
                    " TraceComponent.COMP_TYP_NM as TraceComponent_COMP_TYP_NM, TraceComponent.COMP_NM as TraceComponent_COMP_NM, " +
                    "TraceComponent.COMP_DSC as TraceComponent_COMP_DSC, TraceComponent.COMP_VRS_NB as TraceComponent_COMP_VRS_NB, " +
                    " TraceHttpComponent.ID as TraceHttpComponent_ID, TraceHttpComponent.CONN_NM as TraceHttpComponent_CONN_NM, TraceHttpComponent.TYPE as TraceHttpComponent_TYPE, TraceHttpComponent.ENDPOINT as  TraceHttpComponent_ENDPOINT," +
                    " TraceHttpComponentHeader.ID as TraceHttpComponentHeader_ID, TraceHttpComponentHeader.HTTP_COMP_ID as TraceHttpComponentHeader_HTTP_COMP_ID, TraceHttpComponentHeader.NAME as TraceHttpComponentHeader_NAME , TraceHttpComponentHeader.VALUE as TraceHttpComponentHeader_VALUE," +
                    " TraceHttpComponentQuery.ID as TraceHttpComponentQuery_ID, TraceHttpComponentQuery.HTTP_COMP_ID as TraceHttpComponentQuery_HTTP_COMP_ID, TraceHttpComponentQuery.NAME as TraceHttpComponentQuery_NAME, TraceHttpComponentQuery.VALUE as TraceHttpComponentQuery_VALUE" +
                    " FROM " +
                    getMetadataRepository().getTableNameByLabel("TraceComponent") + " TraceComponent " +
                    "left outer join " + getMetadataRepository().getTableNameByLabel("TraceHttpComponent") + " TraceHttpComponent " +
                    "on TraceComponent.ID=TraceHttpComponent.ID " +
                    "left outer join " + getMetadataRepository().getTableNameByLabel("TraceHttpComponentHeader") + " TraceHttpComponentHeader " +
                    "on TraceHttpComponent.ID=TraceHttpComponentHeader.HTTP_COMP_ID " +
                    "left outer join " + getMetadataRepository().getTableNameByLabel("TraceHttpComponentQuery") + " TraceHttpComponentQuery " +
                    "on TraceHttpComponent.ID=TraceHttpComponentQuery.HTTP_COMP_ID " + ";";

            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, componentTraceBuilderMap);
            }
            return componentTraceBuilderMap.values().stream()
                    .map(ComponentHttpTraceBuilder::build)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ComponentTraceKey metadataKey) {
        String deleteTraceComponent = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TraceComponent").getName() +
                " WHERE ID = " + SQLTools.GetStringForSQL(metadataKey.getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(deleteTraceComponent);
        String deleteTraceHttpComponent = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TraceHttpComponent").getName() +
                " WHERE ID = " + SQLTools.GetStringForSQL(metadataKey.getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(deleteTraceHttpComponent);
        String deleteTraceHttpComponentHeader = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TraceHttpComponentHeader").getName() +
                " WHERE HTTP_COMP_ID = " + SQLTools.GetStringForSQL(metadataKey.getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(deleteTraceHttpComponentHeader);
        String deleteTraceHttpComponentQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TraceHttpComponentQuery").getName() +
                " WHERE HTTP_COMP_ID = " + SQLTools.GetStringForSQL(metadataKey.getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(deleteTraceHttpComponentQuery);
    }

    @Override
    public void insert(HttpComponentTrace metadata) {
        log.trace(MessageFormat.format("Inserting {0}.", metadata.toString()));
        String insertStatementTraceComponent = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TraceComponent").getName() +
                " (ID, RUN_ID,  PRC_ID, ACTION_PAR_NM, COMP_TYP_NM, COMP_NM, COMP_DSC, COMP_VRS_NB ) VALUES (" +
                SQLTools.GetStringForSQL(metadata.getMetadataKey().getUuid().toString()) + ", " +
                SQLTools.GetStringForSQL(metadata.getRunId()) + ", " +
                SQLTools.GetStringForSQL(metadata.getProcessId()) + ", " +
                SQLTools.GetStringForSQL(metadata.getActionParameter()) + ", " +
                SQLTools.GetStringForSQL(metadata.getComponentTypeParameter()) + ", " +
                SQLTools.GetStringForSQL(metadata.getComponentName()) + ", " +
                SQLTools.GetStringForSQL(metadata.getComponentDescription()) + ", " +
                SQLTools.GetStringForSQL(metadata.getComponentVersion()) +
                ");";
        getMetadataRepository().executeUpdate(insertStatementTraceComponent);

        String insertStatementHttpTraceComponent = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TraceHttpComponent").getName() +
                " (ID, CONN_NM, TYPE, ENDPOINT ) VALUES ( "
                +
                SQLTools.GetStringForSQL(metadata.getMetadataKey().getUuid().toString()) + ", " +
                SQLTools.GetStringForSQL(metadata.getConnectionName()) + ", " +
                SQLTools.GetStringForSQL(metadata.getType()) + ", " +
                SQLTools.GetStringForSQL(metadata.getEndpoint()) + ") ";

        getMetadataRepository().executeUpdate(insertStatementHttpTraceComponent);

        metadata.getHttpComponentHeader().forEach(httpComponentHeader ->
                getMetadataRepository().executeUpdate(
                        "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TraceHttpComponentHeader").getName() +
                                " (ID, HTTP_COMP_ID, NAME, VALUE ) VALUES ( " +
                                SQLTools.GetStringForSQL(httpComponentHeader.getId()) + ", " +
                                SQLTools.GetStringForSQL(httpComponentHeader.getHttpComponentHeaderID().getUuid().toString()) + ", " +
                                SQLTools.GetStringForSQL(httpComponentHeader.getName()) + ", " +
                                SQLTools.GetStringForSQL(httpComponentHeader.getValue()) + ") "));

        metadata.getHttpComponentQueries().forEach(httpComponentTrace ->
                getMetadataRepository().executeUpdate(
                        "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TraceHttpComponentQuery").getName() +
                                " (ID, HTTP_COMP_ID,  NAME, VALUE ) VALUES  ( " +
                                SQLTools.GetStringForSQL(httpComponentTrace.getId()) + ", " +
                                SQLTools.GetStringForSQL(httpComponentTrace.getHttpComponentQueryID().getUuid().toString()) + ", " +
                                SQLTools.GetStringForSQL(httpComponentTrace.getName()) + ", " +
                                SQLTools.GetStringForSQL(httpComponentTrace.getValue()) + ") "));
    }

    private void mapRow(CachedRowSet cachedRowSet, Map<String, ComponentHttpTraceBuilder> componentTraceBuilderMap) throws SQLException {
        String componentTraceBuilderId = cachedRowSet.getString("TraceComponent_ID");
        ComponentHttpTraceBuilder componentTraceBuilder = componentTraceBuilderMap.get(componentTraceBuilderId);
        if (componentTraceBuilder == null) {
            componentTraceBuilder = mapHttpComponentTraces(cachedRowSet);
            componentTraceBuilderMap.put(componentTraceBuilderId, componentTraceBuilder);
        }
        mapHttpComponentHeader(cachedRowSet, componentTraceBuilder);
        mapHttpComponentQuery(cachedRowSet, componentTraceBuilder);
    }

    private ComponentHttpTraceBuilder mapHttpComponentTraces(CachedRowSet cachedRowSet) throws SQLException {
        return new ComponentHttpTraceBuilder(
                new ComponentTraceKey(UUID.fromString(cachedRowSet.getString("TraceComponent_ID"))),
                cachedRowSet.getString("TraceComponent_RUN_ID"),
                cachedRowSet.getLong("TraceComponent_PRC_ID"),
                cachedRowSet.getString("TraceComponent_ACTION_PAR_NM"),
                cachedRowSet.getString("TraceComponent_COMP_TYP_NM"),
                cachedRowSet.getString("TraceComponent_COMP_NM"),
                cachedRowSet.getString("TraceComponent_COMP_DSC"),
                cachedRowSet.getLong("TraceComponent_COMP_VRS_NB"),
                cachedRowSet.getString("TraceHttpComponent_CONN_NM"),
                cachedRowSet.getString("TraceHttpComponent_TYPE"),
                cachedRowSet.getString("TraceHttpComponent_ENDPOINT"),
                new LinkedHashMap<>(),
                new LinkedHashMap<>());
    }

    private void mapHttpComponentHeader(CachedRowSet cachedRowSet, ComponentHttpTraceBuilder componentTraceBuilder) throws SQLException {
        String mapHttpComponentHeaderId = cachedRowSet.getString("TraceHttpComponentHeader_ID");
        if (mapHttpComponentHeaderId != null) {
            componentTraceBuilder.getHttpComponentHeader().put(
                    mapHttpComponentHeaderId,
                    new HttpComponentHeader(
                            UUID.fromString(mapHttpComponentHeaderId),
                            new HttpComponentHeaderKey(UUID.fromString(cachedRowSet.getString("TraceHttpComponentHeader_HTTP_COMP_ID"))),
                            cachedRowSet.getString("TraceHttpComponentHeader_NAME"),
                            cachedRowSet.getString("TraceHttpComponentHeader_VALUE")
                    )
            );
        }
    }

    private void mapHttpComponentQuery(CachedRowSet cachedRowSet, ComponentHttpTraceBuilder componentTraceBuilder) throws SQLException {
        String httpComponentQueryId = cachedRowSet.getString("TraceHttpComponentQuery_ID");
        if (httpComponentQueryId != null) {
            componentTraceBuilder.getHttpComponentQueries().put(
                    httpComponentQueryId,
                    new HttpComponentQuery(
                            UUID.fromString(httpComponentQueryId),
                            new HttpComponentQueryKey(UUID.fromString(cachedRowSet.getString("TraceHttpComponentQuery_HTTP_COMP_ID"))),
                            cachedRowSet.getString("TraceHttpComponentQuery_NAME"),
                            cachedRowSet.getString("TraceHttpComponentQuery_VALUE")
                    )
            );
        }
    }

    @AllArgsConstructor
    @Getter
    private abstract class ComponentTraceBuilder {
        private final ComponentTraceKey metadataKey;
        private final String runId;
        private final Long processId;
        private final String actionParameter;
        private final String componentTypeParameter;
        private final String componentName;
        private final String componentDescription;
        private final Long componentVersion;

        public abstract ComponentTrace build();
    }

    @Getter
    @ToString
    private class ComponentHttpTraceBuilder extends ComponentTraceBuilder {
        public ComponentHttpTraceBuilder(ComponentTraceKey metadataKey, String runId, Long processId, String actionParameter, String componentTypeParameter, String componentName, String componentDescription, Long componentVersion, String connectionName, String type, String endpoint, Map<String, HttpComponentHeader> httpComponentHeader, Map<String, HttpComponentQuery> httpComponentQueries) {
            super(metadataKey, runId, processId, actionParameter, componentTypeParameter, componentName, componentDescription, componentVersion);
            this.connectionName = connectionName;
            this.type = type;
            this.endpoint = endpoint;
            this.httpComponentHeader = httpComponentHeader;
            this.httpComponentQueries = httpComponentQueries;
        }

        private final String connectionName;
        private final String type;
        private final String endpoint;
        private Map<String, HttpComponentHeader> httpComponentHeader;
        private Map<String, HttpComponentQuery> httpComponentQueries;

        public HttpComponentTrace build() {
            return new HttpComponentTrace(
                    getMetadataKey(), getRunId(), getProcessId(), getActionParameter(),
                    getComponentTypeParameter(), getComponentName()
                    , getComponentDescription(), getComponentVersion()
                    , connectionName, type, endpoint,
                    new ArrayList<>(getHttpComponentHeader().values()),
                    new ArrayList<>(getHttpComponentQueries().values())
            );
        }
    }
}
