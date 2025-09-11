package vor.nachnamen;

import java.util.Scanner;
//220227

public class VorNachnamen {
 
    public static String ausgabe(String[] vornamen, String[] nachnamen, int anzahl) {
        // gibt einen formatierten Text mit allen Vor-und Nachnamen zurück
        for (int i = 0; i < anzahl; i++) {
            System.out.println(i + ". Eintrag");
            System.out.println("Vornamen:" + vornamen[i]);
            System.out.println("Nachnamen:" + nachnamen[i]);
            System.out.println();
        }

        return null;

    }

    public static String suche(String[] vornamen, String[] nachnamen, String nachname, Scanner sc) {
        // gibt einen formatierten Text mit allen Vor- und (gleichen) Nachnamen zurück
        // ist die Suche erfolglos, dann wird ein leerer Text zurückgegeben
        System.out.println("Nach welchen Vornamen wollen sie suchen?");
        String suchen = sc.next();
        int r = 0;
        for (int i = 0; i < vornamen.length; i++) {
            if (suchen.equals(vornamen[i])) {
                r++;
            }
        }
        System.out.println("Der Vorname wurde " + r + "mal gefunden.");
        System.out.println("Nach welchen Nachnamen wollen sie suchen?");
        String suchen2 = sc.next();
        int rr = 0;
        for (int i = 0; i < nachnamen.length; i++) {
            if (suchen2.equals(nachnamen[i])) {
                rr++;
            }
        }
        System.out.println("Der Nachname wurde " + rr + "mal gefunden.");

        return null;

    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String[] vornamen = new String[100];
        String[] nachnamen = new String[100];
        int x = 0;
        System.out.println("Mit ??? abrechen");
        for (int i = 0; i < vornamen.length; i++) {
            System.out.println("Geben sie den vornamen ein:");
            vornamen[i] = sc.next();
            System.out.println("Geben sie den nachnamen ein:");
            nachnamen[i] = sc.next();

            if (nachnamen[i].equals("???")) {
                break;
            }
            x++;
        }

        String namen = ausgabe(vornamen, nachnamen, x);
        System.out.println(namen);

        String g;
        System.out.println("Wollen sie nach namen suchen? ja/nein");
        g = sc.next();
        if (g.equals("ja")) {
            suche(vornamen, nachnamen, namen, sc);

        }

    }

}
