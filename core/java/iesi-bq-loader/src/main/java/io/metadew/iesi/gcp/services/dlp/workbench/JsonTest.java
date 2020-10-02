package io.metadew.iesi.gcp.services.dlp.workbench;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.gcp.configuration.bigquery.ScriptExecutionConfiguration;
import io.metadew.iesi.gcp.configuration.cco.rest.ScriptExecutionCco;
import io.metadew.iesi.gcp.configuration.cco.core.ScriptResultCco;
import io.metadew.iesi.gcp.connection.bigquery.BigqueryConnection;
import io.metadew.iesi.gcp.services.bigquery.common.BigqueryService;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JsonTest {

    public static void main(String[] args)  {
        System.out.println("test");

        try {
            String input = "{\"runID\":\"041b427f-e5bc-4288-87a1-1c4855b5be3e\",\"processId\":-1,\"parentProcessId\":-1,\"scriptId\":\"936a185caaa266bb9cbe981e9e05cb78cd732b0b3280eb944412bb6f8f8f07af\",\"scriptName\":\"helloworld\",\"scriptVersion\":0,\"environment\":\"tutorial\",\"status\":\"SUCCESS\",\"startTimestamp\":\"2020-09-28 09:34:09.568\",\"endTimestamp\":\"2020-09-28 09:34:10.062\"}";
            String input2 = "{\"_embedded\":[{\"runId\":\"b3567a38-abb0-48eb-9e85-649a70e5c2a4\",\"processId\":-1,\"parentProcessId\":-1,\"scriptId\":\"936a185caaa266bb9cbe981e9e05cb78cd732b0b3280eb944412bb6f8f8f07af\",\"scriptName\":\"helloworld\",\"scriptVersion\":0,\"environment\":\"tutorial\",\"status\":\"SUCCESS\",\"startTimestamp\":\"2020-09-14T16:01:33.064\",\"endTimestamp\":\"2020-09-14T16:01:33.849\",\"inputParameters\":[],\"designLabels\":[],\"executionLabels\":[],\"actions\":[{\"runId\":\"b3567a38-abb0-48eb-9e85-649a70e5c2a4\",\"processId\":0,\"type\":\"fwk.dummy\",\"name\":\"HelloWorld\",\"description\":\"HelloWorld Action\",\"condition\":\"\",\"errorStop\":false,\"errorExpected\":false,\"status\":\"SUCCESS\",\"startTimestamp\":\"2020-09-14T16:01:33.476\",\"endTimestamp\":\"2020-09-14T16:01:33.767\",\"inputParameters\":[],\"output\":[]}],\"output\":[]}]}";

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> map1
                    = objectMapper.readValue(input, new TypeReference<Map<String,Object>>(){});
            ScriptResultCco scriptResultCco = objectMapper.convertValue(map1, ScriptResultCco.class);

            System.out.println(scriptResultCco);
            System.out.println(scriptResultCco.getStartTimestamp());
            System.out.println("------------------------");


            Map<String, Object> map
                    = objectMapper.readValue(input2, new TypeReference<Map<String,Object>>(){});

            System.out.println(map.get("_embedded"));

            List<Object> items =
                    (List<Object>) map.get("_embedded");

            for (Object entry : items) {
                ScriptExecutionCco scriptExecutionCco = objectMapper.convertValue(entry, ScriptExecutionCco.class);
                System.out.println(scriptExecutionCco);
                ScriptExecutionConfiguration scriptExecutionConfiguration = new ScriptExecutionConfiguration();
                System.out.println(scriptExecutionConfiguration.getRowContent(scriptExecutionCco).toString());

                BigqueryConnection.getInstance().tableInsertRows("iesi_script_results_sample", scriptExecutionConfiguration.getTableName(), scriptExecutionConfiguration.getRowContent(scriptExecutionCco));

                //MetadataTableCco metadataTable = objectMapper.convertValue(entry, MetadataTableCco.class);
            }
        }catch (JSONException | IOException err){
            System.out.println(err.toString());
        }

    }
}
