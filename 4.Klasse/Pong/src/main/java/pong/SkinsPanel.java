package pong;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Component;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class SkinsPanel extends JPanel {
    private final AppWindow window;
    private final AppContext context;
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"ID", "Name"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final JButton activateButton = new JButton("Aktivieren");
    private final JButton backButton = new JButton("Zurück");
    private final JLabel infoLabel = new JLabel();
    private final SkinPreview previewPanel = new SkinPreview();
    private final List<Skin> displayedSkins = new ArrayList<>();

    public SkinsPanel(AppWindow window, AppContext context) {
        this.window = Objects.requireNonNull(window);
        this.context = Objects.requireNonNull(context);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    setBackground(new Color(0x2A2A2A));
    BackgroundAnimator.register(this);
    BackgroundAnimator.styleButton(activateButton);
    BackgroundAnimator.styleButton(backButton);

        JLabel title = new JLabel("Skins");
        title.setAlignmentX(CENTER_ALIGNMENT);
    title.setForeground(Color.WHITE);
        add(title);
        add(Box.createVerticalStrut(10));

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setPreferredScrollableViewportSize(new Dimension(400, 200));
        add(new JScrollPane(table));
        add(Box.createVerticalStrut(10));

        previewPanel.setPreferredSize(new Dimension(420, 180));
        previewPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        previewPanel.setAlignmentX(CENTER_ALIGNMENT);
        add(previewPanel);
        add(Box.createVerticalStrut(10));

        infoLabel.setAlignmentX(CENTER_ALIGNMENT);
    infoLabel.setForeground(Color.WHITE);
        add(infoLabel);
        add(Box.createVerticalStrut(10));

        activateButton.setAlignmentX(CENTER_ALIGNMENT);
        backButton.setAlignmentX(CENTER_ALIGNMENT);
        add(activateButton);
        add(Box.createVerticalStrut(10));
        add(backButton);
        add(Box.createVerticalGlue());

        activateButton.addActionListener(e -> activateSelectedSkin());
        backButton.addActionListener(e -> window.showMenu());
        ListSelectionListener listener = e -> {
            if (!e.getValueIsAdjusting()) {
                updatePreview();
            }
        };
        table.getSelectionModel().addListSelectionListener(listener);
    }

    public void refresh() {
        tableModel.setRowCount(0);
        displayedSkins.clear();
        Map<String, Skin> skins = context.skins();
        Storage.Settings settings = context.settings();
        for (Skin skin : skins.values()) {
            if (context.isSkinOwned(skin.id())) {
                displayedSkins.add(skin);
                tableModel.addRow(new Object[]{skin.id(), skin.name()});
            }
        }
        String selectedSkin = settings.selectedSkinId;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (Objects.equals(tableModel.getValueAt(i, 0), selectedSkin)) {
                table.setRowSelectionInterval(i, i);
                break;
            }
        }
        updatePreview();
    }

    private void activateSelectedSkin() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        context.updateSettings(settings -> settings.selectedSkinId = id);
        window.notifySettingsChanged();
        refresh();
    }

    private void updatePreview() {
        Skin skin = null;
        int row = table.getSelectedRow();
        if (row >= 0 && row < displayedSkins.size()) {
            skin = displayedSkins.get(row);
        }
        if (skin == null) {
            skin = context.currentSkinOrDefault();
        }
        previewPanel.setSkin(skin);
        ColorPalette palette = skin.palette();
        boolean owned = context.isSkinOwned(skin.id());
        boolean active = Objects.equals(context.settings().selectedSkinId, skin.id());
        String status = owned ? (active ? "Aktiv" : "Besitzt") : "Gesperrt";
        infoLabel.setText(String.format("Ball: %s | Paddles: %s / %s | Status: %s",
                toHex(palette.ball()), toHex(palette.paddleLeft()), toHex(palette.paddleRight()), status));
        activateButton.setEnabled(owned && !active);
    }

    private static String toHex(Color color) {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

    private static final class SkinPreview extends JPanel {
        private Skin skin;

        private void setSkin(Skin skin) {
            this.skin = skin;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (skin == null) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            ColorPalette palette = skin.palette();

            int width = getWidth();
            int height = getHeight();

            g2.setColor(palette.background());
            g2.fillRect(0, 0, width, height);

            g2.setColor(palette.midline());
            int dashHeight = 14;
            int gap = 10;
            int midX = width / 2 - 2;
            for (int y = 0; y < height; y += dashHeight + gap) {
                g2.fillRect(midX, y, 4, dashHeight);
            }

            int paddleWidth = 16;
            int paddleHeight = height / 3;
            int paddleYOffset = height / 2 - paddleHeight / 2;

            g2.setColor(palette.paddleLeft());
            g2.fillRoundRect(40, paddleYOffset, paddleWidth, paddleHeight, 8, 8);

            g2.setColor(palette.paddleRight());
            g2.fillRoundRect(width - 40 - paddleWidth, paddleYOffset, paddleWidth, paddleHeight, 8, 8);

            g2.setColor(palette.ball());
            int ballSize = 18;
            g2.fillOval(width / 2 - ballSize / 2, height / 2 - ballSize / 2, ballSize, ballSize);

            g2.setColor(palette.text());
            g2.drawString(skin.name(), 12, height - 12);

            g2.dispose();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        BackgroundAnimator.paint(g2, getWidth(), getHeight());
        g2.dispose();
    }
}
