import javax.swing.*;

public class fibo {
    private JTextField textField1; //fiboeingabe
    private JButton fiboButton; //fibo button
    private JTextArea ergebnissTextArea; //taxtarea
    private JPanel mainPanel; //form
    private JTextField textField2; //fakultät textfield
    private JButton fakuButton; //fakultät button
    private JTextArea ergebnissTextArea1; //fakultät ausgabe feld
    private JTextField textField3; //exponent basis eingabefeld
    private JButton expoButton; //exponent button
    private JTextArea ergebnissTextArea2; //exponent ausgabefeld
    private JTextField textField4; //exponent eingabefeld
// form value und name "fibo" und mainPanel ist "mainPanel"
    public fibo() {

        fiboButton.addActionListener(e -> {
            try {
                int n = Integer.parseInt(textField1.getText());
                StringBuilder result = new StringBuilder();

                for (int i = 0; i < n; i++) {
                    result.append(fiboRekursive(i)).append(" ,");
                }

                ergebnissTextArea.setText(result.toString());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainPanel, "zahl eingeben");
            }
        });

        fakuButton.addActionListener(e -> {
            try {
                int n = Integer.parseInt(textField2.getText());
                long result = fakultaetRekursive(n);
                ergebnissTextArea1.setText(Long.toString(result));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainPanel, "zahl eingeben");
            }
        });

        expoButton.addActionListener(e -> {
            try {
                int basis = Integer.parseInt(textField3.getText());
                int exponent = Integer.parseInt(textField4.getText());
                long result = exponentRekursive(basis, exponent);
                ergebnissTextArea2.setText(Long.toString(result));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainPanel, "bitte basis und exponent eingeben");
            }
        });
    }

    private int fiboRekursive(int n) {
        if (n <= 1) {
            return n;
        }
        return fiboRekursive(n - 1) + fiboRekursive(n - 2);
    }

    private long fakultaetRekursive(int n) {
        if (n < 0) return 0;
        if (n == 0 || n == 1) return 1;
        return n * fakultaetRekursive(n - 1);
    }

    private long exponentRekursive(int basis, int exponent) {
        if (exponent < 0) throw new IllegalArgumentException("exponent muss >= 0 sein");
        if (exponent == 0) return 1;
        return basis * exponentRekursive(basis, exponent - 1);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("fibonacci");
        frame.setContentPane(new fibo().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}