

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class DatagramClient  {
   public static void main(String[] args) {
      try {
         Scanner sc = new Scanner(System.in);
         DatagramSocket sock = new DatagramSocket();
 
         
         InetAddress server_IP = InetAddress.getByName(null);
         int server_port = 9000;


         System.out.println("Gib eine Zahl ein (Server antwortet ob es eine Primzahl ist): ");
         String nachricht = sc.next();
         byte[] puffer = nachricht.getBytes();
 
         DatagramPacket paket = new DatagramPacket(puffer,puffer.length,
                                                   server_IP, server_port); 
 
         sock.send(paket); 
 
         
         puffer = new byte[256];  
                               
         DatagramPacket paket2 = new DatagramPacket(puffer, puffer.length); 
         sock.receive(paket2);
 
         String antwort = new String(paket2.getData());
 
         System.out.println("Server: " + antwort);
 
         
         sock.close();

          System.out.println("wiederhohlen? y/n");
          String g = sc.next();
          if (g.equals("y")){
              main(args);
          }
 
      } catch(IOException e) {
         System.out.println("Client: IO Fehler");
         e.printStackTrace(); 
      }
   }
}
