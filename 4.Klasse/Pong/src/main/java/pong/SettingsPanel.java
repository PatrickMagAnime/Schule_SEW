package pong;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
    private final JCheckBox midlineCheckbox = new JCheckBox("Mittellinie anzeigen");

    public SettingsPanel(AppWindow window, AppContext context) {
        this.window = Objects.requireNonNull(window);
        this.context = Objects.requireNonNull(context);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
    setBackground(new java.awt.Color(0x2A2A2A));

    JLabel title = new JLabel("Einstellungen");
    title.setAlignmentX(CENTER_ALIGNMENT);
    title.setForeground(java.awt.Color.WHITE);
        add(title);
        add(Box.createVerticalStrut(20));

    JLabel volumeLabel = new JLabel("Lautstärke (Stub)");
    volumeLabel.setAlignmentX(CENTER_ALIGNMENT);
    volumeLabel.setForeground(java.awt.Color.WHITE);
        add(volumeLabel);
        add(Box.createVerticalStrut(10));

    volumeSlider.setOrientation(SwingConstants.HORIZONTAL);
    volumeSlider.setMajorTickSpacing(20);
    volumeSlider.setPaintTicks(true);
    volumeSlider.setPaintLabels(true);
    volumeSlider.setAlignmentX(CENTER_ALIGNMENT);
    // dark background and readable labels
    volumeSlider.setBackground(new java.awt.Color(0x3A3A3A));
    volumeSlider.setForeground(java.awt.Color.WHITE);
    volumeSlider.setOpaque(true);
        add(volumeSlider);
    updateSliderLabelColors(volumeSlider);
        add(Box.createVerticalStrut(20));

    JLabel fpsLabel = new JLabel("FPS");
    fpsLabel.setAlignmentX(CENTER_ALIGNMENT);
    fpsLabel.setForeground(java.awt.Color.WHITE);
        add(fpsLabel);
        add(Box.createVerticalStrut(10));

    fpsSlider.setOrientation(SwingConstants.HORIZONTAL);
    fpsSlider.setMajorTickSpacing(10);
    fpsSlider.setPaintTicks(true);
    fpsSlider.setPaintLabels(true);
    fpsSlider.setAlignmentX(CENTER_ALIGNMENT);
    // dark background and readable labels
    fpsSlider.setBackground(new java.awt.Color(0x3A3A3A));
    fpsSlider.setForeground(java.awt.Color.WHITE);
    fpsSlider.setOpaque(true);
        add(fpsSlider);
    updateSliderLabelColors(fpsSlider);
        add(Box.createVerticalStrut(20));

    JLabel maxPointsLabel = new JLabel("Max Punkte 1v1");
    maxPointsLabel.setAlignmentX(CENTER_ALIGNMENT);
    maxPointsLabel.setForeground(java.awt.Color.WHITE);
        add(maxPointsLabel);
        add(Box.createVerticalStrut(10));

    maxPointsField.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 28));
    maxPointsField.setHorizontalAlignment(JTextField.CENTER);
    maxPointsField.setBackground(new java.awt.Color(0x444444));
    maxPointsField.setForeground(java.awt.Color.WHITE);
        add(maxPointsField);
        add(Box.createVerticalStrut(20));

    JLabel keybindTitle = new JLabel("Keybinds");
    keybindTitle.setAlignmentX(CENTER_ALIGNMENT);
    keybindTitle.setForeground(java.awt.Color.WHITE);
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
    add(midlineCheckbox);
    add(Box.createVerticalStrut(20));

    JButton resetButton = new JButton("Auf Standard zurücksetzen");
    resetButton.setAlignmentX(CENTER_ALIGNMENT);
    resetButton.setMaximumSize(new java.awt.Dimension(360, 72));
    resetButton.setPreferredSize(new java.awt.Dimension(360, 72));
    resetButton.setFont(resetButton.getFont().deriveFont(20f));
    add(resetButton);
    add(Box.createVerticalStrut(10));

    JButton backButton = new JButton("Zurück");
    backButton.setAlignmentX(CENTER_ALIGNMENT);
    backButton.setMaximumSize(new java.awt.Dimension(360, 72));
    backButton.setPreferredSize(new java.awt.Dimension(360, 72));
    backButton.setFont(backButton.getFont().deriveFont(20f));
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
        resetButton.addActionListener(e -> {
            // reset settings to defaults
            context.updateSettings(s -> {
                Storage.Settings def = new Storage.Settings();
                s.volume = def.volume;
                s.selectedSkinId = def.selectedSkinId;
                s.fps = def.fps;
                s.maxPoints1v1 = def.maxPoints1v1;
                s.player1UpKey = def.player1UpKey;
                s.player1DownKey = def.player1DownKey;
                s.player1BoostKey = def.player1BoostKey;
                s.player2UpKey = def.player2UpKey;
                s.player2DownKey = def.player2DownKey;
                s.player2BoostKey = def.player2BoostKey;
                s.showMidline = def.showMidline;
            });
            window.notifySettingsChanged();
            refresh();
        });
        midlineCheckbox.addActionListener(e -> {
            context.updateSettings(s -> s.showMidline = midlineCheckbox.isSelected());
            window.notifySettingsChanged();
        });

        midlineCheckbox.setForeground(java.awt.Color.WHITE);
        midlineCheckbox.setOpaque(false);

        // style buttons and register for animated background
        BackgroundAnimator.register(this);
        BackgroundAnimator.styleButton(resetButton);
        BackgroundAnimator.styleButton(backButton);
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
        midlineCheckbox.setSelected(settings.showMidline);
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
        field.setBackground(new java.awt.Color(0x444444));
        field.setForeground(java.awt.Color.WHITE);
        return field;
    }

    // ensure any label table entries on sliders are readable on dark bg
    private void updateSliderLabelColors(javax.swing.JSlider slider) {
        if (slider == null) return;
        java.util.Dictionary<?,?> table = slider.getLabelTable();
        if (table == null) return;
        java.util.Enumeration<?> keys = table.keys();
        while (keys.hasMoreElements()) {
            Object k = keys.nextElement();
            Object v = table.get(k);
            if (v instanceof javax.swing.JLabel) {
                javax.swing.JLabel lbl = (javax.swing.JLabel) v;
                lbl.setForeground(java.awt.Color.WHITE);
                lbl.setOpaque(false);
            }
        }
    }

    private JPanel createKeyRow(String label, JTextField field) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        JLabel lbl = new JLabel(label);
        lbl.setForeground(java.awt.Color.WHITE);
        // subtle gray background for keybind rows to separate them from the animated bg
        row.setOpaque(true);
        row.setBackground(new java.awt.Color(0x333333));
        lbl.setOpaque(false);
        row.add(Box.createHorizontalStrut(6));
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

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
        BackgroundAnimator.paint(g2, getWidth(), getHeight());
        g2.dispose();
    }
}
