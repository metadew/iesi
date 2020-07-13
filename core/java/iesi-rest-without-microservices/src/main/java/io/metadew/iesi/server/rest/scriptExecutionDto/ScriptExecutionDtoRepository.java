package io.metadew.iesi.server.rest.scriptExecutionDto;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.rowset.CachedRowSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//@Log4j2
@Repository
public class ScriptExecutionDtoRepository implements IScriptExecutionDtoRepository {

    MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    ScriptExecutionDtoRepository(MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
    }

    @Override
    public ScriptExecutionDto getByRunIdAndProcessId(String runId, Long processId) {
        ScriptExecutionDto scriptExecutionDto;
        String SQLQuery = getSQLQuery(runId, processId);

        CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getResultMetadataRepository()
                .executeQuery(SQLQuery, "reader");

        // Todo: Create a row mapper

        // Todo: return the created Object
        return null;
    }

    private String getSQLQuery(String runId, Long processId) {
        return "select * from " + MetadataTablesConfiguration.getInstance()
                .getMetadataTableNameByLabel("ScriptResults").getName() + " results " +
                "inner join " + MetadataTablesConfiguration.getInstance()
                .getMetadataTableNameByLabel("ScriptDesignTraces").getName() + " script_traces " +
                "on results.RUN_ID = script_traces.RUN_ID and results.PRC_ID = script_traces.PRC_ID " +
                "inner join " + MetadataTablesConfiguration.getInstance()
                .getMetadataTableNameByLabel("ScriptVersionDesignTraces").getName() + " script_version_traces " +
                "on results.RUN_ID = script_version_traces.RUN_ID and results.PRC_ID = script_version_traces.PRC_ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance()
                .getMetadataTableNameByLabel("ScriptParameterDesignTraces").getName() + " script_params_traces " +
                "on results.RUN_ID = script_params_traces.RUN_ID and " +
                "results.PRC_ID = script_params_traces.PRC_ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance()
                .getMetadataTableNameByLabel("ScriptLabelDesignTraces").getName() + " script_labels_traces " +
                "on results.RUN_ID = script_labels_traces.RUN_ID and " +
                "results.PRC_ID = script_labels_traces.PRC_ID " +
                "inner join " + MetadataTablesConfiguration.getInstance()
                .getMetadataTableNameByLabel("ActionResults").getName() + " action_results " +
                "on results.RUN_ID = action_results.RUN_ID and " +
                "results.prc_id = action_results.SCRIPT_PRC_ID " +
                "inner join " + MetadataTablesConfiguration.getInstance()
                .getMetadataTableNameByLabel("ActionDesignTraces").getName() + " action_design_traces " +
                "on results.RUN_ID = action_design_traces.RUN_ID and " +
                "action_results.prc_id = action_design_traces.PRC_ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance()
                .getMetadataTableNameByLabel("ActionParameterDesignTraces").getName() + " action_design_param_traces " +
                "on results.RUN_ID = action_design_param_traces.RUN_ID and " +
                "action_design_traces.PRC_ID = action_design_param_traces.PRC_ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance()
                .getMetadataTableNameByLabel("ActionResultOutputs").getName() + " action_result_outputs " +
                "on action_result_outputs.RUN_ID = results.RUN_ID and " +
                "action_result_outputs.PRC_ID = action_results.PRC_ID " +
                getWhereClause(runId, processId).orElse("")
                + ";";
    }

    private Optional<String> getWhereClause(String runId, Long processId) {
        List<String> conditions = new ArrayList<>();
        if (runId != null) conditions.add(" results.RUN_ID = " + SQLTools.GetStringForSQL(runId));
        if (processId != null) conditions.add(" results.prc_id = " + SQLTools.GetStringForSQL(processId));
        if (conditions.isEmpty()) return Optional.empty();
        return Optional.of(" where " + String.join(" and ", conditions) + " ");
    }

    // Todo : delete after development
    private String getTable(String label) {
        return MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel(label).getName();
    }
}
