package org.ifmo.ru.parser.ast.expressions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class IndexExpression extends Expression {
    private final Expression array;
    private final Expression index;
}