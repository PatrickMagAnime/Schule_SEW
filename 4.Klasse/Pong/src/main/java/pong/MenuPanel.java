package pong;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Objects;

public final class MenuPanel extends JPanel {
    private final AppWindow window;
    private final AppContext context;

    private final JButton playButton = new JButton("Spielen");
    private final JButton settingsButton = new JButton("Einstellungen");
    private final JButton skinsButton = new JButton("Skins");
    private final JButton shopButton = new JButton("Shop");
    private final JButton leaderboardButton = new JButton("Leaderboard");
    private final JTextField nameField = new JTextField();

    public MenuPanel(AppWindow window, AppContext context) {
        this.window = Objects.requireNonNull(window);
        this.context = Objects.requireNonNull(context);
        setLayout(new BorderLayout());

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Pong");
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 48f));
        center.add(Box.createVerticalStrut(40));
        center.add(title);
        center.add(Box.createVerticalStrut(30));
        playButton.setAlignmentX(CENTER_ALIGNMENT);
        settingsButton.setAlignmentX(CENTER_ALIGNMENT);
        skinsButton.setAlignmentX(CENTER_ALIGNMENT);
        shopButton.setAlignmentX(CENTER_ALIGNMENT);
        leaderboardButton.setAlignmentX(CENTER_ALIGNMENT);
        Dimension buttonSize = new Dimension(220, 40);
        playButton.setMaximumSize(buttonSize);
        settingsButton.setMaximumSize(buttonSize);
        skinsButton.setMaximumSize(buttonSize);
        shopButton.setMaximumSize(buttonSize);
        leaderboardButton.setMaximumSize(buttonSize);
        center.add(playButton);
        center.add(Box.createVerticalStrut(10));
        center.add(settingsButton);
        center.add(Box.createVerticalStrut(10));
        center.add(skinsButton);
        center.add(Box.createVerticalStrut(10));
        center.add(shopButton);
        center.add(Box.createVerticalStrut(10));
        center.add(leaderboardButton);
        center.add(Box.createVerticalGlue());
        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel nameLabel = new JLabel("Dein Name:");
        nameField.setPreferredSize(new Dimension(200, 28));
        bottom.add(nameLabel);
        bottom.add(nameField);
        add(bottom, BorderLayout.SOUTH);

        playButton.addActionListener(e -> window.showPlayMenu());
        settingsButton.addActionListener(e -> window.showSettings());
        skinsButton.addActionListener(e -> window.showSkins());
        shopButton.addActionListener(e -> window.showShop());
        leaderboardButton.addActionListener(e -> window.showLeaderboard());

        nameField.getDocument().addDocumentListener(SimpleDocumentListener.of(() -> {
            updateName();
            updateButtonState();
        }));
        updateButtonState();
    }

    public void refresh() {
        Storage.Profile profile = context.profile();
        nameField.setText(profile.currentName);
        updateButtonState();
        SwingUtilities.invokeLater(nameField::requestFocusInWindow);
    }

    private void updateName() {
        String text = nameField.getText().trim();
        context.updateProfile(profile -> profile.currentName = text);
    }

    private void updateButtonState() {
        boolean hasName = !nameField.getText().trim().isBlank();
        playButton.setEnabled(hasName);
    }
}
