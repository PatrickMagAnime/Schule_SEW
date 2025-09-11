
package test;
//220227

import java.util.ArrayList;
import java.util.Scanner;

public class Test {
// methoden
    public static int menu(){
        Scanner sc = new Scanner (System.in);
        System.out.println("1. Schüler hinzufügen");
        System.out.println("2. Schüler anzeigen");
        System.out.println("3. Schüler suchen");
        System.out.println("4. Schüler entfernen");
        System.out.println("5. Proramm beenden");
        int x =sc.nextInt();
        return x;  
       }
    
    public static void sh(ArrayList<Schueler> list){
    Scanner sc = new Scanner (System.in);
        System.out.println("Name:");
        String name =sc.next();
        System.out.println("Alter:");
        String alter =sc.next();
        System.out.println("Kasse:");
        String klasse =sc.next();
        System.out.println("Nummer");
        String nummer =sc.next();
        Schueler schueler = new Schueler(name,alter,klasse,nummer);
        list.add(schueler);
    }
    
    public static void sa(ArrayList<Schueler> list){
    Scanner sc = new Scanner (System.in);
        System.out.println(list.toString());
    }
    
    public static void ss(ArrayList<Schueler> list){
    Scanner sc = new Scanner (System.in);
        System.out.println("Nach welchen Schüler wollen sie suchen?(Schülernnummer)");
        String num=sc.next();
        int a=0;
        for (int i = 0; i < list.size(); i++) {
            if (num.equals(list.get(i).getSchülernummer())) {
                a++;
                System.out.println("Der schüler konnte gefunden werden");
                System.out.println(list.get(i).toString());
                
            }if (a==0) {
                System.out.println("Der schüler konnte nicht gefunden werden");
            }
        }
    } 
    public static void se(ArrayList<Schueler> list){
    Scanner sc = new Scanner (System.in);
        System.out.println("welchen Schüler wollen sie löschen?(Schülernnummer)");
        String num=sc.next();
        int a=0;
        for (int i = 0; i < list.size(); i++) {
            if (num.equals(list.get(i).getSchülernummer())) {
                a++;
                list.remove(i);
                System.out.println("Der schüler wurde gelöscht");
            }if (a==0) {
                System.out.println("Der schüler konnte nicht gefunden werden");
            }
        }
    }
    public static void main(String[] args) {
        ArrayList<Schueler> list= new ArrayList <>();
        boolean t=true;
        while(t){
       int g = menu();
        switch(g){
            case 1 -> {
                sh(list);
            }
            case 2 -> {
                sa(list);
            }
            case 3 -> {
                ss(list);
            }
            case 4 -> {
                se(list);
            }
            case 5 -> {
                t=false;
            }
            default ->{
                    
                    }
        
        }
    }
    
}
}
