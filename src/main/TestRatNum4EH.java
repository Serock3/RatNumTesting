/*
* This is TestRatNumEH.java
* EH 2011-12-04,  
* EH 2016 completely rewritten, including common code, 
* EH 2016 12 07 fixed problem with trailing spacec 
* output file used as input for automatic correction
*/
/*
Instructions for using: java TestRatNumEH -help    see help-flag below
=====================================================================
This is really a test program for the lab corrector, not students, but you may 
run it and perhaps awoid some simple errors in your code.
The code is not written for students too read and understand but you are welcome 
to try if you like, you should be able to understand most of it, at least at 
the end of the course.
*/
package main;
import java.util.Scanner;
public class TestRatNum4EH {
    // ########## ########## ########## ########## ########## ##########
    // Start of common code for taking care of arguments
    // ########## ########## ########## ########## ########## ##########
    //private static String lab = "Rse";
    //private static String lab = "RatNum";
    //private static String lab = "Secant";
    //private static String lab = "Counter";
    //private static String lab = "Goldbach";
    private static String lab = "RatNum";
    private static String prgm = "Test" + lab + "4EH: ";
    private static String meth = "";
    private static String version = " 2016.0 ";

    public  static long nbr = 0;	      // a counter for something
    // /nbr of recursive calls, used in Secant)
    private static int nbrOfErrors = 0;   // well, nbr of errors :-)
    private static boolean error = false; // record error existance
    private static int errNbr = 1;        // 1,2,4,8,16,...
    // System exit 1 == general error
    // System exit 2 == command line argument error
    private static int group = -1;        // group number that is being tested
    private static String test  = "";     // used for choosing test case

    // controlling printouts
    // v0 = none of the below, guru mode => print only existance or nbr of errors
    private static boolean verboseError = true; // v1, print only errors
    private static boolean verbose = false; // v2, long printouts, default, includes v1
    private static boolean debug = false;  // v3, for debugging only, implies v1 and v2

