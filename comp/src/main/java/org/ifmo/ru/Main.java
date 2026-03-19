package org.ifmo.ru;

import java.util.List;

import org.ifmo.ru.lexer.Lexer;
import org.ifmo.ru.parser.Parser;
import org.ifmo.ru.parser.AstPrinter;
import org.ifmo.ru.utils.Token;
import org.ifmo.ru.parser.ast.statements.Statement;
import org.ifmo.ru.semantic.SemanticAnalyzer;

public class Main {
    public static void main(String[] args) {
    try {
        String codeExample = "var x = 123; var y; print (x + y) * 2;";
        Lexer lexer = new Lexer(codeExample);
        List<Token> tokens = lexer.tokenize();

        Parser parser = new Parser(tokens);
        List<Statement> ast = parser.parse();

        System.out.println("Success parsed: " + ast.size() + " instructions");
        
        AstPrinter printer = new AstPrinter();
        printer.print(ast);

        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
        semanticAnalyzer.analyze(ast);

        boolean isCorrect = semanticAnalyzer.getErrors().isEmpty();
        if (!semanticAnalyzer.getErrors().isEmpty()) {
            System.out.println("semantic analysis errors:");
            for (String error : semanticAnalyzer.getErrors()) {
                System.out.println("- " + error);
            }
        }
        if (!semanticAnalyzer.getWarnings().isEmpty()) {
            System.out.println("semantic analysis warnings:");
            for (String error : semanticAnalyzer.getWarnings()) {
                System.out.println("- " + error);
            }
        }
        if (isCorrect)
            System.out.println("The semantic analysis was successful, no errors were found.");

    } catch (Exception e) {
        System.err.println("Errors: " + e.getMessage());
        e.printStackTrace();
    }
}
}