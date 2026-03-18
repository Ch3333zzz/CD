package org.ifmo.ru.parser.ast.expressions;

public class AssignExpression extends Expression {
    private String name;
    private Expression value;

    public AssignExpression(String name, Expression value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Expression getValue() {
        return value;
    }

    public void setValue(Expression value) {
        this.value = value;
    }

}
