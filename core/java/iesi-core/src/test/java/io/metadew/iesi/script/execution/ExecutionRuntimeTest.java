package io.metadew.iesi.script.execution;

import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationHandler;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.in.memory.InMemoryDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.datatypes.text.Text;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.powermock.reflect.Whitebox;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Disabled
public class ExecutionRuntimeTest {

    private final ExecutionControl executionControl = mock(ExecutionControl.class);
    private final ScriptExecutionInitializationParameters scriptExecutionInitializationParameters = mock(ScriptExecutionInitializationParameters.class);

    @Test
    void resolveConceptLookupTest() {
        ExecutionRuntime executionRuntime = new ExecutionRuntime(executionControl, "1", scriptExecutionInitializationParameters);

        String instruction = "TEXT : {{*math.add(1,2)}}";
        LookupResult lookupResult = executionRuntime.resolveConceptLookup(instruction);

        assertThat(lookupResult.getValue()).isEqualTo("TEXT : 3");
    }

    @Test
    void resolveConceptLookupTestNestedInstruction() {
        ExecutionRuntime executionRuntime = new ExecutionRuntime(executionControl, "2", scriptExecutionInitializationParameters);

        String instruction = "TEXT : {{*math.add(1, {{*math.add(2,3)}})}}";
        LookupResult lookupResult = executionRuntime.resolveConceptLookup(instruction);

        assertThat(lookupResult.getValue()).isEqualTo("TEXT : 6");
    }

    @Test
    void resolveConceptLookupTestNestedInstructions() {
        ExecutionRuntime executionRuntime = new ExecutionRuntime(executionControl, "3", scriptExecutionInitializationParameters);

        String instruction = "TEXT : {{*math.add({{*math.add(5,5)}}, {{*math.add(2,3)}})}}";
        LookupResult lookupResult = executionRuntime.resolveConceptLookup(instruction);

        assertThat(lookupResult.getValue()).isEqualTo("TEXT : 15");
    }

    @Test
    void resolveConceptLookupTestWithCommaXml() {
        ExecutionRuntime executionRuntime = new ExecutionRuntime(executionControl, "4", scriptExecutionInitializationParameters);

        String instruction = "XML = {{*text.xmlpath(<text><value>Hello, world</value></text>, /text/value)}}";
        LookupResult lookupResult = executionRuntime.resolveConceptLookup(instruction);

        assertThat(lookupResult.getValue()).isEqualTo("XML = Hello, world");
    }

    @Test
    void resolveConceptLookupTestjson() {
        ExecutionRuntime executionRuntime = new ExecutionRuntime(executionControl, "5", scriptExecutionInitializationParameters);

        String instruction = "TEXT : {{*text.jsonpath({\"test\": \"test\"}, /test)}}";
        LookupResult lookupResult = executionRuntime.resolveConceptLookup(instruction);

        assertThat(lookupResult.getValue()).isEqualTo("TEXT : test");
    }

    @Test
    void resolveConceptLookupTestWithBreakLines() {
        ExecutionRuntime executionRuntime = new ExecutionRuntime(executionControl, "6", scriptExecutionInitializationParameters);

        String instruction = "TEXT : {{*text.jsonpath({\n" +
                "\"test\": \"test\"\n" +
                "}, /test)}}";

        LookupResult lookupResult = executionRuntime.resolveConceptLookup(instruction);
        assertThat(lookupResult.getValue()).isEqualTo("TEXT : test");
    }

