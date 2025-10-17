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

        ButtonGroup group = new ButtonGroup();
        group.add(singlePlayerButton);
        group.add(multiPlayerButton);
        singlePlayerButton.setSelected(true);

        add(new JLabel("Spielmodus"));
        add(Box.createVerticalStrut(10));
        add(singlePlayerButton);
        add(multiPlayerButton);
        add(Box.createVerticalStrut(20));

        JPanel playerOnePanel = new JPanel();
        playerOnePanel.setLayout(new BoxLayout(playerOnePanel, BoxLayout.Y_AXIS));
        JLabel playerOneLabel = new JLabel("Spieler 1 Name (links)");
        playerOnePanel.add(playerOneLabel);
        playerOneField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        playerOnePanel.add(playerOneField);
        add(playerOnePanel);
        add(Box.createVerticalStrut(10));

        JPanel playerTwoPanel = new JPanel();
        playerTwoPanel.setLayout(new BoxLayout(playerTwoPanel, BoxLayout.Y_AXIS));
        JLabel playerTwoLabel = new JLabel("Spieler 2 Name (rechts)");
        playerTwoPanel.add(playerTwoLabel);
        playerTwoField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        playerTwoPanel.add(playerTwoField);
        add(playerTwoPanel);
        add(Box.createVerticalStrut(20));

        startButton.setAlignmentX(CENTER_ALIGNMENT);
        backButton.setAlignmentX(CENTER_ALIGNMENT);
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
