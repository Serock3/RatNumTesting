package main;

/**
 * Created by sebas on 2016-11-29.
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
        if(denominator == 0)throw new NumberFormatException("Denominator is 0");
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


    public RatNum(double db){
        String s = String.valueOf(db);
        int decimalAmount = s.length() - 1 - s.indexOf('.');
        db*=10^decimalAmount;
        setNumerator((int)db);
        setDenominator(10^decimalAmount);

        System.out.println(db);
        trim(this);
    }


    //private methods

    private static RatNum trim(RatNum ratNum) throws IllegalArgumentException {
        int n = ratNum.numerator;
        int d = ratNum.denominator;

        if (d == 0) {
            throw new IllegalArgumentException("Denominator is 0");
        }
        if (n == 0) {
            ratNum.setDenominator(0, false);
            return ratNum;
        }

        n *= Math.signum(d);
        d *= Math.signum(d);

        if (n % d == 0) ratNum.setBoth(n / d, 1, false);
        else {
            int gcd = SimpleMath.gcd(d, n);
            while ((gcd = SimpleMath.gcd(d, n)) != 1) {
                n /= gcd;
                d /= gcd;
            }
            ratNum.setNumerator(n, false);
            ratNum.setDenominator(d, false);
        }
        return ratNum;
    }


    //Public methods

    public static RatNum add(RatNum a, RatNum b) {
        return new RatNum(a.numerator * b.denominator + b.numerator * a.denominator, a.denominator * b.denominator, true);
    }

    public static RatNum subtract(RatNum a, RatNum b) {
        return new RatNum(a.numerator * b.denominator - b.numerator * a.denominator, a.denominator * b.denominator, true);
    }

    public static RatNum multiply(RatNum a, RatNum b) {
        return new RatNum(a.numerator * b.numerator, a.denominator * b.denominator, true);
    }

    public static RatNum divide(RatNum a, RatNum b) throws ArithmeticException {
        if (b.numerator == 0) throw new ArithmeticException("Division by zero");
        return new RatNum(a.numerator * b.denominator, a.denominator * b.numerator, true);
    }

    @Override
    public String toString() {
        return numerator + "/" + denominator;
    }


    public double toDouble(){
        return (double)numerator/(double)denominator;
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
