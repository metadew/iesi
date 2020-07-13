package io.metadew.iesi.server.rest.scriptExecutionDto;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
class ScriptExecutionDtoRepositoryTest {

    @Autowired
    private MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    private ScriptExecutionDtoRepository scriptExecutionDtoRepository;

    // testing the getSQLQuery -> need getSQLQuery to be public
//    @Test
//    void getSQLQueryTest(){
//        assertThat(scriptExecutionDtoRepository.getSQLQuery("0",0L).toLowerCase())
//            .isEqualToIgnoringWhitespace("select * from IESI_RES_SCRIPT results  inner join IESI_TRC_DES_SCRIPT script_traces on results.RUN_ID = script_traces.RUN_ID and results.PRC_ID = script_traces.PRC_ID inner join IESI_TRC_DES_SCRIPT_VRS script_version_traces on results.RUN_ID = script_version_traces.RUN_ID and results.PRC_ID = script_version_traces.PRC_ID left outer join IESI_TRC_DES_SCRIPT_PAR script_params_traces on results.RUN_ID = script_params_traces.RUN_ID and results.PRC_ID = script_params_traces.PRC_ID left outer  join IESI_TRC_DES_SCRIPT_LBL script_labels_traces on results.RUN_ID = script_labels_traces.RUN_ID and results.PRC_ID = script_labels_traces.PRC_ID inner join IESI_RES_ACTION action_results on results.RUN_ID = action_results.RUN_ID and results.prc_id = action_results.SCRIPT_PRC_ID inner join IESI_TRC_DES_ACTION action_design_traces on results.RUN_ID = action_design_traces.RUN_ID and action_results.prc_id = action_design_traces.PRC_ID left outer join IESI_TRC_DES_ACTION_PAR action_design_param_traces on results.RUN_ID = action_design_param_traces.RUN_ID and action_design_traces.PRC_ID = action_design_param_traces.PRC_ID left outer join IESI_RES_ACTION_OUT action_result_outputs on action_result_outputs.RUN_ID = results.RUN_ID and action_result_outputs.PRC_ID = action_results.PRC_ID where results.RUN_ID = '0' and results.prc_id = 0;"
//                    .toLowerCase());
//    }

}