    // local
    private static boolean uman = false;  // no human testing of evalExpr
    // to turn off testing of individual tests, default = all on
    private static boolean testGcd = false;
    private static boolean testConst = false;
    private static boolean testEquals = true;
    private static boolean testLessThan = true;
    private static boolean testParse = true;
    private static boolean testToString = true;
    private static boolean testToDouble = true;
    private static boolean testClone = false;
    private static boolean testEvalExpr = true;
    // ########## ########## ########## ########## ##########
    public static void main(String[] args) {
        //public static void main(String[] args) throws IOException {

        //  general: take care of arguments
        int j = 0; // count arguments
        String oneArg = ""; // temporary storage for one argument
        int numberFlagValue = -1;

        // flags starting with "-"
        while  ( j < args.length && args[j].startsWith("-") )  {
            oneArg = args[j++]; // only shorter
            if ( oneArg.length() <= 1 ) {
                System.err.println(prgm + ": not '-' alone");
                System.exit(errNbr*=2); // #################### System exit 2
            }
            // for v and g, check if there is a number in x
            if (oneArg.charAt(1)=='v' || oneArg.charAt(1)=='g') {
                try {
                    numberFlagValue = Integer.parseInt(oneArg.substring(2));
                } catch ( NumberFormatException e ) {
                    System.err.println(prgm + ": number reqired for this flag: " + oneArg);
                    System.exit(errNbr); // #################### System exit 2
                }
            }
            switch ( oneArg.charAt(1) ) {
                case 'v': {
                    switch (numberFlagValue) {
                        case 0:
                            // only nbr of errors
                            verboseError = false;
                            verbose      = false;
                            debug        = false;
                            break;
                        case 1:
                            // only errors
                            verboseError = true;
                            verbose      = false;
                            debug        = false;
                            break;
                        case 2:
                            // default, print a lot
                            verboseError = true;
                            verbose      = true;
                            debug        = false;
                            break;
                        case 3:
                            // debug mode
                            verboseError = true;
                            verbose      = true;
                            debug        = true;
                            break;
                        default:
                            System.err.println(prgm + ": unknown number for this flag: " + oneArg);
                            System.exit(errNbr); // #################### System exit 2
                    } // end case numberFlagValue
                    break;
                } // end case v

                case 'g': { // not used yet, used in mass-testing
                    if (numberFlagValue<0 || numberFlagValue>200) { // trim this value
                        System.err.println(prgm + ": unknown labgroup number for this flag: " + oneArg);
                        System.exit(errNbr); // #################### System exit 2
                    }
                    group = numberFlagValue;
                    break;
                } // end case g

                case 't': { // used for choosing individual test case
                    if ( oneArg.length() <= 2 || oneArg.length() > 3 ) {
                        System.err.println(prgm + ": not '-t' alone or too many test cases");
                        System.exit(errNbr); // #################### System exit 2
                    }
                    String flag = oneArg.substring(2);
                    testGcd = false;
                    testConst = false;
                    testEquals = false;
                    testLessThan = false;
                    testParse = false;
                    testToString = false;
                    testToDouble = false;
                    testClone = false;
                    testEvalExpr = false;
                    switch ( flag.charAt(0) ) {
                        case 'g': testGcd = true; break;
                        case 'k': testConst = true; break;
                        case '=': testEquals = true; break;
                        case 'l': testLessThan = true; break;
                        case 'p': testParse = true; break;
                        case 's': testToString = true; break;
                        case 'd': testToDouble = true; break;
                        case 'c': testClone = true; break;
                        case 'e': testEvalExpr = true; break;
                        default: System.err.println(prgm + ": unknown flag: " + oneArg);
                            System.exit(errNbr); // ######### System exit 2
                            break;
                    }
                    break;
                } // end case t

                case 'p': { // used to turn off testing of indiv. tests
                    if ( oneArg.length() <= 2 ) {
                        System.err.println(prgm + ": need at least one of gk=lpsdce " + oneArg);
                        System.exit(errNbr); // #################### System exit 2
                    }
                    String flags = oneArg.substring(2);
                    for(int i=0; i<flags.length(); i++) {
                        switch ( flags.charAt(i) ) {
                            case 'g': testGcd = false; break;
                            case 'k': testConst = false; break;
                            case '=': testEquals = false; break;
                            case 'l': testLessThan = false; break;
                            case 'p': testParse = false; break;
                            case 's': testToString = false; break;
                            case 'd': testToDouble = false; break;
                            case 'c': testClone = false; break;
                            case 'e': testEvalExpr = false; break;
                            default: System.err.println(prgm + ": unknown flag: " + oneArg);
                                System.exit(errNbr); // ######### System exit 2
                        }
                    }
                    break;
                } // end case p

                case 'u': { // used to indicate human testing of evalExpr
                    if ( oneArg.length() != 2 ) {
                        System.err.println(prgm + ": unknown argument " + oneArg);
                        System.exit(errNbr); // #################### System exit 2
                    }
                    uman = true;
                    break;
                } // end case u

                case 'h': {
                    System.out.println();
                    System.out.println("===================================================================== ");
                    System.out.println("    Instructions for using: ");
                    System.out.println("    Your class must be named \"" + lab + "\" and be public ");
                    System.out.println("    The signature of the methods in that class ("+ meth + ") must be as in labPM. ");
                    System.out.println("    You put this testprogram and your class \"" + lab + "\" in the same folder. ");
                    System.out.println("    You compile and run with ( [yy] mean that yy is voluntary )");
                    System.out.println();
                    System.out.println("        javac Test" + lab + "4EH.java ");
                    System.out.println("        java Test" + lab + "4EH [-vx] [-h] [-tx] [-u] [-px] ");
                    System.out.println();
                    System.out.println("    v x=0 => guru mode => print only existance or nbr of errors	 ");
                    System.out.println("    v x=1 => print only errors and short \"passed\" msg after each test (default)");
                    System.out.println("    v x=2 => normal printout i.e. errors, success & what I am doing");
                    System.out.println("    v x=3 => debug mode ");
                    System.out.println("      v sometimes the v flag printout are equal regardless of the value of x");
                    System.out.println("    h help i.e. print this message");
                    System.out.println("    u for uman testing of evalExpr (i.e. manual input of data)");
                    System.out.println("      u flag sets -te flag");
                    System.out.println("    t to run an individual test case. X is the same as for -p");
                    System.out.println("    px to turn off testing of individual tests. x is a string consisting of one or more of");
                    System.out.println("       note that some tests are off by default, see below");
                    System.out.println("       g to exclude testing of gcd i.e. testprogram 1");
                    System.out.println("       k to exclude testing of constructors i.e. testprogram 2");
                    System.out.println("       = to exclude testing of equals");
                    System.out.println("       l to exclude testing of lessThan");
                    System.out.println("       p to exclude testing of parse");
                    System.out.println("       s to exclude testing of toString");
                    System.out.println("       d to exclude testing of toDouble");
                    System.out.println("       c to include testing of clone");
                    System.out.println("       e to exclude testing of evalExpr");
                    System.out.println("       everything except clone, gcd and constuctors is on by default");
                    System.out.println("===================================================================== ");
                    System.out.println("Examples: ");
                    System.out.println("java TestRatNum4EH -u // run a test of evalExpr with manual input of data");
                    System.out.println("java TestRatNum4EH -u < input.txt  // run a test of evalExpr with data in file");
                    System.out.println("                                   // you make the file");
                    System.out.println("java TestRatNum4EH -te  // run a test of evalExpr with internal data");
                    System.out.println("java TestRatNum4EH -tp  // run test of parse only");
                    System.out.println("java TestRatNum4EH -pecd  // exclude evalExpr, clone, toDouble from test ");
                    System.out.println("java TestRatNum4EH -tp -v2  // run test of parse with long output of result");
                    System.out.println("===================================================================== ");
                    System.out.println();
                    System.exit(0); // #################### System exit
                    break;
                } // end case h

                default:
                    System.err.println(prgm + ": unknown flag: " + oneArg);
                    System.exit(errNbr); // #################### System exit 2
            } // end switch oneArg
        } // end while
        if ( j < args.length ) {
            System.err.println(prgm + ": unknown argument: " + args[j]);
            System.exit(errNbr); // #################### System exit 2
        }
        if ( uman) {
            testGcd = false; testConst = false; testEquals = false; testLessThan = false;
            testParse = false; testToString = false; testToDouble = false; testClone = false;
            testEvalExpr = true;
        }
        //
        if (verbose) System.out.println("\n### Version:" + version + "#################################");
        if ( group !=-1 ) {
            if (verbose) System.out.println(" ########## group: " + group + " starts here ");
        }
        // ########## ########## ########## ########## ########## ##########
        // End of common code for taking care of arguments
        // ########## ########## ########## ########## ########## ##########
        // ########## ########## ########## ########## ########## ##########
        // ########## ########## ########## ########## ########## ##########
        // Test section

        // test of individual methods
        // test of gcd TODO
        //if (testGcd) testFunction(testOfGcd(), "gcd");
        if (testGcd) System.out.println("No test of gcd yet, run testprogram 1");
        if (testConst) RatNumTest2.divTester(); // test of constructors
        if (testConst) testFunction("ok", "RatNumTest2; test of constuctors");
        // all below @requires RatNum(j, i) works
        if (testEquals) testFunction(testOfequals(), "equals");

        if (testLessThan) testFunction(testOflessThan(), "lessThan");

        if (testParse) testFunction(testOfparse(), "parse"); // @ requires getNumerator(), getDenominator() works

        if (testToString) testFunction(testOftoString(), "toString");

        if (testToDouble) testFunction(testOftoDouble(), "toDouble");

        if (testClone) testFunction(testOfclone(), "clone"); // @requires x.equals(y) works

        // evalExpr, test of add, sub, mul, div
        if (testEvalExpr) testFunction(testOfevalExpr(), "evalExpr");
        System.out.println("Remember: tests can find errors but never guarantee that there are none left.");
    } // end main
    // ##########################################

