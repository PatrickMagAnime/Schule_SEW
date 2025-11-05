import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class CarRace {
    private final JButton[] cars;
    private final Point[] startPos;
    private final int[] finishX;
    private final Random rnd = new Random();
    private Thread thread;
    private volatile boolean running;

    public CarRace(JButton car1, JButton car2, JButton car3) {
        this.cars = new JButton[]{car1, car2, car3};
        this.startPos = new Point[cars.length];
        this.finishX = new int[cars.length];
    }

    // Startpositionen aus den aktuellen Bounds merken (einmal nach UI-Aufbau aufrufen)
    public void captureStartPositionsFromCurrentBounds() {
        for (int i = 0; i < cars.length; i++) {
            startPos[i] = new Point(cars[i].getX(), cars[i].getY());
        }
        recomputeFinishLines();
    }

    public void start() {
        if (running) return;
        recomputeFinishLines();
        running = true;
        thread = new Thread(new Runnable() {
            @Override public void run() {
                while (running) {
                    try {
                        // UI-Änderungen auf dem EDT ausführen
                        SwingUtilities.invokeAndWait(new Runnable() {
                            @Override public void run() {
                                int winner = -1;
                                for (int i = 0; i < cars.length; i++) {
                                    JButton c = cars[i];
                                    int newX = Math.min(c.getX() + (rnd.nextInt(5) + 1), finishX[i]);
                                    if (newX != c.getX()) c.setLocation(newX, c.getY());
                                    if (newX >= finishX[i]) winner = i;
                                }
                                if (winner != -1) {
                                    running = false; // Thread-Schleife beenden
                                    JButton w = cars[winner];
                                    String name = (w.getName() != null && !w.getName().isBlank())
                                            ? w.getName()
                                            : (w.getText() == null || w.getText().isBlank() ? "Car" : w.getText());
                                    JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(w), name + " gewinnt");
                                }
                            }
                        });
                        Thread.sleep(20);
                    } catch (Exception e) {
                        break; // Interrupted/Invoke-Fehler: Thread beenden
                    }
                }
            }
        }, "RaceThread");
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        running = false;
        if (thread != null) thread.interrupt();
    }

    public void reset() {
        stop();
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                for (int i = 0; i < cars.length; i++) {
                    Point p = startPos[i];
                    if (p != null) cars[i].setLocation(p.x, p.y);
                }
            }
        });
    }

    private void recomputeFinishLines() {
        for (int i = 0; i < cars.length; i++) {
            Container p = cars[i].getParent();
            finishX[i] = (p == null) ? Integer.MAX_VALUE : Math.max(0, p.getWidth() - cars[i].getWidth() - 10);
        }
    }
}