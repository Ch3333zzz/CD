package org.ifmo.ru.parser.ast.expressions;

public class NumberExpression extends Expression{
    private double value;

    public NumberExpression(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
