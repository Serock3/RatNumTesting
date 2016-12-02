package main;
/**
 * Created by sebastian on 2016-11-29.
 */
public enum ArithmeticalOperator implements Operator {

    //These operators need to be in order from highest to lower prio when evaluated in an expression
    Pow("^",(a,b)-> a.pow(b)),Div("/", (a, b) -> a.div(b)), Mul("*", (a, b) -> a.mul(b)), Add("+", (a, b) -> a.add(b)), Sub("-", (a, b) -> a.sub(b));

    public final String syntax;
    private final Operator operator;

    ArithmeticalOperator(String syntax, Operator operator) {
        this.syntax = syntax;
        this.operator = operator;
    }

    @Override
    public RatNum run(RatNum a, RatNum b) {
        return operator.run(a, b);
    }

    public String getSyntax() {
        return syntax;
    }
}
