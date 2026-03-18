package org.ifmo.ru.parser.ast.statements;

import org.ifmo.ru.parser.ast.expressions.Expression;

public class PrintStatement extends Statement {
    
    private Expression expression;

    public PrintStatement(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }
}
