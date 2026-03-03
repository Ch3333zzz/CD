package org.ifmo.ru;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String codeExample = "var x = 123; print x + 5;";
        Lexer lexer = new Lexer(codeExample);
        try {
            List<Token> tokens = lexer.tokenize();
            tokens.forEach((x) -> System.out.println(x));
        } catch (UnknownOperatorException e) {
            System.err.println(e);
        }
    }
}