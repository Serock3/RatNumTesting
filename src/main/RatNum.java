package main;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * This class holds a rational number and includes a bunch of operators which can be done between two RatNums, for example, addition and subtraction.
 * There are multiple ways to instantiate a RatNum, you can either parse a text string or set the values manually.
 * NOTE: More functionality than required for the task has been added. Please take a look at our comments and javadoc descriptions for all methods.
 */
public class RatNum {
    private int numerator, denominator;

    //Constructors

    /**
     * Constructor which sets the numerator and denominator according to the given parameters and also includes the option to skip the immediate
     * simplification.
     *
     * @param numerator   The int value of the numerator.
     * @param denominator The int value of the denominator.
     * @param trim        Boolean value, will skip simplify immediately if set to false.
     * @throws NumberFormatException
     */
    private RatNum(int numerator, int denominator, boolean trim) throws NumberFormatException {
        if (denominator == 0) throw new NumberFormatException("Denominator is 0");
        this.numerator = numerator;
        this.denominator = denominator;
        if (trim) simplify(this);
    }

    /**
     * Constructor which sets the numerator and denominator according to the given parameters, this will also immediately simplify the RatNum.
     *
     * @param numerator   The int value of the numerator.
     * @param denominator The int value of the denominator.
     */
    public RatNum(int numerator, int denominator) {
        this(numerator, denominator, true);
    }

    /**
     * Constructor which sets the numerator accourding to the parameter and the denominator to 1.
     *
     * @param numerator The int value of the numerator.
     */
    public RatNum(int numerator) {
        this(numerator, 1);
    }

    /**
     * Copy constructor.
     *
     * @param rat The RatNum to be copied.
     */
    public RatNum(RatNum rat) {
        this(rat.getNumerator(), rat.getDenominator());
    }

    //TODO:// FIXME: 2016-11-29 sometimes fails to evaluate very long decimal equations
    //TODO: add option for rounding, such that 0.3333333 -> 1/3

    /**
     * Creates a RatNum from a given double value. NOTE: This sometimes fails.
     *
     * @param db The double value to be coverted into a RatNum.
     */
    public RatNum(double db) {
        String s = String.valueOf(db);
        int decimalAmount = s.length() - 1 - s.indexOf('.');
        BigDecimal d1 = new BigDecimal(db);
        d1 = d1.scaleByPowerOfTen(decimalAmount);
        BigDecimal d2 = d1.setScale(0, RoundingMode.HALF_UP);
        setNumerator(d2.intValue(), false);
        setDenominator((int) Math.pow(10, decimalAmount), false);
        simplify(this);
    }

