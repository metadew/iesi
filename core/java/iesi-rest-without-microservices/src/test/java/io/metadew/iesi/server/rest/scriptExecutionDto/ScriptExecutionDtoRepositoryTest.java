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

    //    testing the getSQLQuery -> need getSQLQuery to be public
//    @Test
//    void getSQLQueryTest() {
//        assertThat(scriptExecutionDtoRepository.getSQLQuery("0", -1L).toLowerCase())
//                .isEqualToIgnoringWhitespace(
//                        "SELECT 0 INFO_TYPE, results.RUN_ID RUN_ID, results.PRC_ID SCRIPT_PRC_ID, results.PARENT_PRC_ID SCRIPT_PARENT_PRC_ID, results.SCRIPT_ID SCRIPT_ID, results.SCRIPT_NM SCRIPT_NM, results.SCRIPT_VRS_NB SCRIPT_VRS_NB, results.ENV_NM ENV_NM, results.ST_NM SCRIPT_ST_NM, results.STRT_TMS SCRIPT_STRT_TMS, results.END_TMS SCRIPT_END_TMS, trc_des_script_par.SCRIPT_PAR_NM SCRIPT_PAR_NM, trc_des_script_par.SCRIPT_PAR_VAL SCRIPT_PAR_VAL_RAW, trc_script_par.SCRIPT_PAR_VAL SCRIPT_PAR_VAL_RESOLVED, null SCRIPT_LBL_ID, null SCRIPT_LBL_NM, null SCRIPT_LBL_VAL, null SCRIPT_OUTPUT_NM, null SCRIPT_OUTPUT_VAL, null SCRIPT_EXE_LBL_NM, null SCRIPT_EXE_LBL_VAL, null ACTION_PRC_ID, null ACTION_ID, null ACTION_TYP_NM, null ACTION_NM, null ACTION_DSC, null ACTION_CONDITION_VAL, null ACTION_STOP_ERR_FL, null ACTION_EXP_ERR_FL, null ACTION_ST_NM, null ACTION_STRT_TMS, null ACTION_END_TMS, null ACTION_PAR_NM, null ACTION_PAR_VAL_RAW, null ACTION_PAR_VAL_RESOLVED, null ACTION_OUTPUT_NM, null ACTION_OUTPUT_VAL FROM IESI_RES_SCRIPT results LEFT OUTER JOIN IESI_TRC_DES_SCRIPT_PAR trc_des_script_par on results.RUN_ID = trc_des_script_par.RUN_ID AND results.PRC_ID = trc_des_script_par.PRC_ID LEFT OUTER JOIN IESI_TRC_SCRIPT_PAR trc_script_par on results.RUN_ID = trc_script_par.RUN_ID AND results.PRC_ID = trc_script_par.PRC_ID where results.RUN_ID = '0' AND results.PRC_ID = -1 UNION ALL SELECT 1 INFO_TYPE, results.RUN_ID RUN_ID, results.PRC_ID SCRIPT_PRC_ID, results.PARENT_PRC_ID SCRIPT_PARENT_PRC_ID, results.SCRIPT_ID SCRIPT_ID, results.SCRIPT_NM SCRIPT_NM, results.SCRIPT_VRS_NB SCRIPT_VRS_NB, results.ENV_NM ENV_NM, results.ST_NM SCRIPT_ST_NM, results.STRT_TMS SCRIPT_STRT_TMS, results.END_TMS SCRIPT_END_TMS, null SCRIPT_PAR_NM, null SCRIPT_PAR_VAL_RAW, null SCRIPT_PAR_VAL_RESOLVED, trc_des_script_lbl.SCRIPT_LBL_ID SCRIPT_LBL_ID, trc_des_script_lbl.NAME SCRIPT_LBL_NM, trc_des_script_lbl.VALUE SCRIPT_LBL_VAL, null SCRIPT_OUTPUT_NM, null SCRIPT_OUTPUT_VAL, null SCRIPT_EXE_LBL_NM, null SCRIPT_EXE_LBL_VAL, null ACTION_PRC_ID, null ACTION_ID, null ACTION_TYP_NM, null ACTION_NM, null ACTION_DSC, null ACTION_CONDITION_VAL, null ACTION_STOP_ERR_FL, null ACTION_EXP_ERR_FL, null ACTION_ST_NM, null ACTION_STRT_TMS, null ACTION_END_TMS, null ACTION_PAR_NM, null ACTION_PAR_VAL_RAW, null ACTION_PAR_VAL_RESOLVED, null ACTION_OUTPUT_NM, null ACTION_OUTPUT_VAL FROM IESI_RES_SCRIPT results INNER JOIN IESI_TRC_DES_SCRIPT_LBL trc_des_script_lbl on results.RUN_ID = trc_des_script_lbl.RUN_ID AND results.PRC_ID = trc_des_script_lbl.PRC_ID where results.RUN_ID = '0' AND results.PRC_ID = -1 UNION ALL SELECT 2 INFO_TYPE, results.RUN_ID RUN_ID, results.PRC_ID SCRIPT_PRC_ID, results.PARENT_PRC_ID SCRIPT_PARENT_PRC_ID, results.SCRIPT_ID SCRIPT_ID, results.SCRIPT_NM SCRIPT_NM, results.SCRIPT_VRS_NB SCRIPT_VRS_NB, results.ENV_NM ENV_NM, results.ST_NM SCRIPT_ST_NM, results.STRT_TMS SCRIPT_STRT_TMS, results.END_TMS SCRIPT_END_TMS, null SCRIPT_PAR_NM, null SCRIPT_PAR_VAL_RAW, null SCRIPT_PAR_VAL_RESOLVED, null SCRIPT_LBL_ID, null SCRIPT_LBL_NM, null SCRIPT_LBL_VAL, script_output.OUT_NM SCRIPT_OUTPUT_NM, script_output.OUT_VAL SCRIPT_OUTPUT_VAL, null SCRIPT_EXE_LBL_NM, null SCRIPT_EXE_LBL_VAL, null ACTION_PRC_ID, null ACTION_ID, null ACTION_TYP_NM, null ACTION_NM, null ACTION_DSC, null ACTION_CONDITION_VAL, null ACTION_STOP_ERR_FL, null ACTION_EXP_ERR_FL, null ACTION_ST_NM, null ACTION_STRT_TMS, null ACTION_END_TMS, null ACTION_PAR_NM, null ACTION_PAR_VAL_RAW, null ACTION_PAR_VAL_RESOLVED, null ACTION_OUTPUT_NM, null ACTION_OUTPUT_VAL FROM IESI_RES_SCRIPT results INNER JOIN IESI_RES_SCRIPT_OUT script_output on results.RUN_ID = script_output.RUN_ID AND results.PRC_ID = script_output.PRC_ID where results.RUN_ID = '0' AND results.PRC_ID = -1 UNION ALL SELECT 3 INFO_TYPE, results.RUN_ID RUN_ID, results.PRC_ID SCRIPT_PRC_ID, results.PARENT_PRC_ID SCRIPT_PARENT_PRC_ID, results.SCRIPT_ID SCRIPT_ID, results.SCRIPT_NM SCRIPT_NM, results.SCRIPT_VRS_NB SCRIPT_VRS_NB, results.ENV_NM ENV_NM, results.ST_NM SCRIPT_ST_NM, results.STRT_TMS SCRIPT_STRT_TMS, results.END_TMS SCRIPT_END_TMS, null SCRIPT_PAR_NM, null SCRIPT_PAR_VAL_RAW, null SCRIPT_PAR_VAL_RESOLVED, null SCRIPT_LBL_ID, null SCRIPT_LBL_NM, null SCRIPT_LBL_VAL, null SCRIPT_OUTPUT_NM, null SCRIPT_OUTPUT_VAL, script_exec_lbl.NAME SCRIPT_EXE_LBL_NM, script_exec_lbl.VALUE SCRIPT_EXE_LBL_VAL, null ACTION_PRC_ID, null ACTION_ID, null ACTION_TYP_NM, null ACTION_NM, null ACTION_DSC, null ACTION_CONDITION_VAL, null ACTION_STOP_ERR_FL, null ACTION_EXP_ERR_FL, null ACTION_ST_NM, null ACTION_STRT_TMS, null ACTION_END_TMS, null ACTION_PAR_NM, null ACTION_PAR_VAL_RAW, null ACTION_PAR_VAL_RESOLVED, null ACTION_OUTPUT_NM, null ACTION_OUTPUT_VAL FROM IESI_RES_SCRIPT results INNER JOIN IESI_EXE_SCRPT_EXEC script_exec on results.RUN_ID = script_exec.RUN_ID INNER JOIN IESI_EXE_SCRPT_EXEC_REQ IESER on script_exec.SCRPT_REQUEST_ID = IESER.SCRPT_REQUEST_ID INNER JOIN IESI_EXE_REQ IER on IESER.ID = IER.REQUEST_ID INNER JOIN IESI_EXE_REQ_LBL script_exec_lbl on IER.REQUEST_ID = script_exec_lbl.REQUEST_ID where results.RUN_ID = '0' AND results.PRC_ID = -1 UNION ALL SELECT 4 INFO_TYPE, results.RUN_ID RUN_ID, results.PRC_ID SCRIPT_PRC_ID, results.PARENT_PRC_ID SCRIPT_PARENT_PRC_ID, results.SCRIPT_ID SCRIPT_ID, results.SCRIPT_NM SCRIPT_NM, results.SCRIPT_VRS_NB SCRIPT_VRS_NB, results.ENV_NM ENV_NM, results.ST_NM SCRIPT_ST_NM, results.STRT_TMS SCRIPT_STRT_TMS, results.END_TMS SCRIPT_END_TMS, null SCRIPT_PAR_NM, null SCRIPT_PAR_VAL_RAW, null SCRIPT_PAR_VAL_RESOLVED, null SCRIPT_LBL_ID, null SCRIPT_LBL_NM, null SCRIPT_LBL_VAL, null SCRIPT_OUTPUT_NM, null SCRIPT_OUTPUT_VAL, null SCRIPT_EXE_LBL_NM, null SCRIPT_EXE_LBL_VAL, action_trc.PRC_ID ACTION_PRC_ID, action_trc.ACTION_ID ACTION_ID, action_trc.ACTION_TYP_NM ACTION_TYP_NM, action_trc.ACTION_NM ACTION_NM, action_trc.ACTION_DSC ACTION_DSC, action_trc.CONDITION_VAL ACTION_CONDITION_VAL, action_trc.STOP_ERR_FL ACTION_STOP_ERR_FL, action_trc.EXP_ERR_FL ACTION_EXP_ERR_FL, action_res.ST_NM ACTION_ST_NM, action_res.STRT_TMS ACTION_STRT_TMS, action_res.END_TMS ACTION_END_TMS, action_des_trc_par.ACTION_PAR_NM ACTION_PAR_NM, action_des_trc_par.ACTION_PAR_VAL ACTION_PAR_VAL_RAW, action_trc_par.ACTION_PAR_VAL ACTION_PAR_VAL_RESOLVED, null ACTION_OUTPUT_NM, null ACTION_OUTPUT_VAL FROM IESI_RES_SCRIPT results INNER JOIN IESI_TRC_DES_ACTION action_trc on results.RUN_ID = action_trc.RUN_ID INNER JOIN IESI_RES_ACTION action_res on results.RUN_ID = action_res.RUN_ID LEFT OUTER JOIN IESI_TRC_DES_ACTION_PAR action_des_trc_par on action_trc.RUN_ID = action_des_trc_par.RUN_ID AND action_trc.PRC_ID = action_des_trc_par.PRC_ID LEFT OUTER JOIN IESI_TRC_ACTION_PAR action_trc_par on action_trc.RUN_ID = action_trc_par.RUN_ID AND action_trc.PRC_ID = action_trc_par.PRC_ID where results.RUN_ID = '0' AND results.PRC_ID = -1 UNION ALL SELECT 5 INFO_TYPE, results.RUN_ID RUN_ID, results.PRC_ID SCRIPT_PRC_ID, results.PARENT_PRC_ID SCRIPT_PARENT_PRC_ID, results.SCRIPT_ID SCRIPT_ID, results.SCRIPT_NM SCRIPT_NM, results.SCRIPT_VRS_NB SCRIPT_VRS_NB, results.ENV_NM ENV_NM, results.ST_NM SCRIPT_ST_NM, results.STRT_TMS SCRIPT_STRT_TMS, results.END_TMS SCRIPT_END_TMS, null SCRIPT_PAR_NM, null SCRIPT_PAR_VAL_RAW, null SCRIPT_PAR_VAL_RESOLVED, null SCRIPT_LBL_ID, null SCRIPT_LBL_NM, null SCRIPT_LBL_VAL, null SCRIPT_OUTPUT_NM, null SCRIPT_OUTPUT_VAL, null SCRIPT_EXE_LBL_NM, null SCRIPT_EXE_LBL_VAL, action_trc.PRC_ID ACTION_PRC_ID, action_trc.ACTION_ID ACTION_ID, action_trc.ACTION_TYP_NM ACTION_TYP_NM, action_trc.ACTION_NM ACTION_NM, action_trc.ACTION_DSC ACTION_DSC, action_trc.CONDITION_VAL ACTION_CONDITION_VAL, action_trc.STOP_ERR_FL ACTION_STOP_ERR_FL, action_trc.EXP_ERR_FL ACTION_EXP_ERR_FL, action_res.ST_NM ACTION_ST_NM, action_res.STRT_TMS ACTION_STRT_TMS, action_res.END_TMS ACTION_END_TMS, null ACTION_PAR_NM, null ACTION_PAR_VAL_RAW, null ACTION_PAR_VAL_RESOLVED, action_res_output.OUT_NM ACTION_OUTPUT_NM, action_res_output.OUT_VAL ACTION_OUTPUT_VAL FROM IESI_RES_SCRIPT results INNER JOIN IESI_TRC_DES_ACTION action_trc on results.RUN_ID = action_trc.RUN_ID INNER JOIN IESI_RES_ACTION action_res on results.RUN_ID = action_res.RUN_ID INNER JOIN IESI_RES_ACTION_OUT action_res_output on action_res.RUN_ID = action_res_output.RUN_ID AND action_res.PRC_ID = action_res_output.PRC_ID where results.RUN_ID = '0' AND results.PRC_ID = -1;"
//                                .replace("\\S+"," ")
//                                .toLowerCase());
//    }

}