package org.ifmo.ru.parser.ast.statements;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class BlockStatement extends Statement {
    private List<Statement> statements;
}