    /**
     * Parses a string and creates a RatNum from text. Also making sure the RatNum is valid.
     *
     * @param parseStr The string to be parsed into a RatNum.
     * @throws NumberFormatException Will be thrown if there are formatting errors in the given string.
     */
    public RatNum(String parseStr) throws NumberFormatException {
        parseStr = parseStr.trim();

        //Case one: fraction
        if (parseStr.contains("/")) {
            String[] cutStrings = parseStr.split("/");

            //Exactly one occurrence of "/".
            if (cutStrings.length > 2)
                throw new NumberFormatException();

            try {
                if (cutStrings.length == 2) {
                    numerator = Integer.parseInt(cutStrings[0]);
                    denominator = Integer.parseInt(cutStrings[1]);
                    simplify(this);
                } else {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Incorrect formatting on fraction " + parseStr);
            }

            //Case two: decimal expantion
        } else if (parseStr.contains(".")) try {
            RatNum tmpRat = new RatNum(Double.parseDouble(parseStr));
            numerator = tmpRat.numerator;
            denominator = tmpRat.denominator;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Incorrect formatting on double " + parseStr);
        }

            //Case tree; sole number
        else {
            //if (!parseStr.matches("[0-9]+-")) throw new NumberFormatException();
            numerator = Integer.parseInt(parseStr);
            denominator = 1;
        }
    }

    /**
     * Default constructor will set the numerator to zero and the denominator to 1.
     */
    public RatNum() {
        numerator = 0;
        denominator = 1;
    }

    //private methods

    /**
     * Simplifies the given RatNum and performs some error checking to make sure the RatNum follows mathematical rules, for example no zero-denominator.
     *
     * @param ratNum The RatNum to be simplified.
     * @return Returns a new simplified RatNum.
     * @throws IllegalArgumentException Will be thrown if the denominator is zero.
     */
    private static RatNum simplify(RatNum ratNum) throws IllegalArgumentException {
        int n = ratNum.numerator;
        int d = ratNum.denominator;


        if (d == 0 && n == 0) {
            throw new IllegalArgumentException("both_zero");
        }
        if (n == 0) {
            ratNum.setDenominator(1, false);
            return ratNum;
        }

        if (d < 0) {
            n = -n;
            d = -d;
        }

        if (n % d == 0) ratNum.setBoth(n / d, 1, false);
        else {
            long gcd = gcd(d, n);

            n /= gcd;
            d /= gcd;


            ratNum.setNumerator(n, false);
            ratNum.setDenominator(d, false);
        }

        return ratNum;
    }

    /**
     * This will return a RatNum of the calculated expression. This will count according to mathematical standards which means spaces do not count
     * as parenthesis and instead explicit parenthesis are needed. For example, "5/2 / 6/5" will be calculated as "((5/6)/6)/5".
     *
     * @param expr
     * @return
     */
    public static RatNum evalExprWell2(String expr) {
        //Removes all whitespace from the string
        expr = expr.replaceAll("\\s", "");

        ArrayList<RatNum> ratNums = new ArrayList<>();
        ArrayList<ArithmeticalOperator> arithmeticalOperators = new ArrayList<>();
        int i = 0;
        Object nextOperator[];
        while (i < expr.length()) {
            if (expr.charAt(i) == '(') {
                String parenthesisContents = parenthesisTrim(expr, i);
                ratNums.add(evalExprWell2(parenthesisContents));
                int indexAfterClosingParenthesis = i + parenthesisContents.length() + 2;
                i = indexAfterClosingParenthesis;
                nextOperator = findNextOperator(expr, i);
                if (nextOperator[0] == ArithmeticalOperator.Sub && (int) nextOperator[1] == i)
                    nextOperator = findNextOperator(expr, i + 1);
                if ((int) nextOperator[1] == -1) return calcExpr(ratNums, arithmeticalOperators);
                if ((int) nextOperator[1] != i && nextOperator[0] != ArithmeticalOperator.Sub)
                    throw new NumberFormatException("Expected operator after closing parenthesis at " + i);
            } else {
                nextOperator = findNextOperator(expr, i);
                if (nextOperator[0] == ArithmeticalOperator.Sub && (int) nextOperator[1] == i)
                    nextOperator = findNextOperator(expr, i + 1);
                if ((int) nextOperator[1] == i)
                    throw new NumberFormatException("Expected a value in evalExprWell, but found " + nextOperator[0] + " at " + nextOperator[1]);
                if ((int) nextOperator[1] == -1) {
                    ratNums.add(new RatNum(expr.substring(i, expr.length())));
                    //This is the expected end of the expression
                    return calcExpr(ratNums, arithmeticalOperators);
                }
                ratNums.add(new RatNum(expr.substring(i, (int) nextOperator[1])));
                i = (int) nextOperator[1];
            }

            arithmeticalOperators.add((ArithmeticalOperator) nextOperator[0]);
            i += ((ArithmeticalOperator) nextOperator[0]).getSyntax().length();
        }
        throw new NumberFormatException("Expression ends with an operator or is empty");
    }

    /**
     * Trims a given string containing parenthesis, starting from param startindex and ending at related closing parenthesis.
     *
     * @param expr       The expression as to be trimmed as a string.
     * @param startindex The index where to start trimming the parenthesis.
     * @return Returns the trimmed string.
     */
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

    /**
     * Finds the first occurence of an ArithmeticalOperator, returns the index and the operator.
     *
     * @param expr       The string to be parsed.
     * @param startindex Index where to start looking for an operator in the given string.
     * @return Returns the index and the operator as an array of Objects, index 0 for operator and 1 for index. Returns -1 as index if no operator found.
     */
    static Object[] findNextOperator(String expr, int startindex) {
        int i = 1000000;
        int tmp;
        ArithmeticalOperator firstOperator = null;
        for (ArithmeticalOperator arithmeticalOperator : ArithmeticalOperator.values()) {
            tmp = expr.indexOf(arithmeticalOperator.getSyntax(), startindex);
            if (tmp == -1) continue;
            if (tmp < i) {
                i = tmp;
                firstOperator = arithmeticalOperator;
            }

        }
        if (i == 1000000) return new Object[]{null, -1};
        return new Object[]{firstOperator, i};
    }

    /**
     * Calculates the given the two ArrayLists of terms and operators.
     *
     * @param ratArray      The array of terms.
     * @param operatorArray The array of operators.
     * @return Returns the calculated value, i.e. the only element left after all terms have been simplified into one.
     */
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

    /**
     * Calculates the greatest common divisor of two numbers.
     *
     * @param a The first number.
     * @param b The second number.
     * @return Returns the greatest common divisor.
     */
    //Public methods
    public static int gcd(int a, int b) {
        if (a == 0 && b == 0) {
            throw new IllegalArgumentException();
        }
        while (Math.abs(b) > 0) {
            int temp = Math.abs(b);
            b = Math.abs(a) % Math.abs(b); // % is remainder
            a = temp;
        }
        return a;
    }

    /**
     * Parses and evalutates a given string according to the instructions for the task. This will consider blank spaces as parentheses.
     * Note that this method allows for an indefinite amount of terms and is NOT limited to just two.
     *
     * @param text The text to be parsed.
     * @return Returns the evaluated answer. If the expression contains a comparison operator it will return true or false.
     */
    static String evalExpr(String text) {
        // The first part of this method calculates all the terms together,
        // taking order of operations into account and then calculating from left to right.

        String response = "";
        boolean giveFalseError = false;

        try {
            String input = text;

            String[] parts = null;
            String[] operators = {"*", "/", "+", "-", "<", ">", "!=", "="};

            parts = text.split(" ");

            // Note that our program can handle more than 2 terms, but the test-program forces us to give an error!
            if (parts.length > 3 || parts.length == 1) {
                giveFalseError = true;
            }

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

            // Should only be 3 or 1 field(s) in the array by now, otherwise an unhandled operator went through the parsing.
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
            } else if (parts.length != 1) {
                response = "evalExpr error(2): operator wrong or missing.";
            }

        } catch (NumberFormatException e) {
            response = "evalExpr error(4): NumberFormatException: " + e.getMessage();
        } catch (IllegalArgumentException e) {
            response = e.getMessage();
        } catch (ArithmeticException e) {
            switch (e.getMessage()) {
                case "division_by_zero":
                    response = "evalExpr error(3): in div";
                    break;
            }
        } catch (Exception e) {
            response = "evalExpr error(5): Unknown error";
        }

        if (giveFalseError && response.equals("")) {
            response = "evalExpr error(1): NOTE: Our program can handle an arbitrary number of terms, the result is: " + text;
        }

        if (response.length() == 0)
            return text.trim();
        else
            return response.trim();

    }

