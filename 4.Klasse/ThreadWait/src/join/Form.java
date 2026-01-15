package join;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Form {
    private JPanel form;
    private JButton laufschriftStartenButton;
    private JTextField eingabeTextField;
    private JTextArea textArea1;
    private Timer timer;
    private String fullText = "";
    private int pos = 0;

    public Form() {

        //button startet / stoppt die schrift
        laufschriftStartenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleLaufschrift();
            }
        });

        //textArea die sich bewegt soll nur anzeige sein
        textArea1.setEditable(false);
    }

    private void toggleLaufschrift() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
            laufschriftStartenButton.setText("Laufschrift Starten");
            if (textArea1 != null) textArea1.setText(fullText);
            return;
        }

        String input = eingabeTextField != null ? eingabeTextField.getText() : "";
        if (input == null || input.isEmpty()) return;

        fullText = input + "         "; //abstand zum Wiederholen
        pos = 0;

        final int window = 75; //wie viele Zeichen ANGEzeigt werden

        //geschwindigkeit kann man ändern hier
        timer = new Timer(15, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (textArea1 != null) textArea1.setText(getDisplay(window));
            }
        });
        timer.start();
        //start knopf wird zum stop knopf gemacht
        laufschriftStartenButton.setText("Stop");
    }

    private String getDisplay(int window) {
        String s = fullText;
        if (s.length() < window) {
            StringBuilder sb = new StringBuilder(s);
            while (sb.length() < window) sb.append(' ');
            s = sb.toString();
        }
        String doubled = s + s;
        if (pos >= s.length()) pos = 0;
        String sub = doubled.substring(pos, pos + window);
        pos = (pos + 1) % s.length();
        return sub;
    }

    //copy paste wie in jeder übung mit gui
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Laufschrift");
                frame.setContentPane(new Form().form);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
