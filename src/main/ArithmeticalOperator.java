package main;
/**
 * Created by sebastian on 2016-11-29.
 */
public enum ArithmeticalOperator {

    //These operators need to be in order from highest to lower prio when evaluated in an expression
    Pow("^",(a,b)-> a.pow(b)),Div("/", (a, b) -> a.div(b)), Mul("*", (a, b) -> a.mul(b)), Add("+", (a, b) -> a.add(b)), Sub("-", (a, b) -> a.sub(b));

    public final String syntax;
    private final Operator2 operator;

    ArithmeticalOperator(String syntax, Operator2 operator) {
        this.syntax = syntax;
        this.operator = operator;
    }

    public RatNum run(RatNum a, RatNum b) {
        return operator.run(a, b);
    }

    public String getSyntax() {
        return syntax;
    }
}

interface Operator2 {

    RatNum run(RatNum a, RatNum b);

}