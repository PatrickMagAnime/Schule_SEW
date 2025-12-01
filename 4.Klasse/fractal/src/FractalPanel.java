import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

public class FractalPanel extends JPanel {

    // Over-Engineered Core (Maximaler Parallelismus)
    private static final int THREADS = Runtime.getRuntime().availableProcessors();
    private static final ForkJoinPool POOL = new ForkJoinPool(THREADS);

    // --- Konstanten & Parameter ---

    int mode = 0;
    int maxIterations = 150;

    final double initialScale = 300.0;
    private static final int ITER_BASE = 150;
    private static final int ITER_FACTOR = 700;
    private static final int FAST_ITER_COUNT = 15;
    private static final float FAST_BRIGHTNESS = 3.0f;
    private static final int TILE_SIZE = 64; // Neue Kachelgröße für Tiling

    // Kosmetik
    int baseColor = Color.BLUE.getRGB();
    float colorShift = 0.0f;
    double brightnessGlobal = 1.0;
    float colorFrequency = 0.0f;

    // --- Viewport ---
    private BigDouble centerX = new BigDouble(-0.7);
    private BigDouble centerY = new BigDouble(0.0);
    private BigDouble scaleBD = new BigDouble(initialScale);
    private double currentScale = initialScale;

    // --- System & Caching ---
    private BufferedImage displayImage;
    private int[] buffer;
    private final AtomicLong currentEpoch = new AtomicLong(0);
    private JPanel sidebarRef;

    // Speicher für die letzte Schwenkbewegung
    private int lastOffsetX = 0;
    private int lastOffsetY = 0;
    private boolean isPanning = false;

