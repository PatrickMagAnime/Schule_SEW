package pong;

/**
 * Ball
 * Position, Geschwindigkeit und Größe des Balls. Bietet Hilfsmethoden für
 * Kollision/Bewegung und zur Anpassung der Geschwindigkeit (Richtung bleibt erhalten).
 */
public final class Ball {
    private double x;
    private double y;
    private double vx;
    private double vy;
    private double size;

    public Ball(double x, double y, double size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public void setVelocity(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }

    public void setPosition(double x, double y) {

        this.x = x;
        this.y = y;
    }

    public void update() {
        x += vx;
        y += vy;
    }

    public void clampPosition(double minX, double minY, double maxX, double maxY) {
        if (x < minX) {
            x = minX;
        }
        if (y < minY) {
            y = minY;
        }
        if (x > maxX) {
            x = maxX;
        }
        if (y > maxY) {
            y = maxY;
        }
    }

    public void invertX() {
        vx = -vx;
    }

    public void invertY() {
        vy = -vy;
    }

    /**
     * Setzt die Geschwindigkeit im Betrag, behält aber X-/Y-Richtung (Vorzeichen) bei.
     */
    public void setSpeed(double speed) {
        double signX = Math.signum(vx);
        double signY = Math.signum(vy);
        if (signX == 0) {
            signX = 1;
        }
        if (signY == 0) {
            signY = 1;
        }
        vx = signX * speed;
        vy = signY * speed;
    }

    public double left() {
        return x;
    }

    public double right() {
        return x + size;
    }

    public double top() {
        return y;
    }

    public double bottom() {
        return y + size;
    }

    public double centerX() {
        return x + size / 2.0;
    }

    public double centerY() {
        return y + size / 2.0;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double velocityX() {
        return vx;
    }

    public double velocityY() {
        return vy;
    }

    public double size() {
        return size;
    }
}
