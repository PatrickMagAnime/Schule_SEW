package oop_beispiel;

import java.util.Scanner;
//220227 

public class claasssclass {

    public static void main(String[] args) {
        Auto a1 = new Auto();   // Objekt wird erstellt
        a1.setMarke("Diesel Tesla");
        a1.setModell("Diesel E-Truck");
        a1.setBaujahr(2026);
        a1.setKennzeichen("G YAT RIZZ69");
        a1.setGeschwindigkeit(0);

        Scanner yato = new Scanner(System.in);
        int x = 0;
        while (x < 0 || x > 3) {

        }
        System.out.println("In diesem Programm fährst du ein Auto");
        System.out.println("Aktueller Status:");
        String txt = a1.autoDaten();
        System.out.println(txt);
        boolean a = true;
        while (a == true) {
            System.out.println("Wähle aus:");
            System.out.println("Drücke 1 Für beschleunigen");
            System.out.println("Drücke 2 für Bremsen");
            System.out.println("Drücke 3 für Aktuelle Geschwindigkeit");
            System.out.println("Drücke 4 für die aktuellen Infos");
            System.out.println("5 Beenden");
            x = yato.nextInt();
            if (x == 1) {
                int g;
                System.out.println("Um wie viel km/h möchten sie beschleunigen?:");
                g = yato.nextInt();
                while (g < 1) {
                    System.out.println("Fehler! Erneut eingeben");
                    g = yato.nextInt();
                }
                a1.getGeschwindigkeit(g);

            }

            if (x == 2) {
                int y;
                System.out.println("Um wie viel km/h möchten sie abbremsen?:");
                y = yato.nextInt();
                while (y < 1) {
                    System.out.println("Fehler! Erneut eingeben");
                    y = yato.nextInt();
                }
                y = y * (-1);
                a1.getGeschwindigkeit(y);

            }

            if (x == 3) {
                System.out.println("Das auto fährt Aktuell: " + a1.getGeschwindigkeit(0) + " Km/h.");
            }

            if (x == 4) {
                txt = a1.autoDaten();
                System.out.println();
                System.out.println(txt);

            }

            if (x == 5) {
                System.out.println("Das Programm wurde Beendet");
                a = false;
            }

        }

        //System.out.println("Marke: "+a1.getMarke());
        //System.out.println("Modell: "+a1.getModell());
        //System.out.println("Modell: "+a1.getBaujahr());
        //System.out.println("Kennzeichen: "+a1.getKennzeichen());
        //System.out.println("");
    }

}