    public FractalPanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
        //setupKeyBindings();
        setupMouseListeners();

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                // Beim Resizing muss immer das gesamte Bild neu gerendert werden
                lastOffsetX = 0;
                lastOffsetY = 0;
                requestRender(true);
            }
        });
    }

    public void setSidebarReference(JPanel sidebar) { this.sidebarRef = sidebar; }

    // --- Interaktion (Optimiert) ---
    private void setupMouseListeners() {
        MouseAdapter ma = new MouseAdapter() {
            private Point lastDrag;

            @Override public void mouseWheelMoved(MouseWheelEvent e) {
                double factor = Math.pow(1.2, -e.getPreciseWheelRotation());
                // Beim Zoomen immer vollen Render starten (kein Caching)
                lastOffsetX = 0; lastOffsetY = 0;
                zoomToPoint(factor, e.getPoint());
            }

            @Override public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    lastOffsetX = 0; lastOffsetY = 0;
                    zoomToPoint(1.5, e.getPoint());
                }
            }

            @Override public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    lastDrag = e.getPoint();
                    isPanning = true;
                }
            }

            @Override public void mouseDragged(MouseEvent e) {
                if (lastDrag != null && e.getModifiersEx() == MouseEvent.BUTTON3_DOWN_MASK) {

                    int dx = e.getX() - lastDrag.x;
                    int dy = e.getY() - lastDrag.y;

                    // Schwenken der Koordinate in BigDouble
                    // Berechnung der Verschiebung in Fraktal-Einheiten
                    BigDouble shiftX = BigDouble.divide(new BigDouble(dx), scaleBD.xh);
                    BigDouble shiftY = BigDouble.divide(new BigDouble(dy), scaleBD.xh);

                    centerX = BigDouble.subtract(centerX, shiftX);
                    centerY = BigDouble.subtract(centerY, shiftY);

                    lastDrag = e.getPoint();

                    // NEU: Aktuellen Offset speichern, um nur die Ränder zu berechnen
                    lastOffsetX += dx;
                    lastOffsetY += dy;

                    // Low-Res Render reicht während des Draggings
                    requestRender(false);
                }
            }
            @Override public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    isPanning = false;
                    // Nach dem Pan: Volles High-Quality-Bild rendern
                    lastOffsetX = 0;
                    lastOffsetY = 0;
                    requestRender(true);
                }
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
        addMouseWheelListener(ma);
    }

    public void resetView() {
        centerX = new BigDouble(-0.7);
        centerY = new BigDouble(0.0);
        scaleBD = new BigDouble(initialScale);
        currentScale = initialScale;
        lastOffsetX = 0;
        lastOffsetY = 0;
        updateMaxIterations();
        requestRender(true);
    }

    // Zoom und Iterations-Logik unverändert
    public void zoom(double factor) {
        zoomToPoint(factor, new Point(getWidth()/2, getHeight()/2));
    }

    private void zoomToPoint(double factor, Point p) {
        BigDouble factorBD = new BigDouble(factor);
        double w2 = getWidth() / 2.0;
        double h2 = getHeight() / 2.0;

        scaleBD = BigDouble.multiply(scaleBD, factorBD);
        currentScale *= factor;

        BigDouble mx = new BigDouble(p.x - w2);
        BigDouble my = new BigDouble(p.y - h2);

        BigDouble shiftX = BigDouble.divide(mx, scaleBD.xh);
        BigDouble shiftY = BigDouble.divide(my, scaleBD.xh);

        centerX = BigDouble.add(centerX, shiftX);
        centerY = BigDouble.add(centerY, shiftY);

        updateMaxIterations();
        requestRender(false);
    }

    private void updateMaxIterations() {
        if (mode == 0) {
            double zoomRatio = currentScale / initialScale;
            double log2Zoom = Math.log(zoomRatio) / Math.log(2.0);

            int newMax = (int) Math.round(ITER_BASE + log2Zoom * ITER_FACTOR);
            maxIterations = Math.max(150, newMax);

            if (sidebarRef != null && sidebarRef instanceof ControlPanel) {
                ((ControlPanel) sidebarRef).maxIterLabel.setText("Auto: " + maxIterations);
            }
        }
    }

    // --- Render-Steuerung (Optimiert) ---

    // Überschreibt die Render-Anforderung
    public void requestRender(boolean highQuality) {
        currentEpoch.incrementAndGet();

        if (!highQuality) {
            renderScene(2, false);
        }

        // Führt den HQ-Render immer nach dem LQ-Render aus
        renderScene(1, true);
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (displayImage != null) {
            // NEU: Beim Schwenken, das Bild verschieben und dann neu zeichnen
            if (isPanning && Math.abs(lastOffsetX) < TILE_SIZE && Math.abs(lastOffsetY) < TILE_SIZE) {
                // Hier wird der vorhandene Buffer verschoben
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.translate(lastOffsetX, lastOffsetY);
                g2d.drawImage(displayImage, 0, 0, null);
                g2d.dispose();
            } else {
                g.drawImage(displayImage, 0, 0, null);
            }
        }
    }

    private void renderScene(int step, boolean highQualityMode) {
        int w = getWidth(); int h = getHeight();
        if (w <= 0 || h <= 0) return;

        // Initialisierung des Buffers (unverändert)
        if (displayImage == null || displayImage.getWidth() != w || displayImage.getHeight() != h) {
            displayImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            buffer = ((DataBufferInt) displayImage.getRaster().getDataBuffer()).getData();
            lastOffsetX = 0; lastOffsetY = 0; // Kein Offset bei neuer Größe
        }

        final long epoch = currentEpoch.get();
        float[] baseHSB = Color.RGBtoHSB((baseColor >> 16) & 0xFF, (baseColor >> 8) & 0xFF, baseColor & 0xFF, null);

        final BigDouble currentScaleBD = this.scaleBD;
        final BigDouble currentCenterXB = this.centerX;
        final BigDouble currentCenterYB = this.centerY;
        final BigDouble pixelDelta = BigDouble.divide(new BigDouble(1.0), currentScaleBD.xh);

        // Bestimmung der Region, die gerendert werden muss (Tiling)
        int startX = 0; int endX = w;
        int startY = 0; int endY = h;

        if (isPanning && !highQualityMode) {
            // Beim Pannen im Low-Res-Modus nur die Ränder rendern
            // Die Logik ist hier bewusst vereinfacht, um nur die Kacheln zu rendern,
            // die durch den Offset neu ins Bild geraten sind.
            startX = lastOffsetX > 0 ? 0 : w - TILE_SIZE;
            endX = lastOffsetX > 0 ? TILE_SIZE : w;
            startY = lastOffsetY > 0 ? 0 : h - TILE_SIZE;
            endY = lastOffsetY > 0 ? TILE_SIZE : h;

            // Wenn der Offset klein ist, rendern wir den Rand.
            // Bei großen Offsets oder HQ-Mode rendern wir das gesamte Bild (unten: Full Render).
            if (Math.abs(lastOffsetX) < w / 2 && Math.abs(lastOffsetY) < h / 2) {
                // Wir rendern die Ränder in Kacheln der Größe TILE_SIZE
                startTileRender(epoch, w, h, step, highQualityMode, baseHSB, pixelDelta, currentCenterXB, currentCenterYB);
                return; // Springe aus dieser Methode
            }
        }

        // Fallback: Full Render (Zoom, HQ-Render, oder großer Pan)
        startFullRender(epoch, w, h, step, highQualityMode, baseHSB, pixelDelta, currentCenterXB, currentCenterYB);
    }

    // --- Neue Tiling-Logik ---

    private void startTileRender(final long epoch, int w, int h, int step, boolean highQualityMode,
                                 float[] baseHSB, BigDouble pixelDelta, BigDouble currentCenterXB, BigDouble currentCenterYB) {

        // Logik nur für Low-Res/Pan: Berechne nur die neuen Kacheln

        // Zuerst das Bild verschieben (Copy Area)
        if (lastOffsetX != 0 || lastOffsetY != 0) {
            Graphics g = displayImage.getGraphics();
            // Verschiebe das vorhandene Bild im Buffer
            g.copyArea(0, 0, w, h, lastOffsetX, lastOffsetY);
            g.dispose();
        }

        // Die Ränder werden in 64x64 Kacheln gerendert

        // Vertikale Ränder (Links/Rechts)
        renderTiles(0, w, 0, h, TILE_SIZE, TILE_SIZE, lastOffsetX, w, h, epoch, step, highQualityMode, baseHSB, pixelDelta, currentCenterXB, currentCenterYB);

        // Horizontale Ränder (Oben/Unten)
        renderTiles(0, w, 0, h, TILE_SIZE, TILE_SIZE, lastOffsetY, h, w, epoch, step, highQualityMode, baseHSB, pixelDelta, currentCenterXB, currentCenterYB);

        // Reset des Offsets nach dem erfolgreichen Teil-Render
        lastOffsetX = 0; lastOffsetY = 0;
        SwingUtilities.invokeLater(this::repaint);
    }

    private void renderTiles(int startX, int endX, int startY, int endY, int tileW, int tileH, int offset, int dim1, int dim2,
                             final long epoch, int step, boolean highQualityMode, float[] baseHSB, BigDouble pixelDelta, BigDouble currentCenterXB, BigDouble currentCenterYB) {

        int tilesAcross = (dim1 + tileW - 1) / tileW;

        IntStream.range(0, tilesAcross).parallel().forEach(i -> {
            if (currentEpoch.get() != epoch) return;

            int tileStartX, tileStartY;
            if (dim1 == getWidth()) { // Horizontaler Render (Links/Rechts)
                tileStartX = i * tileW;
                tileStartY = 0;

                if (offset > 0) tileStartX = 0; else tileStartX = dim1 - tileW;

            } else { // Vertikaler Render (Oben/Unten)
                tileStartX = 0;
                tileStartY = i * tileH;

                if (offset > 0) tileStartY = 0; else tileStartY = dim1 - tileH;
            }

            // Render der Kachel
            for (int y = tileStartY; y < tileStartY + tileH && y < dim2; y += step) {
                BigDouble yOffset = BigDouble.multiply(new BigDouble(y - dim2 / 2.0), pixelDelta);
                BigDouble cyBD = BigDouble.add(currentCenterYB, yOffset);

                for (int x = tileStartX; x < tileStartX + tileW && x < dim1; x += step) {
                    BigDouble xOffset = BigDouble.multiply(new BigDouble(x - dim1 / 2.0), pixelDelta);
                    BigDouble cxBD = BigDouble.add(currentCenterXB, xOffset);

                    int rgb = computeMandelbrot(cxBD, cyBD, baseHSB[0], highQualityMode);

                    if (currentEpoch.get() == epoch) {
                        if (step == 1) buffer[y * dim1 + x] = rgb;
                        else fillBlock(buffer, dim1, dim2, x, y, step, rgb);
                    }
                }
            }
        });
    }

    // --- Vollständiger Render (Zoom & HQ) ---

    private void startFullRender(final long epoch, int w, int h, int step, boolean highQualityMode,
                                 float[] baseHSB, BigDouble pixelDelta, BigDouble currentCenterXB, BigDouble currentCenterYB) {

        IntStream.range(0, h / step).parallel().forEach(yRaw -> {
            if (currentEpoch.get() != epoch) return;

            int y = yRaw * step;

            BigDouble yOffset = BigDouble.multiply(new BigDouble(y - h / 2.0), pixelDelta);
            BigDouble cyBD = BigDouble.add(currentCenterYB, yOffset);

            for (int x = 0; x < w; x += step) {
                if (currentEpoch.get() != epoch) return;

                BigDouble xOffset = BigDouble.multiply(new BigDouble(x - w / 2.0), pixelDelta);
                BigDouble cxBD = BigDouble.add(currentCenterXB, xOffset);

                int rgb = computeMandelbrot(cxBD, cyBD, baseHSB[0], highQualityMode);

                if (currentEpoch.get() == epoch) {
                    if (step == 1) buffer[y * w + x] = rgb;
                    else fillBlock(buffer, w, h, x, y, step, rgb);
                }
            }
        });

        if (currentEpoch.get() == epoch) SwingUtilities.invokeLater(this::repaint);
    }

    private void fillBlock(int[] buf, int w, int h, int x, int y, int size, int color) {
        int maxY = Math.min(y + size, h);
        int maxX = Math.min(x + size, w);
        for (int cy = y; cy < maxY; cy++) {
            int off = cy * w;
            for (int cx = x; cx < maxX; cx++) buf[off + cx] = color;
        }
    }

    // --- MANDELBROT (z^2 + c) - GC-Optimiert ---

    // Vermeidet die Neuanlage von BigDouble(2.0) und BigDouble(1.0) in der Schleife
    private final BigDouble TWO_BD = new BigDouble(2.0);
    private final BigDouble ZERO_BD = new BigDouble(0.0);

    private int computeMandelbrot(BigDouble cx, BigDouble cy, float baseHue, boolean highQualityMode) {

        int maxIters = highQualityMode ? maxIterations : FAST_ITER_COUNT;
        float brightnessFactor = highQualityMode ? 1.0f : FAST_BRIGHTNESS;

        BigDouble zx = ZERO_BD;
        BigDouble zy = ZERO_BD;
        int iter = 0;

        while (iter < maxIters && (zx.magSq() + zy.magSq()) < 4.0) {
            // GC-Optimierung: Vermeidung von 'new BigDouble(2.0)'
            BigDouble zxSq = BigDouble.square(zx);
            BigDouble zySq = BigDouble.square(zy);
            BigDouble zytemp = BigDouble.multiply(TWO_BD, BigDouble.multiply(zx, zy));

            zx = BigDouble.add(BigDouble.subtract(zxSq, zySq), cx);
            zy = BigDouble.add(zytemp, cy);

            iter++;
        }
        // ... (Farbgebung unverändert)
        if (iter == maxIters) return 0xFF000000;

        double magSq = zx.magSq() + zy.magSq();
        double logZn = Math.log(magSq) / 2.0;
        double nu = Math.log(logZn / Math.log(2.0)) / Math.log(2.0);
        double smoothIter = iter + 1.0 - nu;

        float hue = (float) (colorShift + baseHue + (colorFrequency * smoothIter * 0.005f));
        float sat = 1.0f;

        double brightnessBase = Math.log(smoothIter);
        double denominator = highQualityMode ? Math.log(maxIterations) : Math.log(FAST_ITER_COUNT);
        double brightnessScaled = brightnessBase / denominator;

        float bri = (float) Math.pow(brightnessScaled, 0.25);

        bri *= brightnessFactor;
        bri *= brightnessGlobal;

        return Color.HSBtoRGB(hue % 1.0f, sat, Math.max(0f, Math.min(1f, bri)));
    }
}

