package main;

import java.util.Scanner;

/**
 * Created by sebas on 2016-11-29.
 * Main method, runs the program
 */
public class MainMenu {
    public static void main(String args[]) {
        System.out.println(RatNum.evalExprWell("(27^(1/3))^2"));
        String text = "";

        Scanner scanner = new Scanner(System.in);

        do {
            text = scanner.nextLine();

            System.out.println(RatNum.evalExpr(text));
        } while(text.length() != 0);
    }
}