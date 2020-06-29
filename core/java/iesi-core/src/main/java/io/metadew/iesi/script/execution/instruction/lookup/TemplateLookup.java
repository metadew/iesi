//package io.metadew.iesi.script.execution.instruction.lookup;
//
//import io.metadew.iesi.datatypes.DataType;
//import io.metadew.iesi.datatypes.DataTypeHandler;
//import io.metadew.iesi.datatypes.template.TemplateService;
//import io.metadew.iesi.datatypes.text.Text;
//import io.metadew.iesi.metadata.definition.template.Template;
//import io.metadew.iesi.script.execution.ExecutionRuntime;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.text.MessageFormat;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class TemplateLookup implements LookupInstruction {
//
//    private final String DATASET_NAME_KEY = "name";
//
//
//    private final Pattern INPUT_PARAMETER_PATTERN = Pattern
//            .compile("\\s*\"?(?<" + DATASET_NAME_KEY + ">(\\w|\\.)+)\"?\\s*");
//
//    private final ExecutionRuntime executionRuntime;
//    private static final Logger LOGGER = LogManager.getLogger();
//
//    public TemplateLookup(ExecutionRuntime executionRuntime) {
//        this.executionRuntime = executionRuntime;
//    }
//
//    @Override
//    public String getKeyword() {
//        return "template";
//    }
//
//    @Override
//    public String generateOutput(String parameters) {
//        Matcher inputParameterMatcher = INPUT_PARAMETER_PATTERN.matcher(parameters);
//        if (!inputParameterMatcher.find()) {
//            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to connection lookup: {0}", parameters));
//        }
//        DataType templateName = DataTypeHandler.getInstance().resolve(inputParameterMatcher.group(DATASET_NAME_KEY), executionRuntime);
//        if (templateName instanceof Text) {
//            return TemplateService.getInstance().get(((Text) templateName).getString())
//                    .map(Template::toString)
//                    .orElseThrow(() -> new IllegalArgumentException(MessageFormat.format("cannot find template with nale: {0}", ((Text) templateName).getString())));
//        } else {
//            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to template lookup: {0}", templateName));
//        }
//    }
//
//}