// ... (ControlPanel und BigDouble sind unverändert, BigDouble.java muss natürlich existieren)
// HILFSKLASSE ControlPanel für FractalPanel
class ControlPanel extends JPanel {
    private final FractalPanel fractalPanel;
    JSlider maxIterSlider;
    JLabel maxIterLabel;

    ControlPanel(FractalPanel panel) {
        this.fractalPanel = panel;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(300, 0));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(32, 32, 32));

        // ... (GUI-Elemente unverändert)
        JLabel title = new JLabel("FRAKTAL WAHL");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setAlignmentX(CENTER_ALIGNMENT);
        add(title);
        add(Box.createVerticalStrut(10));

        String[] modes = {"Mandelbrot (BigDouble - INFINITE)"};
        JComboBox<String> modeBox = new JComboBox<>(modes);
        modeBox.setFocusable(false);
        modeBox.setEnabled(false);
        add(modeBox);
        add(Box.createVerticalStrut(20));

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        btnPanel.setOpaque(false);
        JButton zoomInBtn = createButton("Rein (F)", e -> panel.zoom(2.0));
        JButton zoomOutBtn = createButton("Raus (J)", e -> panel.zoom(0.5));
        btnPanel.add(zoomInBtn);
        btnPanel.add(zoomOutBtn);
        add(btnPanel);
        add(Box.createVerticalStrut(15));

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
                "Details & Geometrie", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12), Color.LIGHT_GRAY));

        JPanel iterPanel = createSlider("Genauigkeit (Iterationen)", 50, 2000, 150, val -> {
            panel.maxIterations = val;
            panel.requestRender(false);
        }, panel);

        maxIterSlider = (JSlider) iterPanel.getComponent(1);
        maxIterLabel = (JLabel) ((JPanel)iterPanel.getComponent(0)).getComponent(1);

        p.add(iterPanel);
        add(p);

        maxIterSlider.setEnabled(false);
        maxIterLabel.setText("Auto");
        panel.maxIterations = 150;

        addSection("Farbe & Style", sec -> {
            JButton colorBtn = new JButton("Basisfarbe wählen");
            colorBtn.setFocusable(false);
            colorBtn.addActionListener(e -> {
                Color newC = JColorChooser.showDialog(this, "Wähle Basisfarbe", new Color(panel.baseColor));
                if (newC != null) {
                    panel.baseColor = newC.getRGB();
                    panel.requestRender(true);
                }
            });
            sec.add(colorBtn);
            sec.add(Box.createVerticalStrut(5));

            sec.add(createSlider("Farbverschiebung (Hue)", 0, 360, 0, val -> {
                panel.colorShift = val / 360.0f;
                panel.requestRender(false);
            }, panel));

            sec.add(createSlider("Psychedelic Stripes", 0, 100, 0, val -> {
                panel.colorFrequency = val / 10.0f;
                panel.requestRender(false);
            }, panel));

            sec.add(createSlider("Helligkeit (Global)", 0, 200, 100, val -> {
                panel.brightnessGlobal = val / 100.0f;
                panel.requestRender(false);
            }, panel));
        });

        add(Box.createVerticalGlue());

        JButton resetBtn = createButton("Reset View (R)", e -> panel.resetView());
        resetBtn.setAlignmentX(CENTER_ALIGNMENT);
        add(resetBtn);

        JLabel hint = new JLabel("<html><center>Steuerung:<br>Mausrad / Linksklick: Zoom<br>Rechter Mausklick + Drag: Verschieben<br>F/J: Rein/Raus (Mitte)</center></html>");
        hint.setForeground(Color.GRAY);
        hint.setAlignmentX(CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(10));
        add(hint);
    }

    private void addSection(String title, java.util.function.Consumer<JPanel> builder) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
                title, TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12), Color.LIGHT_GRAY));
        builder.accept(p);
        add(p);
        add(Box.createVerticalStrut(10));
    }

    private JButton createButton(String txt, ActionListener act) {
        JButton b = new JButton(txt);
        b.addActionListener(act);
        b.setFocusable(false);
        return b;
    }

    private JPanel createSlider(String title, int min, int max, int def, java.util.function.IntConsumer action, FractalPanel panel) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Color.LIGHT_GRAY);
        JLabel lblVal = new JLabel(String.valueOf(def));
        lblVal.setForeground(Color.LIGHT_GRAY);
        lblVal.setPreferredSize(new Dimension(40, 15));
        lblVal.setHorizontalAlignment(SwingConstants.RIGHT);

        JSlider sl = new JSlider(min, max, def);
        sl.setOpaque(false);
        sl.addChangeListener(e -> {
            lblVal.setText(String.valueOf(sl.getValue()));
            action.accept(sl.getValue());
            if (!sl.getValueIsAdjusting()) panel.requestRender(true);
        });

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(lblTitle, BorderLayout.WEST);
        top.add(lblVal, BorderLayout.EAST);
        p.add(top, BorderLayout.NORTH);
        p.add(sl, BorderLayout.CENTER);
        return p;
    }
}