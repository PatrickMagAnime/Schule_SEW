import java.net.*;
import java.io.*;

public class RanServer {

    public static void main(String[] args) throws IOException {

        int port = 1234;
        ServerSocket server = new ServerSocket(port);
        System.out.println("Server läuft auf Port " + port + "...");

        while (true) {
            try {
                Socket client = server.accept();
                System.out.println("Client verbunden: " + client.getInetAddress());

                // Thread pro Client → stabil bei mehreren Verbindungen
                new Thread(() -> handleClient(client)).start();

            } catch (IOException e) {
                System.out.println("Fehler beim Accept: " + e.getMessage());
            }
        }
    }

    private static void handleClient(Socket client) {
        try (
                PrintWriter out = new PrintWriter(
                        client.getOutputStream(), true);
        ) {

            while (!client.isClosed()) {

                int zahl = (int) (Math.random() * 101);

                out.println(zahl); // Text senden
                out.flush();

                System.out.println("Zahl " + zahl + " an "
                        + client.getInetAddress() + " gesendet.");

                Thread.sleep(1000); // 1 Sekunde warten
            }

        } catch (Exception e) {
            System.out.println("Client getrennt: "
                    + client.getInetAddress());
        }
    }
}
