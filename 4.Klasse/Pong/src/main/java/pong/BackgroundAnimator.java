package pong;

import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.awt.Component;
import javax.swing.JButton;

/**
 * BackgroundAnimator
 * Zeichnet dekorative, langsam herum-bounce'nde Farbkugeln im Hintergrund.
 * - Verwaltet eine Liste zufällig initialisierter Bälle in Normalized-Koordinaten (0..1).
 * - Nutzt WeakReferences für Panels, damit keine Memory-Leaks entstehen.
 * - Ein einzelner Timer treibt alle registrierten Panels an (repaint auf jedem Tick).
 */
final class BackgroundAnimator {
    private static final int BALL_COUNT = 8;
    private static final int TIMER_MS = 30;
    private static final List<DecorativeBall> balls = new ArrayList<>();
    private static final List<WeakReference<Component>> listeners = new ArrayList<>();
    private static final Random RANDOM = new Random(12345);
    private static Timer timer;

    /** Startet den Timer einmalig und erstellt die dekorativen Bälle. */
    static synchronized void ensureStarted() {
        if (timer != null) return;
        for (int i = 0; i < BALL_COUNT; i++) {
            balls.add(new DecorativeBall(RANDOM));
        }
        timer = new Timer(TIMER_MS, e -> {
            updateBalls();
            // repaint all registered listeners
            Iterator<WeakReference<Component>> it = listeners.iterator();
            while (it.hasNext()) {
                WeakReference<Component> ref = it.next();
                Component c = ref.get();
                if (c == null) {
                    it.remove();
                } else {
                    c.repaint();
                }
            }
        });
        timer.start();
    }

    /** Panel registrieren, damit es automatisch neu gezeichnet wird. */
    static synchronized void register(Component component) {
        ensureStarted();
        // keep weak refs so panels can be GC'd
        listeners.add(new WeakReference<>(component));
    }

    /** Zeichnet alle Bälle relativ zur Panelgröße. */
    static void paint(Graphics2D g2, int width, int height) {
        if (width <= 0 || height <= 0) return;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (DecorativeBall b : balls) {
            double x = b.x * width;
            double y = b.y * height;
            double size = Math.max(6, b.size * Math.min(width, height));
            g2.setColor(b.color);
            g2.fill(new Ellipse2D.Double(x - size / 2.0, y - size / 2.0, size, size));
        }
    }

    /** Vereinheitlichtes Button-Styling für das Dark Theme. */
    static void styleButton(javax.swing.JButton btn) {
        btn.setBackground(new Color(0x2E2E2E));
        btn.setForeground(new Color(0xFFFFFF));
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0x666666), 2, true));
    }

    /** Aktualisiert Position + Bounce-Reflektion der Bälle pro Tick. */
    private static void updateBalls() {
        for (DecorativeBall b : balls) {
            b.x += b.vx;
            b.y += b.vy;
            if (b.x < 0) {
                b.x = 0;
                b.vx = Math.abs(b.vx);
            } else if (b.x > 1) {
                b.x = 1;
                b.vx = -Math.abs(b.vx);
            }
            if (b.y < 0) {
                b.y = 0;
                b.vy = Math.abs(b.vy);
            } else if (b.y > 1) {
                b.y = 1;
                b.vy = -Math.abs(b.vy);
            }
        }
    }

    /** Interner Datenhalter für einen dekorativen Ball (normalized Position + Velocity). */
    private static final class DecorativeBall {
        double x;
        double y;
        double vx;
        double vy;
        double size;
        Color color;

        DecorativeBall(Random rnd) {
            this.x = rnd.nextDouble();
            this.y = rnd.nextDouble();
            this.vx = (rnd.nextDouble() - 0.5) * 0.01;
            this.vy = (rnd.nextDouble() - 0.5) * 0.01;
            this.size = 0.02 + rnd.nextDouble() * 0.04;
            this.color = new Color(java.awt.Color.HSBtoRGB((float) rnd.nextDouble(), 0.6f, 0.95f));
        }
    }
}
