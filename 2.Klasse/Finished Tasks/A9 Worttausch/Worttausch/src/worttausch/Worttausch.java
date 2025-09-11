package worttausch;

import java.util.Arrays;
import java.util.Scanner;
//220227

public class Worttausch {

    public static void einlesen(String array[], Scanner sc) {

        for (int i = 0; i < array.length; i++) {
            System.out.println("Geben sie ihre wörter ein. Mit ??? abrechen");
            array[i] = sc.next();
            if (array[i].equals("???")) {
                break;
            }
        }
    }

    public static String worttausch(String array[], String array2[], Scanner sc) {
 
        int k = array.length - 1;
        for (int i = 0; i < array2.length; i++) {

            array2[i] = array[k];

            k--;

        }
        return Arrays.toString(array2);
    }

    public static void main(String[] args) {
        String[] array = new String[10];
        Scanner sc = new Scanner(System.in);
        einlesen(array, sc);
        String[] array2 = new String[array.length];
        String gw = worttausch(array, array2, sc);
        System.out.println(gw);
        String g;
        System.out.println("Wollen sie das programm wiederholen? ja/nein");
        g=sc.next();
        if(g.equals("ja")){
            main(args);
        }
    }
}
