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
@ContextConfiguration(classes = { TestConfiguration.class, DataTypeHandler.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class TextRegexTest {

    TextRegex textRegex;

    @MockBean
    DataTypeHandler dataTypeHandler;

    @BeforeEach
    void beforeEach() {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        textRegex = new TextRegex(executionRuntime);
    }

    @Test
    void generateOutput() {
        String text = "Hello world";
        String pattern = "world";

        when(dataTypeHandler.resolve(eq(String.format("%s,%s", text, pattern)), isA(ExecutionRuntime.class)))
                .thenReturn(new Text(String.format("%s,%s", text, pattern)));

        assertThat(textRegex.generateOutput(String.format("%s,%s", text, pattern)))
                .isEqualTo("world");
    }

    @Test
    void generateOutputNotFound() {
        String text = "Hello worl";
        String pattern = "world";

        when(dataTypeHandler.resolve(eq(String.format("%s,%s", text, pattern)), isA(ExecutionRuntime.class)))
                .thenReturn(new Text(String.format("%s,%s", text, pattern)));

        assertThat(textRegex.generateOutput(String.format("%s,%s", text, pattern)))
                .isEqualTo("");
    }


    @Test
    void generateOutputComplexPattern() {
        String text = "Hello my e-mail address is test.unit@accenture.com";
        String pattern = "[^@\\s]*@.+\\.[a-z]*";

        when(dataTypeHandler.resolve(eq(String.format("%s,%s", text, pattern)), isA(ExecutionRuntime.class)))
                .thenReturn(new Text(String.format("%s,%s", text, pattern)));

        assertThat(textRegex.generateOutput(String.format("%s,%s", text, pattern)))
                .isEqualTo("test.unit@accenture.com");
    }

    @Test
    void generateOutputComplexPatternNotFound() {
        String text = "Hello my e-mail address is test.unitaccenture.com";
        String pattern = "[^@\\s]*@.+\\.[a-z]*";

        when(dataTypeHandler.resolve(eq(String.format("%s,%s", text, pattern)), isA(ExecutionRuntime.class)))
                .thenReturn(new Text(String.format("%s,%s", text, pattern)));

        assertThat(textRegex.generateOutput(String.format("%s,%s", text, pattern)))
                .isEqualTo("");
    }
}
