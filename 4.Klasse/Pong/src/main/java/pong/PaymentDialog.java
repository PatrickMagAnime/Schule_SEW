package pong;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.util.Objects;

public final class PaymentDialog extends JDialog {
    private final AppContext context;
    private final Storage.ShopItem item;
    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private boolean success;

    public PaymentDialog(AppWindow owner, AppContext context, Storage.ShopItem item) {
        super(owner, "Fake PayPal", true);
        this.context = Objects.requireNonNull(context);
        this.item = Objects.requireNonNull(item);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(320, 220));

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel info = new JLabel("Fake PayPal Checkout");
        info.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(info);
        panel.add(Box.createVerticalStrut(10));

        JLabel itemLabel = new JLabel("Artikel: " + item.name);
        itemLabel.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(itemLabel);
        panel.add(Box.createVerticalStrut(10));

        JLabel emailLabel = new JLabel("E-Mail");
        panel.add(emailLabel);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        panel.add(emailField);
        panel.add(Box.createVerticalStrut(10));

        JLabel passwordLabel = new JLabel("Passwort");
        panel.add(passwordLabel);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(15));

        JPanel buttonPanel = new JPanel();
        JButton payButton = new JButton("Pay");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(payButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        payButton.addActionListener(e -> performPayment());
        cancelButton.addActionListener(e -> dispose());

        setContentPane(panel);
        pack();
    }

    private void performPayment() {
        success = true;
        context.recordPurchase(item.id);
        context.addOwnedSkin(item.id);
        JOptionPane.showMessageDialog(this,
                "Zahlung erfolgreich! Skin " + item.name + " freigeschaltet.",
                "Erfolg",
                JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    public boolean wasSuccessful() {
        return success;
    }
}
