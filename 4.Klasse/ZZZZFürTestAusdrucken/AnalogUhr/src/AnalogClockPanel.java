import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;

class AnalogClockPanel extends JPanel {
    private LocalTime time = LocalTime.now();
    void setTime(LocalTime t) { time = t; repaint(); }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight(), s = Math.min(w, h);
        int cx = w / 2, cy = h / 2, r = (int) (s * 0.45);

        g2.setColor(Color.WHITE);
        g2.fillOval(cx - r, cy - r, 2 * r, 2 * r);
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(cx - r, cy - r, 2 * r, 2 * r);

        g2.setStroke(new BasicStroke(3f));
        for (int i = 0; i < 12; i++) {
            double a = Math.toRadians(i * 30 - 90);
            int in = (int) (r * 0.80), out = r - 4;
            int x1 = cx + (int) Math.round(in * Math.cos(a));
            int y1 = cy + (int) Math.round(in * Math.sin(a));
            int x2 = cx + (int) Math.round(out * Math.cos(a));
            int y2 = cy + (int) Math.round(out * Math.sin(a));
            g2.drawLine(x1, y1, x2, y2);
        }

        int H = time.getHour(), M = time.getMinute(), S = time.getSecond();
        double ah = Math.toRadians(((H % 12) + M / 60.0 + S / 3600.0) * 30 - 90);
        double am = Math.toRadians((M + S / 60.0) * 6 - 90);
        double as = Math.toRadians(S * 6 - 90);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        drawHand(g2, cx, cy, (int) (r * 0.55), ah);

        g2.setColor(Color.GRAY);
        g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        drawHand(g2, cx, cy, (int) (r * 0.75), am);

        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        drawHand(g2, cx, cy, (int) (r * 0.82), as);

        g2.setColor(Color.BLACK);
        g2.fillOval(cx - 4, cy - 4, 8, 8);
    }

    private void drawHand(Graphics2D g2, int cx, int cy, int len, double a) {
        int x = cx + (int) Math.round(len * Math.cos(a));
        int y = cy + (int) Math.round(len * Math.sin(a));
        g2.drawLine(cx, cy, x, y);
    }
}