package org.ifmo.ru.semantic;

import java.util.HashMap;
import java.util.Map;

public class SemanticEnvironment {
    private final SemanticEnvironment parent;
    private final Map<String, VariableInfo> variables;

    public SemanticEnvironment() {
        this(null);
    }

    public SemanticEnvironment(SemanticEnvironment parent) {
        this.parent = parent;
        this.variables = new HashMap<>();
    }

    public boolean defineVariable(String name) {
        if (variables.containsKey(name)) {
            return false;
        }
        variables.put(name, new VariableInfo());
        return true;
    }

    public boolean isVariableDefined(String name) {
        if (variables.containsKey(name)) {
            return true; 
        }
        if (parent != null) {
            return parent.isVariableDefined(name);
        }
        return false;
    }

    public boolean isVariableInitialized(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name).isInitialized();
        }
        
        if (parent != null) {
            return parent.isVariableInitialized(name);
        }

        return false;
    }

    public void markAsUsed(String name) {
        if (variables.containsKey(name)) {
            variables.get(name).setUsed(true);
        } else if (parent != null) {
            parent.markAsUsed(name); 
        }
    }

    public void markAsInitialized(String name) {
         if (variables.containsKey(name)) {
            variables.get(name).setInitialized(true);
        } else if (parent != null) {
            parent.markAsInitialized(name); 
        }
    }

    public Map<String, VariableInfo> getVariables() {
        return this.variables;
    }

    public VariableInfo getVariableInfo(String name) {
    if (variables.containsKey(name)) {
        return variables.get(name);
    }
    if (parent != null) {
        return parent.getVariableInfo(name);
    }
    return null;
}
}