    // ######### ######### ######### ######### ######### ######### #########
    // ######### ######### ######### ######### ######### ######### #########
    private static String equal(String a, String ans) {
        return a.equals(ans)?" ok":" ERROR";
    }
    private static String equal(String a, String b, String ans) {
        return a.equals(ans)||b.equals(ans)?" ok":" ERROR";
    }
    private static void testFunction(String str,  String test) {
        if( !str.equals("ok") ) {
            System.out.println("Error in "+ test + " <<<<<<<<<<<<<<<<<<<<");
            System.out.println(str);
            //System.out.println("aborting");
            //System.exit(0);
        } else {
            System.out.println(" >>> OK passed test of " + test);
        }
    }
    // ######### ######### ######### ######### ######### ######### #########
    // ######### ######### ######### ######### ######### ######### #########
    // ######### ######### ######### ######### ######### ######### #########
    // ######### ######### ######### ######### ######### ######### #########
    // different tests of ratnum methods
    // Test of gcd
	/* TODO 
	private static String testOfGcd() { 
		// @requires
		String msg = "ok"; 
		return msg;
	}
	
    private static int javaSTLgcdWorkaround (int a, int b) {
        //Cred: http://stackoverflow.com/questions/4009198/java-get-greatest-common-divisor
        BigInteger b1 = new BigInteger("" + a); // there's a better way to do this. I forget.
        BigInteger b2 = new BigInteger("" + b);
        BigInteger gcd = b1.gcd(b2);
        return gcd.intValue();
    }
	*/
    // ######### ######### ######### ######### ######### ######### #########
    // ######### ######### ######### ######### ######### ######### #########
    private static String testOfequals() {
        // @ requires RatNum(x,y) works
        RatNum x = new RatNum(6,2);
        RatNum y = new RatNum(0);
        RatNum z = new RatNum(0,1);
        RatNum w = new RatNum(75,25);
        Object v = new RatNum(75,25);
        String str = new String("TEST");

        //System.out.println("<<<< Start of equals test");
        //System.out.println("equals test 1 ");
        if ( x.equals(y) || !y.equals(z) || !x.equals(w) ) {
            return "RatNumTest4EH: ERROR 1 in testOfequals!!";
        }
        //System.out.println("equals test 2 ");
        if ( !w.equals(v)  ) { //  w skall vara lika med v
            // med equals(RatNum r) sÃ¥ vÃ¤ljs dock objects equals
            // eftersom parameterprofilen stÃ¤mmer dÃ¤r och dÃ¥ blir dom olika
            return "RatNumTest4EH: ERROR 2 in testOfequals!!";
        }
        //System.out.println("equals test 3 ");
        if ( !v.equals(w) ) { // dyn. bindningen ger RatNums equals
            // men med equals(RatNum r) sÃ¥ blir det som ovan
            return "RatNumTest4EH: ERROR 3 in testOfequals!!";
        }
        //System.out.println("equals test 4 ");
        try {
            if ( w.equals(null)  ) { //skall inte vara lika
                return "RatNumTest4EH: ERROR 4.1 in testOfequals!!";
            }
        } catch (NullPointerException e) { // men skall klara null
            return "RatNumTest4EH: ERROR 4.2 in testOfequals!!";
        }
        //System.out.println("equals test 5 ");
        if ( w.equals(str) ) { // skall ge false
            // med equals(RatNum r) fÃ¥r man Ã¥terigen Objects equals
            // och den ger rÃ¤tt svar hÃ¤r
            return "RatNumTest4EH: ERROR 5 in testOfequals!!";
        }
        //System.out.println("equals test 6 ");
        if ( w.equals(v) != v.equals(w)) { // skall ge samma svar dvs false
            // med equals(RatNum r) fÃ¥r man Ã¥terigen Objects equals
            return "RatNumTest4EH: ERROR 6 in testOfequals!!";
        }
        return "ok";
        //System.out.println("<<<< End of equals test");
    } // end testOfEquals
    // ######### ######### ######### ######### ######### ######### #########
    // ######### ######### ######### ######### ######### ######### #########

