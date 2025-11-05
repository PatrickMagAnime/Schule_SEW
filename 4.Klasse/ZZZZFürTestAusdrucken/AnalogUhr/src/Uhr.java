import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

class Uhr extends JFrame {
    // Für IntelliJ .form-Binding
    private JPanel UhrPanel;

    private final AnalogClockPanel analog = new AnalogClockPanel();
    private final JLabel digital = new JLabel("", SwingConstants.CENTER);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    Uhr() {
        super("Uhr");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        UhrPanel = new JPanel(new BorderLayout());
        setContentPane(UhrPanel);

        analog.setPreferredSize(new Dimension(300, 300));
        digital.setFont(new Font(Font.MONOSPACED, Font.BOLD, 22));
        digital.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        UhrPanel.add(analog, BorderLayout.CENTER);
        UhrPanel.add(digital, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);

        Thread th = new Thread(new Runnable() {
            @Override public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    final LocalTime t = LocalTime.now();
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override public void run() {
                            analog.setTime(t);
                            digital.setText(FMT.format(t));
                        }
                    });
                    try {
                        long ms = System.currentTimeMillis();
                        Thread.sleep(1000 - (ms % 1000));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }, "ClockThread");
        th.setDaemon(true);
        th.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() { new Uhr().setVisible(true); }
        });
    }
}