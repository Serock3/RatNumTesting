package main;

import java.util.ArrayList;

/**
 * Created by sebas on 2016-11-29.
 * Main method, runs the program
 */
public class MainMenu {
    public static void main(String args[]) {
        String tmp = " as sd s  ddf   fg fg g";
        String tmp2[] = tmp.split("\\s");
        tmp = "";
        for (String tmp3:tmp2) {
            tmp += tmp3;
        }
        System.out.println("|"+tmp+"|");

        //System.out.println(RatNum.evalExpr("(23*3-(2/4))+0.5"));
    }
}
