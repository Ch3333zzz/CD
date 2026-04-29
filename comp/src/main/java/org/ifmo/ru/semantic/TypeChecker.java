package org.ifmo.ru.semantic;

import org.ifmo.ru.utils.TokenType;

public class TypeChecker {

    public static VariableType getBinaryOperationResultType(VariableType left, VariableType right, TokenType operator) throws SemanticException {
        if (left == VariableType.UNKNOWN || right == VariableType.UNKNOWN) {
            return VariableType.UNKNOWN;
        }

        return switch (operator) {
            case PLUS -> {
                if (left == VariableType.STRING || right == VariableType.STRING) {
                    yield VariableType.STRING;
                }
                if (left == VariableType.NUMBER && right == VariableType.NUMBER) {
                    yield VariableType.NUMBER;
                }
                throw new SemanticException("Operator '+' cannot be applied to " + left + " and " + right);
            }
            case MINUS, STAR, SLASH -> {
                if (left == VariableType.NUMBER && right == VariableType.NUMBER) {
                    yield VariableType.NUMBER;
                }
                throw new SemanticException("Operator '" + operator + "' requires both operands to be NUMBER");
            }
            case EQEQ, NEQ -> {
                if (left != right) {
                    throw new SemanticException("Type mismatch: cannot compare " + left + " and " + right);
                }
                yield VariableType.BOOLEAN;
            }
            case LT, LTEQ, GT, GTEQ -> {
                if (left == VariableType.NUMBER && right == VariableType.NUMBER) {
                    yield VariableType.BOOLEAN;
                }
                throw new SemanticException("Comparison operators require NUMBER, but got " + left + " and " + right);
            }
            case AND, OR -> {
                if (left == VariableType.BOOLEAN && right == VariableType.BOOLEAN) {
                    yield VariableType.BOOLEAN;
                }
                throw new SemanticException("Logical operators require BOOLEAN operands");
            }
            default -> throw new SemanticException("Unknown binary operator: " + operator);
        };
    }

    public static VariableType getUnaryOperationResultType(VariableType type, TokenType operator) throws SemanticException {
        if (type == VariableType.UNKNOWN) {
            return VariableType.UNKNOWN;
        }

        return switch (operator) {
            case EXCL -> {
                if (type == VariableType.BOOLEAN) yield VariableType.BOOLEAN;
                throw new SemanticException("Operator '!' requires BOOLEAN");
            }
            case MINUS -> {
                if (type == VariableType.NUMBER) yield VariableType.NUMBER;
                throw new SemanticException("Unary '-' requires NUMBER");
            }
            default -> throw new SemanticException("Unknown unary operator: " + operator);
        };
    }
}