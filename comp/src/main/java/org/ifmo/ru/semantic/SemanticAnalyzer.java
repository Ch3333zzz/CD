package org.ifmo.ru.semantic;

import org.ifmo.ru.parser.ast.expressions.*;
import org.ifmo.ru.parser.ast.statements.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SemanticAnalyzer {
    private SemanticEnvironment environment = new SemanticEnvironment();
    private final List<String> errors = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();

    public void analyze(Iterable<Statement> statements) {
        for (Statement statement : statements) {
            visitStatement(statement);
        }
        
        checkUnusedVariables(environment);
    }

    private void checkUnusedVariables(SemanticEnvironment env) {
        for (Map.Entry<String, VariableInfo> entry : env.getVariables().entrySet()) {
            if (!entry.getValue().isUsed()) {
                warnings.add("Unused variable: '" + entry.getKey() + "'");
            }
        }
    }

    public void visitStatement(Statement statement) {
        if (statement == null) return;

        if (statement instanceof VarStatement varStatement) {
            if (varStatement.getInitializer() != null) {
                visitExpression(varStatement.getInitializer());
            }

            if (!environment.defineVariable(varStatement.getName())) {
                errors.add("Variable '" + varStatement.getName() + "' is already defined.");
            }

        } else if (statement instanceof PrintStatement printStatement) {
            visitExpression(printStatement.getExpression());

        } else if (statement instanceof ExpressionStatement expressionStatement) {
            visitExpression(expressionStatement.getExpression());

        } else if (statement instanceof BlockStatement blockStatement) {
            SemanticEnvironment previousEnvironment = environment;
            environment = new SemanticEnvironment(previousEnvironment);

            for (Statement innerStatement : blockStatement.getStatements()) {
                visitStatement(innerStatement);
            }

            checkUnusedVariables(environment);

            environment = previousEnvironment;

        } else if (statement instanceof IfStatement ifStatement) {
            visitExpression(ifStatement.getCondition());
            visitStatement(ifStatement.getThenBranch());
            if (ifStatement.getElseBranch() != null) {
                visitStatement(ifStatement.getElseBranch());
            }

        } else if (statement instanceof WhileStatement whileStatement) {
            visitExpression(whileStatement.getCondition());
            visitStatement(whileStatement.getBody());

        } else {
            errors.add("Unsupported statement type: " + statement.getClass().getSimpleName());
        }
    }

    public void visitExpression(Expression expression) {
        if (expression == null) return;

        if (expression instanceof NumberExpression) {
        } else if (expression instanceof VariableExpression v) {
            if (!environment.isVariableDefined(v.getName())) {
                errors.add("Variable '" + v.getName() + "' is not defined.");
            } else {
                environment.markAsUsed(v.getName());
            }

        } else if (expression instanceof AssignExpression a) {
            visitExpression(a.getValue());
            if (!environment.isVariableDefined(a.getName())) {
                errors.add("Variable '" + a.getName() + "' is not defined.");
            }

        } else if (expression instanceof BinaryExpression b) {
            visitExpression(b.getLeft());
            visitExpression(b.getRight());

        } else if (expression instanceof UnaryExpression u) {
            visitExpression(u.getRight());

        } else {
            errors.add("Unsupported expression type: " + expression.getClass().getSimpleName());
        }
    }

    public List<String> getErrors() { return errors; }
    public List<String> getWarnings() { return warnings; }
}