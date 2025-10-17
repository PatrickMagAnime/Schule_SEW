package pong;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class SettingsPanel extends JPanel {
    private final AppWindow window;
    private final AppContext context;
    private final JSlider volumeSlider = new JSlider(0, 100);
    private final JSlider fpsSlider = new JSlider(30, 120, 60);
    private final JTextField maxPointsField = new JTextField();
    private final JTextField player1UpField = createKeyField();
    private final JTextField player1DownField = createKeyField();
    private final JTextField player1BoostField = createKeyField();
    private final JTextField player2UpField = createKeyField();
    private final JTextField player2DownField = createKeyField();
    private final JTextField player2BoostField = createKeyField();
    private boolean updatingFields;

    public SettingsPanel(AppWindow window, AppContext context) {
        this.window = Objects.requireNonNull(window);
        this.context = Objects.requireNonNull(context);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Einstellungen");
        title.setAlignmentX(CENTER_ALIGNMENT);
        add(title);
        add(Box.createVerticalStrut(20));

        JLabel volumeLabel = new JLabel("Lautstärke (Stub)");
        volumeLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(volumeLabel);
        add(Box.createVerticalStrut(10));

        volumeSlider.setOrientation(SwingConstants.HORIZONTAL);
        volumeSlider.setMajorTickSpacing(20);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setAlignmentX(CENTER_ALIGNMENT);
        add(volumeSlider);
        add(Box.createVerticalStrut(20));

        JLabel fpsLabel = new JLabel("FPS");
        fpsLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(fpsLabel);
        add(Box.createVerticalStrut(10));

        fpsSlider.setOrientation(SwingConstants.HORIZONTAL);
        fpsSlider.setMajorTickSpacing(10);
        fpsSlider.setPaintTicks(true);
        fpsSlider.setPaintLabels(true);
        fpsSlider.setAlignmentX(CENTER_ALIGNMENT);
        add(fpsSlider);
        add(Box.createVerticalStrut(20));

        JLabel maxPointsLabel = new JLabel("Max Punkte 1v1");
        maxPointsLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(maxPointsLabel);
        add(Box.createVerticalStrut(10));

        maxPointsField.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 28));
        maxPointsField.setHorizontalAlignment(JTextField.CENTER);
        add(maxPointsField);
        add(Box.createVerticalStrut(20));

        JLabel keybindTitle = new JLabel("Keybinds");
        keybindTitle.setAlignmentX(CENTER_ALIGNMENT);
        add(keybindTitle);
        add(Box.createVerticalStrut(10));

        add(createKeyRow("P1 Hoch", player1UpField));
        add(Box.createVerticalStrut(5));
        add(createKeyRow("P1 Runter", player1DownField));
        add(Box.createVerticalStrut(5));
        add(createKeyRow("P1 Boost", player1BoostField));
        add(Box.createVerticalStrut(10));
        add(createKeyRow("P2 Hoch", player2UpField));
        add(Box.createVerticalStrut(5));
        add(createKeyRow("P2 Runter", player2DownField));
        add(Box.createVerticalStrut(5));
        add(createKeyRow("P2 Boost", player2BoostField));
        add(Box.createVerticalStrut(20));

        JButton backButton = new JButton("Zurück");
        backButton.setAlignmentX(CENTER_ALIGNMENT);
        add(backButton);
        add(Box.createVerticalGlue());

        volumeSlider.addChangeListener(e -> updateVolume());
        fpsSlider.addChangeListener(e -> updateFps());
        maxPointsField.getDocument().addDocumentListener(SimpleDocumentListener.of(this::updateMaxPoints));
        player1UpField.getDocument().addDocumentListener(SimpleDocumentListener.of(() -> updateKey(player1UpField, s -> s.player1UpKey, (s, value) -> s.player1UpKey = value)));
        player1DownField.getDocument().addDocumentListener(SimpleDocumentListener.of(() -> updateKey(player1DownField, s -> s.player1DownKey, (s, value) -> s.player1DownKey = value)));
        player1BoostField.getDocument().addDocumentListener(SimpleDocumentListener.of(() -> updateKey(player1BoostField, s -> s.player1BoostKey, (s, value) -> s.player1BoostKey = value)));
        player2UpField.getDocument().addDocumentListener(SimpleDocumentListener.of(() -> updateKey(player2UpField, s -> s.player2UpKey, (s, value) -> s.player2UpKey = value)));
        player2DownField.getDocument().addDocumentListener(SimpleDocumentListener.of(() -> updateKey(player2DownField, s -> s.player2DownKey, (s, value) -> s.player2DownKey = value)));
        player2BoostField.getDocument().addDocumentListener(SimpleDocumentListener.of(() -> updateKey(player2BoostField, s -> s.player2BoostKey, (s, value) -> s.player2BoostKey = value)));
        backButton.addActionListener(e -> window.showMenu());
    }

    public void refresh() {
        updatingFields = true;
        Storage.Settings settings = context.settings();
        int value = (int) Math.round(settings.volume * 100);
        volumeSlider.setValue(value);
        fpsSlider.setValue(settings.fps);
        maxPointsField.setText(Integer.toString(settings.maxPoints1v1));
        player1UpField.setText(settings.player1UpKey);
        player1DownField.setText(settings.player1DownKey);
        player1BoostField.setText(settings.player1BoostKey);
        player2UpField.setText(settings.player2UpKey);
        player2DownField.setText(settings.player2DownKey);
        player2BoostField.setText(settings.player2BoostKey);
        updatingFields = false;
    }

    private void updateVolume() {
        if (updatingFields) {
            return;
        }
        double value = volumeSlider.getValue() / 100.0;
        context.updateSettings(settings -> settings.volume = value);
    }

    private void updateFps() {
        if (updatingFields) {
            return;
        }
        int value = fpsSlider.getValue();
        context.updateSettings(settings -> settings.fps = value);
        window.notifySettingsChanged();
    }

    private void updateMaxPoints() {
        if (updatingFields) {
            return;
        }
        String text = maxPointsField.getText().trim();
        if (text.isBlank()) {
            return;
        }
        try {
            int value = Integer.parseInt(text);
            if (value > 0) {
                context.updateSettings(settings -> settings.maxPoints1v1 = value);
                window.notifySettingsChanged();
            }
        } catch (NumberFormatException ignored) {
        }
    }

    private JTextField createKeyField() {
        JTextField field = new JTextField();
        field.setMaximumSize(new java.awt.Dimension(100, 28));
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setColumns(4);
        return field;
    }

    private JPanel createKeyRow(String label, JTextField field) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        JLabel lbl = new JLabel(label);
        row.add(lbl);
        row.add(Box.createHorizontalStrut(10));
        row.add(field);
        row.setAlignmentX(LEFT_ALIGNMENT);
        return row;
    }

    private void updateKey(JTextField field,
                           Function<Storage.Settings, String> getter,
                           BiConsumer<Storage.Settings, String> setter) {
        if (updatingFields) {
            return;
        }
        String normalized = normalizeKey(field.getText());
        if (normalized == null) {
            return;
        }
        Storage.Settings settings = context.settings();
        String current = getter.apply(settings);
        if (normalized.equals(current)) {
            return;
        }
        if (!normalized.equals(field.getText())) {
            updatingFields = true;
            field.setText(normalized);
            updatingFields = false;
        }
        context.updateSettings(s -> setter.accept(s, normalized));
        window.notifySettingsChanged();
    }

    private String normalizeKey(String raw) {
        if (raw == null) {
            return null;
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (trimmed.length() == 1) {
            return trimmed.toUpperCase(Locale.ROOT);
        }
        return trimmed.toUpperCase(Locale.ROOT);
    }
}
