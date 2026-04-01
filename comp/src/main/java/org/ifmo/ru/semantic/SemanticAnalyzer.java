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
            if (!entry.getValue().isUsed() && entry.getValue().isDefined()) {
                warnings.add("Unused variable: '" + entry.getKey() + "'");
            }
        }
    }

    public void visitStatement(Statement statement) {
        if (statement == null) return;

        if (statement instanceof VarStatement varStatement) {
            if (!environment.defineVariable(varStatement.getName())) {
                errors.add("Variable '" + varStatement.getName() + "' is already defined.");
            }

            if (varStatement.getInitializer() != null) {
                VariableType initType = visitExpression(varStatement.getInitializer());
                
                VariableInfo info = environment.getVariableInfo(varStatement.getName());
                if (info != null) {
                    info.setVariableType(initType);
                    environment.markAsInitialized(varStatement.getName());
                }
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
            VariableType condType = visitExpression(ifStatement.getCondition());
            if (condType != VariableType.BOOLEAN && condType != VariableType.UNKNOWN) {
                errors.add("Condition in 'if' statement must evaluate to a boolean, but got " + condType);
            }
            
            visitStatement(ifStatement.getThenBranch());
            if (ifStatement.getElseBranch() != null) {
                visitStatement(ifStatement.getElseBranch());
            }

        } else if (statement instanceof WhileStatement whileStatement) {
            VariableType condType = visitExpression(whileStatement.getCondition());
            if (condType != VariableType.BOOLEAN && condType != VariableType.UNKNOWN) {
                errors.add("Condition in 'while' statement must evaluate to a boolean, but got " + condType);
            }
            
            visitStatement(whileStatement.getBody());

        } else {
            errors.add("Unsupported statement type: " + statement.getClass().getSimpleName());
        }
    }
    public VariableType visitExpression(Expression expression) {
        if (expression == null) return VariableType.UNKNOWN;

        if (expression instanceof NumberExpression) {
            return VariableType.NUMBER;
        } 
        
        else if (expression instanceof StringExpression) {
            return VariableType.STRING;
        } else if (expression instanceof BooleanExpression) {
            return VariableType.BOOLEAN;
        } 

        else if (expression instanceof VariableExpression v) {
            VariableInfo info = environment.getVariableInfo(v.getName());
            if (info == null) {
                errors.add("Variable '" + v.getName() + "' is not defined.");
                return VariableType.UNKNOWN;
            } 
            if (!info.isInitialized()) {
                errors.add("Variable '" + v.getName() + "' is used before initialization.");
            }
            environment.markAsUsed(v.getName());
            return info.getVariableType() != null ? info.getVariableType() : VariableType.UNKNOWN;

        } else if (expression instanceof AssignExpression a) {
            VariableType valueType = visitExpression(a.getValue());
            VariableInfo info = environment.getVariableInfo(a.getName());

            if (info == null) {
                errors.add("Variable '" + a.getName() + "' is not defined.");
                return VariableType.UNKNOWN;
            }

            if (info.getVariableType() == null) {
                info.setVariableType(valueType);
                environment.markAsInitialized(a.getName());
            } else {
                if (info.getVariableType() != valueType && valueType != VariableType.UNKNOWN) {
                    errors.add("Type mismatch: Cannot assign " + valueType + " to " + info.getVariableType());
                }
            }
            return valueType;

        } else if (expression instanceof BinaryExpression b) {
            VariableType left = visitExpression(b.getLeft());
            VariableType right = visitExpression(b.getRight());
            try {
                return TypeChecker.getBinaryOperationResultType(left, right, b.getOperator());
            } catch (SemanticException e) {
                errors.add(e.getMessage());
                return VariableType.UNKNOWN;
            }

        } else if (expression instanceof UnaryExpression u) {
            VariableType type = visitExpression(u.getRight());
            try {
                return TypeChecker.getUnaryOperationResultType(type, u.getOperator());
            } catch (SemanticException e) {
                errors.add(e.getMessage());
                return VariableType.UNKNOWN;
            }

        } else {
            errors.add("Unsupported expression type: " + expression.getClass().getSimpleName());
            return VariableType.UNKNOWN;
        }
    }

    public List<String> getErrors() { return errors; }
    public List<String> getWarnings() { return warnings; }
}