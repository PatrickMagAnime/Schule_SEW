import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class CarRace {
    private final JButton[] cars;
    private final Timer timer;
    private final Random rnd = new Random();

    private final Point[] startPositions;
    private final int[] finishX; // Ziellinien-X für jedes Auto (gecacht)

    public CarRace(JButton car1, JButton car2, JButton car3) {
        this.cars = new JButton[]{car1, car2, car3};
        // 20 ms ~ 50 FPS, fühlt sich meist flüssiger an als 30 ms
        this.timer = new Timer(20, e -> tick());
        this.timer.setCoalesce(true); // mehrere fällige Events zusammenfassen
        this.startPositions = new Point[cars.length];
        this.finishX = new int[cars.length];
    }

    public void captureStartPositionsFromCurrentBounds() {
        for (int i = 0; i < cars.length; i++) {
            // validate() ist hier nicht nötig
            startPositions[i] = new Point(cars[i].getX(), cars[i].getY());
        }
        // Beim Erfassen direkt einmal die Ziellinie berechnen (falls Größen schon feststehen)
        recomputeFinishLines();
    }

    public void start() {
        // Vor dem Start noch einmal die Ziellinie anhand der aktuellen Größen berechnen
        // (falls das Fenster zwischenzeitlich sichtbar/resize wurde)
        recomputeFinishLines();
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    public void stop() {
        timer.stop();
    }

    public void reset() {
        stop();
        for (int i = 0; i < cars.length; i++) {
            Point p = startPositions[i];
            if (p != null) {
                cars[i].setLocation(p.x, p.y);
            }
        }
    }

    private void tick() {
        int winnerIndex = -1;

        for (int i = 0; i < cars.length; i++) {
            JButton car = cars[i];

            // Schrittweite 1..5
            int dx = rnd.nextInt(5) + 1;
            int newX = cars[i].getX() + dx;

            // an Ziellinie kappen
            if (newX > finishX[i]) {
                newX = finishX[i];
            }

            if (newX != car.getX()) {
                car.setLocation(newX, car.getY());
            }

            // Gewinner ermitteln (in derselben Schleife, spart einen zweiten Durchlauf)
            if (newX >= finishX[i]) {
                winnerIndex = i;
            }
        }

        if (winnerIndex != -1) {
            stop();
            JButton winner = cars[winnerIndex];
            String name = winner.getName();
            if (name == null || name.isBlank()) {
                String txt = winner.getText();
                name = (txt == null || txt.isBlank()) ? "Car" : txt;
            }
            // Hinweis: JOptionPane blockiert die EDT (normal bei Swing).
            // Hier ist das okay, weil das Rennen bereits gestoppt wurde.
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(winner), name + " gewinnt");
        }
    }

    private void recomputeFinishLines() {
        for (int i = 0; i < cars.length; i++) {
            JButton car = cars[i];
            Container p = car.getParent();
            if (p == null) {
                finishX[i] = Integer.MAX_VALUE; // sollte nicht passieren, aber verhindert NPE
            } else {
                // 10px Rand rechts
                int fx = p.getWidth() - car.getWidth() - 10;
                finishX[i] = Math.max(0, fx);
            }
        }
    }
}