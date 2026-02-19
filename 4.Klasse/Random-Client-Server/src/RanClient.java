import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class RanClient extends JFrame {
    private JTextField tfAdresse, tfPort, tfResultat;
    private JButton btnVerbinden, btnZufall;

    public RanClient() {
        setTitle("Zufalls Client");
        setSize(450, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Oberer Bereich
        JPanel panelTop = new JPanel();
        tfAdresse = new JTextField("localhost", 10);
        tfPort = new JTextField("1234", 5);
        btnVerbinden = new JButton("Verbinden");

        panelTop.add(new JLabel("IP:"));
        panelTop.add(tfAdresse);
        panelTop.add(new JLabel("Port:"));
        panelTop.add(tfPort);
        panelTop.add(btnVerbinden);

        // Mittlerer Bereich
        JPanel panelCenter = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;

        btnZufall = new JButton("Zufall anfordern");
        tfResultat = new JTextField(25);
        tfResultat.setEditable(false);
        tfResultat.setHorizontalAlignment(JTextField.CENTER);

        gbc.gridy = 0;
        panelCenter.add(btnZufall, gbc);
        gbc.gridy = 1;
        panelCenter.add(tfResultat, gbc);

        add(panelTop, BorderLayout.NORTH);
        add(panelCenter, BorderLayout.CENTER);

        // LOGIK VERBINDEN
        btnVerbinden.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String host = tfAdresse.getText();
                    int port = Integer.parseInt(tfPort.getText());

                    // Test-Verbindung um Erreichbarkeit zu prüfen
                    Socket testSocket = new Socket();
                    // Timeout nach 2 Sekunden, falls Server nicht reagiert
                    testSocket.connect(new InetSocketAddress(host, port), 2000);
                    testSocket.close();

                    tfResultat.setText("Erfolgreich verbunden mit " + host);
                    tfResultat.setForeground(new Color(0, 150, 0)); // Grün bei Erfolg
                } catch (UnknownHostException ex) {
                    tfResultat.setText("Fehler: Host nicht gefunden!");
                    tfResultat.setForeground(Color.RED);
                } catch (ConnectException ex) {
                    tfResultat.setText("Fehler: Server nicht erreichbar!");
                    tfResultat.setForeground(Color.RED);
                } catch (Exception ex) {
                    tfResultat.setText("Fehler: " + ex.getMessage());
                    tfResultat.setForeground(Color.RED);
                }
            }
        });

        // LOGIK ZUFALL
        btnZufall.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String host = tfAdresse.getText();
                    int port = Integer.parseInt(tfPort.getText());

                    Socket s = new Socket(host, port);
                    int zahl = s.getInputStream().read();

                    tfResultat.setText("Server Zufallszahl: " + zahl);
                    tfResultat.setForeground(Color.BLACK);
                    s.close();
                } catch (Exception ex) {
                    tfResultat.setText("Zufall fehlgeschlagen: " + ex.getMessage());
                    tfResultat.setForeground(Color.RED);
                }
            }
        });
    }

    public static void main(String[] args) {
        new RanClient().setVisible(true);
    }
}
