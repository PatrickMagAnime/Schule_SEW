import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;

public class AnalogClockPanel extends JPanel {

    private LocalTime time = LocalTime.now();

    public void setTime(LocalTime t) {
        this.time = t;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int size = Math.min(w, h);
            int cx = w / 2;
            int cy = h / 2;
            int radius = (int) (size * 0.45);

            g2.setColor(getBackground());
            g2.fillRect(0, 0, w, h);

            g2.setColor(new Color(245, 245, 245));
            g2.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
            g2.setColor(Color.DARK_GRAY);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);

            g2.setStroke(new BasicStroke(3f));
            for (int i = 0; i < 12; i++) {
                double angle = Math.toRadians(i * 30 - 90);
                int inner = (int) (radius * 0.80);
                int outer = radius - 4;
                int x1 = cx + (int) Math.round(inner * Math.cos(angle));
                int y1 = cy + (int) Math.round(inner * Math.sin(angle));
                int x2 = cx + (int) Math.round(outer * Math.cos(angle));
                int y2 = cy + (int) Math.round(outer * Math.sin(angle));
                g2.drawLine(x1, y1, x2, y2);
            }

            g2.setStroke(new BasicStroke(1.2f));
            g2.setColor(new Color(120, 120, 120));
            for (int i = 0; i < 60; i++) {
                if (i % 5 == 0) continue;
                double angle = Math.toRadians(i * 6 - 90);
                int inner = (int) (radius * 0.86);
                int outer = radius - 4;
                int x1 = cx + (int) Math.round(inner * Math.cos(angle));
                int y1 = cy + (int) Math.round(inner * Math.sin(angle));
                int x2 = cx + (int) Math.round(outer * Math.cos(angle));
                int y2 = cy + (int) Math.round(outer * Math.sin(angle));
                g2.drawLine(x1, y1, x2, y2);
            }

            int hour = time.getHour();
            int minute = time.getMinute();
            int second = time.getSecond();

            double hourAngle   = Math.toRadians(((hour % 12) + minute / 60.0 + second / 3600.0) * 30 - 90);
            double minuteAngle = Math.toRadians((minute + second / 60.0) * 6 - 90);
            double secondAngle = Math.toRadians(second * 6 - 90);

            int hourLen = (int) (radius * 0.55);
            int minuteLen = (int) (radius * 0.75);
            int secondLen = (int) (radius * 0.82);

            g2.setColor(new Color(30, 30, 30));
            g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            drawHand(g2, cx, cy, hourLen, hourAngle);

            g2.setColor(new Color(50, 50, 50));
            g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            drawHand(g2, cx, cy, minuteLen, minuteAngle);

            g2.setColor(new Color(200, 50, 50));
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            drawHand(g2, cx, cy, secondLen, secondAngle);

            g2.setColor(new Color(30, 30, 30));
            g2.fillOval(cx - 5, cy - 5, 10, 10);
        } finally {
            g2.dispose();
        }
    }

    private void drawHand(Graphics2D g2, int cx, int cy, int length, double angleRad) {
        int x = cx + (int) Math.round(length * Math.cos(angleRad));
        int y = cy + (int) Math.round(length * Math.sin(angleRad));
        g2.drawLine(cx, cy, x, y);
    }
}