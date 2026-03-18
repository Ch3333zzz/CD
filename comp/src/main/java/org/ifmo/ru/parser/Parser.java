package org.ifmo.ru.parser;

import java.util.ArrayList;
import java.util.List;

import org.ifmo.ru.parser.ast.statements.BlockStatement;
import org.ifmo.ru.parser.ast.statements.ExpressionStatement;
import org.ifmo.ru.parser.ast.statements.IfStatement;
import org.ifmo.ru.parser.ast.statements.PrintStatement;
import org.ifmo.ru.parser.ast.statements.Statement;
import org.ifmo.ru.parser.ast.statements.VarStatement;
import org.ifmo.ru.parser.ast.statements.WhileStatement;
import org.ifmo.ru.utils.Token;
import org.ifmo.ru.utils.TokenType;
import org.ifmo.ru.parser.ast.expressions.*;

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
        if(match(TokenType.VAR)) return parseVarDeclaration();

        return parseStatement();
    }

    private Statement parseStatement() throws Exception {
        if(match(TokenType.IF)) return parseIfStatement();
        if(match(TokenType.WHILE)) return parseWhileStatement();
        if(match(TokenType.PRINT)) return parsePrintStatement();
        if(match(TokenType.LBRACE)) return new BlockStatement((parseBlock()));

        return parseExpressionStatement();
    }


    private Statement parseVarDeclaration() throws Exception {
        Token name = consume(TokenType.ID, "Expecting variable name");
        Expression initializer = null;

        if(match(TokenType.EQ))
            initializer = parseExpression();

        consume(TokenType.SEMICOLON, "Expecting \';\' after variable declaration");
        return new VarStatement(name.getValue(), initializer);
    }

    private Statement parseIfStatement() throws Exception {
        consume(TokenType.LPAREN, "Expecting \'(\' after 'if'");
        Expression condition = parseExpression();
        consume(TokenType.RPAREN, "Expecting \')\' after 'if'");

        Statement thenBranch = parseStatement();
        Statement elseBranch = null;

        if(match(TokenType.ELSE))
            elseBranch = parseStatement();

        return new IfStatement(condition, thenBranch, elseBranch);
    }

    private Statement parseWhileStatement() throws Exception {
        consume(TokenType.LPAREN, "Expecting \'(\' after 'while'");
        Expression condition = parseExpression();
        consume(TokenType.RPAREN, "Expecting \')\' after 'while'");

        Statement body = parseStatement();

        return new WhileStatement(condition, body);
    }

    private Statement parsePrintStatement() throws Exception {
        Expression value = parseExpression();
        consume(TokenType.SEMICOLON, "Expecting \';\' after value");
        return new PrintStatement(value);
    }

    private Statement parseExpressionStatement() throws Exception {
        Expression expression = parseExpression();
        consume(TokenType.SEMICOLON, "Expecting \';\' after expression");

        return new ExpressionStatement(expression);
    }

    private List<Statement> parseBlock() throws Exception {
        List<Statement> statements = new ArrayList<>();

        while(!check(TokenType.RBRACE) && !isAtEnd()) {
            statements.add(parseDeclaration());
        }

        consume(TokenType.RBRACE, "Expecting \'}\' after block");
        return statements;
    }

    private Expression parseExpression() throws Exception {
        return parseAssignment();
    }

    private Expression parseAssignment() throws Exception {
        Expression expression = parseLogicalOr();

        if(match(TokenType.EQ)) {
            Token equals = previouse();
            Expression value = parseAssignment();

            if(expression instanceof VariableExpression varExpr) {
                return new AssignExpression(varExpr.getName(), value);
            }

            throw new Exception("[Parse Error] Line %d: Incorrect target of assignment".formatted(equals.getLine()));
        }

        return expression;
    }

    private Expression parseLogicalOr() throws Exception {
        Expression expression = parseLogicalAnd();

        while(match(TokenType.OR)) {
            TokenType op = previouse().getTokenType();
            Expression right = parseLogicalAnd();
            expression = new BinaryExpression(expression, op, right);
        }

        return expression;
    }

    private Expression parseLogicalAnd() throws Exception {
        Expression expression = parseEquality();

        while(match(TokenType.AND)) {
            TokenType op = previouse().getTokenType();
            Expression right = parseEquality();
            expression = new BinaryExpression(expression, op, right);
        }

        return expression;

    }

    private Expression parseEquality() throws Exception {
        Expression expression = parseComparison();

        while(match(TokenType.EQEQ, TokenType.NEQ)) {
            TokenType op = previouse().getTokenType();
            Expression right = parseComparison();
            expression = new BinaryExpression(expression, op, right);
        }

        return expression;
    }

    private Expression parseComparison() throws Exception {
        Expression expression = parseTerm();

        while(match(TokenType.LT, TokenType.LTEQ, TokenType.GT, TokenType.GTEQ)) {
            TokenType op = previouse().getTokenType();
            Expression right = parseTerm();
            expression = new BinaryExpression(expression, op, right);
        }

        return expression;   
    }

    private Expression parseTerm() throws Exception {
        Expression expression = parseFactor();

        while(match(TokenType.PLUS, TokenType.MINUS)) {
            TokenType op = previouse().getTokenType();
            Expression right = parseFactor();
            expression = new BinaryExpression(expression, op, right);
        }

        return expression;   
    }

    private Expression parseFactor() throws Exception {
        Expression expression = parseUnary();

        while(match(TokenType.STAR, TokenType.SLASH)) {
            TokenType op = previouse().getTokenType();
            Expression right = parseUnary();
            expression = new BinaryExpression(expression, op, right);
        }

        return expression;   
    }

    private Expression parseUnary() throws Exception {

        while(match(TokenType.EXCL, TokenType.MINUS)) {
            TokenType op = previouse().getTokenType();
            Expression right = parseUnary();
            return new UnaryExpression(op, right);
        }

        return parsePrimary();
    }

    private Expression parsePrimary() throws Exception{
        if(match(TokenType.NUMBER)) {
            double value = Double.parseDouble(previouse().getValue());

            return new NumberExpression(value);
        }

        if(match(TokenType.ID)) {
            return new VariableExpression(previouse().getValue());
        }

        if(match(TokenType.LPAREN)) {
            Expression expression = parseExpression();
            consume(TokenType.RPAREN, "Expecting \')\' after expression");
            return expression;
        }

        throw new Exception("[Parser Error] Line %d, Col %d: Expecting expression".formatted(peek().getLine(), peek().getColumn()));
    }




    private boolean match(TokenType... types) {
        for(TokenType type : types) {
            if(check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getTokenType() == type;
    }

    private Token advance() {
        if(!isAtEnd()) _position++;
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
        if(check(type)) return advance();
        Token token = peek();
        throw new Exception("[Parser Error] Line %d, Col %d: %s\n".formatted(token.getLine(), token.getColumn(), message));
    }
}