    // Test of lessThan
    private static String testOflessThan() {
        // @ requires RatNum(x,y) works
        RatNum a = new RatNum(3,5);
        RatNum b = new RatNum(4,5);
        if (verbose) { System.out.println("    testing lessThan comparing 3/5 < 4/5 "); }
        if ( !a.lessThan(b) || b.lessThan(a) ) {
            return "RatNumTest4EH: ERROR 1 in lessThan: cannot compare 3/5 and 4/5";
        }
        if (verbose) { System.out.println("    testing lessThan comparing 3/5 < 3/5 "); }
        if ( a.lessThan(a) ) {
            return "RatNumTest4EH: ERROR 2 in lessThan: cannot compare itself";
        }
        RatNum c = new RatNum(3, 10);
        RatNum d = new RatNum(1, 100000000); // mindre ger wraparound
        RatNum e = c.add(d);
        //System.out.println(c.toString());  // 3/10
        //System.out.println(d.toString());  // 1/100000000
        //System.out.println(e.toString());  // 30000001/100000000 = 0,30000001
        if (verbose) { System.out.println("    testing lessThan comparing 3/10 < 3/10 + 1/100000000 "); }
        if ( !c.lessThan(e) ) {
            return "RatNumTest4EH: ERROR 3 in lessThan: you probably ....";
        }
        return "ok";
    }
    // ######### ######### ######### ######### ######### ######### #########
    // ######### ######### ######### ######### ######### ######### #########

