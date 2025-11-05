package pong;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.Objects;

public final class PlayMenuPanel extends JPanel {
    private final AppWindow window;
    private final AppContext context;

    private final JRadioButton singlePlayerButton = new JRadioButton("Single Player");
    private final JRadioButton multiPlayerButton = new JRadioButton("Multiplayer 1v1");
    private final JTextField playerOneField = new JTextField();
    private final JTextField playerTwoField = new JTextField();
    private final JButton startButton = new JButton("Start");
    private final JButton backButton = new JButton("Zurück");

    public PlayMenuPanel(AppWindow window, AppContext context) {
        this.window = Objects.requireNonNull(window);
        this.context = Objects.requireNonNull(context);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

    Theme.applyPanelTheme(this);
    Theme.styleButtons(startButton, backButton);

        ButtonGroup group = new ButtonGroup();
        group.add(singlePlayerButton);
        group.add(multiPlayerButton);
        singlePlayerButton.setSelected(true);

    javax.swing.JLabel modeLabel = new javax.swing.JLabel("Spielmodus");
    modeLabel.setForeground(Color.WHITE);
    // dark background block behind mode label and make radio buttons visually distinct
    modeLabel.setOpaque(true);
    modeLabel.setBackground(new Color(0x333333));
    modeLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(6,6,6,6));
    add(modeLabel);
        add(Box.createVerticalStrut(10));
    singlePlayerButton.setForeground(Color.WHITE);
    singlePlayerButton.setBackground(new Color(0x3A3A3A));
    singlePlayerButton.setOpaque(true);
    singlePlayerButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(4,6,4,6));
    multiPlayerButton.setForeground(Color.WHITE);
    multiPlayerButton.setBackground(new Color(0x3A3A3A));
    multiPlayerButton.setOpaque(true);
    multiPlayerButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(4,6,4,6));
    add(singlePlayerButton);
    add(Box.createVerticalStrut(6));
    add(multiPlayerButton);
        add(Box.createVerticalStrut(20));

        JPanel playerOnePanel = new JPanel();
        playerOnePanel.setLayout(new BoxLayout(playerOnePanel, BoxLayout.Y_AXIS));
        playerOnePanel.setOpaque(false);
    JLabel playerOneLabel = new JLabel("Spieler 1 Name (links)");
    playerOneLabel.setForeground(Color.WHITE);
        playerOnePanel.add(playerOneLabel);
    playerOneField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    playerOneField.setPreferredSize(new Dimension(Integer.MAX_VALUE, 36));
    Theme.styleTextField(playerOneField);
        playerOnePanel.add(playerOneField);
        add(playerOnePanel);
        add(Box.createVerticalStrut(10));

        JPanel playerTwoPanel = new JPanel();
        playerTwoPanel.setLayout(new BoxLayout(playerTwoPanel, BoxLayout.Y_AXIS));
        playerTwoPanel.setOpaque(false);
    JLabel playerTwoLabel = new JLabel("Spieler 2 Name (rechts)");
    playerTwoLabel.setForeground(Color.WHITE);
        playerTwoPanel.add(playerTwoLabel);
    playerTwoField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    playerTwoField.setPreferredSize(new Dimension(Integer.MAX_VALUE, 36));
    Theme.styleTextField(playerTwoField);
        playerTwoPanel.add(playerTwoField);
        add(playerTwoPanel);
        add(Box.createVerticalStrut(20));

    startButton.setAlignmentX(CENTER_ALIGNMENT);
    startButton.setMaximumSize(new Dimension(360, 72));
    startButton.setPreferredSize(new Dimension(360, 72));
    startButton.setFont(startButton.getFont().deriveFont(22f));
    backButton.setAlignmentX(CENTER_ALIGNMENT);
    backButton.setMaximumSize(new Dimension(360, 72));
    backButton.setPreferredSize(new Dimension(360, 72));
    backButton.setFont(backButton.getFont().deriveFont(22f));
    add(startButton);
    add(Box.createVerticalStrut(10));
    add(backButton);
        add(Box.createVerticalGlue());

        singlePlayerButton.addActionListener(e -> updateState());
        multiPlayerButton.addActionListener(e -> updateState());
        startButton.addActionListener(e -> startGame());
        backButton.addActionListener(e -> window.showMenu());
        playerOneField.getDocument().addDocumentListener(SimpleDocumentListener.of(this::updateState));
        playerTwoField.getDocument().addDocumentListener(SimpleDocumentListener.of(this::updateState));
    singlePlayerButton.setForeground(Color.WHITE);
    multiPlayerButton.setForeground(Color.WHITE);
    }

    public void refresh() {
        Storage.Profile profile = context.profile();
        playerOneField.setText(profile.currentName);
        playerTwoField.setText(profile.player2Name);
        updateState();
    }

    private void updateState() {
        boolean single = singlePlayerButton.isSelected();
        playerTwoField.setEnabled(!single);
        String p1 = playerOneField.getText().trim();
        String p2 = playerTwoField.getText().trim();
        boolean valid = !p1.isBlank();
        if (!single) {
            valid &= !p2.isBlank();
        }
        startButton.setEnabled(valid);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        BackgroundAnimator.paint(g2, getWidth(), getHeight());
        g2.dispose();
    }

    private void startGame() {
        boolean single = singlePlayerButton.isSelected();
        String p1 = playerOneField.getText().trim();
        String p2 = playerTwoField.getText().trim();
        if (p1.isBlank() || (!single && p2.isBlank())) {
            return;
        }
        context.updateProfile(profile -> {
            profile.currentName = p1;
            profile.player2Name = p2;
        });
        GameConfiguration configuration = new GameConfiguration(
                single ? GameConfiguration.Mode.SINGLE_PLAYER : GameConfiguration.Mode.MULTI_PLAYER,
                p1,
                single ? "" : p2
        );
        window.startGame(configuration);
    }
}
