package main;

import javafx.util.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * Created by sebas on 2016-11-29.
 * Rational number.
 * <p>
 * This class //TODO: skriv tråkiga saker här
 */
public class RatNum {
    private int numerator, denominator;

    //Constructors

    /**
     * Private constructor with option to skip trimming
     *
     * @param numerator
     * @param denominator
     * @param trim        true:trim during construction, false: don't
     */
    private RatNum(int numerator, int denominator, boolean trim) throws NumberFormatException {
        if (denominator == 0) throw new NumberFormatException("Denominator is 0");
        this.numerator = numerator;
        this.denominator = denominator;
        if (trim) trim(this);
    }

    public RatNum(int numerator, int denominator) {
        this(numerator, denominator, true);
    }

    public RatNum(int numerator) {
        this(numerator, 1);
    }

    public RatNum(RatNum rat) {
        this(rat.getNumerator(), rat.getDenominator());
    }

    //TODO:// FIXME: 2016-11-29 sometimes fails to evaluate very long decimal equations
    //TODO: add option for rounding, such that 0.3333333 -> 1/3

    /**
     * Accurately converts a double to a fraction with no rounding, using BigDecimal
     *
     * @param db double to be converted to a fraction
     */
    public RatNum(double db) {
        String s = String.valueOf(db);
        int decimalAmount = s.length() - 1 - s.indexOf('.');
        BigDecimal d1 = new BigDecimal(db);
        d1 = d1.scaleByPowerOfTen(decimalAmount);
        BigDecimal d2 = d1.setScale(0, RoundingMode.HALF_UP);
        setNumerator(d2.intValue(), false);
        setDenominator((int) Math.pow(10, decimalAmount), false);
        trim(this);
    }

    public RatNum(String parseStr) throws NumberFormatException {
        parseStr = parseStr.trim();

        //Case one: fraction
        if (parseStr.contains("/")) {
            String[] cutStrings = parseStr.split("/");

            //Exactly one occurrence of "/".
            if (cutStrings.length > 2) throw new NumberFormatException();

            try {
                numerator = Integer.parseInt(cutStrings[0]);
                denominator = Integer.parseInt(cutStrings[1]);
                trim(this);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.err.print("Incorrect formatting on fraction " + parseStr);
            }

            //Case two: decimal expantion
        } else if (parseStr.contains(".")) try {
            RatNum tmpRat = new RatNum(Double.parseDouble(parseStr));
            numerator = tmpRat.numerator;
            denominator = tmpRat.denominator;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.err.print("Incorrect formatting on double " + parseStr);
        }

            //Case tree; sole number
        else {
            //if (!parseStr.matches("[0-9]+-")) throw new NumberFormatException();
            numerator = Integer.parseInt(parseStr);
            denominator = 1;
        }
    }

    public RatNum() {
        numerator = 0;
        denominator = 1;
    }

    //private methods

    private static RatNum trim(RatNum ratNum) throws IllegalArgumentException {
        int n = ratNum.numerator;
        int d = ratNum.denominator;

        if (d == 0) {
            throw new IllegalArgumentException("Denominator is 0");
        }
        if (n == 0) {
            ratNum.setDenominator(1, false);
            return ratNum;
        }

        n *= Math.signum(d);
        d *= Math.signum(d);

        if (n % d == 0) ratNum.setBoth(n / d, 1, false);
        else {
            int gcd = gcd(d, n);
            while ((gcd = gcd(d, n)) != 1) {
                n /= gcd;
                d /= gcd;
            }
            ratNum.setNumerator(n, false);
            ratNum.setDenominator(d, false);
        }
        return ratNum;
    }

    /**
     * Calulates expression of RatNums and operators with correct priority, returns resulting RatNum
     *
     * @param ratArray
     * @param operatorArray
     * @return
     */

