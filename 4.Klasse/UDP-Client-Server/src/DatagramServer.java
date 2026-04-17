

import java.io.*;
import java.net.*;
import java.sql.SQLOutput;

import static java.lang.Integer.parseInt;

public class DatagramServer {
   public static void main(String[] args) {
      try  {
         DatagramSocket sock = new DatagramSocket(9000);
         while(true) {


             int laenge = 100;
             byte[] puffer = new byte[laenge];
             DatagramPacket paket = new DatagramPacket(puffer, laenge);


             System.out.println("Server: warte auf Anfrage ");
             sock.receive(paket);


             String nachricht = new String(paket.getData(), 0, paket.getLength()).trim();

             System.out.println("Server: empfangen wurde: " + nachricht);
             boolean p = isPrim(parseInt(nachricht));


             InetAddress client_IP = paket.getAddress();
             int client_port = paket.getPort();
String z;

if(p==true){
    z= nachricht+" ist Primzahl";
}else{
    z= nachricht+" ist keine Primzahl!";
}



             String antwort = z;
             puffer = antwort.getBytes();

             DatagramPacket paket2 = new DatagramPacket(
                     puffer, puffer.length, client_IP, client_port);
             sock.send(paket2);
         }
      } catch(SocketException e)  {
         System.out.println("Server: Socket kann nicht angelegt werden");
         e.printStackTrace(); 
      } catch(IOException e) {
         System.out.println("Server: I/O Fehler ");
         e.printStackTrace(); 
      }
   }
    public static boolean isPrim(int value) {

        if (value <= 2) {
            return (value == 2);
        }
        for (long i = 2; i * i <= value; i++) {
            if (value % i == 0) {
                return false;
            }
        }
        return true;
    }
}
