package test;

/**
 * Created by sebas on 2016-11-29.
 */
/**
 * RatNumTest1 testar om gcd funktionen fungerar
 * 2013-10-15 all text tio English
 * 2016 res + javaSTLgcdWorkaround + some small things
 * TODO (even better formating (indentation) of answers - lowpri)
 */
import main.SimpleMath;

import java.util.*;
import java.math.BigInteger;
public class RatNumTest1 {
    // String är egentligen helt olämplig här, StringBuffer är bättre
    private static String testa(int m, int n) {
        int size = 0;
        String res = "testing numbers: " + m + " and " + n;
        size = res.length();
        int diff = 35-size;
        res = res + spaces(diff);
        int z = 0;
        try {
            z = SimpleMath.gcd(m,n);
        }
        // exception genererad
        catch (IllegalArgumentException e1) {
            if (m==0 && n==0) {
                res = res + "Correct IllegalArgumentException generated  ok";
            } else {
                res = res + "Wrong IllegalArgumentException generated" +
                        "  *************";
            }
            return res;
        }
        catch (Exception e2) {  // naturkatastrof
            res = res + e2.getMessage();
            res = res + " Wrong exception generated *************";
            return res;
        }
        // exception inte genererad
        if (m==0 && n==0) {
            res = res + "gcd should have generated IllegalArgumentException" + "  *************";
            return res;
        }
        //
        //int b = SgdUtil.computeSgdEH(m,n);
        int b = javaSTLgcdWorkaround(m,n);
        res = res + "Your result: " + z +
                ".\tCorrect result: " + b + ".";
        if (z != b)
            res = res + "  *************";
        else
            res = res + "  ok";
        return res;
    }

    private static String spaces(int i) {
        if(i<=0) {return "";}
        return "....................".substring(0,i);
    }

    private static int javaSTLgcdWorkaround (int a, int b) {
        //Cred: http://stackoverflow.com/questions/4009198/java-get-greatest-common-divisor
        BigInteger b1 = new BigInteger("" + a);
        BigInteger b2 = new BigInteger("" + b);
        BigInteger g = b1.gcd(b2);
        return g.intValue();
    }

    public static void main (String[] arg) {
		/*
		System.out.println("*" + spaces(-1) + "*-1");
		System.out.println("*" + spaces(0) + "*0");
		System.out.println("*" + spaces(1) + "*1");
		System.out.println("*" + spaces(2) + "*2");
		System.out.println("*" + spaces(3) + "*3");
		System.out.println("*" + spaces(4) + "*");
		System.out.println("*" + spaces(5) + "*");
		System.out.println("*" + spaces(6) + "*");
		System.out.println("*" + spaces(7) + "*");
		System.out.println("*" + spaces(8) + "*");
		System.out.println("*" + spaces(9) + "*");
		System.out.println("*" + spaces(10) + "*10");
		System.out.println("*" + spaces(11) + "*");

		*/
        System.out.println("Test of gcd");
        System.out.println(testa(0, 0));
        System.out.println(testa(1, 1));
        System.out.println(testa(12, 1));
        System.out.println(testa(12, 2));
        System.out.println(testa(12, 14));
        System.out.println(testa(22, 14));
        System.out.println(testa(39, 15));
        System.out.println(testa(40, 12));
        System.out.println(testa(168, 49));
        System.out.println(testa(143, 7));
        System.out.println(testa(7, 143));
        System.out.println(testa(1260, 36));
        System.out.println(testa(36, 1260));
        System.out.println(testa(15775, 100));
        System.out.println(testa(100, 15775));
        System.out.println(testa(15776, 100));
        System.out.println(testa(15775, 12));
        System.out.println(testa(0, 12));
        System.out.println(testa(12, 0));
        System.out.println(testa(6, 39));
        System.out.println(testa(-6, 39));
        System.out.println(testa(39, -6));
        System.out.println(testa(-6, -39));

    }
}