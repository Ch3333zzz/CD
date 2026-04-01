package org.ifmo.ru.semantic;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VariableInfo { 
    private boolean isUsed = false;
    private boolean isDefined = false;
    private boolean isInitialized = false;

    private VariableType variableType = null;
}
