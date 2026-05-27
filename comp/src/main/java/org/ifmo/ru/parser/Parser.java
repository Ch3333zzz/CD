package org.ifmo.ru.parser;

import java.util.ArrayList;
import java.util.List;

import org.ifmo.ru.parser.ast.statements.*;
import org.ifmo.ru.parser.ast.expressions.*;
import org.ifmo.ru.utils.Token;
import org.ifmo.ru.utils.TokenType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Parser {
    private final List<Token> _tokens;
    private int _position;

    public List<Statement> parse() throws Exception {
        List<Statement> statements = new ArrayList<>();

        while (!isAtEnd()) {
            statements.add(parseDeclaration());
        }
        return statements;
    }

    private Statement parseDeclaration() throws Exception {
        if (match(TokenType.DEF))
            return parseFunctionDeclaration();
        if (match(TokenType.VAR))
            return parseVarDeclaration();

        return parseStatement();
    }

    private Statement parseFunctionDeclaration() throws Exception {
        Token name = consume(TokenType.ID, "Expect function name.");

        consume(TokenType.LPAREN, "Expect '(' after function name.");
        List<String> parameters = new ArrayList<>();
        if (!check(TokenType.RPAREN)) {
            do {
                parameters.add(consume(TokenType.ID, "Expect parameter name.").getValue());
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RPAREN, "Expect ')' after parameters.");

        consume(TokenType.LBRACE, "Expect '{' before function body.");
        BlockStatement body = new BlockStatement(parseBlock());

        return new FunctionStatement(name.getValue(), parameters, body);
    }

    private Statement parseStatement() throws Exception {
        if (match(TokenType.IF))
            return parseIfStatement();
        if (match(TokenType.WHILE))
            return parseWhileStatement();
        if (match(TokenType.PRINT))
            return parsePrintStatement();
        if (match(TokenType.RETURN))
            return parseReturnStatement();
        if (match(TokenType.LBRACE))
            return new BlockStatement((parseBlock()));

        return parseExpressionStatement();
    }

    private Statement parseReturnStatement() throws Exception {
        Expression value = null;
        if (!check(TokenType.SEMICOLON)) {
            value = parseExpression();
        }
        consume(TokenType.SEMICOLON, "Expect ';' after return value.");
        return new ReturnStatement(value);
    }

    private Statement parseVarDeclaration() throws Exception {
        Token name = consume(TokenType.ID, "Expecting variable name");
        Expression initializer = null;

        if (match(TokenType.EQ))
            initializer = parseExpression();

        consume(TokenType.SEMICOLON, "Expecting ';' after variable declaration");
        return new VarStatement(name.getValue(), initializer);
    }

    private Statement parseIfStatement() throws Exception {
        consume(TokenType.LPAREN, "Expecting '(' after 'if'");
        Expression condition = parseExpression();
        consume(TokenType.RPAREN, "Expecting ')' after 'if'");

        Statement thenBranch = parseStatement();
        Statement elseBranch = null;

        if (match(TokenType.ELSE))
            elseBranch = parseStatement();

        return new IfStatement(condition, thenBranch, elseBranch);
    }

    private Statement parseWhileStatement() throws Exception {
        consume(TokenType.LPAREN, "Expecting '(' after 'while'");
        Expression condition = parseExpression();
        consume(TokenType.RPAREN, "Expecting ')' after 'while'");

        Statement body = parseStatement();

        return new WhileStatement(condition, body);
    }

    private Statement parsePrintStatement() throws Exception {
        Expression value = parseExpression();
        consume(TokenType.SEMICOLON, "Expecting ';' after value");
        return new PrintStatement(value);
    }

    private Statement parseExpressionStatement() throws Exception {
        Expression expression = parseExpression();
        consume(TokenType.SEMICOLON, "Expecting ';' after expression");
        return new ExpressionStatement(expression);
    }

    private List<Statement> parseBlock() throws Exception {
        List<Statement> statements = new ArrayList<>();

        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            statements.add(parseDeclaration());
        }

        consume(TokenType.RBRACE, "Expecting '}' after block");
        return statements;
    }

    private Expression parseExpression() throws Exception {
        return parseAssignment();
    }

    private Expression parseAssignment() throws Exception {
        Expression expression = parseLogicalOr();

        if (match(TokenType.EQ)) {
            Token equals = previouse();
            Expression value = parseAssignment();

            if (expression instanceof VariableExpression varExpr) {
                return new AssignExpression(varExpr.getName(), value);
            }

            if (expression instanceof IndexExpression indexExpr) {
                return new ArrayAssignExpression(indexExpr.getArray(), indexExpr.getIndex(), value);
            }

            throw new Exception("[Parse Error] Line %d: Incorrect target of assignment".formatted(equals.getLine()));
        }

        return expression;
    }

    private Expression parseLogicalOr() throws Exception {
        Expression expression = parseLogicalAnd();

        while (match(TokenType.OR)) {
            TokenType op = previouse().getTokenType();
            Expression right = parseLogicalAnd();
            expression = new BinaryExpression(expression, op, right);
        }

        return expression;
    }

    private Expression parseLogicalAnd() throws Exception {
        Expression expression = parseEquality();

        while (match(TokenType.AND)) {
            TokenType op = previouse().getTokenType();
            Expression right = parseEquality();
            expression = new BinaryExpression(expression, op, right);
        }

        return expression;
    }

    private Expression parseEquality() throws Exception {
        Expression expression = parseComparison();

        while (match(TokenType.EQEQ, TokenType.NEQ)) {
            TokenType op = previouse().getTokenType();
            Expression right = parseComparison();
            expression = new BinaryExpression(expression, op, right);
        }

        return expression;
    }

    private Expression parseComparison() throws Exception {
        Expression expression = parseTerm();

        while (match(TokenType.LT, TokenType.LTEQ, TokenType.GT, TokenType.GTEQ)) {
            TokenType op = previouse().getTokenType();
            Expression right = parseTerm();
            expression = new BinaryExpression(expression, op, right);
        }

        return expression;
    }

    private Expression parseTerm() throws Exception {
        Expression expression = parseFactor();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            TokenType op = previouse().getTokenType();
            Expression right = parseFactor();
            expression = new BinaryExpression(expression, op, right);
        }

        return expression;
    }

    private Expression parseFactor() throws Exception {
        Expression expression = parseUnary();

        while (match(TokenType.STAR, TokenType.SLASH)) {
            TokenType op = previouse().getTokenType();
            Expression right = parseUnary();
            expression = new BinaryExpression(expression, op, right);
        }

        return expression;
    }

    private Expression parseUnary() throws Exception {
        if (match(TokenType.EXCL, TokenType.MINUS)) {
            TokenType op = previouse().getTokenType();
            Expression right = parseUnary();
            return new UnaryExpression(op, right);
        }

        return parseCall();
    }

    private Expression parseCall() throws Exception {
        Expression expression = parsePrimary();

        while (true) {
            if (match(TokenType.LPAREN)) {
                expression = finishCall(expression);
            } else if (match(TokenType.LBRACK)) {
                Expression index = parseExpression();
                consume(TokenType.RBRACK, "Expect ']' after array index.");
                expression = new IndexExpression(expression, index);
            } else {
                break;
            }
        }

        return expression;
    }

    private Expression finishCall(Expression callee) throws Exception {
        List<Expression> arguments = new ArrayList<>();
        if (!check(TokenType.RPAREN)) {
            do {
                arguments.add(parseExpression());
            } while (match(TokenType.COMMA));
        }

        consume(TokenType.RPAREN, "Expect ')' after arguments.");
        return new CallExpression(callee, arguments);
    }

    private Expression parsePrimary() throws Exception {
        if (match(TokenType.NUMBER)) {
            double value = Double.parseDouble(previouse().getValue());
            return new NumberExpression(value);
        }
        if (match(TokenType.STRING)) {
            return new StringExpression(previouse().getValue());
        }

        if (match(TokenType.TRUE)) {
            return new BooleanExpression(true);
        }
        if (match(TokenType.FALSE)) {
            return new BooleanExpression(false);
        }

        if (match(TokenType.ID)) {
            return new VariableExpression(previouse().getValue());
        }

        if (match(TokenType.LPAREN)) {
            Expression expression = parseExpression();
            consume(TokenType.RPAREN, "Expecting ')' after expression");
            return expression;
        }

        if (match(TokenType.LBRACK)) {
            List<Expression> elements = new ArrayList<>();
            if (!check(TokenType.RBRACK)) {
                do {
                    elements.add(parseExpression());
                } while (match(TokenType.COMMA));
            }
            consume(TokenType.RBRACK, "Expect ']' after array elements.");
            return new ArrayExpression(elements);
        }

        throw new Exception(
                "[Parser Error] Line %d, Col %d: Expecting expression. Got: %s"
                        .formatted(peek().getLine(), peek().getColumn(), peek().getTokenType()));
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd())
            return false;
        return peek().getTokenType() == type;
    }

    private Token advance() {
        if (!isAtEnd())
            _position++;
        return previouse();
    }

    private boolean isAtEnd() {
        return peek().getTokenType() == TokenType.EOF;
    }

    private Token peek() {
        return _tokens.get(_position);
    }

    private Token previouse() {
        return _tokens.get(_position - 1);
    }

    private Token consume(TokenType type, String message) throws Exception {
        if (check(type))
            return advance();
        Token token = peek();
        throw new Exception(
                "[Parser Error] Line %d, Col %d: %s\n".formatted(token.getLine(), token.getColumn(), message));
    }
}