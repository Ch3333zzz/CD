package org.ifmo.ru.semantic;

import org.ifmo.ru.utils.TokenType;

public class TypeChecker {

    public static VariableType getBinaryOperationResultType(VariableType left, VariableType right, TokenType operator) throws SemanticException {
        if (left == VariableType.UNKNOWN || right == VariableType.UNKNOWN) {
            return VariableType.UNKNOWN;
        }

        if (left != right) {
            throw new SemanticException("Type mismatch: cannot compare " + left + " and " + right);
        }

        VariableType result = switch (operator) {
            case PLUS -> {
                if (left == VariableType.STRING || left == VariableType.NUMBER) yield left;
                yield null;
            }
            case MINUS, STAR, SLASH -> {
                if (left == VariableType.NUMBER) yield VariableType.NUMBER;
                yield null;
            }
            case EQ, NEQ -> {
                yield VariableType.BOOLEAN;
            }
            case LT, LTEQ, GT, GTEQ -> {
                if (left == VariableType.NUMBER) yield VariableType.BOOLEAN;
                if (left == VariableType.BOOLEAN) yield VariableType.BOOLEAN;
                yield null;
            }
            case AND, OR -> {
                if (left == VariableType.BOOLEAN) yield VariableType.BOOLEAN;
                yield null;
            }
            default -> null;
        };

        if (result == null) {
            throw new SemanticException("Operator '" + operator + "' cannot be applied to type " + left);
        }

        return result;
    }

    public static VariableType getUnaryOperationResultType(VariableType type, TokenType operator) throws SemanticException {
        if (type == VariableType.UNKNOWN) {
            return VariableType.UNKNOWN;
        }

        VariableType result = switch (operator) {
            case NOT -> {
                if (type == VariableType.BOOLEAN) yield VariableType.BOOLEAN;
                yield null;
            }
            case MINUS -> {
                if (type == VariableType.NUMBER) yield VariableType.NUMBER;
                yield null;
            }
            default -> null;
        };

        if (result == null) {
            throw new SemanticException("Unary operator '" + operator + "' cannot be applied to type " + type);
        }

        return result;
    }
}