import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Random;

public class NewJFrame {
    private JButton testStartenButton;
    private JPanel panel;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JLabel JLabel_Reaktionszeit;
    private final Random random = new Random();
    private Reaktion r;
    private int activeButton = -1;
    private Color defaultBtnColor;

    public NewJFrame() {
        if (button1 != null) {
            defaultBtnColor = button1.getBackground();
        } else {
            defaultBtnColor = UIManager.getColor("Button.background");
        }

        if (testStartenButton != null) {
            testStartenButton.addActionListener(e -> startRound());
        }

        ActionListener btnListener = e -> {
            JButton src = (JButton) e.getSource();
            int index = src == button1 ? 1 : src == button2 ? 2 : 3;
            handleButtonClick(index);
        };

        if (button1 != null) button1.addActionListener(btnListener);
        if (button2 != null) button2.addActionListener(btnListener);
        if (button3 != null) button3.addActionListener(btnListener);
    }

    private void startRound() {
        if (testStartenButton != null) testStartenButton.setEnabled(false);
        resetButtons();
        if (JLabel_Reaktionszeit != null) JLabel_Reaktionszeit.setText("warten");

        int waitMs = random.nextInt(4000) + 1;

        Timer t = new Timer(waitMs, e -> {
            ((Timer) e.getSource()).stop();
            colorRandomButton();
        });
        t.setRepeats(false);
        t.start();
    }

    private void colorRandomButton() {
        activeButton = random.nextInt(3) + 1;
        JButton b = (activeButton == 1) ? button1 : (activeButton == 2) ? button2 : button3;

        if (b != null) {
            b.setBackground(Color.RED);
            b.setOpaque(true);
            b.setBorderPainted(true);
        }

        r = new Reaktion(JLabel_Reaktionszeit);
        r.start();

        if (JLabel_Reaktionszeit != null) JLabel_Reaktionszeit.setText("klick den roten button");
    }

    private void handleButtonClick(int clicked) {
        if (activeButton == -1) {
            if (JLabel_Reaktionszeit != null) JLabel_Reaktionszeit.setText("drücke start");
            return;
        }

        if (clicked == activeButton) {
            r.reportAndStop();
        } else {
            r.cancel();
            SwingUtilities.invokeLater(() -> {
                if (JLabel_Reaktionszeit != null) JLabel_Reaktionszeit.setText("falsch geklickt! versuch nochmal");
            });
        }

        resetAfterRound();
    }

    private void resetAfterRound() {
        resetButtons();
        activeButton = -1;
        if (testStartenButton != null) testStartenButton.setEnabled(true);
        r = null;
    }

    private void resetButtons() {
        if (button1 != null) {
            button1.setBackground(defaultBtnColor);
            button1.setOpaque(false);
        }
        if (button2 != null) {
            button2.setBackground(defaultBtnColor);
            button2.setOpaque(false);
        }
        if (button3 != null) {
            button3.setBackground(defaultBtnColor);
            button3.setOpaque(false);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Reaktionszeit");
            NewJFrame ui = new NewJFrame();
            frame.setContentPane(ui.panel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}