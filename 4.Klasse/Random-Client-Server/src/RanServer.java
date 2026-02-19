import java.net.*;
import java.io.*;

public class RanServer {
    public static void main(String[] args) throws IOException {
        int port = 1234;
        ServerSocket server = new ServerSocket(port);
        System.out.println("Server läuft auf Port " + port + "...");

        while (true) {
            try (Socket client = server.accept()) {
                // Zufallszahl 0 bis 100
                int zahl = (int) (Math.random() * 101);
                client.getOutputStream().write(zahl);
                System.out.println("Zahl " + zahl + " an " + client.getInetAddress() + " gesendet.");
            } catch (IOException e) {
                System.out.println("Fehler: " + e.getMessage());
            }
        }
    }
}
