import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewJFrame {
    private JButton start_Button;
    private JButton stopp_Button;
    private JTextField textField;
    private JPanel field;
    Counter c;

    public NewJFrame() {

        start_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
            c=new Counter(textField);
            c.start();
            }
        });

        stopp_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
            c.interrupt();
            }
        });

    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Button Race"); // Fenster erstellen
        f.setContentPane(new NewJFrame().field); // GUI-Panel einfügen
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Fenster schließen
        f.pack(); // Fenstergröße an Inhalt anpassen
        f.setVisible(true); // Fenster anzeigen
    }
}
