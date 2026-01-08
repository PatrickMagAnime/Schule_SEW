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
        //falls die GUI mit dem Form Designer nicht initialisiert wurde
        //erstelle einfache Komponenten programmgesteuert oder so
        if (form == null) {
            form = new JPanel(new java.awt.BorderLayout(5, 5));
        }
        if (eingabeTextField == null) {
            eingabeTextField = new JTextField(20);
        }
        if (textArea1 == null) {
            textArea1 = new JTextArea(5, 30);
        }
        if (laufschriftStartenButton == null) {
            laufschriftStartenButton = new JButton("Start");
        }

        //nur hinzufügen wenn noch nicht vom Form Designer eingebunden
        if (laufschriftStartenButton.getParent() == null) {
            JPanel top = new JPanel();
            top.add(eingabeTextField);
            top.add(laufschriftStartenButton);
            form.add(top, java.awt.BorderLayout.NORTH);
        }
        if (textArea1.getParent() == null) {
            form.add(new JScrollPane(textArea1), java.awt.BorderLayout.CENTER);
        }

        //button startet / stoppt die schrift
        laufschriftStartenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleLaufschrift();
            }
        });

        //textArea soll nur anzeige sein
        textArea1.setEditable(false);
    }

    private void toggleLaufschrift() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
            laufschriftStartenButton.setText("Start");
            if (textArea1 != null) textArea1.setText(fullText);
            return;
        }

        String input = eingabeTextField != null ? eingabeTextField.getText() : "";
        if (input == null || input.isEmpty()) return;

        fullText = input + "         "; //abstand zum Wiederholen
        pos = 0;

        final int window = 75; //wie viele Zeichen angezeigt werden

        timer = new Timer(150, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (textArea1 != null) textArea1.setText(getDisplay(window));
            }
        });
        timer.start();
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
