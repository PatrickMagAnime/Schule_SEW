import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * VSRG Trainer (Single File)
 * - 4 Lanes (A S K L)
 * - BPM-exakte Beat-Engine
 * - Klar strukturierte Patterns: Stream, Chordjack, Jumpstream, Handstream, Quadstream
 * - Density 1–10 steuert Dichte, aber kein 1/16-Spam
 * - ESC-Overlay: BPM, ScrollSpeed, Density, Pattern-Auswahl
 */
public class ManiaTrainer extends JFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ManiaTrainer t = new ManiaTrainer();
            t.setVisible(true);
        });
    }

    private static final int LANES = 4;
    private static final int LANE_WIDTH = 120;
    private static final int NOTE_RADIUS = 32;          // GRÖSSERE KREISE
    private static final int HIT_LINE_Y = 900;
    private static final int HIT_LINE_THICKNESS = 4;

    private static final int TARGET_FPS = 120;
    private static final int MS_PER_FRAME = 1000 / TARGET_FPS;

    private static final int[] KEY_CODES = { KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_K, KeyEvent.VK_L };

    // Timing Windows (ms)
    private static final int WINDOW_MARVELOUS = 22;
    private static final int WINDOW_PERFECT   = 45;
    private static final int WINDOW_GREAT     = 90;
    private static final int WINDOW_GOOD      = 135;
    private static final int WINDOW_BAD       = 180;
    private static final int WINDOW_MISS      = 200;

    // Beat subdivision: wir rechnen intern in 1/8, nicht 1/16, um es ruhiger zu machen
    private static final int SUBDIV_PER_BEAT = 2; // 1 Beat = 2 Subdivisions (1/8)
    // Pattern-Wechsel nach 8 Beats:
    private static final int BEATS_PER_PATTERN = 8;

    private final GamePanel gamePanel;
    private final OverlayPanel overlayPanel;

    public ManiaTrainer() {
        super("VSRG Trainer - osu!mania / Etterna / Quaver Style");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 1200);          // Hochformat
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLayeredPane layeredPane = new JLayeredPane();
        add(layeredPane, BorderLayout.CENTER);

        gamePanel = new GamePanel();
        overlayPanel = new OverlayPanel(gamePanel);

        gamePanel.setBounds(0, 0, getWidth(), getHeight());
        overlayPanel.setBounds(0, 0, getWidth(), getHeight());

        layeredPane.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(overlayPanel, JLayeredPane.PALETTE_LAYER);

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                gamePanel.setBounds(0, 0, getWidth(), getHeight());
                overlayPanel.setBounds(0, 0, getWidth(), getHeight());
            }
        });

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(ev -> {
            if (ev.getID() == KeyEvent.KEY_PRESSED && ev.getKeyCode() == KeyEvent.VK_ESCAPE) {
                overlayPanel.toggleVisible();
                return true;
            }
            return false;
        });

        gamePanel.startGame();
    }

    // =====================================================================
    // GAME PANEL
    // =====================================================================
    private static class GamePanel extends JPanel implements KeyListener {

        private volatile double bpm = 180.0;
        private volatile double scrollSpeed = 1.0;
        private volatile int density = 5; // 1–10

        private double secondsPerBeat;
        private double secondsPerSubdiv;

        private long songStartNano;
        private long lastFrameNano;

        private long currentSubdivIndex = 0;  // globaler 1/8-Zähler
        private long subdivsSincePatternChange = 0;

        private final List<Note> notes = new CopyOnWriteArrayList<>();
        private final boolean[] keysHeld = new boolean[LANES];

        private final Random rng = new Random();

        private enum PatternType {
            STREAM, CHORDJACK, JUMPSTREAM, HANDSTREAM, QUADSTREAM
        }

        static class PatternConfig {
            boolean stream = true;
            boolean chordjack = true;
            boolean jumpstream = true;
            boolean handstream = true;
            boolean quadstream = true;
        }

        private final PatternConfig patternConfig = new PatternConfig();
        private PatternType currentPattern = PatternType.STREAM;
        private boolean mirror = false;

        // Stats
        private int marvelous, perfect, great, good, bad, miss;
        private int combo, maxCombo;

        private javax.swing.Timer frameTimer;

        public GamePanel() {
            setBackground(Color.BLACK);
            setFocusable(true);
            addKeyListener(this);
            recalcBeatDurations();
        }

        public void startGame() {
            songStartNano = System.nanoTime();
            lastFrameNano = songStartNano;
            frameTimer = new javax.swing.Timer(MS_PER_FRAME, e -> tick());
            frameTimer.start();
        }

        private void recalcBeatDurations() {
            secondsPerBeat = 60.0 / bpm;
            secondsPerSubdiv = secondsPerBeat / SUBDIV_PER_BEAT; // 1/8-Abstand
        }

        public void setBpm(double bpm) {
            this.bpm = Math.max(30, Math.min(300, bpm));
            recalcBeatDurations();
        }

        public void setScrollSpeed(double scrollSpeed) {
            this.scrollSpeed = Math.max(0.25, Math.min(5.0, scrollSpeed));
        }

        public void setDensity(int density) {
            this.density = Math.max(1, Math.min(10, density));
        }

        public double getBpm() { return bpm; }
        public double getScrollSpeed() { return scrollSpeed; }
        public int getDensity() { return density; }
        public PatternConfig getPatternConfig() { return patternConfig; }
        public PatternType getCurrentPattern() { return currentPattern; }
        public boolean isMirror() { return mirror; }

        // -----------------------------------------------------------------
        // MAIN TICK
        // -----------------------------------------------------------------
        private void tick() {
            long now = System.nanoTime();
            lastFrameNano = now;

            double songTimeSec = (now - songStartNano) / 1_000_000_000.0;
            long expectedSubdivIndex = (long) (songTimeSec / secondsPerSubdiv);

            while (currentSubdivIndex <= expectedSubdivIndex) {
                generateOnSubdiv(currentSubdivIndex);
                currentSubdivIndex++;
                subdivsSincePatternChange++;

                long subdivsPerPattern = (long) (BEATS_PER_PATTERN * SUBDIV_PER_BEAT);
                if (subdivsSincePatternChange >= subdivsPerPattern) {
                    subdivsSincePatternChange = 0;
                    chooseNextPattern();
                    mirror = rng.nextBoolean();
                }
            }

            handleAutoMisses(songTimeSec);
            repaint();
        }

        // -----------------------------------------------------------------
        // PATTERN-GENERIERUNG (immer exakt auf 1/8)
        // -----------------------------------------------------------------
        private void generateOnSubdiv(long subdivIndex) {
            // Wir spawnen ein paar Beats „vorneweg“, damit die Noten von oben reinlaufen.
            int preSpawnBeats = 4; // 4 Beats Vorlauf
            long preSpawnSubdiv = preSpawnBeats * SUBDIV_PER_BEAT;

            double beatForNote = (subdivIndex + preSpawnSubdiv) / (double) SUBDIV_PER_BEAT;
            double noteTimeSec = beatForNote * secondsPerBeat;
            long noteTimeMs = (long) (noteTimeSec * 1000);

            int posInBeat = (int) (subdivIndex % SUBDIV_PER_BEAT); // 0 = Beat, 1 = Offbeat

            // Grund-Chance: bei niedriger Density wenige Noten, bei hoher deutlich mehr
            double baseChanceOnBeat   = 0.25 + 0.05 * density; // auf dem Beat
            double baseChanceOffBeat  = 0.10 + 0.04 * density; // Offbeat

            double chance = (posInBeat == 0) ? baseChanceOnBeat : baseChanceOffBeat;
            if (rng.nextDouble() > chance) return;

            switch (currentPattern) {
                case STREAM      -> spawnStream(noteTimeMs, posInBeat);
                case CHORDJACK   -> spawnChordjack(noteTimeMs, posInBeat);
                case JUMPSTREAM  -> spawnJumpstream(noteTimeMs, posInBeat);
                case HANDSTREAM  -> spawnHandstream(noteTimeMs, posInBeat);
                case QUADSTREAM  -> spawnQuadstream(noteTimeMs, posInBeat);
            }
        }

        private void chooseNextPattern() {
            List<PatternType> avail = new ArrayList<>();
            if (patternConfig.stream)     avail.add(PatternType.STREAM);
            if (patternConfig.chordjack)  avail.add(PatternType.CHORDJACK);
            if (patternConfig.jumpstream) avail.add(PatternType.JUMPSTREAM);
            if (patternConfig.handstream) avail.add(PatternType.HANDSTREAM);
            if (patternConfig.quadstream) avail.add(PatternType.QUADSTREAM);

            if (avail.isEmpty()) {
                currentPattern = PatternType.STREAM;
                return;
            }
            currentPattern = avail.get(rng.nextInt(avail.size()));
        }

        // ----- konkrete Pattern-Logik (alle strikt auf 1/4 / 1/8) -----

        // einfacher 1/4 / 1/8 Stream
        private void spawnStream(long timeMs, int posInBeat) {
            int lastLane = notes.isEmpty() ? -1 : notes.get(notes.size() - 1).lane;
            int lane;
            do {
                lane = rng.nextInt(LANES);
            } while (lane == lastLane);
            lane = applyMirror(lane);
            spawnChord(timeMs, List.of(lane)); // immer Single; bei sehr hoher Density kleine Chance auf Jump
            if (density >= 8 && rng.nextDouble() < 0.15) {
                int other;
                do other = rng.nextInt(LANES); while (other == lane);
                spawnChord(timeMs, List.of(applyMirror(other)));
            }
        }

        // Chordjack: auf jedem Beat (posInBeat==0) ein Akkord, Offbeats fast leer
        private void spawnChordjack(long timeMs, int posInBeat) {
            if (posInBeat != 0 && rng.nextDouble() < 0.7) return; // Offbeats stark reduziert
            int chordSize = 2;
            if (density >= 5) chordSize = 3;
            if (density >= 8 && rng.nextDouble() < 0.3) chordSize = 4;

            spawnRandomChord(timeMs, chordSize);
        }

        // Jumpstream: Beat = Jumps, Offbeat = Singles
        private void spawnJumpstream(long timeMs, int posInBeat) {
            if (posInBeat == 0) {
                int chordSize = (density >= 7) ? 3 : 2;
                spawnRandomChord(timeMs, chordSize);
            } else {
                // leichte Singles/ gelegentliche Lücken
                if (rng.nextDouble() > 0.6) return;
                int lane = applyMirror(rng.nextInt(LANES));
                spawnChord(timeMs, List.of(lane));
            }
        }

        // Handstream: viele 3er-Akkorde, evtl. 4er auf Beats bei hoher Density
        private void spawnHandstream(long timeMs, int posInBeat) {
            int chordSize;
            if (posInBeat == 0) {
                chordSize = 3;
                if (density >= 8 && rng.nextDouble() < 0.4) chordSize = 4;
            } else {
                chordSize = (density >= 4) ? 2 : 1;
                if (rng.nextDouble() > 0.75) return; // ab und zu Lücken
            }
            spawnRandomChord(timeMs, chordSize);
        }

        // Quadstream: Hauptschläge = 4er, Offbeats = 2–3er
        private void spawnQuadstream(long timeMs, int posInBeat) {
            int chordSize;
            if (posInBeat == 0) {
                chordSize = 4;
                if (density <= 4 && rng.nextDouble() < 0.4) chordSize = 3; // etwas entschärfen
            } else {
                chordSize = 2;
                if (density >= 6) chordSize = 3;
                if (rng.nextDouble() > 0.6) return;
            }
            spawnRandomChord(timeMs, chordSize);
        }

        private void spawnRandomChord(long timeMs, int size) {
            boolean[] used = new boolean[LANES];
            List<Integer> lanes = new ArrayList<>();
            while (lanes.size() < size) {
                int lane = applyMirror(rng.nextInt(LANES));
                if (!used[lane]) {
                    used[lane] = true;
                    lanes.add(lane);
                }
            }
            spawnChord(timeMs, lanes);
        }

        private void spawnChord(long timeMs, List<Integer> lanes) {
            for (int lane : lanes) {
                notes.add(new Note(lane, timeMs));
            }
        }

        private int applyMirror(int lane) {
            return mirror ? (LANES - 1 - lane) : lane;
        }

        // =====================================================================
        // RENDERING
        // =====================================================================
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            requestFocusInWindow();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int totalWidth = LANES * LANE_WIDTH;
            int offsetX = (w - totalWidth) / 2;

            g2.setColor(Color.DARK_GRAY.darker());
            g2.fillRect(0, 0, w, h);

            for (int i = 0; i < LANES; i++) {
                int x = offsetX + i * LANE_WIDTH;
                g2.setColor(new Color(40, 40, 40));
                g2.fillRect(x, 0, LANE_WIDTH, h);
                g2.setColor(Color.GRAY);
                g2.drawRect(x, 0, LANE_WIDTH, h);

                if (keysHeld[i]) {
                    g2.setColor(new Color(255, 255, 255, 70));
                    g2.fillRect(x, HIT_LINE_Y - 70, LANE_WIDTH, 140);
                }
            }

            g2.setColor(Color.WHITE);
            g2.fillRect(offsetX, HIT_LINE_Y - HIT_LINE_THICKNESS / 2, totalWidth, HIT_LINE_THICKNESS);

            long nowMs = (System.nanoTime() - songStartNano) / 1_000_000;
            double pixelsPerSecond = 400 * scrollSpeed;  // LANGSAMER SCROLL

            for (Note n : notes) {
                double timeToHitSec = (n.timeMs - nowMs) / 1000.0;
                int y = (int) (HIT_LINE_Y - timeToHitSec * pixelsPerSecond);

                if (y < -NOTE_RADIUS * 2 || y > h + NOTE_RADIUS * 2) continue;

                int laneXCenter = offsetX + n.lane * LANE_WIDTH + LANE_WIDTH / 2;
                int x = laneXCenter - NOTE_RADIUS;

                long dt = Math.abs(n.timeMs - nowMs);
                Color c;
                if (dt <= WINDOW_MARVELOUS)      c = new Color(0, 255, 255);
                else if (dt <= WINDOW_PERFECT)   c = new Color(0, 255, 128);
                else if (dt <= WINDOW_GREAT)     c = new Color(255, 255, 0);
                else if (dt <= WINDOW_GOOD)      c = new Color(255, 200, 0);
                else if (dt <= WINDOW_BAD)       c = new Color(255, 80, 80);
                else                             c = new Color(90, 90, 90);

                g2.setColor(c);
                g2.fillOval(x, y - NOTE_RADIUS, NOTE_RADIUS * 2, NOTE_RADIUS * 2);
                g2.setColor(Color.BLACK);
                g2.drawOval(x, y - NOTE_RADIUS, NOTE_RADIUS * 2, NOTE_RADIUS * 2);
            }

            // HUD
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18f));
            g2.setColor(Color.WHITE);
            int hudX = 10;
            int hudY = 30;
            g2.drawString(String.format("BPM: %.1f", bpm), hudX, hudY);
            g2.drawString(String.format("Scroll: %.2f", scrollSpeed), hudX, hudY + 22);
            g2.drawString(String.format("Density: %d", density), hudX, hudY + 44);
            g2.drawString(String.format("Pattern: %s%s", currentPattern, mirror ? " (Mirror)" : ""), hudX, hudY + 66);

            int rightX = w - 260;
            int yHUD = 30;
            g2.drawString(String.format("Combo: %d (Max %d)", combo, maxCombo), rightX, yHUD);
            yHUD += 22;

            g2.setColor(new Color(0, 255, 255));
            g2.drawString("Marvelous: " + marvelous, rightX, yHUD); yHUD += 22;
            g2.setColor(new Color(0, 255, 128));
            g2.drawString("Perfect:   " + perfect, rightX, yHUD);   yHUD += 22;
            g2.setColor(new Color(255, 255, 0));
            g2.drawString("Great:     " + great, rightX, yHUD);     yHUD += 22;
            g2.setColor(new Color(255, 200, 0));
            g2.drawString("Good:      " + good, rightX, yHUD);      yHUD += 22;
            g2.setColor(new Color(255, 80, 80));
            g2.drawString("Bad:       " + bad, rightX, yHUD);       yHUD += 22;
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString("Miss:      " + miss, rightX, yHUD);

            g2.dispose();
        }

        // =====================================================================
        // HIT / MISS
        // =====================================================================
        private void handleAutoMisses(double songTimeSec) {
            long nowMs = (long) (songTimeSec * 1000);
            for (Note n : notes) {
                if (!n.hit && nowMs - n.timeMs > WINDOW_MISS) {
                    n.hit = true;
                    registerJudgement("Miss");
                }
            }
            notes.removeIf(n -> n.hit);
        }

        private void handleKeyHit(int lane) {
            long nowMs = (System.nanoTime() - songStartNano) / 1_000_000;

            Note best = null;
            long bestAbs = Long.MAX_VALUE;
            for (Note n : notes) {
                if (n.lane != lane || n.hit) continue;
                long diff = nowMs - n.timeMs;
                long abs = Math.abs(diff);
                if (abs < bestAbs) {
                    bestAbs = abs;
                    best = n;
                }
            }
            if (best == null) return;

            String j;
            if (bestAbs <= WINDOW_MARVELOUS)      j = "Marvelous";
            else if (bestAbs <= WINDOW_PERFECT)   j = "Perfect";
            else if (bestAbs <= WINDOW_GREAT)     j = "Great";
            else if (bestAbs <= WINDOW_GOOD)      j = "Good";
            else if (bestAbs <= WINDOW_BAD)       j = "Bad";
            else if (bestAbs <= WINDOW_MISS)      j = "Miss";
            else                                  return;

            best.hit = true;
            registerJudgement(j);
            notes.removeIf(n -> n.hit);
        }

        private void registerJudgement(String j) {
            switch (j) {
                case "Marvelous" -> { marvelous++; combo++; }
                case "Perfect"   -> { perfect++;   combo++; }
                case "Great"     -> { great++;     combo++; }
                case "Good"      -> { good++;      combo++; }
                case "Bad"       -> { bad++;       combo = 0; }
                case "Miss"      -> { miss++;      combo = 0; }
            }
            if (combo > maxCombo) maxCombo = combo;
        }

        // =====================================================================
        // KEYLISTENER
        // =====================================================================
        @Override public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {
            for (int i = 0; i < LANES; i++) {
                if (e.getKeyCode() == KEY_CODES[i]) {
                    if (!keysHeld[i]) {
                        keysHeld[i] = true;
                        handleKeyHit(i);
                    }
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            for (int i = 0; i < LANES; i++) {
                if (e.getKeyCode() == KEY_CODES[i]) {
                    keysHeld[i] = false;
                }
            }
        }

        // =====================================================================
        // DATA
        // =====================================================================
        private static class Note {
            int lane;
            long timeMs;
            boolean hit;

            Note(int lane, long timeMs) {
                this.lane = lane;
                this.timeMs = timeMs;
            }
        }
    }

    // =====================================================================
    // OVERLAY (ESC)
    // =====================================================================
    private static class OverlayPanel extends JPanel {
        private final GamePanel gamePanel;
        private boolean visible;

        private final JSlider bpmSlider;
        private final JSlider scrollSlider;
        private final JSlider densitySlider;

        private final JCheckBox streamBox;
        private final JCheckBox chordjackBox;
        private final JCheckBox jumpstreamBox;
        private final JCheckBox handstreamBox;
        private final JCheckBox quadstreamBox;

        public OverlayPanel(GamePanel gamePanel) {
            this.gamePanel = gamePanel;
            setOpaque(false);
            setLayout(new GridBagLayout());

            JPanel panel = new JPanel();
            panel.setBackground(new Color(0, 0, 0, 210));
            panel.setForeground(Color.WHITE);
            panel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.WHITE),
                    "Advanced Settings (ESC zum Schließen)",
                    TitledBorder.CENTER,
                    TitledBorder.TOP,
                    panel.getFont().deriveFont(Font.BOLD, 16f),
                    Color.WHITE
            ));
            panel.setLayout(new GridBagLayout());

            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(4, 8, 4, 8);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0; c.gridy = 0;

            bpmSlider = new JSlider(60, 300, (int) gamePanel.getBpm());
            bpmSlider.setPaintTicks(true);
            bpmSlider.setPaintLabels(true);
            bpmSlider.setMajorTickSpacing(60);
            bpmSlider.setMinorTickSpacing(10);

            scrollSlider = new JSlider(25, 400, (int) (gamePanel.getScrollSpeed() * 100));
            scrollSlider.setPaintTicks(true);
            scrollSlider.setPaintLabels(true);
            scrollSlider.setMajorTickSpacing(75);
            scrollSlider.setMinorTickSpacing(25);

            densitySlider = new JSlider(1, 10, gamePanel.getDensity());
            densitySlider.setPaintTicks(true);
            densitySlider.setPaintLabels(true);
            densitySlider.setMajorTickSpacing(1);

            JLabel bpmLabel = new JLabel("BPM:");
            bpmLabel.setForeground(Color.WHITE);
            JLabel scrollLabel = new JLabel("Scroll-Speed (x):");
            scrollLabel.setForeground(Color.WHITE);
            JLabel densityLabel = new JLabel("Density (1–10):");
            densityLabel.setForeground(Color.WHITE);

            c.gridx = 0; c.gridy = 0;
            panel.add(bpmLabel, c);
            c.gridx = 1;
            panel.add(bpmSlider, c);

            c.gridx = 0; c.gridy = 1;
            panel.add(scrollLabel, c);
            c.gridx = 1;
            panel.add(scrollSlider, c);

            c.gridx = 0; c.gridy = 2;
            panel.add(densityLabel, c);
            c.gridx = 1;
            panel.add(densitySlider, c);

            // Pattern-Checkboxen
            c.gridx = 0; c.gridy = 3; c.gridwidth = 2;
            JPanel pattPanel = new JPanel(new GridLayout(0, 1));
            pattPanel.setOpaque(false);
            pattPanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.WHITE),
                    "Pattern-Selector (Random Mix alle 8 Beats)",
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    pattPanel.getFont().deriveFont(Font.BOLD, 13f),
                    Color.WHITE
            ));

            GamePanel.PatternConfig cfg = gamePanel.getPatternConfig();

            streamBox     = new JCheckBox("Stream",     cfg.stream);
            chordjackBox  = new JCheckBox("Chordjack",  cfg.chordjack);
            jumpstreamBox = new JCheckBox("Jumpstream", cfg.jumpstream);
            handstreamBox = new JCheckBox("Handstream", cfg.handstream);
            quadstreamBox = new JCheckBox("Quadstream", cfg.quadstream);

            JCheckBox[] boxes = {streamBox, chordjackBox, jumpstreamBox, handstreamBox, quadstreamBox};
            for (JCheckBox b : boxes) {
                b.setOpaque(false);
                b.setForeground(Color.WHITE);
                pattPanel.add(b);
            }

            panel.add(pattPanel, c);
            add(panel, new GridBagConstraints());

            // Listener
            bpmSlider.addChangeListener(e -> gamePanel.setBpm(bpmSlider.getValue()));
            scrollSlider.addChangeListener(e -> gamePanel.setScrollSpeed(scrollSlider.getValue() / 100.0));
            densitySlider.addChangeListener(e -> gamePanel.setDensity(densitySlider.getValue()));

            ItemListener pattListener = e -> {
                cfg.stream     = streamBox.isSelected();
                cfg.chordjack  = chordjackBox.isSelected();
                cfg.jumpstream = jumpstreamBox.isSelected();
                cfg.handstream = handstreamBox.isSelected();
                cfg.quadstream = quadstreamBox.isSelected();
            };
            for (JCheckBox b : boxes) {
                b.addItemListener(pattListener);
            }

            setVisible(false);
            visible = false;
        }

        public void toggleVisible() {
            visible = !visible;
            setVisible(visible);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
        }
    }
}