package io.metadew.iesi.server.rest.configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class IesiHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String[]> parameterMap = new HashMap<>();

    public IesiHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public void addParameter(String name, String value) {
        parameterMap.put(name, new String[]{value});
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (parameterMap.containsKey(name)) {
            value = Arrays.stream(parameterMap.get(name)).findFirst().orElse("");
        }
        return value;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }
}
