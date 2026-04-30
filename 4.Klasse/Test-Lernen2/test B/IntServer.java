import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class IntServer {
    private static final int PORT = 5000;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT, 50, InetAddress.getLoopbackAddress())) {
            System.out.println("Server läuft auf localhost:" + PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader reader = new BufferedReader(
                             new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                     BufferedWriter writer = new BufferedWriter(
                             new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8))) {

                    String request = reader.readLine();
                    if (request == null) {
                        continue;
                    }

                    int number = Integer.parseInt(request.trim());
                    writer.write(Integer.toString(number + 1));
                    writer.newLine();
                    writer.flush();
                } catch (Exception exception) {
                    System.out.println("Fehler im Server: " + exception.getMessage());
                }
            }
        } catch (Exception exception) {
            System.out.println("Fehler beim Starten des Servers: " + exception.getMessage());
        }
    }
}