    // Test of parse
    private static String testOfparse() {
        // @ requires RatNum(x,y) works
        // @ requires getNumerator(), getDenominator() works
        String msg;
        RatNum rn;
        String rnStr;
        int num; int den;
        String ok;
        rnStr = "1/3";
        rn = RatNum.parse(rnStr); num = rn.getNumerator(); den = rn.getDenominator();
        msg = "testing parse(\"" + rnStr + "\"),    rÃ¤tt svar:  " + rnStr + " ditt svar= " + num + "/" + den;
        ok = equal(rnStr, num + "/" + den);  // " ok" eller " ERROR"
        msg = msg + ok;
        if (verbose) System.out.println("    " + msg);
        if( !ok.equals(" ok") ) { return msg; }

        rnStr = "3/1";
        rn = RatNum.parse(rnStr); num = rn.getNumerator(); den = rn.getDenominator();
        msg = "testing parse(\"" + rnStr + "\"),    rÃ¤tt svar:  " + rnStr + " ditt svar= " + num + "/" + den;
        ok = equal(rnStr, num + "/" + den);
        msg = msg + ok;
        if (verbose) System.out.println("    " + msg);
        if( !ok.equals(" ok") ) { return msg; }

        rnStr = "3";
        rn = RatNum.parse(rnStr); num = rn.getNumerator(); den = rn.getDenominator();
        msg = "testing parse(\"" + rnStr + "\"),      rÃ¤tt svar:  " + rnStr+"/1"  + " ditt svar= " + num + "/" + den;
        ok = equal(rnStr+"/1", num + "/" + den);
        msg = msg + ok;
        if (verbose) System.out.println("    " + msg);
        if( !ok.equals(" ok") ) { return msg; }

        rnStr = "4/5";
        rn = RatNum.parse(rnStr); num = rn.getNumerator(); den = rn.getDenominator();
        msg = "testing parse(\"" + rnStr + "\"),    rÃ¤tt svar:  " + rnStr + " ditt svar= " + num + "/" + den;
        ok = equal(rnStr, num + "/" + den);
        msg = msg + ok;
        if (verbose) System.out.println("    " + msg);
        if( !ok.equals(" ok") ) { return msg; }

        rnStr = "-4/5";
        rn = RatNum.parse(rnStr); num = rn.getNumerator(); den = rn.getDenominator();
        msg = "testing parse(\"" + rnStr + "\"),   rÃ¤tt svar:  " + "-4/5" + " ditt svar= " + num + "/" + den;
        ok = equal(rnStr, num + "/" + den);
        msg = msg + ok;
        if (verbose) System.out.println("    " + msg);
        if( !ok.equals(" ok") ) { return msg; }

        rnStr = "4/-5";
        rn = RatNum.parse(rnStr); num = rn.getNumerator(); den = rn.getDenominator();
        msg = "testing parse(\"" + rnStr + "\"),   rÃ¤tt svar:  " + "-4/5" + " ditt svar= " + num + "/" + den;
        ok = equal("-4/5", num + "/" + den);
        msg = msg + ok;
        if (verbose) System.out.println("    " + msg);
        if( !ok.equals(" ok") ) { return msg; }

        rnStr = "-4/-5";
        rn = RatNum.parse(rnStr); num = rn.getNumerator(); den = rn.getDenominator();
        msg = "testing parse(\"" + rnStr + "\"),  rÃ¤tt svar:  " + "4/5" + " ditt svar= " + num + "/" + den;
        ok = equal("4/5", num + "/" + den);
        msg = msg + ok;
        if (verbose) System.out.println("    " + msg);
        if( !ok.equals(" ok") ) { return msg; }

        try {
            rnStr = "5/";
            rn = RatNum.parse(rnStr); num = rn.getNumerator(); den = rn.getDenominator();
            // hit bÃ¶r vi inte komma
            msg = "testing parse(\"" + rnStr + "\"),     rÃ¤tt svar:  NumberFormatException  ditt svar= " + num + "/" + den;
            ok = " ERROR";
            msg = msg + ok;
            if (verbose) System.out.println("    " + msg);
            if( !ok.equals(" ok") ) { return msg; }

        } catch (NumberFormatException e) {
            msg = "testing parse(\"" + rnStr + "\"),     rÃ¤tt svar:  NumberFormatException  ditt svar=  NumberFormatException: " + e.getMessage();
            ok = " ok";
            msg = msg + ok;
            if (verbose) System.out.println("    " + msg);
            if( !ok.equals(" ok") ) { return msg; } // onÃ¶dig
        }

        try {
            rnStr = "/5";
            rn = RatNum.parse(rnStr); num = rn.getNumerator(); den = rn.getDenominator();
            // hit skall vi inte komma
            msg = "testing parse(\"" + rnStr + "\"),     rÃ¤tt svar:  NumberFormatException  ditt svar= " + num + "/" + den;
            ok = " ERROR";
            msg = msg + ok;
            if (verbose) System.out.println("    " + msg);
            if( !ok.equals(" ok") ) { return msg; }

        } catch (NumberFormatException e) {
            msg = "testing parse(\"" + rnStr + "\"),     rÃ¤tt svar:  NumberFormatException  ditt svar=  NumberFormatException: " + e.getMessage();
            ok = " ok";
            msg = msg + ok;
            if (verbose) System.out.println("    " + msg);
            if( !ok.equals(" ok") ) { return msg; } // obsolete
        }
        try {
            rnStr = "4 / 5";
            rn = RatNum.parse(rnStr); num = rn.getNumerator(); den = rn.getDenominator();
            msg = "testing parse(\"" + rnStr + "\"),     rÃ¤tt svar:  NumberFormatException  ditt svar= " + num + "/" + den;
            ok = " ERROR";
            msg = msg + ok;
            if (verbose) System.out.println("    " + msg);
            if( !ok.equals(" ok") ) { return msg; }

        } catch (NumberFormatException e) {
            msg = "testing parse(\"" + rnStr + "\"),  rÃ¤tt svar:  NumberFormatException  ditt svar=  NumberFormatException: " + e.getMessage();
            ok = " ok";
            msg = msg + ok;
            if (verbose) System.out.println("    " + msg);
            if( !ok.equals(" ok") ) { return msg; }
        }
        try {
            rnStr = "4//5";
            rn = RatNum.parse(rnStr); num = rn.getNumerator(); den = rn.getDenominator();
            msg = "testing parse(\"" + rnStr + "\"),   rÃ¤tt svar:  NumberFormatException  ditt svar= " + num + "/" + den;
            ok = " ERROR";
            msg = msg + ok;
            if (verbose) System.out.println("    " + msg);
            if( !ok.equals(" ok") ) { return msg; }

        } catch (NumberFormatException e) {
            msg = "testing parse(\"" + rnStr + "\"),   rÃ¤tt svar:  NumberFormatException  ditt svar=  NumberFormatException: " + e.getMessage();
            ok = " ok";
            msg = msg + ok;
            if (verbose) System.out.println("    " + msg);
            if( !ok.equals(" ok") ) { return msg; }
        }
        try {
            rnStr = "4/a";
            rn = RatNum.parse(rnStr); num = rn.getNumerator(); den = rn.getDenominator();
            msg = "testing parse(\"" + rnStr + "\"),    rÃ¤tt svar:  NumberFormatException  ditt svar= " + num + "/" + den;
            ok = " ERROR";
            msg = msg + ok;
            if (verbose) System.out.println("    " + msg);
            if( !ok.equals(" ok") ) { return msg; }

        } catch (NumberFormatException e) {
            msg = "testing parse(\"" + rnStr + "\"),    rÃ¤tt svar:  NumberFormatException  ditt svar=  NumberFormatException: " + e.getMessage();
            ok = " ok";
            msg = msg + ok;
            if (verbose) System.out.println("    " + msg);
            if( !ok.equals(" ok") ) { return msg; }
        }

        return "ok";
    }
    // ######### ######### ######### ######### ######### ######### #########
    // ######### ######### ######### ######### ######### ######### #########

