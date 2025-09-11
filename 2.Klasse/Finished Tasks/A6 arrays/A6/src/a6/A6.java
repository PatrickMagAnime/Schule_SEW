package a6;

import java.util.Scanner;
//220227

public class A6 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String[] liste = new String[120];
        int wC = read(liste, scanner);

        System.out.println("Bitte wählen Sie aus:");
        System.out.println("(a) Anzahl der Worte anzeigen");
        System.out.println("(b) Text als Ganzes anzeigen");
        System.out.println("(c) Text nach bestimmten Worten durchsuchen");
        System.out.println("(d) Text in umgekehrter Reihenfolge anzeigen");

        char g = scanner.next().charAt(0);

        switch (g) {
            case 'a':
                System.out.println("Anzahl der Worte: " + wC);
                break;
            case 'b':
                String verkt = concat(liste);
                System.out.println("Gesamter Text: " + verkt);
                break;
            case 'c':
                System.out.println("Bitte geben Sie das zu suchende Wort ein:");
                String sW = scanner.next();
                int s = search(liste, sW);
                System.out.println("Anzahl der Treffer: " + s);
                break;
            case 'd':
                reverse(liste);
                System.out.println("Text in umgekehrter Reihenfolge:");
                for (String word : liste) {
                    if (word.equals("?????")) {
                        break;
                    }
                    System.out.print(word + " ");
                }
                break;
            default:
                System.out.println("Ungültige Auswahl");
        }

    }

    static int read(String[] list, Scanner sc) {
        int wC = 0;
        System.out.println("Bitte geben Sie den Text Wort für Wort ein (????? zum Beenden):");
        for (int i = 0; i < list.length; i++) {
            list[i] = sc.next();
            wC++;
            if (list[i].equals("?????")) {
                break;
            }
        }
        return wC;
    }

    static String concat(String[] list) {
        StringBuilder result = new StringBuilder();
        for (String word : list) {
            if (word.equals("?????")) {
                break;
            }
            result.append(word).append(" ");
        }
        return result.toString();
    }

    static void reverse(String[] list) {
        int i = 0;
        int j = list.length - 1;
        while (i < j) {
            if (list[i] != null && list[j] != null) {
                String temp = list[i];
                list[i] = list[j];
                list[j] = temp;
            } //methode funktioniert nicht :(  <-------------------
            i++;
            j--;
        }
    }

    static int search(String[] list, String st) {
        int x = 0;
        for (String word : list) {
            if (word.equals(st)) {
                x++;
            }
            if (word.equals("?????")) {
                break;
            }
        }
        return x;
    }
}
