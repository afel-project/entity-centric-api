package org.mksmart.ecapi.impl.js;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.mksmart.ecapi.api.MicrocompilerSet;

public class JsMicrocompilerSet implements MicrocompilerSet<String> {

    private Map<String,String> functions = new HashMap<>();

    @Override
    public String getCode(String functionName) {
        return functions.get(functionName);
    }

    @Override
    public Set<String> getFunctionNames() {
        return functions.keySet();
    }

    @Override
    public boolean hasFunction(String functionName) {
        return functions.containsKey(functionName);
    }

    @Override
    public void setFunction(String functionName, String code) {
        functions.put(functionName, code);
    }

    @Override
    public void unsetFunction(String functionName) {
        functions.remove(functionName);
    }

}