    // Test of toString
    private static String testOftoString() {
        // @ requires RatNum(x,y) works
        RatNum rn = new RatNum(6,2);
        String str = rn.toString();
        if (verbose) { System.out.println("    testing toString(" +  rn + "), ditt svar " + str); }
        if (!"3/1".equals(str)) {return prgm + "ERROR 1 in testOftoString!!";}
        rn = new RatNum(-3,-2);
        str = rn.toString();
        if (verbose) { System.out.println("    testing toString(" +  rn + "), ditt svar " + str); }
        if (!"3/2".equals(str)) {return prgm + "ERROR 1 in testOftoString!!";}
        rn = new RatNum(3,-2);
        str = rn.toString();
        if (verbose) { System.out.println("    testing toString(" +  rn + "), ditt svar " + str); }
        if (!"-3/2".equals(str)) {return prgm + "ERROR 1 in testOftoString!!";}

        return "ok";
    }
    // ######### ######### ######### ######### ######### ######### #########
    // ######### ######### ######### ######### ######### ######### #########
    private static String testOftoDouble() {
        // @ requires RatNum(x,y) works
        for (int i=1; i<=9; i++) {
            if (verbose) { System.out.println("    testing toDouble " +  i + "/j 0<=j<=2*i"); }
            for (int j=0; j <= 2*i; j++) {
                if( Math.abs(new RatNum(j, i).toDouble() - (double)j/i) > 1.0e-10) {
                    return "TestRatNum4EH: ERROR in toDouble!! for " + j + "/" + i;
                }
            }
        }
        return "ok";
    } // end testOfToDouble

