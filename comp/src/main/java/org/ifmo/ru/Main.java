package org.ifmo.ru;

import java.util.List;

import org.ifmo.ru.lexer.Lexer;
import org.ifmo.ru.parser.Parser;
import org.ifmo.ru.parser.AstPrinter;
import org.ifmo.ru.utils.Token;
import org.ifmo.ru.parser.ast.statements.Statement;

public class Main {
    public static void main(String[] args) {
    try {
        String codeExample = "var x = 123; print (x + 5) * 2;";
        Lexer lexer = new Lexer(codeExample);
        List<Token> tokens = lexer.tokenize();

        Parser parser = new Parser(tokens);
        List<Statement> ast = parser.parse();

        System.out.println("Success parsed: " + ast.size() + " instructions");
        
        AstPrinter printer = new AstPrinter();
        printer.print(ast);
        
    } catch (Exception e) {
        System.err.println("Errors: " + e.getMessage());
        e.printStackTrace();
    }
}
}