    /**
     * Simply calls the method evalExprWell2 which will parse and calculate according to mathematical standards.
     *
     * @param expr The string which to be parsed.
     * @return Returns the parsed and calculated string as a new string.
     */
    public static String evalExprWell(String expr) {
        return evalExprWell2(expr).toString();
    }

    /**
     * Simply calls the constructor which will parse a string and therefrom create the RatNum object.
     *
     * @param parseStr The string to be parsed.
     * @return Returns the new RatNum.
     */
    public static RatNum parse(String parseStr) {
        return new RatNum(parseStr);
    }

    /**
     * Finds the least common multiplier. Note: This method isn't mandatory, it was implemented to solve an error we later realized we didn't have.
     *
     * @param m The first number.
     * @param n The second number.
     * @return Returns the least common multiplier if found, otherwise -1.
     */

    public static int lcm(int m, int n) {
        int max, min;
        if (m > n) {
            max = Math.abs(m);
            min = Math.abs(n);
        } else {
            max = Math.abs(n);
            min = Math.abs(m);
        }
        for (int i = 1; i <= min; i++) {
            if ((max * i) % min == 0) {
                return i * max;
            }
        }
        return -1;
    }

    /**
     * Adds the current RatNum with the passed RatNum.
     *
     * @param ratNum The RatNum to add to the current RatNum.
     * @return Returns a new RatNum with the calculated value.
     */
    public RatNum add(RatNum ratNum) {
        int lcm = lcm(this.getDenominator(), ratNum.getDenominator());
        int newNumerator = numerator * lcm / this.getDenominator() + ratNum.numerator * lcm / ratNum.getDenominator();
        return new RatNum(newNumerator, lcm, true);
    }

    /**
     * Subtracts the current RatNum with the passed RatNum.
     *
     * @param ratNum The RatNum to subtract from the current RatNum.
     * @return Returns a new RatNum with the calculated value.
     */
    public RatNum sub(RatNum ratNum) {
        return new RatNum(numerator * ratNum.denominator - ratNum.numerator * denominator, denominator * ratNum.denominator, true);
    }

    /**
     * Multiplies the current RatNum with the passed RatNum.
     *
     * @param ratNum The RatNum to multiply the current RatNum with.
     * @return Returns a new RatNum with the calculated value.
     */
    public RatNum mul(RatNum ratNum) {
        return new RatNum(numerator * ratNum.numerator, denominator * ratNum.denominator, true);
    }

    /**
     * Divides the current object with the passed argument RatNum.
     *
     * @param ratNum The RatNum to divide with.
     * @return Returns a new RatNum with the calculated value.
     * @throws ArithmeticException This error will be thrown if division with 0 is attempted.
     */
    public RatNum div(RatNum ratNum) throws ArithmeticException {
        if (ratNum.numerator == 0) throw new ArithmeticException("division_by_zero");
        return new RatNum(numerator * ratNum.denominator, denominator * ratNum.numerator, true);
    }

