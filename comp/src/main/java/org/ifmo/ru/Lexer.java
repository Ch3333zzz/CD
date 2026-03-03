package org.ifmo.ru;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String input;
    private final int length;

    private int position;

    public Lexer(String input) {
        this.input = input;
        this.length = input.length();
        this.position = 0;
    }

    public List<Token> tokenize() throws UnknownOperatorException{
        List<Token> result = new ArrayList<>();

        while(position < length) {
            char current = peek(input);

            if (Character.isWhitespace(current))
                next();
            else if (Character.isDigit(current))
                tokenizeNumber(result);
            else if (Character.isLetter(current))
                tokenizeWord(result);
            else
                tokenizeOperator(result);
        }

        return result;
    }

    private void tokenizeNumber(List<Token> result) {
        int start = position;

        while (Character.isDigit(peek(input)))
            next();

        String numberStr = input.substring(start, position);

        addToken(result, TokenType.NUMBER, numberStr, start);
    }
    private void tokenizeWord(List<Token> result) {
        int start = position;
        
        while (Character.isLetterOrDigit(peek(input))) 
            next();

        String word = input.substring(start, position);

        switch (word) {
            case "var" -> addToken(result, TokenType.VAR, word, start);
            case "print" -> addToken(result, TokenType.PRINT, word, start);
            case "if" -> addToken(result, TokenType.IF, word, start);
            case "else" -> addToken(result, TokenType.ELSE, word, start);
            case "while" -> addToken(result, TokenType.WHILE, word, start);
            default -> addToken(result, TokenType.ID, word, start);
        }

    }

    private void tokenizeOperator(List<Token> result) throws UnknownOperatorException{
        int current = peek(input);
        int start = position;

        switch (current) {
            case '+' -> {
                next();
                addToken(result, TokenType.PLUS, "+", start);
            }
            case '-' -> {
                next();
                addToken(result, TokenType.MINUS, "-", start);
            }
            case '*' -> {
                next();
                addToken(result, TokenType.STAR, "*", start);
            }
            case '/' -> {
                next();
                addToken(result, TokenType.SLASH, "/", start);
            }
            case '=' -> {
                next();
                addToken(result, TokenType.EQ, "=", start);
            }
            case ';' -> {
                next();
                addToken(result, TokenType.SEMICOLON, ";", start);
            }
            default -> throw new UnknownOperatorException();
        }
    }
    // Checking current simbol, without changing position
    private char peek(String input) {
        if (position >= length) {
            return '\0';
        }
        return input.charAt(position);
    }

    // Checking current simbol and increasing position
    private char next() {
        if (position >= length)
            return '\0';

        return input.charAt(position++);
    }

    private void addToken(List<Token> result, TokenType type, String value, int start) {
        result.add(new Token(type, value, start));
    }
}