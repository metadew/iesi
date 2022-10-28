package io.metadew.iesi.script.execution.instruction.data.text;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class, DataTypeHandler.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class TextTrimTest {

    TextTrim textTrim;

    @MockBean
    DataTypeHandler dataTypeHandler;

    @BeforeEach
    void beforeEach() {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        textTrim = new TextTrim(executionRuntime);
    }


    @Test
    void generateOutput() {
        String text = "        My text           ";

        when(dataTypeHandler.resolve(eq(text), isA(ExecutionRuntime.class)))
                .thenReturn(new Text(text));

        assertThat(textTrim.generateOutput(text))
                .isEqualTo("My text");
    }

    @Test
    void generateOutputWhitespace() {
        String text = "\t \t \t My text \t \t";

        when(dataTypeHandler.resolve(eq(text), isA(ExecutionRuntime.class)))
                .thenReturn(new Text(text));

        assertThat(textTrim.generateOutput(text))
                .isEqualTo("My text");
    }

    @Test
    void generateOutputBeforeWhitespace() {
        String text = "\t \t \t My text";

        when(dataTypeHandler.resolve(eq(text), isA(ExecutionRuntime.class)))
                .thenReturn(new Text(text));

        assertThat(textTrim.generateOutput(text))
                .isEqualTo("My text");
    }

    @Test
    void generateOutputAfterWhitespace() {
        String text = "My text \t \t \t";

        when(dataTypeHandler.resolve(eq(text), isA(ExecutionRuntime.class)))
                .thenReturn(new Text(text));

        assertThat(textTrim.generateOutput(text))
                .isEqualTo("My text");
    }
    @Test
    void generateOutputNoWhitespace() {
        String text = "My text";

        when(dataTypeHandler.resolve(eq(text), isA(ExecutionRuntime.class)))
                .thenReturn(new Text(text));

        assertThat(textTrim.generateOutput(text))
                .isEqualTo("My text");
    }

}
