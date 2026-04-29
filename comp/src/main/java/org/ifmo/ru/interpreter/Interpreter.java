package org.ifmo.ru.interpreter;

import org.ifmo.ru.parser.ast.expressions.*;
import org.ifmo.ru.parser.ast.statements.*;

import java.util.List;

public class Interpreter {

    private RuntimeEnvironment environment = new RuntimeEnvironment();

    public void interpret(List<Statement> statements) {
        try {
            for (Statement statement : statements) {
                execute(statement);
            }
        } catch (RuntimeException e) {
            System.err.println("Runtime Error: " + e.getMessage());
        }
    }

    private void execute(Statement stmt) {
        if (stmt instanceof VarStatement v) {
            Object value = null;
            if (v.getInitializer() != null) {
                value = evaluate(v.getInitializer());
            }
            environment.define(v.getName(), value);

        } else if (stmt instanceof PrintStatement p) {
            Object value = evaluate(p.getExpression());
            if (value instanceof Double d && d % 1 == 0) {
                System.out.println(d.intValue());
            } else {
                System.out.println(value);
            }

        } else if (stmt instanceof ExpressionStatement e) {
            evaluate(e.getExpression());

        } else if (stmt instanceof BlockStatement b) {
            executeBlock(b.getStatements(), new RuntimeEnvironment(environment));

        } else if (stmt instanceof IfStatement i) {
            Object conditionResult = evaluate(i.getCondition());
            if ((boolean) conditionResult) {
                execute(i.getThenBranch());
            } else if (i.getElseBranch() != null) {
                execute(i.getElseBranch());
            }

        } else if (stmt instanceof WhileStatement w) {
            while ((boolean) evaluate(w.getCondition())) {
                execute(w.getBody());
            }
        }
    }

    private void executeBlock(List<Statement> statements, RuntimeEnvironment localEnvironment) {
        RuntimeEnvironment previous = this.environment;
        try {
            this.environment = localEnvironment;
            for (Statement statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    private Object evaluate(Expression expr) {
        if (expr instanceof NumberExpression n) {
            return n.getValue();
        }
        if (expr instanceof StringExpression s) {
            return s.getValue();
        }
        if (expr instanceof BooleanExpression b) {
            return b.isValue();
        }
        if (expr instanceof VariableExpression v) {
            return environment.get(v.getName());
        }

        if (expr instanceof AssignExpression a) {
            Object value = evaluate(a.getValue());
            environment.assign(a.getName(), value);
            return value;
        }

        if (expr instanceof UnaryExpression u) {
            Object right = evaluate(u.getRight());
            
            return switch (u.getOperator()) {
                case MINUS -> -(double) right;
                case EXCL -> !(boolean) right;
                default -> null;
            };
        }

        if (expr instanceof BinaryExpression b) {
            Object left = evaluate(b.getLeft());
            Object right = evaluate(b.getRight());

            return switch (b.getOperator()) {
                case PLUS -> {
                    if (left instanceof String || right instanceof String) {
                        yield String.valueOf(left) + String.valueOf(right);
                    }
                    yield (double) left + (double) right;
                }
                case MINUS -> (double) left - (double) right;
                case STAR -> (double) left * (double) right;
                case SLASH -> {
                    if ((double) right == 0) throw new RuntimeException("Division by zero.");
                    yield (double) left / (double) right;
                }
                
                case GT -> (double) left > (double) right;
                case GTEQ -> (double) left >= (double) right;
                case LT -> (double) left < (double) right;
                case LTEQ -> (double) left <= (double) right;
                
                case EQEQ -> isEqual(left, right);
                case NEQ -> !isEqual(left, right);
                
                case AND -> (boolean) left && (boolean) right;
                case OR -> (boolean) left || (boolean) right;
                
                default -> throw new RuntimeException("Unknown binary operator.");
            };
        }

        return null;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
        return a.equals(b);
    }
}