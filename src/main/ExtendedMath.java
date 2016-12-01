package main;

/**
 * Created by sebas on 2016-11-29.
 */
public abstract class ExtendedMath {

    //TODO: // FIXME: 2016-11-29
    //Brutalt stulen kod från internet ples fix
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
}
