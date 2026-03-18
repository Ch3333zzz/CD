package org.ifmo.ru.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Token {
    private TokenType tokenType;
    private String value;
    private int position;
    private int line;
    private int column;
    @Override
    public String toString() {
        return "[%d:%d] Token(%s, '%s')".formatted(line, column, tokenType, value);
    }
}