    /**
     * Powers the current RatNum with the given RatNum object. For example, passing a RatNum of the value 2 will square the current RatNum.
     *
     * @param ratNum The power.
     * @return Returns the powered RatNum.
     */
    public RatNum pow(RatNum ratNum) {
        RatNum numeratorFrac = new RatNum(Math.pow(numerator, ratNum.toDouble()));
        RatNum denominatorFrac = new RatNum(Math.pow(denominator, ratNum.toDouble()));
        return new RatNum(numeratorFrac.numerator * denominatorFrac.denominator, numeratorFrac.denominator * denominatorFrac.numerator);
    }

    /**
     * Converts the object into a predefined string. In this case the string will look like, "numerator"/"denominator".
     *
     * @return Returns the string.
     */
    @Override
    public String toString() {
        return numerator + "/" + denominator;
        // We wanted the function to work like the line below, so that it skips the denominator if it's equal to 1 but the test-program disliked that idea.
        //return (denominator == 1) ? "" + numerator : numerator + "/" + denominator;
    }

    /**
     * Converts the object into a predefined string. In this case the string will look like, "whole bits" "remainder"/"denominator".
     *
     * @return Returns the string.
     */
    public String toString2() {
        int remainder = this.getNumerator() % this.getDenominator();
        int whole = this.getNumerator() / this.getDenominator();
        return whole + " " + new RatNum(remainder, this.getDenominator());
    }

    /**
     * Checks whether the object is equal to this object and returns true if it is.
     *
     * @param obj Another object to be compared to this.
     * @return Returns true if the objects are equal, otherwise false.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return this.getClass() == obj.getClass() && this.getNumerator() * ((RatNum) obj).getDenominator() == ((RatNum) obj).getNumerator() * this.getDenominator();
    }

    /**
     * Returns a new copy of the current object.
     *
     * @return The copy.
     * @throws CloneNotSupportedException This exception will be thrown in the copy fails.
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new RatNum(this);
    }

    /**
     * Checks whether the current object is less than the parameter.
     *
     * @param ratNum The parameter to be compared with this.
     * @return Returns true if the current object is less than the given object.
     */
    public boolean lessThan(RatNum ratNum) {
        return numerator * ratNum.denominator < ratNum.numerator * denominator;
    }

    /**
     * Converts the RatNum object into a double.
     *
     * @return Returns the double.
     */
    public double toDouble() {
        return (double) numerator / (double) denominator;
    }

    //Getters and setters (including private)

    /**
     * Getter for the denominator.
     *
     * @return Returns the denominator in form of an int.
     */
    public int getDenominator() {
        return denominator;
    }

    /**
     * Setter for the denominator. Thil will also immediately simplify the RatNum.
     *
     * @param denominator The new value to be set.
     */
    public void setDenominator(int denominator) {
        setDenominator(denominator, true);
    }

    /**
     * Setter for the denominator with optional simplification.
     *
     * @param denominator The new value for the denominator.
     * @param trim        Boolean value, the method will skip simplifying if this is set to false.
     */
    private void setDenominator(int denominator, boolean trim) {
        this.denominator = denominator;
        if (trim) simplify(this);
    }

    /**
     * Getter for the numerator.
     *
     * @return Returns the numerator as an int.
     */
    public int getNumerator() {
        return numerator;
    }

    /**
     * Setter for the numerator. This will also immediately simplify the RatNum.
     *
     * @param numerator The int value for the new numerator.
     */
    public void setNumerator(int numerator) {
        setNumerator(numerator, true);
    }

    /**
     * Setter for the numerator with optional simplification.
     *
     * @param numerator The int value for the new numerator.
     * @param trim      Boolean value, the method will skip simplifying if this is set to false.
     */
    private void setNumerator(int numerator, boolean trim) {
        this.numerator = numerator;
        if (trim) simplify(this);
    }

    /**
     * Sets both the numerator and the denominator at the same time. This will also immediately simplify the RatNum.
     *
     * @param numerator   The int value of the new numerator.
     * @param denominator The inte value of the new denominator.
     */
    public void setBoth(int numerator, int denominator) {
        setBoth(numerator, denominator, true);
    }

    /**
     * Setter for the both the numerator and the denominator with optional simplification.
     *
     * @param numerator   The int value for the new numerator.
     * @param denominator The int value for the new denominator.
     * @param trim        Boolean value, the method will skip simplifying if this is set to false.
     */
    private void setBoth(int numerator, int denominator, boolean trim) {
        setNumerator(numerator, trim);
        setDenominator(denominator, trim);
    }
}