package org.ifmo.ru.parser.ast.statements;

import org.ifmo.ru.parser.ast.expressions.Expression;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WhileStatement extends Statement {
    private Expression condition;
    private Statement body;
}
