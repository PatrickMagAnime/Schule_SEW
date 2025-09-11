package arraymethoden;
import java.util.Scanner;
public class Arraymethoden {

    public static void main(String[] args) {
Scanner sc = new Scanner(System.in);
        
        double [] arr = {2.5,3.5,2.5,5.4,4.6};
        
        einlesen(sc,arr);
        
        int anzahl = suchen(arr,2.5);
        System.out.println(anzahl);
        
       
        
         
        
    }

    public static int suchen(double[] liste, double wert) {
        int a = 0;
        for (int i = 0; i < liste.length; i++) {
            if (liste[i] == wert) {
                a++;
            }
        }
        return a;
    }
public static int einlesen(Scanner sc,double []arr){
    int anzahl=0;
    for (int i = 0; i < arr.length; i++) {
        System.out.println("Geben sie die "+(i+1)+". Zahl ein:");
        arr[i]=sc.nextInt();
        anzahl++;
    }
    
    return anzahl;
}
}
