package main;

import main.RatNum;

/**
 * RatNumTest2 testar konstruktorerna
 * 2016 some small things
 * TODO bätter utskrifter
 */
public class RatNumTest2 {

    private static void fel(int nr) {
        System.out.println("RatNumTest2: Error number " + nr);
        System.exit(1);
    }

    public static void divTester() {
        RatNum r;

        // test av konstruktor
        r = new RatNum(9);
        if (r.getNumerator() != 9 || r.getDenominator() != 1)
            fel(1);
        r = new RatNum(4, 9);
        if (r.getNumerator() != 4 || r.getDenominator() != 9)
            fel(2);
        r = new RatNum(49, 168);
        if (r.getNumerator() != 7 || r.getDenominator() != 24)
            fel(3);
        RatNum r2 = new RatNum(r);
        // har r2 och r samma värden?
        if (r2.getNumerator() != 7 || r2.getDenominator() != 24)
            fel(4);
        RatNum x = new RatNum();
        if (x.getNumerator() != 0 || x.getDenominator() != 1)
            fel(5);
        // testar om instansvariablerna egentligen är statiska
        if (r2.getNumerator() == 0 || r2.getDenominator() == 1)
            fel(6);
        RatNum y = new RatNum(5);
        if (y.getNumerator() != 5 || y.getDenominator() != 1)
            fel(7);
        RatNum z = new RatNum(20, 4);
        if (z.getNumerator() != 5 || z.getDenominator() != 1)
            fel(8);
        RatNum w = new RatNum(0,1);
        if (w.getNumerator() != 0 || w.getDenominator() != 1)
            fel(9);
        RatNum q = new RatNum(y);
        if (q.getNumerator() != 5 || q.getDenominator() != 1)
            fel(10);

        // test av negativa parametrar
        r = new RatNum(-49, 168);
        if (r.getNumerator() != -7 || r.getDenominator() != 24)
            fel(11);
        r = new RatNum(49, -168);
        if (r.getNumerator() != -7 || r.getDenominator() != 24)
            fel(12);
        r = new RatNum(-49, -168);
        if (r.getNumerator() != 7 || r.getDenominator() != 24)
            fel(13);

        // Test av exception
        boolean ok = false;
        try {
            q = new RatNum(5,0);
        }
        catch (NumberFormatException e1) {ok = true;}
        catch (Exception e2) {}
        if (!ok)
            fel(14);
    }

    public static void main(String[] arg) {
        divTester();
        System.out.println("Inga fel hittades på konstruktorerna!");
    }
}
