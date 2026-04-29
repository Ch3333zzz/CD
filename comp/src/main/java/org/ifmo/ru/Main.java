package org.ifmo.ru;

import java.util.List;
import org.ifmo.ru.lexer.Lexer;
import org.ifmo.ru.parser.Parser;
import org.ifmo.ru.parser.AstPrinter;
import org.ifmo.ru.utils.Token;
import org.ifmo.ru.parser.ast.statements.Statement;
import org.ifmo.ru.semantic.SemanticAnalyzer;
import org.ifmo.ru.interpreter.Interpreter;

public class Main {
    public static void main(String[] args) {
        try {
            String codeExample = """
                    var str1 = "Hello, ";
                    var str2 = "World!\n";
                    print str1 + str2;

                    var count = 0;
                    while (count < 3) {
                        print "Iteration: " + count;
                        count = count + 1;
                    }

                    if (count == 3) {
                        print "Loop finished successfully!";
                    }
                    """;

            Lexer lexer = new Lexer(codeExample);
            List<Token> tokens = lexer.tokenize();

            Parser parser = new Parser(tokens);
            List<Statement> ast = parser.parse();

            System.out.println("--- AST Tree ---");
            AstPrinter printer = new AstPrinter();
            printer.print(ast);
            System.out.println("----------------\n");

            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
            semanticAnalyzer.analyze(ast);

            if (!semanticAnalyzer.getErrors().isEmpty()) {
                System.out.println("Semantic analysis errors found:");
                for (String error : semanticAnalyzer.getErrors()) {
                    System.out.println(error);
                }
                return;
            }

            if (!semanticAnalyzer.getWarnings().isEmpty()) {
                System.out.println("Semantic analysis warnings:");
                for (String warning : semanticAnalyzer.getWarnings()) {
                    System.out.println(warning);
                }
            }

            System.out.println("Semantic analysis successful. Starting execution...\n");

            System.out.println("--- Program Output ---");
            Interpreter interpreter = new Interpreter();
            interpreter.interpret(ast);
            System.out.println("----------------------");

        } catch (Exception e) {
            System.err.println("Fatal Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}