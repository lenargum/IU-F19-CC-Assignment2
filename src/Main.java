import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

class Expression {

    public long calculate() {
        return 0;
    }

}

class Binary extends Expression {

    Expression left;
    Expression right;
}

class Relation extends Binary {

}

class Less extends Relation {

    public Less(Expression expression, Expression expression1) {
        this.left = expression;
        this.right = expression1;
    }

    @Override
    public long calculate() {
        if (this.left.calculate() < this.right.calculate()) {
            return 1;
        } else {
            return 0;
        }
    }
}

class Greater extends Relation {

    public Greater(Expression expression, Expression expression1) {
        this.left = expression;
        this.right = expression1;
    }

    @Override
    public long calculate() {
        if (this.left.calculate() > this.right.calculate()) {
            return 1;
        } else {
            return 0;
        }
    }
}

class Equal extends Relation {

    public Equal(Expression expression, Expression expression1) {
        this.left = expression;
        this.right = expression1;
    }

    @Override
    public long calculate() {
        if (this.left.calculate() == this.right.calculate()) {
            return 1;
        } else {
            return 0;
        }
    }
}

class Term extends Binary {

}

class Plus extends Term {

    public Plus(Expression expression, Expression expression1) {
        this.left = expression;
        this.right = expression1;
    }

    @Override
    public long calculate() {
        return this.left.calculate() + this.right.calculate();
    }
}

class Minus extends Term {

    public Minus(Expression expression, Expression expression1) {
        this.left = expression;
        this.right = expression1;
    }

    @Override
    public long calculate() {
        return this.left.calculate() - this.right.calculate();
    }


}

class Factor extends Term {

    public Factor(Expression expression, Expression expression1) {
        this.left = expression;
        this.right = expression1;
    }

    @Override
    public long calculate() {
        return this.left.calculate() * this.right.calculate();
    }
}

class Primary extends Expression {

}

class Int extends Primary {

    private long value;

    public Int(String value) {
        this.value = Long.parseLong(value);
    }

    @Override
    public long calculate() {
        return value;
    }
}

class Parenthesized extends Primary {

    private Expression expression;

    public Parenthesized(Expression parse) {
        this.expression = parse;
    }

    @Override
    public long calculate() {
        return expression.calculate();
    }
}

class Parser {

    private ArrayList<String> tokens;
    private int iterator = 0;

    public Parser(String s) {

        tokens = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        boolean isBufferForNumber = true;
        int bracketLevel = 0;

        for (char character : s.toCharArray()) {
            if (buffer.length() == 0) {
                if (Character.isDigit(character)) {
                    buffer.append(character);
                    isBufferForNumber = true;
                } else if (character == '(') {
                    buffer.append(character);
                    isBufferForNumber = false;
                    bracketLevel++;
                } else {
                    tokens.add(String.valueOf(character));
                }
            } else if (isBufferForNumber) {
                if (Character.isDigit(character)) {
                    buffer.append(character);
                } else {
                    tokens.add(buffer.toString());
                    buffer = new StringBuilder();

                    if (character == '(') {
                        buffer.append(character);
                        isBufferForNumber = false;
                        bracketLevel++;
                    } else {
                        tokens.add(String.valueOf(character));
                    }
                }
            } else {
                if (character == ')') {
                    if (bracketLevel == 1) {
                        buffer.append(character);
                        tokens.add(buffer.toString());
                        buffer = new StringBuilder();
                    } else {
                        buffer.append(character);
                        bracketLevel--;
                    }

                } else if (character == '(') {
                    buffer.append(character);
                    bracketLevel++;
                } else {
                    buffer.append(character);
                }
            }
        }
        tokens.add(buffer.toString());
        buffer = new StringBuilder();

        System.out.println(tokens.toString());
    }

    public Expression parse() {
        return parseRelation();
    }

    private Expression parseRelation() {
        Expression result = parseTerm();
        String op = getToken();
        incIterator();
        switch (op) {
            case "<": {
                Expression right = parseTerm();
                result = new Less(result, right);
                break;
            }
            case ">": {
                Expression right = parseTerm();
                result = new Greater(result, right);
                break;
            }
            case "=": {
                Expression right = parseTerm();
                result = new Equal(result, right);
                break;
            }
        }
        return result;
    }


    private Expression parseTerm() {
        Expression result = parseFactor();
        while (true) {
            String op = getToken(); // takes the next token
            if (op.equals("+")) {
                incIterator();
                Expression right = parseFactor();
                result = new Plus(result, right);
            } else if (op.equals("-")) {
                incIterator();
                Expression right = parseFactor();
                result = new Minus(result, right);
            } else {
                break;
            }
        }
        return result;
    }

    private Expression parseFactor() {
        Expression result = parsePrimary();
        while (true) {
            String op = getToken(); // takes the next token
            if (op.equals("*")) {
                incIterator();
                Expression right = parsePrimary();
                result = new Factor(result, right);
            } else {
                break;
            }
        }
        return result;
    }

    private Expression parsePrimary() {
        Expression result = null;
        String token = getToken();

        if (isNumber(token)) {
            incIterator();
            result = parseInteger(token);
        } else if (token.charAt(0) == '(') {
            incIterator();
            Parser parser = new Parser(getTextBetweenBrackets(token));
            result = new Parenthesized(parser.parse());
        } else {
            System.err.println(
                "[Error] Token \"" + token + "\" is not identified as an integer or expression");
        }
        return result;
    }

    private Expression parseInteger(String token) {
        return new Int(token);
    }

    private String getToken() {
        return tokens.get(this.iterator);
    }

    private void incIterator() {
        if (this.iterator + 1 < tokens.size()) {
            iterator++;
        }
    }

    private String getTextBetweenBrackets(String primary) {
        return primary.substring(1, primary.length() - 1);
    }

    private boolean isNumber(String token) {
        try {
            Integer.parseInt(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

public class Main {

    public static void main(String[] args) {

        String input = readLine();
        Parser parser = new Parser(input);
        Expression expressionTree = parser.parse();
        long result = expressionTree.calculate();
        System.out.println("Calculation result: " + result);
    }

    private static String readLine() {
        Scanner in = null;
        try {
            in = new Scanner(new BufferedReader(new FileReader("input.txt")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return in.nextLine().replaceAll(" ", "");
    }
}