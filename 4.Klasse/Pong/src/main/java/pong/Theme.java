package pong;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JPanel;
import java.awt.Color;

/**
 * Central UI theme helpers to avoid duplicated styling across panels.
 * Use these helpers to keep a consistent dark theme and reduce repeated code.
 */
public final class Theme {
    public static final Color BACKGROUND = new Color(0x2A2A2A);
    public static final Color PANEL_DARK = new Color(0x333333);
    public static final Color PANEL_DARKER = new Color(0x3A3A3A);
    public static final Color FIELD_BG = new Color(0x444444);
    public static final Color BUTTON_BG = new Color(0x2E2E2E);
    public static final Color BUTTON_BORDER = new Color(0x666666);
    public static final Color HEADER_BG = new Color(0x222222);
    public static final Color TABLE_ALT = new Color(0x2A2A2A);
    public static final Color TABLE_ROW = new Color(0x333333);
    public static final Color TABLE_SELECTION = new Color(0x55607A);
    public static final Color TEXT = new Color(0xFFFFFF);

    private Theme() {}

    public static void applyPanelTheme(JPanel panel) {
        panel.setBackground(BACKGROUND);
        panel.setOpaque(true);
        BackgroundAnimator.register(panel);
    }

    public static void styleButton(javax.swing.JButton btn) {
        BackgroundAnimator.styleButton(btn);
    }

    public static void styleButtons(javax.swing.JButton... btns) {
        if (btns == null) return;
        for (JButton b : btns) {
            if (b != null) styleButton(b);
        }
    }

    public static void styleTextField(JTextField f) {
        if (f == null) return;
        f.setBackground(FIELD_BG);
        f.setForeground(TEXT);
        f.setOpaque(true);
    }

    public static void styleSlider(JSlider s) {
        if (s == null) return;
        s.setBackground(PANEL_DARKER);
        s.setForeground(TEXT);
        s.setOpaque(true);
        // color label table if present
        if (s.getLabelTable() != null) {
            java.util.Enumeration<?> keys = s.getLabelTable().keys();
            while (keys.hasMoreElements()) {
                Object k = keys.nextElement();
                Object v = s.getLabelTable().get(k);
                if (v instanceof javax.swing.JLabel) {
                    javax.swing.JLabel lbl = (javax.swing.JLabel) v;
                    lbl.setForeground(TEXT);
                    lbl.setOpaque(false);
                }
            }
        }
    }

    public static void styleComboBox(JComboBox<?> cb) {
        if (cb == null) return;
        cb.setBackground(PANEL_DARKER);
        cb.setForeground(TEXT);
        cb.setOpaque(true);
    }

    public static void styleTable(JTable table) {
        if (table == null) return;
        table.setBackground(TABLE_ROW);
        table.setForeground(TEXT);
        table.setShowGrid(false);
        table.setRowHeight(26);
        table.setSelectionBackground(TABLE_SELECTION);
        table.setSelectionForeground(TEXT);
        if (table.getTableHeader() != null) {
            table.getTableHeader().setBackground(HEADER_BG);
            table.getTableHeader().setForeground(TEXT);
            table.getTableHeader().setOpaque(true);
        }
    }

    public static void styleScrollViewport(javax.swing.JScrollPane scroll) {
        if (scroll == null) return;
        scroll.getViewport().setBackground(BACKGROUND);
        scroll.setOpaque(false);
        scroll.setBorder(javax.swing.BorderFactory.createEmptyBorder());
    }
}
