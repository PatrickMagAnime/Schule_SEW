import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class CarRace {
    private final JButton[] cars;
    private final Timer timer;
    private final Random rnd = new Random();

    private final Point[] startPositions;
    private final int[] finishX;

    public CarRace(JButton car1, JButton car2, JButton car3) {
        this.cars = new JButton[]{car1, car2, car3};

        this.timer = new Timer(20, e -> tick());
        this.timer.setCoalesce(true);
        this.startPositions = new Point[cars.length];
        this.finishX = new int[cars.length];
    }

    public void captureStartPositionsFromCurrentBounds() {
        for (int i = 0; i < cars.length; i++) {

            startPositions[i] = new Point(cars[i].getX(), cars[i].getY());
        }

        recomputeFinishLines();
    }

    public void start() {

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

            int dx = rnd.nextInt(5) + 1;
            int newX = cars[i].getX() + dx;

            if (newX > finishX[i]) {
                newX = finishX[i];
            }

            if (newX != car.getX()) {
                car.setLocation(newX, car.getY());
            }

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
            //stack overflow copy paste, hoffe das geht in ordnung
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(winner), name + " gewinnt");
        }
    }

    private void recomputeFinishLines() {
        for (int i = 0; i < cars.length; i++) {
            JButton car = cars[i];
            Container p = car.getParent();
            if (p == null) {
                finishX[i] = Integer.MAX_VALUE;
            } else {
                int fx = p.getWidth() - car.getWidth() - 10;
                finishX[i] = Math.max(0, fx);
            }
        }
    }
}