    @Test
    void resolveConceptLookupTestWithComplexJsonDataset() {
        ExecutionRuntime executionRuntime = new ExecutionRuntime(executionControl, "7", scriptExecutionInitializationParameters);
        DatasetImplementationHandler datasetImplementationHandler = mock(DatasetImplementationHandler.class);
        DatasetImplementationHandler datasetImplementationHandlerSpy = spy(datasetImplementationHandler);
        Whitebox.setInternalState(DatasetImplementationHandler.class, "instance", datasetImplementationHandlerSpy);

        DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey(UUID.randomUUID());
        InMemoryDatasetImplementation inMemoryDatasetImplementation = new InMemoryDatasetImplementation(
                datasetImplementationKey,
                new DatasetKey(UUID.randomUUID()),
                "FeatureTestOutput",
                Stream.of(
                        new DatasetImplementationLabel(new DatasetImplementationLabelKey(UUID.randomUUID()), datasetImplementationKey, "jsonpath"),
                        new DatasetImplementationLabel(new DatasetImplementationLabelKey(UUID.randomUUID()), datasetImplementationKey, "api-response"),
                        new DatasetImplementationLabel(new DatasetImplementationLabelKey(UUID.randomUUID()), datasetImplementationKey, "output")
                ).collect(Collectors.toSet()),
                new HashSet<>()
        );

        executionRuntime.setKeyValueDataset("FeatureTestOutput", inMemoryDatasetImplementation);
        when(datasetImplementationHandlerSpy.getDataItem(any(DatasetImplementation.class), eq("rawbody"), any(ExecutionRuntime.class)))
                .thenReturn(Optional.of(new Text("[\n" +
                        "   {\n" +
                        "      \"userId\":1,\n" +
                        "      \"id\":1,\n" +
                        "      \"title\":\"SUM : Ok\", \n" +
                        "      \"completed\":false\n" +
                        "   },\n" +
                        "   {\n" +
                        "      \"userId\":1,\n" +
                        "      \"id\":2,\n" +
                        "      \"title\": \"MY TITLE\",\n" +
                        "      \"completed\":false\n" +
                        "   }]")));

        String instruction = "My <!{{text!> : {{*text.jsonPath({{=dataset(FeatureTestOutput, rawbody)}}, /0/title)}}";

        LookupResult lookupResult = executionRuntime.resolveConceptLookup(instruction);

        assertThat(lookupResult.getValue()).isEqualTo("My {{text : SUM : Ok");
    }

    @Test
    void resolveConceptLookupTestWithComplexJsonArray() {
        ExecutionRuntime executionRuntime = new ExecutionRuntime(executionControl, "8", scriptExecutionInitializationParameters);
        String instruction = "My <!{{text!> : {{*text.jsonPath(<!{\n" +
                "    \"glossary\": {\n" +
                "        \"title\": \"example glossary\",\n" +
                "\t\t\"GlossDiv\": {\n" +
                "            \"title\": \"S\",\n" +
                "\t\t\t\"GlossList\": {\n" +
                "                \"GlossEntry\": {\n" +
                "                    \"ID\": \"SGML\",\n" +
                "\t\t\t\t\t\"SortAs\": \"SG,ML\",\n" +
                "\t\t\t\t\t\"GlossTerm\": \"Stan,dard Generalized Markup Language\",\n" +
                "\t\t\t\t\t\"Acronym\": \"SGML\",\n" +
                "\t\t\t\t\t\"Abbrev\": \"ISO 88,79:1986\",\n" +
                "\t\t\t\t\t\"GlossDef\": {\n" +
                "                        \"para\": \"A meta-markup language, ,used to create, markup languages such as DocBook.\",\n" +
                "\t\t\t\t\t\t\"GlossSeeAlso\": [\"GM,L\", \",XM,L\"]\n" +
                "                    },\n" +
                "\t\t\t\t\t\"GlossSee\": \"markup\"\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}!>, /glossary/GlossDiv/GlossList/GlossEntry/GlossDef/GlossSeeAlso/1)}}";

        LookupResult lookupResult = executionRuntime.resolveConceptLookup(instruction);

        assertThat(lookupResult.getValue()).isEqualTo("My {{text : ,XM,L");
    }

    @Test
    void resolveConceptLookupTestWithCurlyBracesInside() {
        ExecutionRuntime executionRuntime = new ExecutionRuntime(executionControl, "9", scriptExecutionInitializationParameters);

        String instruction = "TEXT = {{*text.replace(\"<!{{hello}}!>\", \"<!{{hello}}!>\", \"world\")}}";
        LookupResult lookupResult = executionRuntime.resolveConceptLookup(instruction);

        assertThat(lookupResult.getValue()).isEqualTo("TEXT = world");
    }

    @Test
    void resolveConceptLookupTestWithOneClosingCurlyBracesInside() {
        ExecutionRuntime executionRuntime = new ExecutionRuntime(executionControl, "10", scriptExecutionInitializationParameters);

        String instruction = "TEXT = {{*text.replace(\"hello<!}}!>\", \"hello<!}}!>\", \"world\")}}";
        LookupResult lookupResult = executionRuntime.resolveConceptLookup(instruction);

        assertThat(lookupResult.getValue()).isEqualTo("TEXT = world");
    }

