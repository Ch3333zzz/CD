package org.ifmo.ru.parser.ast.statements;

import java.util.List;

public class FunctionStatement extends Statement {
    private final String name;
    private final List<String> parameters;
    private final BlockStatement body;

    public FunctionStatement(String name, List<String> parameters, BlockStatement body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public BlockStatement getBody() {
        return body;
    }
}