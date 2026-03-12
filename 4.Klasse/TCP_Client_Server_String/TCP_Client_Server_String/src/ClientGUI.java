import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ClientGUI extends JFrame {
    private JTextField ipField, portField, messageField;
    private JLabel statusLabel, responseLabel;
    private JButton connectBtn, sendBtn;
    int f = 0;

    public ClientGUI() {
        setTitle("Java Socket Client");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(7, 1, 5, 5));

        //IP & Port Eingabe
        JPanel topPanel = new JPanel(new FlowLayout());
        ipField = new JTextField("localhost", 10);
        portField = new JTextField("9000", 5);
        topPanel.add(new JLabel("IP:"));
        topPanel.add(ipField);
        topPanel.add(new JLabel("Port:"));
        topPanel.add(portField);
        add(topPanel);

        //Verbinden Button
        connectBtn = new JButton("Verbinden & Testen");
        add(connectBtn);

        //Status Anzeige
        statusLabel = new JLabel("Status: Nicht verbunden", SwingConstants.CENTER);
        add(statusLabel);

        //Sende Feld
        messageField = new JTextField("Hallo Server!");
        add(new JLabel(" Nachricht:", SwingConstants.LEFT));
        add(messageField);

        //Sende Button
        sendBtn = new JButton("Nachricht senden");
        add(sendBtn);

        //Server Antwort
        responseLabel = new JLabel("Antwort: -", SwingConstants.CENTER);
        add(responseLabel);

        //Logik f�r den Sende-Button
        sendBtn.addActionListener(e -> sendMessage());

        //Logik f�r den Verbinden-Button (Checkt nur Erreichbarkeit)
        connectBtn.addActionListener(e -> checkConnection());

        setVisible(true);
    }

    private void checkConnection() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ipField.getText(), Integer.parseInt(portField.getText())), 2000);
            statusLabel.setText("Status: Server erreichbar!");
        } catch (Exception ex) {
            statusLabel.setText("Status: Verbindung fehlgeschlagen");
        }
    }

    private void sendMessage() {
        f++;
        String ip = ipField.getText();
        int port = Integer.parseInt(portField.getText());
        String message = messageField.getText();

        //Da dein Server nach dem Senden direkt schlie�t,
        //m�ssen wir f�r jede Nachricht eine neue Verbindung aufbauen.
        try (Socket socket = new Socket(ip, port);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            //Nachricht senden
            out.println(message);
            //out.newLine(); //Wichtig für readLine() auf Server-Seite
            //out.flush();
            socket.shutdownOutput(); //Signalisiert dem Server: "Ich bin fertig mit Senden"

            //Antwort lesen
            String response = in.readLine();
            responseLabel.setText("Antwort: " + response);
            statusLabel.setText("Status: Gesendet & Empfangen ("+f+")");

        } catch (IOException ex) {
            responseLabel.setText("Fehler: " + ex.getMessage());
            statusLabel.setText("Status: Fehler beim Senden");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientGUI::new);
    }
}