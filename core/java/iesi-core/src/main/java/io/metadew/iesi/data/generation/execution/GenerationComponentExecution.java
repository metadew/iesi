package io.metadew.iesi.data.generation.execution;

import io.metadew.iesi.data.generation.tools.GenerationTools;
import io.metadew.iesi.data.generation.tools.StringTools;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public abstract class GenerationComponentExecution {

    private static final String DIGIT_SYMBOL = "#";
    private static final String PARSE_REGEXP = "\\#\\{(.+?)\\}";

    private final GenerationDataExecution execution;
    private GenerationTools generationTools;

    public GenerationComponentExecution(GenerationDataExecution execution) {
        this.execution = execution;
        this.setGenerationTools(new GenerationTools());
    }

    public String getKey() {
        return this.getGenerationTools().getStringTools().camelToSnake(this.getClass().getSimpleName());
    }

    @SuppressWarnings("rawtypes")
    protected String fetch(String key) {
        String[] keys = key.split("\\.");

        List list;
        if (keys.length == 2) {
            list = getList(keys[0], keys[1]);
        } else {
            list = (List) getMap(keys[0], keys[1]).get(keys[2]);
        }

        return sampleFromList(list);
    }

    protected String numerify(String input) {
        return this.getGenerationTools().getStringTools().replaceMethod(input, DIGIT_SYMBOL, new StringTools.StringReplacer() {
            @Override
            public String replaceWith(Matcher matcher) {
                return generationTools.getRandomTools().digit();
            }
        });
    }

    protected String parse(String input) {
        return this.getGenerationTools().getStringTools().replaceMethod(input, PARSE_REGEXP, new StringTools.StringReplacer() {
            @Override
            public String replaceWith(Matcher matcher) {
                String key = matcher.group(1);
                return call(key);
            }
        });
    }

    protected String call(String key) {
        if (key.contains(".")) {
            String[] keys = key.split("\\.");
            return execution.getExecutionByKey(keys[0]).callMethod(keys[1]);
        } else {
            return callMethod(key);
        }
    }

    protected String getSeparator() {
        return (String) execution.get("separator");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected String sampleFromList(List options) {
        Object option = this.getGenerationTools().getRandomTools().sample(options);

        if (option instanceof String) {
            return (String) option;
        } else if (option instanceof List) { // List of lists
            return (String) this.getGenerationTools().getRandomTools().sample((List) option);
        } else {
            throw new UnsupportedOperationException("Unsupported execution type");
        }
    }

    @SuppressWarnings("rawtypes")
    protected List getList(String componentKey, String listKey) {
        List list = (List) getComponentData(componentKey).get(listKey);
        if (list == null) {
            throw new UnsupportedOperationException("Unsupported method '" + listKey + "'");
        }
        return list;
    }

    protected <K extends GenerationComponentExecution> K getComponent(Class<K> klass) {
        try {
            return klass.getConstructor(GenerationDataExecution.class).newInstance(execution);
        } catch (InstantiationException | NoSuchMethodException |
                IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Unsupported component '" + klass + "'", e);
        }
    }

    private String callMethod(String methodKey) {
        String methodKeyCamel = this.getGenerationTools().getStringTools().snakeToCamel(methodKey);
        String value;
        try {
            value = (String) getClass().getDeclaredMethod(methodKeyCamel).invoke(this);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(
                    "Unsupported method '" + methodKey + "' " +
                            "for component '" + this.getKey() + "'", e);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> getMap(String componentKey, String listKey) {
        Map<String, Object> map = (Map<String, Object>) getComponentData(componentKey).get(listKey);
        if (map == null) {
            throw new UnsupportedOperationException("Unsupported method '" + listKey + "'");
        }
        return map;
    }

    private Map<String, Object> getComponentData(String componentKey) {
        return execution.getComponentData(componentKey);
    }

    // Getters and setters
    public GenerationTools getGenerationTools() {
        return generationTools;
    }

    public void setGenerationTools(GenerationTools generationTools) {
        this.generationTools = generationTools;
    }
}
