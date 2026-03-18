package org.ifmo.ru.parser.ast.expressions;

public class StringExpression extends Expression{
    private String value;

    public StringExpression(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}
