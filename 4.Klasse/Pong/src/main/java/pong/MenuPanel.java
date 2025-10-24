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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.Objects;

public final class MenuPanel extends JPanel {
    private final AppWindow window;
    private final AppContext context;

    private final JButton playButton = new JButton("Spielen");
    private final JButton settingsButton = new JButton("Einstellungen");
    private final JButton skinsButton = new JButton("Skins");
    private final JButton shopButton = new JButton("Shop");
    private final JButton leaderboardButton = new JButton("Leaderboard");
    private final JButton quitButton = new JButton("Beenden");
    private final JTextField nameField = new JTextField();

    public MenuPanel(AppWindow window, AppContext context) {
        this.window = Objects.requireNonNull(window);
        this.context = Objects.requireNonNull(context);
        setLayout(new BorderLayout());

    JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
    center.setOpaque(false);
        JLabel title = new JLabel("Pong");
    title.setForeground(Color.WHITE);
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
    // slightly smaller buttons so they don't dominate the menu
    Dimension buttonSize = new Dimension(260, 52);
        playButton.setMaximumSize(buttonSize);
        playButton.setPreferredSize(buttonSize);
    playButton.setFont(playButton.getFont().deriveFont(18f));
        settingsButton.setMaximumSize(buttonSize);
        settingsButton.setPreferredSize(buttonSize);
    settingsButton.setFont(settingsButton.getFont().deriveFont(18f));
        skinsButton.setMaximumSize(buttonSize);
        skinsButton.setPreferredSize(buttonSize);
    skinsButton.setFont(skinsButton.getFont().deriveFont(18f));
        shopButton.setMaximumSize(buttonSize);
        shopButton.setPreferredSize(buttonSize);
    shopButton.setFont(shopButton.getFont().deriveFont(18f));
        leaderboardButton.setMaximumSize(buttonSize);
        leaderboardButton.setPreferredSize(buttonSize);
    leaderboardButton.setFont(leaderboardButton.getFont().deriveFont(18f));
    quitButton.setMaximumSize(buttonSize);
    quitButton.setPreferredSize(buttonSize);
    quitButton.setFont(quitButton.getFont().deriveFont(18f));
    quitButton.setAlignmentX(CENTER_ALIGNMENT);
        center.add(playButton);
        center.add(Box.createVerticalStrut(10));
        center.add(settingsButton);
        center.add(Box.createVerticalStrut(10));
        center.add(skinsButton);
        center.add(Box.createVerticalStrut(10));
        center.add(shopButton);
        center.add(Box.createVerticalStrut(10));
        center.add(leaderboardButton);
    center.add(Box.createVerticalStrut(10));
    center.add(quitButton);
        center.add(Box.createVerticalGlue());
    JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
    wrapper.setOpaque(false);
    wrapper.add(center);
    add(wrapper, BorderLayout.CENTER);

    // theme
    setBackground(new Color(0x2A2A2A));
    BackgroundAnimator.register(this);
    BackgroundAnimator.styleButton(playButton);
    BackgroundAnimator.styleButton(settingsButton);
    BackgroundAnimator.styleButton(skinsButton);
    BackgroundAnimator.styleButton(shopButton);
    BackgroundAnimator.styleButton(leaderboardButton);
    BackgroundAnimator.styleButton(quitButton);

    JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    bottom.setOpaque(false);
        JLabel nameLabel = new JLabel("Dein Name:");
    nameLabel.setForeground(Color.WHITE);
        nameField.setPreferredSize(new Dimension(200, 28));
    nameField.setBackground(new Color(0x444444));
    nameField.setForeground(Color.WHITE);
        bottom.add(nameLabel);
        bottom.add(nameField);
        add(bottom, BorderLayout.SOUTH);

        playButton.addActionListener(e -> window.showPlayMenu());
        settingsButton.addActionListener(e -> window.showSettings());
        skinsButton.addActionListener(e -> window.showSkins());
        shopButton.addActionListener(e -> window.showShop());
        leaderboardButton.addActionListener(e -> window.showLeaderboard());
    quitButton.addActionListener(e -> System.exit(0));

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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        BackgroundAnimator.paint(g2, getWidth(), getHeight());
        g2.dispose();
    }
}
