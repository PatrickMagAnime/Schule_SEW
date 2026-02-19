import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class RanClient extends JFrame implements ActionListener {

    private JTextField tfIP = new JTextField("localhost",10);
    private JTextField tfPort = new JTextField("1234",5);
    private JTextField tfOut = new JTextField(20);
    private JButton btnConnect = new JButton("Verbinden");
    private JButton btnRandom = new JButton("Zufall");

    private Socket socket;
    private BufferedReader in;

    public RanClient() {

        setTitle("Client");
        setSize(400,180);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        tfOut.setEditable(false);
        btnRandom.setEnabled(false);

        add(new JLabel("IP:")); add(tfIP);
        add(new JLabel("Port:")); add(tfPort);
        add(btnConnect); add(btnRandom);
        add(tfOut);

        btnConnect.addActionListener(this);
        btnRandom.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {

        if(e.getSource()==btnConnect) connect();
        if(e.getSource()==btnRandom) random();
    }

    private void connect() {

        String host = tfIP.getText();
        int port;

        try {
            port = Integer.parseInt(tfPort.getText());
        } catch(Exception ex) {
            tfOut.setText("Port ungültig!");
            return;
        }

        tfOut.setText("Verbinde...");

        new Thread(new Runnable() {
            public void run() {
                try {

                    // Alte Verbindung schließen (falls vorhanden)
                    if(socket != null && !socket.isClosed())
                        socket.close();

                    socket = new Socket();
                    socket.connect(new InetSocketAddress(host,port),3000);

                    in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            tfOut.setText("Verbunden mit " + host);
                            btnRandom.setEnabled(true);
                        }
                    });

                } catch(Exception ex) {

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            tfOut.setText("Verbindung fehlgeschlagen!");
                            btnRandom.setEnabled(false);
                        }
                    });
                }
            }
        }).start();
    }

    private void random() {

        if(socket == null || socket.isClosed()) {
            tfOut.setText("Nicht verbunden!");
            return;
        }

        new Thread(new Runnable() {
            public void run() {
                try {

                    socket.setSoTimeout(5000);
                    final String s = in.readLine();

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            tfOut.setText("Zahl: " + s);
                        }
                    });

                } catch(Exception ex) {

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            tfOut.setText("Server Fehler!");
                            btnRandom.setEnabled(false);
                        }
                    });
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        new RanClient().setVisible(true);
    }
}
