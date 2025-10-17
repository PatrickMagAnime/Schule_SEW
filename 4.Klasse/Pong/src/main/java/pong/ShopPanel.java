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
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class ShopPanel extends JPanel {
    private final AppWindow window;
    private final AppContext context;
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Name", "Preis", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final JButton buyButton = new JButton("Kaufen");
    private final JButton backButton = new JButton("Zurück");
    private final List<Storage.ShopItem> displayedItems = new ArrayList<>();

    public ShopPanel(AppWindow window, AppContext context) {
        this.window = Objects.requireNonNull(window);
        this.context = Objects.requireNonNull(context);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Shop");
        title.setAlignmentX(CENTER_ALIGNMENT);
        add(title);
        add(Box.createVerticalStrut(10));

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setPreferredScrollableViewportSize(new Dimension(420, 220));
        add(new JScrollPane(table));
        add(Box.createVerticalStrut(10));

        buyButton.setAlignmentX(CENTER_ALIGNMENT);
        backButton.setAlignmentX(CENTER_ALIGNMENT);
        add(buyButton);
        add(Box.createVerticalStrut(10));
        add(backButton);
        add(Box.createVerticalGlue());

        buyButton.addActionListener(e -> openPaymentForSelected());
        backButton.addActionListener(e -> window.showMenu());
        table.getSelectionModel().addListSelectionListener(e -> updateButtonState());
    }

    public void refresh() {
        tableModel.setRowCount(0);
        displayedItems.clear();
        Storage.ShopCatalog catalog = context.shopCatalog();
        if (catalog.items != null) {
            for (Storage.ShopItem item : catalog.items) {
                displayedItems.add(item);
                String price = String.format(Locale.GERMANY, "%.2f %s", item.price, item.currency);
                boolean owned = context.isSkinOwned(item.id);
                String status = owned ? "Besitzt" : "Verfügbar";
                tableModel.addRow(new Object[]{item.name, price, status});
            }
        }
        if (tableModel.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        }
        updateButtonState();
    }

    private void updateButtonState() {
        int row = table.getSelectedRow();
        boolean enable = row >= 0;
        if (enable) {
            Storage.ShopItem item = displayedItems.get(row);
            enable = !context.isSkinOwned(item.id);
        }
        buyButton.setEnabled(enable);
    }

    private void openPaymentForSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        Storage.ShopItem item = displayedItems.get(row);
        if (context.isSkinOwned(item.id)) {
            return;
        }
        PaymentDialog dialog = new PaymentDialog(window, context, item);
        dialog.setLocationRelativeTo(window);
        dialog.setVisible(true);
        if (dialog.wasSuccessful()) {
            window.notifySkinInventoryChanged();
            refresh();
        }
    }
}
