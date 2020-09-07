package io.metadew.iesi.metadata.definition.component.trace;

import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class ComponentDesignTraceConfiguration extends Configuration<ComponentDesignTrace, ComponentDesignTraceKey> {

    @Override
    public Optional<ComponentDesignTrace> get(ComponentDesignTraceKey metadataKey) {
        try {
            Map<String, ComponentDesignTraceBuilder> componentDesignTraceBuilderMap = new LinkedHashMap<>();
            String query = "select TraceComponentDesign.ID as TraceComponentDesign_ID, TraceComponentDesign.RUN_ID, TraceComponentDesign.SCRIPT_NM, TraceComponentDesign.PRC_ID, TraceComponentDesign.ACTION_PAR_NM, " +
                    "TraceComponentDesign.COMP_TYP_NM, TraceComponentDesign.COMP_NM, TraceComponentDesign.COMP_DSC, TraceComponentDesign.COMP_VRS_NB " +
                    " TraceHttpComponentDesign.ID as TraceHttpComponentDesign_ID, TraceHttpComponentDesign.CONN_NM as TraceHttpComponentDesign_CONN_NM, TraceHttpComponentDesign.TYPE as TraceHttpComponentDesign_TYPE, TraceHttpComponentDesign.ENDPOINT as TraceHttpComponentDesign_ENDPOINT " +
                    " TraceHttpComponentHeaderDesign.ID as TraceHttpComponentHeaderDesign_ID, TraceHttpComponentHeaderDesign.HEADER_DES_ID as TraceHttpComponentHeaderDesign_HEADER_DES_ID, TraceHttpComponentHeaderDesign.NAME as TraceHttpComponentHeaderDesign_NAME , TraceHttpComponentHeaderDesign.VALUE as TraceHttpComponentHeaderDesign_VALUE" +
                    " TraceHttpComponentQueryDesign.ID as TraceHttpComponentQueryDesign_ID, TraceHttpComponentQueryDesign.QUERY_DES_ID as TraceHttpComponentQueryDesign_QUERY_DES_ID, TraceHttpComponentQueryDesign.NAME as TraceHttpComponentQueryDesign_NAME, TraceHttpComponentQueryDesign.VALUE as TraceHttpComponentQueryDesign_VALUE" +
                     "FROM " +
                    getMetadataRepository().getTableNameByLabel("TraceComponentDesign") + " TraceComponentDesign " +
                    "left outer join " + getMetadataRepository().getTableNameByLabel("TraceHttpComponentDesign") + " TraceHttpComponentDesign " +
                    "on TraceComponentDesign.ID=TraceHttpComponentDesign.ID " +
                    "left outer join " + getMetadataRepository().getTableNameByLabel("TraceHttpComponentHeaderDesign") + " TraceHttpComponentHeaderDesign " +
                    "on TraceHttpComponentDesign.ID=TraceHttpComponentHeaderDesign.ID " +
                    "left outer join " + getMetadataRepository().getTableNameByLabel("TraceHttpComponentQueryDesign") + " TraceHttpComponentQueryDesign " +
                    "on TraceHttpComponentDesign.ID=TraceHttpComponentQueryDesign.ID " +
                    " WHERE TraceComponentDesign.ID = " + SQLTools.GetStringForSQL(metadataKey.getUuid().toString()) + ";";

            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, componentDesignTraceBuilderMap);
            }
            return componentDesignTraceBuilderMap.values().stream()
                    .findFirst()
                    .map(ComponentDesignTraceBuilder::build);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ComponentDesignTrace> getAll() {
        try {
            Map<String, ComponentDesignTraceBuilder> componentDesignTraceBuilderMap = new LinkedHashMap<>();
            String query = "select TraceComponentDesign.ID as TraceComponentDesign_ID, TraceComponentDesign.RUN_ID, TraceComponentDesign.SCRIPT_NM, TraceComponentDesign.PRC_ID, TraceComponentDesign.ACTION_PAR_NM, " +
                    "TraceComponentDesign.COMP_TYP_NM, TraceComponentDesign.COMP_NM, TraceComponentDesign.COMP_DSC, TraceComponentDesign.COMP_VRS_NB " +
                    " TraceHttpComponentDesign.ID as TraceHttpComponentDesign_ID, TraceHttpComponentDesign.CONN_NM as TraceHttpComponentDesign_CONN_NM, TraceHttpComponentDesign.TYPE as TraceHttpComponentDesign_TYPE, TraceHttpComponentDesign.ENDPOINT as TraceHttpComponentDesign_ENDPOINT " +
                    " TraceHttpComponentHeaderDesign.ID as TraceHttpComponentHeaderDesign_ID, TraceHttpComponentHeaderDesign.HEADER_DES_ID as TraceHttpComponentHeaderDesign_HEADER_DES_ID, TraceHttpComponentHeaderDesign.NAME as TraceHttpComponentHeaderDesign_NAME , TraceHttpComponentHeaderDesign.VALUE as TraceHttpComponentHeaderDesign_VALUE" +
                    " TraceHttpComponentQueryDesign.ID as TraceHttpComponentQueryDesign_ID, TraceHttpComponentQueryDesign.QUERY_DES_ID as TraceHttpComponentQueryDesign_QUERY_DES_ID, TraceHttpComponentQueryDesign.NAME as TraceHttpComponentQueryDesign_NAME, TraceHttpComponentQueryDesign.VALUE as TraceHttpComponentQueryDesign_VALUE" +
                    "FROM " +
                    getMetadataRepository().getTableNameByLabel("TraceComponentDesign") + "TraceComponentDesign" +
                    "left outer join " + getMetadataRepository().getTableNameByLabel("TraceHttpComponentDesign") + " TraceHttpComponentDesign " +
                    "on TraceComponentDesign.ID=TraceHttpComponentDesign.ID " +
                    "left outer join " + getMetadataRepository().getTableNameByLabel("TraceHttpComponentHeaderDesign") + " TraceHttpComponentHeaderDesign " +
                    "on TraceHttpComponentDesign.ID=TraceHttpComponentHeaderDesign.ID " +
                    "left outer join " + getMetadataRepository().getTableNameByLabel("TraceHttpComponentQueryDesign") + " TraceHttpComponentQueryDesign " +
                    "on TraceHttpComponentDesign.ID=TraceHttpComponentQueryDesign.ID " + ";";

            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, componentDesignTraceBuilderMap);
            }
            return componentDesignTraceBuilderMap.values().stream()
                    .map(ComponentDesignTraceBuilder::build)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ComponentDesignTraceKey metadataKey) {
        String deleteTraceComponentDesign = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TraceComponentDesign").getName() +
                " WHERE ID = " + SQLTools.GetStringForSQL(metadataKey.getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(deleteTraceComponentDesign);
        String deleteTraceHttpComponentDesign = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TraceHttpComponentDesign").getName() +
                " WHERE ID = " + SQLTools.GetStringForSQL(metadataKey.getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(deleteTraceHttpComponentDesign);
        String deleteTraceHttpComponentHeaderDesign = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TraceHttpComponentHeaderDesign").getName() +
                " WHERE ID = " + SQLTools.GetStringForSQL(metadataKey.getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(deleteTraceHttpComponentHeaderDesign);
        String deleteTraceHttpComponentQueryDesign = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TraceHttpComponentQueryDesign").getName() +
                " WHERE ID = " + SQLTools.GetStringForSQL(metadataKey.getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(deleteTraceHttpComponentQueryDesign);
    }

    @Override
    public void insert(ComponentDesignTrace metadata) {
        log.trace(MessageFormat.format("Inserting {0}.", metadata.toString()));
        String insertStatementTraceComponentDesign = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TraceComponentDesign").getName() +
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
        getMetadataRepository().executeUpdate(insertStatementTraceComponentDesign);

        String insertStatementTraceHttpComponentDesign = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TraceHttpComponentDesign").getName() +
                " (ID, CONN_NM,  TYPE, ENDPOINT ) VALUES ({0}, {1}, {2}, {3});";

        metadata.getHttpComponentDesignTraces().forEach(httpComponentDesignTrace ->
                getMetadataRepository().executeUpdate(MessageFormat.format(insertStatementTraceHttpComponentDesign,
                        SQLTools.GetStringForSQL(httpComponentDesignTrace.getMetadataKey().getUuid()),
                        SQLTools.GetStringForSQL(httpComponentDesignTrace.getConnectionName()),
                        SQLTools.GetStringForSQL(httpComponentDesignTrace.getType()),
                        SQLTools.GetStringForSQL(httpComponentDesignTrace.getEndpoint())))
        );
        getMetadataRepository().executeUpdate(insertStatementTraceHttpComponentDesign);

        String insertStatementTraceHttpComponentHeaderDesign = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("HttpComponentHeaderDesign").getName() +
                " (ID, HEADER_DES_ID, NAME, VALUE ) VALUES ({0}, {1}, {2}, {3});";

        metadata.getHttpComponentHeaderDesigns().forEach(httpComponentDesignTrace ->
                getMetadataRepository().executeUpdate(MessageFormat.format(insertStatementTraceHttpComponentHeaderDesign,
                        SQLTools.GetStringForSQL(httpComponentDesignTrace.getMetadataKey().getUuid()),
                        SQLTools.GetStringForSQL(httpComponentDesignTrace.getHttpComponentHeaderDesignID()),
                        SQLTools.GetStringForSQL(httpComponentDesignTrace.getName()),
                        SQLTools.GetStringForSQL(httpComponentDesignTrace.getValue())))
        );
        getMetadataRepository().executeUpdate(insertStatementTraceHttpComponentHeaderDesign);

        String insertStatementTraceHttpComponentQueryDesign = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TraceHttpComponentQueryDesign").getName() +
                " (ID, QUERY_DES_ID,  NAME, VALUE ) VALUES ({0}, {1}, {2}, {3});";

        metadata.getHttpComponentQueryDesigns().forEach(httpComponentDesignTrace ->
                getMetadataRepository().executeUpdate(MessageFormat.format(insertStatementTraceHttpComponentQueryDesign,
                        SQLTools.GetStringForSQL(httpComponentDesignTrace.getMetadataKey().getUuid()),
                        SQLTools.GetStringForSQL(httpComponentDesignTrace.getHttpComponentQueryDesignID()),
                        SQLTools.GetStringForSQL(httpComponentDesignTrace.getName()),
                        SQLTools.GetStringForSQL(httpComponentDesignTrace.getValue())))
        );
        getMetadataRepository().executeUpdate(insertStatementTraceHttpComponentQueryDesign);
    }

    private void mapRow(CachedRowSet cachedRowSet, Map<String, ComponentDesignTraceBuilder> componentDesignTraceBuilderMap) throws SQLException {
        String componentDesignTraceBuilderId = cachedRowSet.getString("TraceComponentDesign_ID");
        ComponentDesignTraceBuilder componentDesignTraceBuilder = componentDesignTraceBuilderMap.get(componentDesignTraceBuilderId);
        if (componentDesignTraceBuilder == null) {
            componentDesignTraceBuilderMap.put(componentDesignTraceBuilderId, componentDesignTraceBuilder);
        }
        mapHttpComponentDesignTraces(cachedRowSet, componentDesignTraceBuilder);
        mapHttpComponentHeaderDesigns(cachedRowSet, componentDesignTraceBuilder);
        mapHttpComponentQueryDesigns(cachedRowSet, componentDesignTraceBuilder);
    }

    private void mapHttpComponentDesignTraces(CachedRowSet cachedRowSet, ComponentDesignTraceBuilder componentDesignTraceBuilder) throws SQLException {
        String httpComponentDesignTracesId = cachedRowSet.getString("TraceHttpComponentDesign_ID");
        if (httpComponentDesignTracesId != null && componentDesignTraceBuilder.getHttpComponentDesignTraces().get(httpComponentDesignTracesId) == null) {
            componentDesignTraceBuilder.getHttpComponentDesignTraces().put(
                    httpComponentDesignTracesId,
                    new HttpComponentDesignTrace(
                            new HttpComponentDesignTraceKey(UUID.fromString(httpComponentDesignTracesId)),
                            cachedRowSet.getString("TraceHttpComponentDesign_CONN_NM"),
                            cachedRowSet.getString("TraceHttpComponentDesign_TYPE"),
                            cachedRowSet.getString("TraceHttpComponentDesign_ENDPOINT")
                    )
            );
        }
    }

    private void mapHttpComponentHeaderDesigns(CachedRowSet cachedRowSet, ComponentDesignTraceBuilder componentDesignTraceBuilder) throws SQLException {
        String mapHttpComponentHeaderDesignsId = cachedRowSet.getString("TraceHttpComponentHeaderDesign_ID");
        if (mapHttpComponentHeaderDesignsId != null && componentDesignTraceBuilder.getHttpComponentHeaderDesigns().get(mapHttpComponentHeaderDesignsId) == null) {
            componentDesignTraceBuilder.getHttpComponentHeaderDesigns().put(
                    mapHttpComponentHeaderDesignsId,
                    new HttpComponentHeaderDesign(
                            new HttpComponentHeaderDesignKey(UUID.fromString(mapHttpComponentHeaderDesignsId)),
                            cachedRowSet.getString("TraceHttpComponentHeaderDesign_HEADER_DES_ID"),
                            cachedRowSet.getString("TraceHttpComponentHeaderDesign_NAME"),
                            cachedRowSet.getString("TraceHttpComponentHeaderDesign_VALUE")
                    )
            );
        }
    }

    private void mapHttpComponentQueryDesigns(CachedRowSet cachedRowSet, ComponentDesignTraceBuilder componentDesignTraceBuilder) throws SQLException {
        String httpComponentDesignQueryId = cachedRowSet.getString("TraceHttpComponentQueryDesign_ID");
        if (httpComponentDesignQueryId != null && componentDesignTraceBuilder.getHttpComponentQueryDesigns().get(httpComponentDesignQueryId) == null) {
            componentDesignTraceBuilder.getHttpComponentQueryDesigns().put(
                    httpComponentDesignQueryId,
                    new HttpComponentQueryDesign(
                            new HttpComponentQueryDesignKey(UUID.fromString(httpComponentDesignQueryId)),
                            cachedRowSet.getString("TraceHttpComponentQueryDesign_QUERY_DES_ID"),
                            cachedRowSet.getString("TraceHttpComponentQueryDesign_NAME"),
                            cachedRowSet.getString("TraceHttpComponentQueryDesign_VALUE")
                    )
            );
        }
    }

    @AllArgsConstructor
    @Getter
    private abstract class ComponentDesignTraceBuilder {
        private final String runId;
        private final Long processId;
        private final String actionParameter;
        private final String componentTypeParameter;
        private final String componentName;
        private final String componentDescription;
        private final String componentVersion;
        private final Map<String, HttpComponentDesignTrace> httpComponentDesignTraces;
        private final Map<String, HttpComponentHeaderDesign> httpComponentHeaderDesigns;
        private final Map<String, HttpComponentQueryDesign> httpComponentQueryDesigns;

        public abstract ComponentDesignTrace build();
    }
}
