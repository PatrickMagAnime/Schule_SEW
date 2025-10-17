package pong;

public final class Paddle {
    private double x;
    private double y;
    private final double width;
    private final double height;
    private double velocity;

    public Paddle(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public void update() {
        y += velocity;
    }

    public double left() {
        return x;
    }

    public double right() {
        return x + width;
    }

    public double top() {
        return y;
    }

    public double bottom() {
        return y + height;
    }

    public double width() {
        return width;
    }

    public double height() {
        return height;
    }

    public double centerY() {
        return y + height / 2.0;
    }
}
