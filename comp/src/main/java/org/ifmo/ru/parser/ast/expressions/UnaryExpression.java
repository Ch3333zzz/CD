package org.ifmo.ru.parser.ast.expressions;

import org.ifmo.ru.utils.TokenType;

public class UnaryExpression extends Expression {
    private TokenType operator;
    private Expression right;

    public UnaryExpression(TokenType operator, Expression right) {
        this.operator = operator;
        this.right = right;
    }

    public TokenType getOperator() {
        return operator;
    }

    public void setOperator(TokenType operator) {
        this.operator = operator;
    }

    public Expression getRight() {
        return right;
    }

    public void setRight(Expression right) {
        this.right = right;
    }

}
