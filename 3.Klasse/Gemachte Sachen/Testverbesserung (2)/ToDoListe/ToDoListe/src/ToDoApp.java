import java.io.IOException;
import java.util.*;
import javax.swing.*;

public class ToDoApp extends JFrame {
    private JTextField tfIndex;
    private JTextArea taOutput;
    private JButton neuButton;
    private JButton bearbeitenButton;
    private JButton loeschenButton;
    private JButton ladenButton;
    private JButton speichernButton;
    private JPanel mainPanel;
    private JProgressBar progressBar;
    private ArrayList<ToDoItem> todos = new ArrayList<>();

    public ToDoApp() {
        neuButton.addActionListener(e -> {
            new ToDoDialog(todos, -1, ToDoApp.this).setVisible(true);
        });
        bearbeitenButton.addActionListener(e -> {
            int idx = getIndex();
            if (idx < 0 || idx >= todos.size()) {
                JOptionPane.showMessageDialog(this, "Ungültiger Index");
                return;
            }
            new ToDoDialog(todos, idx, ToDoApp.this).setVisible(true);
        });
        loeschenButton.addActionListener(e -> {
            int idx = getIndex();
            if (idx < 0 || idx >= todos.size()) {
                JOptionPane.showMessageDialog(this, "Ungültiger Index");
                return;
            }
            todos.remove(idx);
            updateOutput();
        });
        ladenButton.addActionListener(e -> {
            try {
                todos = ToDoJsonManager.loadToDos();
                updateOutput();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Fehler beim Laden: " + ex.getMessage());
            }
        });
        speichernButton.addActionListener(e -> {
            try {
                ToDoJsonManager.saveToDos(todos);
                JOptionPane.showMessageDialog(this, "Gespeichert!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Fehler beim Speichern: " + ex.getMessage());
            }
        });
        updateOutput();
    }

    private int getIndex() {
        String s = tfIndex.getText().trim();
        if(!s.matches("\\d+")) return -1;
        return Integer.parseInt(s);
    }

    public void updateOutput() {
        // Nach Priorität 5 → 1 sortieren
        todos.sort(Comparator.comparingInt(ToDoItem::getPrioritaet).reversed());
        StringBuilder sb = new StringBuilder();
        int erledigte = 0;
        for (int i = 0; i < todos.size(); i++) {
            ToDoItem t = todos.get(i);
            sb.append(i).append(": ").append(t.toString()).append("\n");
            if (t.isErledigt()) erledigte++;
        }
        taOutput.setText(sb.toString());
        // ProgressBar
        if (todos.size() > 0) {
            progressBar.setValue(100 * erledigte / todos.size());
        } else {
            progressBar.setValue(0);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ToDoApp");
        frame.setContentPane(new ToDoApp().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(700, 400);
        frame.setVisible(true);
    }
}