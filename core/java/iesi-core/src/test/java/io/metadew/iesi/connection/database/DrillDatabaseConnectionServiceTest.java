package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.drill.DrillDatabaseConnectionService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DrillDatabaseConnectionServiceTest {

    @Test
    void refactorOneLimitAndOffset() {

        String query = "SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 10 offset 77";
        String query2 = "SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 10 offset 77 Select script.SCRIPT_ID offset 23";
        assertThat(DrillDatabaseConnectionService.getInstance().refactorLimitAndOffset(query))
                .isEqualTo("SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 10 offset 77 ROWS ");
        assertThat(DrillDatabaseConnectionService.getInstance().refactorLimitAndOffset(query2))
                .isEqualTo("SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 10 offset 77 ROWS  Select script.SCRIPT_ID offset 23");;

    }

    @Test
    void refactorSeveralLimitAndOffset() {

        String query = "SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 10 offset 3 , SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 20 offset 7 , limit 34";
        String query2 = "SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 10 offset 3, SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 20 offset 7, SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 40 offset 16 limit 34";

        assertThat(DrillDatabaseConnectionService.getInstance().refactorLimitAndOffset(query))
                .isEqualTo("SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 10 offset 3 ROWS  , SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 20 offset 7 ROWS  , limit 34");
        assertThat(DrillDatabaseConnectionService.getInstance().refactorLimitAndOffset(query2))
                .isEqualTo("SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 10 offset 3 ROWS , SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 20 offset 7 ROWS , SELECT script.SCRIPT_ID, script.SCRIPT_DSC limit 40 offset 16 ROWS  limit 34");;

    }
}
