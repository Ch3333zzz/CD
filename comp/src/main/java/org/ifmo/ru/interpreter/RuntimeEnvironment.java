package org.ifmo.ru.interpreter;

import java.util.HashMap;
import java.util.Map;

public class RuntimeEnvironment {

    private final RuntimeEnvironment parent;
    private Map<String, Object> variables = new HashMap<>();

    public RuntimeEnvironment() {
        this(null);
    }

    public RuntimeEnvironment(RuntimeEnvironment enviroment) {
        this.parent = enviroment;
    }

    public void define(String key, Object value) {
        variables.put(key, value);
    }

    public void assign(String name, Object value) {
        if (variables.containsKey(name)) {
            variables.put(name, value);
            return;
        }
        if (parent != null) {
            parent.assign(name, value);
            return;
        }
        throw new RuntimeException("Runtime exception: undefined variable: " + name + ".");
    } 

    public Object get(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        }
        if (parent != null) {
            return parent.get(name);
        }

        throw new RuntimeException("Runtime exception: undefined variable: " + name + ".");
    }

}