    // ######### ######### ######### ######### ######### ######### #########
    // ######### ######### ######### ######### ######### ######### #########
    private static String testOfclone() {
        // @requires RatNum(j, i) works
        // @requires x.equals(y) works
        // The general intent is that, for any object x, the expression:
        // 1) x.clone() != x will be true
        // 2) x.clone().getClass() == x.getClass() will be true, this is not an absolute requirement
        // 3) x.clone().equals(x) will be true, this is not an absolute requirement.
        try {
            if (verbose) { System.out.println("    testing clone: y = x.clone()");}
            if (verbose) { System.out.println("       y.equals(x) && y!=x && y.getClass() == x.getClass() + throws CloneNotSupportedException");}
            RatNum x = new RatNum(6,2);
            // om det inte finns en clone metod i RatNum klassen sÃ¥ kan inte fÃ¶ljande rad kompilera
            // utan ger error: clone() has protected access in Object
            //RatNum y = (RatNum) x.clone();
            // med fÃ¶ljande rad sÃ¥ kastas en exeption vid runtime istÃ¤llet
            RatNum y = (RatNum) x.getClass().getMethod("clone").invoke(x);
            if (!y.equals(x) || y==x ||  y.getClass() != x.getClass() ) {
                return "TestRatNum4EH: ERROR in clone!!";
            }
        }

        //catch (CloneNotSupportedException ce) {
        //	return "TestRatNum4EH: ERROR in clone, CloneNotSupportedException";
        //}
        catch ( NoSuchMethodException e) {
            testClone = false;
            return "No clone method found - clone test turned off";
        }

        catch (Exception e) {
            return "TestRatNum4EH: ERROR in clone, some exception " + e.getMessage();
        }
        return "ok";

    } // end testOfClone

    // ######### ######### ######### ######### ######### ######### #########
    // ######### ######### ######### ######### ######### ######### #########

    private static String testOfevalExpr() {
        // @ requires evalExpr works
        //StringBuilder stringToPrint;
        String correctAnswer1 = "";
        String correctAnswer2 = "";
        String rnToTest = null;
        Scanner in;
        boolean error = false; // no errors found yet
        //uman = true;
        //System.exit(0); // debug
        if (uman) { // human manual testing
            in = new Scanner(System.in);
            System.out.println("Manual input, only test of evalExpr");
            System.out.println("Skriv uttryck pÃ¥ formen a/b ? c/d, dÃ¤r ? Ã¤r nÃ¥got av tecknen + - * / = <");
            System.out.println("Du fÃ¥r sjÃ¤lv kontrollera att resultatet Ã¤r rÃ¤tt");
            System.out.println("Avsluta med ^D (control och D) direkt efter '>'");
            System.out.print("> ");
            while ( in.hasNext() ) {
                String s = in.nextLine();
                if ( s == null || s.length()==0 ) {
                    return "TestRatNum4EH: ERROR in testOfevalExpr, empty line, no input given";
                }
                //stringToPrint = new StringBuilder();
                String givenAnswer = RatNum.evalExpr(s);
                System.out.println("expression given is: " + s + "\t--> given answer is " + givenAnswer );
                //System.out.println(stringToPrint.toString());
                System.out.print("> ");
            } // end while
            return "ok";
        } else { // machine testing
            in = new Scanner(umanInput); // read from string given below
            if (verbose) { System.out.println("automatic testing of evalExpr - reading from internal data"); }
            if (verbose) { System.out.println("Result: \"which is ok\" is good, \"###### Error ######\" is bad"); }

            while ( in.hasNext() ) {
                String s = in.nextLine();
                if ( s == null || s.length()==0 ) {
                    return "TestRatNum4EH: ERROR in testOfevalExpr, empty line, error in internal data";
                }
                correctAnswer1 = "";
                correctAnswer2 = "";
                //stringToPrint = new StringBuilder();
                // split input in question and correct answer
                int i = s.indexOf("-->");
                int j = s.indexOf("&&");
                if (i<1) {
                    return "TestRatNum4EH: ERROR in testOfevalExpr, No answers found in file - cannot correct";
                }
                rnToTest = s.substring(0,i).trim();
                if (j>0) { // det finns tvÃ¥ svar, plocka ut dem
                    correctAnswer1 = s.substring(i+4, j).trim();
                    correctAnswer2 = s.substring(j+2).trim();
                } else { // ett svar
                    correctAnswer1 = s.substring(i+4).trim();
                }
                String givenAnswer = RatNum.evalExpr(rnToTest);
                if (verbose) {
                    System.out.println("------------------------------------------------------------------------");
                    System.out.print(   "   given expression  = " 	+ rnToTest
                            + "\n   --> given ans.    = " + givenAnswer
                            + "\n       correct answer= " + correctAnswer1);
                }
                if ( j>0 && verbose ) {System.out.print(" or " + correctAnswer2);}
                if (verbose) System.out.println();
                //if (verbose) System.out.print(" ## ");
                // given answer correct?
                if ( correctAnswer1.equals(givenAnswer) || correctAnswer2.equals(givenAnswer) ) {
                    if (verbose) System.out.println("which is ok");
                    // perhaps correct error?
                } else if ( correctAnswer1.indexOf("evalExpr") != -1
                        && givenAnswer.indexOf("evalExpr") != -1 ) {
                    // treat the error
                    String errorText = correctAnswer1.substring(0,17); // = "evalExpr error(x)"
                    char errorNbr = correctAnswer1.charAt(15);         // = x
                    char givenErrorNbr = givenAnswer.charAt(15);
                    //System.out.println("\n <<<<"+errorText+">>>>" + errorNbr + ">>");
                    if ( errorNbr == givenErrorNbr ) {
                        if (verbose) System.out.println("which is ok");
                    }

                } else {
                    if (verbose) System.out.println(" ###### Error ######" );
                    error = true;
                }
            } // end while
            if (verbose) System.out.println("------------------------------------------------------------------------");
            if (error) {return "See comment above. If no comments, use -v2 flag.";} else {return "ok";}
        } // end if uman
    } // end testOfevalExpr

