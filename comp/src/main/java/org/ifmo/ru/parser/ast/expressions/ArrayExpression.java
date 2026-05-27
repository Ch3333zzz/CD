package org.ifmo.ru.parser.ast.expressions;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ArrayExpression extends Expression {
    private final List<Expression> elements;
}