    @Test
    void resolveConceptLookupWithMultipleInstruction() {
        ExecutionRuntime executionRuntime = new ExecutionRuntime(executionControl, "11", scriptExecutionInitializationParameters);
        DatasetImplementationHandler datasetImplementationHandler = mock(DatasetImplementationHandler.class);
        DatasetImplementationHandler datasetImplementationHandlerSpy = spy(datasetImplementationHandler);
        Whitebox.setInternalState(DatasetImplementationHandler.class, "instance", datasetImplementationHandlerSpy);

        DatasetImplementationKey phoneNumberDatasetImplementationKey = new DatasetImplementationKey(UUID.randomUUID());
        InMemoryDatasetImplementation inMemoryPhoneNumberDatasetImplementationKey = new InMemoryDatasetImplementation(
                phoneNumberDatasetImplementationKey,
                new DatasetKey(UUID.randomUUID()),
                "FeatureTestOutput",
                Stream.of(
                        new DatasetImplementationLabel(new DatasetImplementationLabelKey(UUID.randomUUID()), phoneNumberDatasetImplementationKey, "mylabel")
                ).collect(Collectors.toSet()),
                new HashSet<>()
        );

        DatasetImplementationKey locationDatasetImplementationKey = new DatasetImplementationKey(UUID.randomUUID());
        InMemoryDatasetImplementation inMemoryLocationDatasetImplementationKey = new InMemoryDatasetImplementation(
                locationDatasetImplementationKey,
                new DatasetKey(UUID.randomUUID()),
                "FeatureTestOutput",
                Stream.of(
                        new DatasetImplementationLabel(new DatasetImplementationLabelKey(UUID.randomUUID()), locationDatasetImplementationKey, "mylabel")
                ).collect(Collectors.toSet()),
                new HashSet<>()
        );

        executionRuntime.setKeyValueDataset("FeatureTestPhoneNumber", inMemoryPhoneNumberDatasetImplementationKey);
        executionRuntime.setKeyValueDataset("FeatureTestLocation", inMemoryLocationDatasetImplementationKey);
        when(datasetImplementationHandlerSpy.getDataItem(any(DatasetImplementation.class), eq("phoneNumber"), any(ExecutionRuntime.class)))
                .thenReturn(Optional.of(new Text("{{*text.replace(\"+32 478 67 5\", \" \", \"-\")}}")));
        when(datasetImplementationHandlerSpy.getDataItem(any(DatasetImplementation.class), eq("location"), any(ExecutionRuntime.class)))
                .thenReturn(Optional.of(new Text("Nowhere")));

        String instruction = "My name is : {{*text.replace(\"placeholder Doe\", \"placeholder\", \"Jane\")}}, My phone number is : {{*text.replace(\"04 92 16 09 04\", \" \", \"-\")}}";
        LookupResult lookupResult = executionRuntime.resolveConceptLookup(instruction);
        assertThat(lookupResult.getValue()).isEqualTo("My name is : Jane Doe, My phone number is : 04-92-16-09-04");
    }

    @Test
    void simpleinstruction() {
        ExecutionRuntime executionRuntime = new ExecutionRuntime(executionControl, "12", scriptExecutionInitializationParameters);

        String instruction = "TEXT = {{*math.add(4,5)}}";
        LookupResult lookupResult = executionRuntime.resolveConceptLookup(instruction);

        assertThat(lookupResult.getValue()).isEqualTo("TEXT = 9");
    }

    @Test
    void ignoringConceptWithMultipleIgnores() {
        ExecutionRuntime executionRuntime = new ExecutionRuntime(executionControl, "13", scriptExecutionInitializationParameters);

        String instruction = "TEXT = {{*text.substring(<!{{hamza!>,1,5)}}{{*text.substring(hamza<!}}!>,4,7)}}{{*text.substring(hamza,1,3)}}";

        LookupResult lookupResult = executionRuntime.resolveConceptLookup(instruction);
        assertThat(lookupResult.getValue()).isEqualTo("TEXT = {{hamza}}ham");
    }

    @Test
    void ignoringConceptWithRandomMultipleIgnores() {
        ExecutionRuntime executionRuntime = new ExecutionRuntime(executionControl, "14", scriptExecutionInitializationParameters);
        String instruction = "[ \"Hello\", \"{{*text.replace(\"Hello<!}}!> World\", \"Hello\", \"<!{{!>World<!}}!>\")}}, \"WithBrackets<!}}!>, \"WithBrackets<!{{!> ]";

        LookupResult lookupResult = executionRuntime.resolveConceptLookup(instruction);
        assertThat(lookupResult.getValue()).isEqualTo("[ \"Hello\", \"{{World}}}} World, \"WithBrackets<!}}!>, \"WithBrackets<!{{!> ]");
    }
}
