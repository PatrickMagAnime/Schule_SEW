package pong;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class LeaderboardPanel extends JPanel {
    private static final String MODE_SP = "SP";
    private static final String MODE_MP = "1v1";

    private final AppWindow window;
    private final AppContext context;
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Name", "Score", "Datum"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final JComboBox<String> modeSelector = new JComboBox<>(new String[]{"Single Player", "1v1"});

    public LeaderboardPanel(AppWindow window, AppContext context) {
        this.window = Objects.requireNonNull(window);
        this.context = Objects.requireNonNull(context);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Leaderboard");
        title.setAlignmentX(CENTER_ALIGNMENT);
        add(title);
        add(Box.createVerticalStrut(10));

        modeSelector.setMaximumSize(modeSelector.getPreferredSize());
        modeSelector.addActionListener(e -> refreshTable());
        add(modeSelector);
        add(Box.createVerticalStrut(10));

        table.setFillsViewportHeight(true);
        add(new JScrollPane(table));
        add(Box.createVerticalStrut(10));

        JButton backButton = new JButton("Zurück");
        backButton.setAlignmentX(CENTER_ALIGNMENT);
        backButton.addActionListener(e -> window.showMenu());
        add(backButton);
        add(Box.createVerticalGlue());
    }

    public void refresh() {
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        String modeKey = MODE_SP;
        Object selection = modeSelector.getSelectedItem();
        if (selection != null && selection.toString().contains("1v1")) {
            modeKey = MODE_MP;
        }
        List<Storage.LeaderboardEntry> entries = context.leaderboardForMode(modeKey);
        for (Storage.LeaderboardEntry entry : entries) {
            tableModel.addRow(new Object[]{entry.name, entry.score, entry.date});
        }
    }
}
