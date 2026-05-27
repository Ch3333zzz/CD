package org.ifmo.ru.parser.ast.expressions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ArrayAssignExpression extends Expression {
    private final Expression array;
    private final Expression index;
    private final Expression value;
}