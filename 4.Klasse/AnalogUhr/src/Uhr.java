import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Uhr extends JFrame implements Runnable {

    private JPanel UhrPanel;//form
    private AnalogClockPanel analogPanel;//die mitte
    private JLabel lblDigitalTime;//süden

    private volatile boolean running = false;
    private Thread clockThread;
    private static final DateTimeFormatter DIGITAL_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public Uhr() {
        setTitle("Fensta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if (UhrPanel == null) {
            UhrPanel = new JPanel(new BorderLayout());
        }
        setContentPane(UhrPanel);
        if (!(UhrPanel.getLayout() instanceof BorderLayout)) {
            UhrPanel.setLayout(new BorderLayout());
        }
        if (analogPanel == null) {
            analogPanel = new AnalogClockPanel();
            analogPanel.setPreferredSize(new Dimension(320, 320));
            UhrPanel.add(analogPanel, BorderLayout.CENTER);
        }
        if (lblDigitalTime == null) {
            lblDigitalTime = new JLabel("--:--:--", SwingConstants.CENTER);
            lblDigitalTime.setFont(new Font(Font.MONOSPACED, Font.BOLD, 24));
            lblDigitalTime.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            UhrPanel.add(lblDigitalTime, BorderLayout.SOUTH);
        }

        pack();
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override public void windowOpened(WindowEvent e) { startClockThread(); }
            @Override public void windowClosing(WindowEvent e) { stopClockThread(); }
        });
    }

    private void startClockThread() {
        if (running) return;
        running = true;
        clockThread = new Thread(this, "ClockThread");
        clockThread.setDaemon(true);
        clockThread.start();
    }

    private void stopClockThread() {
        running = false;
        if (clockThread != null) clockThread.interrupt();
    }

    @Override
    public void run() {
        while (running) {
            LocalTime now = LocalTime.now();

            SwingUtilities.invokeLater(() -> {
                analogPanel.setTime(now);
                lblDigitalTime.setText(DIGITAL_FMT.format(now));
            });

            try {
                long ms = System.currentTimeMillis();
                long sleep = 1000 - (ms % 1000);
                Thread.sleep(sleep);
            } catch (InterruptedException ignored) {}
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Uhr().setVisible(true));
    }
}