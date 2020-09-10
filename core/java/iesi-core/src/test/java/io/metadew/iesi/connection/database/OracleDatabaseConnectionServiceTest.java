package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.oracle.OracleDatabaseConnectionService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OracleDatabaseConnectionServiceTest {

    @Test
    void refactorOneLimitAndOffset() {

        String query = "SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 10 offset 77";
        String query2 = "SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 10 offset 77 Select script.SCRIPT_ID offset 23";
        assertThat(OracleDatabaseConnectionService.getInstance().refactorLimitAndOffset(query))
                .isEqualTo("SELECT script.SCRIPT_ID, script.SCRIPT_DSC  OFFSET 77 ROWS FETCH NEXT 10 ROWS ONLY ");
        assertThat(OracleDatabaseConnectionService.getInstance().refactorLimitAndOffset(query2))
                .isEqualTo("SELECT script.SCRIPT_ID, script.SCRIPT_DSC  OFFSET 77 ROWS FETCH NEXT 10 ROWS ONLY  Select script.SCRIPT_ID offset 23");
        ;

    }

    @Test
    void refactorSeveralLimitAndOffset() {

        String query = "SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 10 offset 3, SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 20 offset 7, limit 34";
        String query2 = "SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 10 offset 3, SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 20 offset 7, SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 40 offset 16 limit 34";
        assertThat(OracleDatabaseConnectionService.getInstance().refactorLimitAndOffset(query))
                .isEqualTo("SELECT script.SCRIPT_ID, script.SCRIPT_DSC  OFFSET 3 ROWS FETCH NEXT 10 ROWS ONLY , SELECT script.SCRIPT_ID, script.SCRIPT_DSC  OFFSET 7 ROWS FETCH NEXT 20 ROWS ONLY , limit 34");
        assertThat(OracleDatabaseConnectionService.getInstance().refactorLimitAndOffset(query2))
                .isEqualTo("SELECT script.SCRIPT_ID, script.SCRIPT_DSC  OFFSET 3 ROWS FETCH NEXT 10 ROWS ONLY , SELECT script.SCRIPT_ID, script.SCRIPT_DSC  OFFSET 7 ROWS FETCH NEXT 20 ROWS ONLY , SELECT script.SCRIPT_ID, script.SCRIPT_DSC  OFFSET 16 ROWS FETCH NEXT 40 ROWS ONLY  limit 34");
    }
}
