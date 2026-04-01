package org.ifmo.ru.lexer;

import org.ifmo.ru.utils.Token;
import org.ifmo.ru.utils.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Lexer {
    private final String input;
    private int position;
    private int line = 1;
    private int column = 1;

    private static final Map<String, TokenType> KEYWORDS = Map.of(
        "var", TokenType.VAR,
        "print", TokenType.PRINT,
        "if", TokenType.IF,
        "else", TokenType.ELSE,
        "while", TokenType.WHILE,
        "true", TokenType.TRUE,
        "false", TokenType.FALSE
    );

    private static final Map<String, TokenType> OPERATORS = Map.ofEntries(
            Map.entry("==", TokenType.EQEQ),
            Map.entry("!=", TokenType.NEQ),
            Map.entry("<=", TokenType.LTEQ),
            Map.entry(">=", TokenType.GTEQ),
            Map.entry("&&", TokenType.AND),
            Map.entry("||", TokenType.OR),
            Map.entry("+", TokenType.PLUS),
            Map.entry("-", TokenType.MINUS),
            Map.entry("*", TokenType.STAR),
            Map.entry("/", TokenType.SLASH),
            Map.entry("=", TokenType.EQ),
            Map.entry("<", TokenType.LT),
            Map.entry(">", TokenType.GT),
            Map.entry("!", TokenType.EXCL),
            Map.entry("(", TokenType.LPAREN),
            Map.entry(")", TokenType.RPAREN),
            Map.entry("{", TokenType.LBRACE),
            Map.entry("}", TokenType.RBRACE),
            Map.entry(";", TokenType.SEMICOLON)
    );

    public Lexer(String input) {
        this.input = input != null ? input : "";
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (position < input.length()) {
            char current = peek();

            if (Character.isWhitespace(current)) {
                next();
                continue;
            }

            if (Character.isDigit(current)) {
                tokens.add(readNumber());
                continue;
            }

            if (Character.isLetter(current)) {
                tokens.add(readWord());
                continue;
            }

            if (current == '"') {
                tokens.add(readString());
                continue;
            }

            tokens.add(readOperatorOrPunctuation());
        }

        tokens.add(new Token(TokenType.EOF, "\0", position, line, column));
        return tokens;
    }

    private Token readNumber() {
        int startPos = position;
        int startLine = line;
        int startCol = column;

        while (Character.isDigit(peek())) {
            next();
        }

        String text = input.substring(startPos, position);
        return new Token(TokenType.NUMBER, text, startPos, startLine, startCol);
    }

    private Token readWord() {
        int startPos = position;
        int startLine = line;
        int startCol = column;

        while (Character.isLetterOrDigit(peek())) {
            next();
        }

        String text = input.substring(startPos, position);
        
        TokenType type = KEYWORDS.getOrDefault(text, TokenType.ID);

        return new Token(type, text, startPos, startLine, startCol);
    }

    private Token readOperatorOrPunctuation() {
        int startPos = position;
        int startLine = line;
        int startCol = column;

        if (position + 1 < input.length()) {
            String twoChars = input.substring(position, position + 2);

            TokenType opType = OPERATORS.get(twoChars);
            if (opType != null) {
                next();
                next();
                return new Token(opType, twoChars, startPos, startLine, startCol);
            }
        }

        String oneChar = String.valueOf(input.charAt(position));
        TokenType type = OPERATORS.get(oneChar);
        if (type != null) {
            next();
            return new Token(type, oneChar, startPos, startLine, startCol);
        }

        char badChar = peek();
        
        throw new RuntimeException("[Lexer Error] Unexpected character '%s' at Line %d, Column %d"
                .formatted(badChar, startLine, startCol));
    }

    private char peek() {
        return position >= input.length() ? '\0' : input.charAt(position);
    }

    private char next() {
        if (position >= input.length()) return '\0';

        char current = input.charAt(position++);

        if (current == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }

        return current;
    }

    private Token readString() {
        int startPos = position;
        int startLine = line;
        int startCol = column;

        next();

        StringBuilder sb = new StringBuilder();
        while (peek() != '"' && peek() != '\0') {
            sb.append(next());
        }

        if (peek() == '\0') {
            throw new RuntimeException("[Lexer Error] Unterminated string starting at Line %d, Column %d"
                    .formatted(startLine, startCol));
        }

        next();

        return new Token(TokenType.STRING, sb.toString(), startPos, startLine, startCol);
    }
}