package io.metadew.iesi.script.execution.instruction.lookup;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import io.metadew.iesi.script.execution.LookupResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = TestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class CoalesceLookupTest {

    @SpyBean
    private DataTypeHandler dataTypeHandlerSpy;

    @Test
    void test() {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        doReturn(new Text("test"))
                .when(dataTypeHandlerSpy)
                .resolve("test", executionRuntime);
        when(executionRuntime.resolveVariables("{{^null()}}"))
                .thenReturn("{{^null()}}");
        when(executionRuntime.resolveConceptLookup("{{^null()}}"))
                .thenReturn(new LookupResult("{{^null()}}", null, null));

        when(executionRuntime.resolveVariables("{{^text()}}"))
                .thenReturn("{{^text()}}");
        when(executionRuntime.resolveConceptLookup("{{^text()}}"))
                .thenReturn(new LookupResult("{{^text()}}", null, null));

        when(executionRuntime.resolveVariables("{{^text(test)}}"))
                .thenReturn("{{^text(test)}}");
        when(executionRuntime.resolveConceptLookup("{{^text(test)}}"))
                .thenReturn(new LookupResult("{{^text(test)}}", null, null));

        when(executionRuntime.resolveVariables("test"))
                .thenReturn("test");
        when(executionRuntime.resolveConceptLookup("test"))
                .thenReturn(new LookupResult("test", null, null));

        when(executionRuntime.resolveVariables(""))
                .thenReturn("");
        when(executionRuntime.resolveConceptLookup(""))
                .thenReturn(new LookupResult("", null, null));
        CoalesceLookup coalesceLookup = new CoalesceLookup(executionRuntime);
        assertThat(coalesceLookup.generateOutput("test"))
                .isEqualTo("test");
        assertThat(coalesceLookup.generateOutput(",test"))
                .isEqualTo("test");
        assertThat(coalesceLookup.generateOutput("{{^null()}},test"))
                .isEqualTo("test");
        assertThat(coalesceLookup.generateOutput("{{^null()}},{{^text()}}"))
                .isEmpty();
        assertThat(coalesceLookup.generateOutput("{{^null()}},{{^text()}},test"))
                .isEmpty();
        assertThat(coalesceLookup.generateOutput("{{^null()}},{{^text(test)}},{{^null()}}"))
                .isEqualTo("test");
    }

}
