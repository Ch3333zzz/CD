package org.ifmo.ru.parser.ast.statements;

import org.ifmo.ru.parser.ast.expressions.Expression;

import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@Setter
@AllArgsConstructor
public class VarStatement extends Statement {
  private String name;
  private Expression Initializer;
}
