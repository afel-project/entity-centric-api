package org.mksmart.ecapi.api;

import java.util.Set;

/**
 * A function used to obtain a query or part of it.
 * 
 * @author alessandro <alexdma@apache.org>
 *
 * @param <C>
 *            the code to be executed
 */
public interface MicrocompilerSet<C> {

    public C getCode(String functionName);

    public Set<String> getFunctionNames();

    public boolean hasFunction(String functionName);

    public void setFunction(String functionName, C code);

    public void unsetFunction(String functionName);

}
