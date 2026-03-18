package org.ifmo.ru.semantic;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VariableInfo { 
    private boolean isUsed = false;
    private boolean isInitialized = false;
}
