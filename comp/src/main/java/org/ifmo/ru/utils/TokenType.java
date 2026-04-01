package org.ifmo.ru.utils;


public enum TokenType{
    NUMBER,
    ID,
    STRING,
    VAR,
    TRUE,
    FALSE,

    PRINT,
    IF, ELSE,
    WHILE,

    PLUS, MINUS, STAR, SLASH,   // + - * /
    EQ, EQEQ, EXCL, NEQ,        // = == ! !=
    LT, GT, LTEQ, GTEQ,         // < > <= >=
    AND, OR,                    // && ||
    NOT,                        // !

    LPAREN, RPAREN, // ( )
    LBRACE, RBRACE, // { }

    SEMICOLON, // ;

    EOF
}