    public static RatNum evalExprWell2(String expr) {
        //Removes all whitespace from the string
        expr = expr.replaceAll("\\s", "");

        ArrayList<RatNum> ratNums = new ArrayList<>();
        ArrayList<ArithmeticalOperator> arithmeticalOperators = new ArrayList<>();
        int i = 0;
        Pair<ArithmeticalOperator, Integer> nextOperator;
        while (i < expr.length()) {
            if (expr.charAt(i) == '(') {
                String parenthesisContents = parenthesisTrim(expr, i);
                ratNums.add(evalExprWell2(parenthesisContents));
                int indexAfterClosingParenthesis = i + parenthesisContents.length() + 2;
                i = indexAfterClosingParenthesis;
                nextOperator = findNextOperator(expr, i);
                if (nextOperator.getKey() == ArithmeticalOperator.Sub && nextOperator.getValue() == i)
                    nextOperator = findNextOperator(expr, i + 1);
                if (nextOperator.getValue() == -1) return calcExpr(ratNums, arithmeticalOperators);
                if (nextOperator.getValue() != i && nextOperator.getKey() != ArithmeticalOperator.Sub)
                    throw new NumberFormatException("Expected operator after closing parenthesis at " + i);
            } else {
                nextOperator = findNextOperator(expr, i);
                if (nextOperator.getKey() == ArithmeticalOperator.Sub && nextOperator.getValue() == i)
                    nextOperator = findNextOperator(expr, i + 1);
                if (nextOperator.getValue() == i)
                    throw new NumberFormatException("Expected a value in evalExprWell, but found " + nextOperator.getKey() + " at " + nextOperator.getValue());
                if (nextOperator.getValue() == -1) {
                    ratNums.add(new RatNum(expr.substring(i, expr.length())));
                    //This is the expected end of the expression
                    return calcExpr(ratNums, arithmeticalOperators);
                }
                ratNums.add(new RatNum(expr.substring(i, nextOperator.getValue())));
                i = nextOperator.getValue();
            }

            arithmeticalOperators.add(nextOperator.getKey());
            i += nextOperator.getKey().getSyntax().length();
        }
        throw new NumberFormatException("Expression end with an operator or is empty");
    }

    //returns the trimmed string and the index of the respective end parenthesis
    public static String parenthesisTrim(String expr, int startindex) {
        if (expr.charAt(startindex) != '(')
            throw new IllegalArgumentException("Start index in parenthesisTrim is not a parenthesis");
        int parenthesisCnt = 1;
        char c;
        for (int i = 1 + startindex; i < expr.length(); i++) {
            c = expr.toCharArray()[i];
            if (c == '(') parenthesisCnt++;
            else if (c == ')') parenthesisCnt--;
            if (parenthesisCnt == 0) return expr.substring(startindex + 1, i);
        }
        throw new IllegalArgumentException("Amount of closing and opening parenthesises are not equal");
    }

    //finds the first occurrence of an ArithmeticalOperator, returns the index and the operator, if no operator is found then the index returned is -1
    public static Pair<ArithmeticalOperator, Integer> findNextOperator(String expr, int startindex) {
        int i = 1000000;
        int itmp;
        ArithmeticalOperator firstOperator = null;
        for (ArithmeticalOperator arithmeticalOperator : ArithmeticalOperator.values()) {
            itmp = expr.indexOf(arithmeticalOperator.getSyntax(), startindex);
            if (itmp == -1) continue;
            if (itmp < i) {
                i = itmp;
                firstOperator = arithmeticalOperator;
            }

        }
        if (i == 1000000) return new Pair<>(null, -1);
        return new Pair<>(firstOperator, i);
    }

    private static RatNum calcExpr(ArrayList<RatNum> ratArray, ArrayList<ArithmeticalOperator> operatorArray) {
        if (ratArray.size() - operatorArray.size() != 1) {
            System.out.print(ratArray);
            throw new ArithmeticException("Proportion of values to operators is incorrect, this should ideally never happen");
        }
        int i;
        for (ArithmeticalOperator operator : ArithmeticalOperator.values()) {
            while ((i = operatorArray.indexOf(operator)) != -1) {
                ratArray.set(i, operator.run(ratArray.get(i), ratArray.get(i + 1)));
                ratArray.remove(i + 1);
                operatorArray.remove(i);
            }
        }
        return ratArray.get(0);
    }

    //Public methods
    public static int gcd(int a, int b){
        if(a==0 && b==0){
            throw new IllegalArgumentException();
        }
        while (Math.abs(b) > 0)
        {
            int temp = Math.abs(b);
            b = Math.abs(a) % Math.abs(b); // % is remainder
            a = temp;
        }
        return a;
    }