    // ######### ######### ######### ######### ######### ######### #########
    // ######### ######### ######### ######### ######### ######### #########


    private static String umanInput =
            "1/3 + 1/4      --> 7/12"+ "\n" +
                    "2/9 * -4/5     --> -8/45"+ "\n" +
                    "2/6 - 7/9      --> -4/9"+ "\n" +
                    "7/-2 / -2/5    --> 35/4 && 8 3/4"+ "\n" +
                    "-5/10 + -3/4   --> -5/4 && -1 1/4"+ "\n" +
                    "-5/3 * 4       --> -20/3 && -6 2/3"+ "\n" +
                    "7/9 * 2        --> 14/9 && 1 5/9"+ "\n" +
                    "-5 * 1/3       --> -5/3 && -1 2/3"+ "\n" +
                    "2 / -5         --> -2/5"+ "\n" +
                    "2/5 = 40/100   --> true"+ "\n" +
                    "6/18 = -1/3    --> false"+ "\n" +
                    "2/9 < 1/5      --> false"+ "\n" +
                    "-5/9 < 1/2     --> true"+ "\n" +
                    "1/2 +1/3       --> evalExpr error(1): Felaktigt uttryck!"+ "\n" +
                    "1/5            --> evalExpr error(1): Felaktigt uttryck!"+ "\n" +
                    "/4 + 1/3       --> evalExpr error(4): NumberFormatException: For input string: "+ "\n" +
                    "5/ + 1/3       --> evalExpr error(4): NumberFormatException: For input string: "+ "\n" +
                    "1//4 + 1/4     --> evalExpr error(4): NumberFormatException: For input string: '/4'"+ "\n" +
                    "1/ - 2 + 1/3   --> evalExpr error(1): Felaktigt uttryck!"+ "\n" +
                    "1/3 a + 1/3    --> evalExpr error(1): Felaktigt uttryck!"+ "\n" +
                    "-/3 + 1/3	    --> evalExpr error(4): NumberFormatException: For input string: '-\'"+ "\n" +
                    "1/3 + 1/3 + 1/3 --> evalExpr error(1): Felaktigt uttryck!"+ "\n" +
                    "1/3 & 1/3      --> evalExpr error(2): Felaktig operator!"+ "\n" +
                    "1/0 + 1/3      --> evalExpr error(4): NumberFormatException: Denominator = 0"+ "\n" +
                    "1 / 0          --> evalExpr error(4): NumberFormatException: Denominator = 0"
            ;

} // end TestRatNum4EH