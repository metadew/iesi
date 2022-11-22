package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.presto.PrestoDatabaseConnectionService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PrestoDatabaseConnectionServiceTest {

    @Test
    void refactorOneLimitAndOffset() {

        String query = "SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 10 offset 77";
        String query2 = "SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 10 offset 77 Select script.SCRIPT_ID offset 23";
        assertThat(PrestoDatabaseConnectionService.getInstance().refactorLimitAndOffset(query))
                .isEqualTo("SELECT script.SCRIPT_ID, script.SCRIPT_DSC  OFFSET 77 LIMIT 10 ");
        assertThat(PrestoDatabaseConnectionService.getInstance().refactorLimitAndOffset(query2))
                .isEqualTo("SELECT script.SCRIPT_ID, script.SCRIPT_DSC  OFFSET 77 LIMIT 10  Select script.SCRIPT_ID offset 23");
    }

    @Test
    void refactorSeveralLimitAndOffset() {

        String query = "SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 10 offset 3, SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 20 offset 7, limit 34";
        String query2 = "SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 10 offset 3, SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 20 offset 7, SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 40 offset 16 limit 34";
        assertThat(PrestoDatabaseConnectionService.getInstance().refactorLimitAndOffset(query))
                .isEqualTo("SELECT script.SCRIPT_ID, script.SCRIPT_DSC  OFFSET 3 LIMIT 10 , SELECT script.SCRIPT_ID, script.SCRIPT_DSC  OFFSET 7 LIMIT 20 , limit 34");
        assertThat(PrestoDatabaseConnectionService.getInstance().refactorLimitAndOffset(query2))
                .isEqualTo("SELECT script.SCRIPT_ID, script.SCRIPT_DSC  OFFSET 3 LIMIT 10 , SELECT script.SCRIPT_ID, script.SCRIPT_DSC  OFFSET 7 LIMIT 20 , SELECT script.SCRIPT_ID, script.SCRIPT_DSC  OFFSET 16 LIMIT 40  limit 34");
    }
}
