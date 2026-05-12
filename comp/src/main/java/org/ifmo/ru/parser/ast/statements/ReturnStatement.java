package org.ifmo.ru.parser.ast.statements;

import org.ifmo.ru.parser.ast.expressions.Expression;

public class ReturnStatement extends Statement {
    private final Expression value;

    public ReturnStatement(Expression value) {
        this.value = value;
    }

    public Expression getValue() {
        return value;
    }
}