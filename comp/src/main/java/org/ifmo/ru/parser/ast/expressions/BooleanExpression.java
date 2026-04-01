package org.ifmo.ru.parser.ast.expressions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BooleanExpression extends Expression {
    private boolean value;
    
    public BooleanExpression(boolean value) {
        this.value = value;
    }
}
