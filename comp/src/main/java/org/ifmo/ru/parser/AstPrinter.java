package org.ifmo.ru.parser;

import org.ifmo.ru.parser.ast.statements.*;
import org.ifmo.ru.parser.ast.expressions.*;

import java.util.List;

public class AstPrinter {
    public void print(List<Statement> statements) {
        System.out.println("Root (Program)");
        
        for (int i = 0; i < statements.size(); i++) {
            printNode(statements.get(i), "", i == statements.size() - 1);
        }
    }

    private void printNode(Object node, String indent, boolean isLast) {
        if (node == null) return;
        
        String marker = isLast ? "└── " : "├── ";
        System.out.print(indent + marker);
        
        String childIndent = indent + (isLast ? "    " : "│   ");

        if (node instanceof VarStatement v) {
            System.out.println("VarStatement: %s".formatted(v.getName()));
            if (v.getInitializer() != null) {
                printNode(v.getInitializer(), childIndent, true);
            }
        } else if (node instanceof PrintStatement p) {
            System.out.println("PrintStatement");
            printNode(p.getExpression(), childIndent, true);
        } else if (node instanceof IfStatement i) {
            System.out.println("IfStatement");
            printNode(i.getCondition(), childIndent, false);
            printNode(i.getThenBranch(), childIndent, i.getElseBranch() == null);
            
            if (i.getElseBranch() != null) {
                printNode(i.getElseBranch(), childIndent, true);
            }
        } else if (node instanceof WhileStatement w) {
            System.out.println("WhileStatement");
            printNode(w.getCondition(), childIndent, false);
            printNode(w.getBody(), childIndent, true);
        } else if (node instanceof BlockStatement b) {
            System.out.println("BlockStatement");
            List<Statement> stmts = b.getStatements();
            for (int j = 0; j < stmts.size(); j++) {
                printNode(stmts.get(j), childIndent, j == stmts.size() - 1);
            }
        } else if (node instanceof ExpressionStatement e) {
            System.out.println("ExpressionStatement");
            printNode(e.getExpression(), childIndent, true);
        } else if (node instanceof BinaryExpression bin) {
            System.out.println("BinaryExpression: " + bin.getOperator());
            printNode(bin.getLeft(), childIndent, false);
            printNode(bin.getRight(), childIndent, true);
        } else if (node instanceof UnaryExpression un) {
            System.out.println("UnaryExpression: " + un.getOperator());
            printNode(un.getRight(), childIndent, true);
        } else if (node instanceof AssignExpression assign) {
            System.out.println("AssignExpression: " + assign.getName() + " =");
            printNode(assign.getValue(), childIndent, true);
        } else if (node instanceof NumberExpression num) {
            System.out.println("Number: " + num.getValue());
        } else if (node instanceof VariableExpression varExpr) {
            System.out.println("Variable: " + varExpr.getName());
        } else {
            System.out.println("Unknown Node: " + node.getClass().getSimpleName());
        }
    }
}