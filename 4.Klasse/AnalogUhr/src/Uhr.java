import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Uhr extends JFrame implements Runnable {

    private JPanel UhrPanel;//form
    private AnalogClockPanel analogPanel;//uhr in mitte
    private JLabel lblDigitalTime;//uhr die unten ist
    private JButton btnStartStop;//start/stop für stopuhr
    private JLabel lblStopwatchTime; // zeigt die gestoppte/aktuelle Stopuhrzeit

    private volatile boolean running = false;
    private Thread clockThread;
    private volatile boolean stopwatchRunning = false;
    private volatile long stopwatchStartMillis = 0L;
    private volatile long stopwatchElapsedMillis = 0L;
    private Thread stopwatchThread;
    private static final DateTimeFormatter DIGITAL_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public Uhr() {
        setTitle("Fensta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnStartStop = new JButton("Start");
        lblStopwatchTime = new JLabel("00:00.000");
        lblStopwatchTime.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        btnStartStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!stopwatchRunning) {
                    stopwatchElapsedMillis = 0L;
                    startStopwatch();
                } else {
                    stopStopwatch();
                }
            }
        });
        top.add(lblStopwatchTime);
        top.add(btnStartStop);
        UhrPanel.add(top, BorderLayout.NORTH);

        pack();
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override public void windowOpened(WindowEvent e) { startClockThread(); }
            @Override public void windowClosing(WindowEvent e) { stopClockThread(); }
        });
    }

    private void runOnEDT(Runnable r) {
        SwingUtilities.invokeLater(r);
    }

    private void startStopwatch() {
        if (stopwatchRunning) return;
        stopwatchRunning = true;
        btnStartStop.setText("Stop");
        stopwatchStartMillis = System.currentTimeMillis();
        stopwatchThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (stopwatchRunning && !Thread.currentThread().isInterrupted()) {
                        stopwatchElapsedMillis = System.currentTimeMillis() - stopwatchStartMillis;
                        runOnEDT(new Runnable() {
                            @Override
                            public void run() {
                                analogPanel.setStopwatchMillis(stopwatchElapsedMillis);
                                analogPanel.setStopwatchRunning(true);
                                lblStopwatchTime.setText(formatMillis(stopwatchElapsedMillis));
                            }
                        });
                        Thread.sleep(10);
                    }
                } catch (InterruptedException ignored) {
                } finally {
                    runOnEDT(new Runnable() {
                        @Override
                        public void run() {
                            analogPanel.setStopwatchRunning(stopwatchRunning);
                        }
                    });
                }
            }
        }, "StopwatchThread");
        stopwatchThread.setDaemon(true);
        stopwatchThread.start();
    }

    private void stopStopwatch() {
        if (!stopwatchRunning) return;
        stopwatchRunning = false;
        btnStartStop.setText("Start");
        if (stopwatchThread != null) stopwatchThread.interrupt();
    }

    private String formatMillis(long ms) {
        long hours = ms / 3600000L;
        long rem = ms % 3600000L;
        long minutes = rem / 60000L;
        rem = rem % 60000L;
        long seconds = rem / 1000L;
        long millis = rem % 1000L;
        if (hours > 0) {
            return String.format("%d:%02d:%02d.%03d", hours, minutes, seconds, millis);
        }
        return String.format("%02d:%02d.%03d", minutes, seconds, millis);
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

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    analogPanel.setTime(now);
                    lblDigitalTime.setText(DIGITAL_FMT.format(now));
                }
            });

            try {
                long ms = System.currentTimeMillis();
                long sleep = 1000 - (ms % 1000);
                Thread.sleep(sleep);
            } catch (InterruptedException ignored) {}
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Uhr().setVisible(true);
            }
        });
    }
}