    static String evalExpr(String text) {
        // The first part of this method calculates all the terms together,
        // taking order of operations into account and then calculating from left to right.

        String input = text;

        String[] parts = null;
        String[] operators = {"*", "/", "+", "-", "<", ">", "!=", "=="};

        int found;

        do {
            found = 0;

            for (int o = 0; o < 4; o += 2) {
                if (found == 0) {
                    parts = text.split(" ");

                    text = "";

                    for (int i = 0; i < parts.length; i++) {
                        if (i < parts.length - 1 && (parts[i + 1].equals(operators[o]) || parts[i + 1].equals(operators[o + 1])) && found == 0) {

                            // Since multiplication and division have the same priority and both of them are checked in the if-statement above,
                            // I have to do a check which one of them that was triggered. The same goes for addition and subtraction.

                            int mod;
                            if (parts[i + 1].equals(operators[o])) {
                                mod = 0;
                            } else {
                                mod = 1;
                            }

                            switch (operators[o + mod]) {
                                case "*":
                                    text += parse(parts[i]).mul(parse(parts[i + 2])) + " ";
                                    break;
                                case "/":
                                    text += parse(parts[i]).div(parse(parts[i + 2])) + " ";
                                    break;
                                case "+":
                                    text += parse(parts[i]).add(parse(parts[i + 2])) + " ";
                                    break;
                                case "-":
                                    text += parse(parts[i]).sub(parse(parts[i + 2])) + " ";
                                    break;
                            }

                            found = i + 1;

                            // If there isn't an operator at the current field, and it's not included into the newly calculated two terms I'll just keep it as it is.
                        } else {
                            if (Math.abs(found - i) > 1 || found == 0) {
                                boolean justadd = false;

                                for (String op : operators) {
                                    if (op.equals(parts[i]))
                                        justadd = true;
                                }

                                if (justadd)
                                    text += parts[i] + " ";
                                else
                                    text += parse(parts[i]) + " ";
                            }
                        }
                    }
                }
            }
        } while (found != 0);

        // Once the string leaves the loop it should be fully simplified and handling of the expression can take place, if there are one.

        String response = "";

        // Should only be 3 fields in the array by now

        if (parts.length == 3) {
            switch (parts[1]) {
                case "<":
                    response = (parse(parts[0]).lessThan(parse(parts[2]))) ? "true" : "false";
                    break;
                case ">":
                    response = (parse(parts[2]).lessThan(parse(parts[0]))) ? "true" : "false";
                    break;
                case "=":
                    response = (parse(parts[0]).equals(parse(parts[2]))) ? "true" : "false";
                    break;
                case "!=":
                    response = (parse(parts[0]).equals(parse(parts[2]))) ? "false" : "true";
                    break;
            }
        }

        return "Input:\t" + input + "\n" + "Simp:\t" + text + "\n" + "Eval:\t" + response;
    }

    public static String evalExprWell(String expr) {
        return evalExprWell2(expr).toString();
    }

    public static RatNum parse(String parseStr) {
        return new RatNum(parseStr);
    }

    public RatNum add(RatNum ratNum) {
        return new RatNum(numerator * ratNum.denominator + ratNum.numerator * denominator, denominator * ratNum.denominator, true);
    }

    public RatNum sub(RatNum ratNum) {
        return new RatNum(numerator * ratNum.denominator - ratNum.numerator * denominator, denominator * ratNum.denominator, true);
    }

    public RatNum mul(RatNum ratNum) {
        return new RatNum(numerator * ratNum.numerator, denominator * ratNum.denominator, true);
    }

    public RatNum div(RatNum ratNum) throws ArithmeticException {
        if (ratNum.numerator == 0) throw new ArithmeticException("Division by zero");
        return new RatNum(numerator * ratNum.denominator, denominator * ratNum.numerator, true);
    }

    public RatNum pow(RatNum ratNum){
        RatNum numeratorFrac = new RatNum(Math.pow(numerator,ratNum.toDouble()));
        RatNum denominatorFrac = new RatNum(Math.pow(denominator,ratNum.toDouble()));
        return new RatNum(numeratorFrac.numerator*denominatorFrac.denominator,numeratorFrac.denominator*denominatorFrac.numerator);
    }

    @Override
    public String toString() {
        return (denominator == 1) ? "" + numerator : numerator + "/" + denominator;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new RatNum(this);
    }

    public boolean lessThan(RatNum ratNum) {
        return numerator * ratNum.denominator < ratNum.numerator * denominator;
    }

    public double toDouble() {
        return (double) numerator / (double) denominator;
    }

    //Getters and setters (including private)

    public int getDenominator() {
        return denominator;
    }

    public void setDenominator(int denominator) {
        setDenominator(denominator, true);
    }

    private void setDenominator(int denominator, boolean trim) {
        this.denominator = denominator;
        if (trim) trim(this);
    }

    public int getNumerator() {
        return numerator;
    }

    public void setNumerator(int numerator) {
        setNumerator(numerator, true);
    }

    private void setNumerator(int numerator, boolean trim) {
        this.numerator = numerator;
        if (trim) trim(this);
    }

    public void setBoth(int numerator, int denominator) {
        setBoth(numerator, denominator, true);
    }

    private void setBoth(int numerator, int denominator, boolean trim) {
        setNumerator(numerator, trim);
        setDenominator(denominator, trim);
    }
}
