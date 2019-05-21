package io.metadew.iesi.framework.operation;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.util.NoSuchElementException;

public class ClassOperation {

    public ClassOperation() {
    }
	
	/*
	@SuppressWarnings("rawtypes")
	public static Class getActionClass(String actionType) {
		Class result = null;
		for (Class currentClass : ReflectionTools.getClasses("io.metadew.iesi.script.action")) {
			if (actionType.replace(".", "").toLowerCase().equalsIgnoreCase(currentClass.getSimpleName().toLowerCase()) ) {
				result = currentClass;
				break;
			}
		}		
		return result;
	}
	*/

    @SuppressWarnings("rawtypes")
    public static Class getActionClass(String actionType) {

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("io.metadew.iesi.script.action")))
                .setUrls(ClasspathHelper.forClassLoader()).setScanners(new SubTypesScanner(false)));

        return reflections.getSubTypesOf(Object.class).stream()
                .filter(clazz -> clazz.getSimpleName().toLowerCase().equalsIgnoreCase(StringUtils.remove(actionType, '.').toLowerCase()))
                .findFirst().orElseThrow(NoSuchElementException::new);
    }

    @SuppressWarnings("rawtypes")
    public static Class getExecutionRuntime(String executionRuntime) {

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("io.metadew.iesi.script.execution")))
                .setUrls(ClasspathHelper.forClassLoader()).setScanners(new SubTypesScanner(false)));

        return reflections.getSubTypesOf(Object.class).stream()
                .filter(clazz -> clazz.getSimpleName().toLowerCase().equalsIgnoreCase(StringUtils.remove(executionRuntime, '.').toLowerCase()))
                .findFirst().orElseThrow(NoSuchElementException::new);
    }

}