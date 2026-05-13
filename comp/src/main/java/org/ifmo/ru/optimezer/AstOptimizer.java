package org.ifmo.ru.optimezer;

import org.ifmo.ru.parser.ast.expressions.*;
import org.ifmo.ru.parser.ast.statements.*;
import org.ifmo.ru.utils.TokenType;

import java.util.List;
import java.util.stream.Collectors;

public class AstOptimizer {

    public List<Statement> optimize(List<Statement> statements) {
        return statements.stream()
                .map(this::optimizeStatement)
                .collect(Collectors.toList());
    }

    private Statement optimizeStatement(Statement stmt) {
        if (stmt instanceof VarStatement v) {
            return new VarStatement(v.getName(), optimizeExpression(v.getInitializer()));
        } else if (stmt instanceof IfStatement i) {
            return new IfStatement(
                    optimizeExpression(i.getCondition()),
                    optimizeStatement(i.getThenBranch()),
                    i.getElseBranch() != null ? optimizeStatement(i.getElseBranch()) : null);
        } else if (stmt instanceof WhileStatement w) {
            return new WhileStatement(
                    optimizeExpression(w.getCondition()),
                    optimizeStatement(w.getBody()));
        } else if (stmt instanceof BlockStatement b) {
            return new BlockStatement(optimize(b.getStatements()));
        } else if (stmt instanceof PrintStatement p) {
            return new PrintStatement(optimizeExpression(p.getExpression()));
        } else if (stmt instanceof ExpressionStatement e) {
            return new ExpressionStatement(optimizeExpression(e.getExpression()));
        } else if (stmt instanceof FunctionStatement f) {
            return new FunctionStatement(
                    f.getName(),
                    f.getParameters(),
                    (BlockStatement) optimizeStatement(f.getBody()));
        } else if (stmt instanceof ReturnStatement r) {
            return new ReturnStatement(optimizeExpression(r.getValue()));
        }
        return stmt;
    }

    private Expression optimizeExpression(Expression expr) {
        if (expr == null)
            return null;

        if (expr instanceof BinaryExpression b) {
            Expression left = optimizeExpression(b.getLeft());
            Expression right = optimizeExpression(b.getRight());

            if (left instanceof NumberExpression lNum && right instanceof NumberExpression rNum) {
                double lVal = lNum.getValue();
                double rVal = rNum.getValue();

                return switch (b.getOperator()) {
                    case PLUS -> new NumberExpression(lVal + rVal);
                    case MINUS -> new NumberExpression(lVal - rVal);
                    case STAR -> new NumberExpression(lVal * rVal);
                    case SLASH -> rVal != 0 ? new NumberExpression(lVal / rVal)
                            : new BinaryExpression(left, b.getOperator(), right);
                    default -> new BinaryExpression(left, b.getOperator(), right);
                };
            }

            if (left instanceof StringExpression lStr && right instanceof StringExpression rStr) {
                if (b.getOperator() == TokenType.PLUS) {
                    return new StringExpression(lStr.getValue() + rStr.getValue());
                }
            }

            return new BinaryExpression(left, b.getOperator(), right);
        }

        if (expr instanceof UnaryExpression u) {
            Expression right = optimizeExpression(u.getRight());
            if (right instanceof NumberExpression n && u.getOperator() == TokenType.MINUS) {
                return new NumberExpression(-n.getValue());
            }
            return new UnaryExpression(u.getOperator(), right);
        }

        if (expr instanceof AssignExpression a) {
            return new AssignExpression(a.getName(), optimizeExpression(a.getValue()));
        }

        if (expr instanceof CallExpression c) {
            List<Expression> optimizedArgs = c.getArguments().stream()
                    .map(this::optimizeExpression)
                    .collect(Collectors.toList());
            return new CallExpression(c.getCallee(), optimizedArgs);
        }

        return expr;
    }
}