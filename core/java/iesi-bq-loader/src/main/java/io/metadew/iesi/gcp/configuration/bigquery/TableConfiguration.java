package io.metadew.iesi.gcp.configuration.bigquery;

import com.google.cloud.bigquery.Schema;
import io.metadew.iesi.gcp.configuration.cco.core.ScriptResultCco;
import io.metadew.iesi.gcp.configuration.cco.rest.ScriptExecutionCco;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TableConfiguration {

    private String tableName;

    public Schema getSchema() {
        return null;
    }

    public Map<String, Object> getRowContent(ScriptResultCco scriptResultCco) { return  null; }
    public Map<String, Object> getRowContent(ScriptExecutionCco scriptExecutionCco) { return  null; }
}
