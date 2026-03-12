import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ChatClient {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton connectButton;
    private JComboBox<String> themeBox;

    private JTextField ipField;
    private JTextField portField;
    private JTextField nameField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatClient::new);
    }

    public ChatClient() {
        frame = new JFrame("Chat Client");
        frame.setSize(600, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel top = new JPanel(new GridLayout(2, 4, 5, 5));
        ipField = new JTextField("localhost");
        portField = new JTextField("55555");
        nameField = new JTextField("User" + (int)(Math.random() * 1000));

        connectButton = new JButton("Connect");
        themeBox = new JComboBox<>(new String[]{"Light", "Dark"});

        top.add(new JLabel("IP:"));
        top.add(ipField);
        top.add(new JLabel("Port:"));
        top.add(portField);
        top.add(new JLabel("Username:"));
        top.add(nameField);
        top.add(connectButton);
        top.add(themeBox);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(chatArea);

        JPanel bottom = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Send");
        sendButton.setEnabled(false);

        bottom.add(messageField, BorderLayout.CENTER);
        bottom.add(sendButton, BorderLayout.EAST);

        frame.add(top, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);

        connectButton.addActionListener(e -> connect());
        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());
        themeBox.addActionListener(e -> applyTheme());

        applyTheme();
        frame.setVisible(true);
    }

    private void connect() {
        try {
            socket = new Socket(ipField.getText(), Integer.parseInt(portField.getText()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(nameField.getText()); // send username

            sendButton.setEnabled(true);
            connectButton.setEnabled(false);

            new Thread(this::listen).start();
            chatArea.append("Connected to server.\n");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Connection failed: " + e.getMessage());
        }
    }

    private void listen() {
        try {
            String msg;
            while ((msg = in.readLine()) != null) {
                chatArea.append(msg + "\n");
            }
        } catch (IOException e) {
            chatArea.append("Disconnected from server.\n");
        }
    }

    private void sendMessage() {
        String msg = messageField.getText().trim();
        if (!msg.isEmpty()) {
            out.println(msg);
            messageField.setText("");
        }
    }

    private void applyTheme() {
        boolean dark = themeBox.getSelectedItem().equals("Dark");

        Color bg = dark ? new Color(30, 30, 30) : Color.WHITE;
        Color fg = dark ? Color.WHITE : Color.BLACK;

        chatArea.setBackground(bg);
        chatArea.setForeground(fg);
        messageField.setBackground(bg);
        messageField.setForeground(fg);
    }
}