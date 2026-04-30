import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class StringClient {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        try (BufferedReader consoleReader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            System.out.print("String eingeben: ");
            String message = consoleReader.readLine();

            if (message == null) {
                message = "";
            }

            try (Socket socket = new Socket(HOST, PORT);
                 BufferedReader reader = new BufferedReader(
                         new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                 BufferedWriter writer = new BufferedWriter(
                         new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

                writer.write(message);
                writer.newLine();
                writer.flush();

                String response = reader.readLine();
                System.out.println("Antwort vom Server: " + response);
            }
        } catch (Exception exception) {
            System.out.println("Fehler im Client: " + exception.getMessage());
        }
    }
}