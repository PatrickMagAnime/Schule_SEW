import java.io.*;
import java.net.*;

public class ServerSocketDemo {

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(9000)) {
            System.out.println("Socket-Server: lausche auf Port 9000");

            while (true) {
                try {
                    Socket sock = server.accept();
                    System.out.println("Socket-Server: Verbindung aufgebaut von " + sock.getInetAddress());

                    // Streams anlegen
                    BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()), true);

                    String daten = in.readLine();
                    System.out.println("Socket-Server: Nachricht von Client: " + daten);

                    out.println("Hallo Client, Deine Nachricht kam an!");
                    //out.newLine();
                    //out.flush();

                    //sock.close();
                    //System.out.println("Socket-Server: Verbindung geschlossen, warte auf nächsten Client...");
                    //System.out.println("---------------------------------------------------");

                } catch (IOException e) {
                    System.out.println("Socket-Server: Fehler bei der Bearbeitung eines Clients!");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Socket-Server: Konnte Port 9000 nicht öffnen!");
            e.printStackTrace();
        }
    }
}