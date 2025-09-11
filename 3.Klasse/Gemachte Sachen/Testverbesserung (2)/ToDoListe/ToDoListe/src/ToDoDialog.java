import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class ToDoDialog extends JDialog {
    private JTextField tfTitel;
    private JTextField tfBeschreibung;
    private JTextField tfPrioritaet;
    private JComboBox<Category> cbKategorie;
    private JCheckBox erledigtCheckBox;
    private JButton speichernButton;
    private JButton farbeButton;
    private JPanel dialogPanel;
    private Color selectedColor = null;

    public ToDoDialog(ArrayList<ToDoItem> todos, int index, ToDoApp parent) {
        this.setContentPane(dialogPanel);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(350, 350);

        cbKategorie.setModel(new DefaultComboBoxModel<>(Category.values()));

        if(index >= 0) {
            // Bearbeiten
            ToDoItem item = todos.get(index);
            tfTitel.setText(item.getTitel());
            tfBeschreibung.setText(item.getBeschreibung());
            tfPrioritaet.setText(String.valueOf(item.getPrioritaet()));
            erledigtCheckBox.setSelected(item.isErledigt());
            cbKategorie.setSelectedItem(item.getKategorie());
            selectedColor = item.getFarbeAsColor();
        }

        farbeButton.addActionListener(e -> {
            Color color = JColorChooser.showDialog(this, "Farbe wählen", selectedColor);
            if(color != null) {
                selectedColor = color;
            }
        });

        speichernButton.addActionListener(e -> {
            // Validierung
            String titel = tfTitel.getText().trim();
            String prioritaetText = tfPrioritaet.getText().trim();
            if (titel.isEmpty() || prioritaetText.isEmpty() || !prioritaetText.matches("\\d+")) {
                JOptionPane.showMessageDialog(ToDoDialog.this, "Titel und Priorität (ganzzahlig) sind Pflichtfelder!");
                return;
            }
            int prio = Integer.parseInt(prioritaetText);
            String beschreibung = tfBeschreibung.getText().trim();
            boolean erledigt = erledigtCheckBox.isSelected();
            Category kat = (Category) cbKategorie.getSelectedItem();
            String farbe = null;
            if (selectedColor != null) {
                farbe = String.format("#%02x%02x%02x", selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue());
            }

            ToDoItem item = new ToDoItem(titel, beschreibung, erledigt, prio, kat, farbe);

            if(index < 0) {
                // Neu
                todos.add(item);
            } else {
                // Bearbeiten
                todos.set(index, item);
            }
            parent.updateOutput();
            dispose();
        });
    }
}