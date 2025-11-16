package pong;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

/**
 * GamePanel
 * - Enthält den Game-Loop (Timer) und die komplette Spiel-Logik: Input, Physik,
 *   Kollisionen, Scoring und Rendering.
 * - Skaliert alle Größen (Paddles, Ball, Geschwindigkeiten, Fonts) relativ zur Panelgröße.
 *   Beim Resize/Fullscreen-Wechsel bleibt die „logische" Geschwindigkeit konstant,
 *   damit das Level nicht springt.
 * - Keybindings kommen aus den Settings; ESC bringt ins Menü, F11 toggelt Fullscreen.
 */
public final class GamePanel extends JPanel {
    private static final int DEFAULT_WIDTH = 900;
    private static final int DEFAULT_HEIGHT = 540;
    private static final double PADDLE_WIDTH = 12; //stt ist 12
    private static final double PADDLE_HEIGHT = 90; //std ist 90
    private static final double PADDLE_SPEED = 7.0;
    private static final double BALL_SIZE = 14;
    private static final double START_SPEED = 4.0;
    private static final double SPEED_DELTA = 0.25; //std ist 0.25w
    private static final double MAX_SPEED = 16.0; //std ist 16.0
    private static final int HITS_PER_LEVEL = 3; //std ist 4
    private static final double SINGLEPLAYER_SCORE_FACTOR = 25.0;
    private static final double BOOST_MULTIPLIER = 1.8; //std ist 1.8

    private final AppWindow window;
    private final AppContext context;
    private Paddle leftPaddle;
    private Paddle rightPaddle;
    private Ball ball;
    private int width;
    private int height;
    private final Timer timer;
    private final Random random = new Random();
    // scale-aware values (updated on resize)
    private double scale = 1.0;
    private double scaledPaddleWidth;
    private double scaledPaddleHeight;
    private double scaledBallSize;
    private double scaledPaddleSpeed;
    private double scaledStartSpeed;
    private double scaledSpeedDelta;
    private double scaledMaxSpeed;

    private void updateScaleAndSizes() {
        // Skaliere relativ zur kleineren Achse, damit das Seitenverhältnis gewahrt bleibt
        scale = Math.min((double) Math.max(1, width) / DEFAULT_WIDTH, (double) Math.max(1, height) / DEFAULT_HEIGHT);
        scaledPaddleWidth = PADDLE_WIDTH * scale;
        scaledPaddleHeight = PADDLE_HEIGHT * scale;
        scaledBallSize = BALL_SIZE * scale;
        scaledPaddleSpeed = PADDLE_SPEED * scale;
        scaledStartSpeed = START_SPEED * scale;
        scaledSpeedDelta = SPEED_DELTA * scale;
        scaledMaxSpeed = MAX_SPEED * scale;
    }

    private GameConfiguration configuration;
    private boolean running;
    private boolean paused;

    private boolean player1UpPressed;
    private boolean player1DownPressed;
    private boolean player1BoostPressed;
    private boolean player2UpPressed;
    private boolean player2DownPressed;
    private boolean player2BoostPressed;
    private int keyActionCounter;

    private double currentSpeed = START_SPEED;
    private int hitsUntilSpeedIncrease = HITS_PER_LEVEL;
    private int singleScore;
    private int leftScore;
    private int rightScore;

    public GamePanel(AppWindow window, AppContext context) {
        this.window = Objects.requireNonNull(window);
        this.context = Objects.requireNonNull(context);
        this.timer = new Timer(defaultDelay(), e -> onTick());
        this.timer.setInitialDelay(0);
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        updateScaleAndSizes();
        leftPaddle = new Paddle(40, height / 2.0 - scaledPaddleHeight / 2.0, scaledPaddleWidth, scaledPaddleHeight);
        rightPaddle = new Paddle(width - 40 - scaledPaddleWidth, height / 2.0 - scaledPaddleHeight / 2.0, scaledPaddleWidth, scaledPaddleHeight);
        ball = new Ball(width / 2.0 - scaledBallSize / 2.0, height / 2.0 - scaledBallSize / 2.0, scaledBallSize);
        setPreferredSize(new Dimension(width, height));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                // Wichtig: Beim Resize die „logische" Speed erhalten, Positionen proportional anpassen
                int oldWidth = Math.max(1, width);
                int oldHeight = Math.max(1, height);
                double oldScale = scale;
                // read new size
                width = getWidth();
                height = getHeight();
                // update scale and scaled sizes
                updateScaleAndSizes();

                if (running && ball != null) {
                    // Behalte logische Geschwindigkeit über Skalenwechsel (Level bleibt konstant)
                    if (oldScale <= 0) oldScale = 1.0;
                    double logicalSpeed = currentSpeed / oldScale;
                    currentSpeed = logicalSpeed * scale;

                    // Paddles: Größe aktualisieren und vertikale Zentrierung proportional halten
                    double leftCenterRel = leftPaddle.centerY() / (double) oldHeight;
                    double rightCenterRel = rightPaddle.centerY() / (double) oldHeight;
                    leftPaddle.setSize(scaledPaddleWidth, scaledPaddleHeight);
                    rightPaddle.setSize(scaledPaddleWidth, scaledPaddleHeight);
                    leftPaddle.setPosition(40, Math.max(0, Math.min(height - scaledPaddleHeight, leftCenterRel * height - scaledPaddleHeight / 2.0)));
                    rightPaddle.setPosition(width - 40 - scaledPaddleWidth, Math.max(0, Math.min(height - scaledPaddleHeight, rightCenterRel * height - scaledPaddleHeight / 2.0)));

                    // Ball: Größe updaten, Mittelpunkt proportional beibehalten
                    double ballCenterRelX = ball.centerX() / (double) oldWidth;
                    double ballCenterRelY = ball.centerY() / (double) oldHeight;
                    ball.setSize(scaledBallSize);
                    double newCenterX = ballCenterRelX * width;
                    double newCenterY = ballCenterRelY * height;
                    ball.setPosition(newCenterX - ball.size() / 2.0, newCenterY - ball.size() / 2.0);
                    // ensure ball inside bounds
                    ball.clampPosition(0, 0, width - ball.size(), height - ball.size());
                    // Velocity-Betrag an neue currentSpeed anpassen, Richtung behalten
                    ball.setSpeed(currentSpeed);
                } else {
                    // when not running, keep previous behavior: recenter and reset ball
                    resetPositions();
                    ball.setSize(scaledBallSize);
                    resetBall(randomHorizontalDirection());
                }
            }
        });
        setFocusable(true);
        setDoubleBuffered(true);
        configureKeyBindings();
    }

    public void startGame(GameConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration);
        running = true;
        paused = false;
        currentSpeed = scaledStartSpeed;
        hitsUntilSpeedIncrease = HITS_PER_LEVEL;
        singleScore = 0;
        leftScore = 0;
        rightScore = 0;
        player1UpPressed = false;
        player1DownPressed = false;
        player1BoostPressed = false;
        player2UpPressed = false;
        player2DownPressed = false;
        player2BoostPressed = false;
        resetPositions();
        resetBall(randomHorizontalDirection());
        configureKeyBindings();
        updateTimerDelay();
        timer.start();
    }

    public void stop() {
        timer.stop();
        running = false;
    }

    private void configureKeyBindings() {
        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        inputMap.clear();
        actionMap.clear();
        keyActionCounter = 0;

        Storage.Settings settings = context.settings();

        registerStateKey(inputMap, actionMap, settings.player1UpKey,
                () -> player1UpPressed = true,
                () -> player1UpPressed = false);
        registerStateKey(inputMap, actionMap, settings.player1DownKey,
                () -> player1DownPressed = true,
                () -> player1DownPressed = false);
        registerStateKey(inputMap, actionMap, settings.player1BoostKey,
                () -> player1BoostPressed = true,
                () -> player1BoostPressed = false);

        registerStateKey(inputMap, actionMap, settings.player2UpKey,
                () -> player2UpPressed = true,
                () -> player2UpPressed = false);
        registerStateKey(inputMap, actionMap, settings.player2DownKey,
                () -> player2DownPressed = true,
                () -> player2DownPressed = false);
        registerStateKey(inputMap, actionMap, settings.player2BoostKey,
                () -> player2BoostPressed = true,
                () -> player2BoostPressed = false);

        registerActionKey(inputMap, actionMap, "P", this::togglePause);
        registerActionKey(inputMap, actionMap, "R", this::resetMatch);
        registerActionKey(inputMap, actionMap, "ESCAPE", () -> window.returnToMenu());
        registerActionKey(inputMap, actionMap, "F11", () -> window.toggleFullscreen());
    }

    private void registerStateKey(InputMap inputMap, ActionMap actionMap, String key,
                                  Runnable onPress, Runnable onRelease) {
        registerKey(inputMap, actionMap, key, true, onPress);
        registerKey(inputMap, actionMap, key, false, onRelease);
    }

    private void registerActionKey(InputMap inputMap, ActionMap actionMap, String key, Runnable action) {
        registerKey(inputMap, actionMap, key, true, action);
    }

    private void registerKey(InputMap inputMap, ActionMap actionMap, String key, boolean pressed, Runnable action) {
        for (KeyStroke stroke : keyStrokesFor(key, !pressed)) {
            if (stroke == null) {
                continue;
            }
            String mapKey = "key_action_" + keyActionCounter++;
            inputMap.put(stroke, mapKey);
            actionMap.put(mapKey, new KeyAction(action));
        }
    }

    private KeyStroke[] keyStrokesFor(String key, boolean release) {
        if (key == null) {
            return new KeyStroke[0];
        }
        String trimmed = key.trim();
        if (trimmed.isEmpty()) {
            return new KeyStroke[0];
        }
        if (trimmed.length() == 1) {
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(trimmed.charAt(0));
            if (keyCode == KeyEvent.VK_UNDEFINED) {
                return new KeyStroke[0];
            }
            return new KeyStroke[]{KeyStroke.getKeyStroke(keyCode, 0, release)};
        }
        String normalized = trimmed.toUpperCase(Locale.ROOT);
        KeyStroke stroke = KeyStroke.getKeyStroke(release ? ("released " + normalized) : normalized);
        return stroke == null ? new KeyStroke[0] : new KeyStroke[]{stroke};
    }

    private void onTick() {
        // Ein Tick des Game-Loops: Eingaben verarbeiten, Ball updaten, rendern
        if (!running) {
            return;
        }
        if (paused) {
            repaint();
            return;
        }

        updateInputs();
        updateBall();
        repaint();
    }

    private void updateInputs() {
        double leftVelocity = 0;
        double leftSpeed = scaledPaddleSpeed * (player1BoostPressed ? BOOST_MULTIPLIER : 1.0);
        if (player1UpPressed) {
            leftVelocity -= leftSpeed;
        }
        if (player1DownPressed) {
            leftVelocity += leftSpeed;
        }
        leftPaddle.setVelocity(leftVelocity);
        leftPaddle.update();
        clampPaddle(leftPaddle);

        if (configuration.mode() == GameConfiguration.Mode.MULTI_PLAYER) {
            double rightVelocity = 0;
            double rightSpeed = scaledPaddleSpeed * (player2BoostPressed ? BOOST_MULTIPLIER : 1.0);
            if (player2UpPressed) {
                rightVelocity -= rightSpeed;
            }
            if (player2DownPressed) {
                rightVelocity += rightSpeed;
            }
            rightPaddle.setVelocity(rightVelocity);
            rightPaddle.update();
            clampPaddle(rightPaddle);
        } else {
            rightPaddle.setVelocity(0);
            rightPaddle.setY(height / 2.0 - scaledPaddleHeight / 2.0);
        }
    }

    private void updateBall() {
        ball.update();
        handleWallCollisions();
        handlePaddleCollisions();
        handleScoring();
    }

    private void handleWallCollisions() {
        if (ball.top() <= 0 && ball.velocityY() < 0) {
            ball.setPosition(ball.x(), 0);
            ball.invertY();
        } else if (ball.bottom() >= height && ball.velocityY() > 0) {
            ball.setPosition(ball.x(), height - ball.size());
            ball.invertY();
        }

        if (configuration.mode() == GameConfiguration.Mode.SINGLE_PLAYER) {
            if (ball.right() >= width && ball.velocityX() > 0) {
                ball.setPosition(width - ball.size(), ball.y());
                ball.invertX();
                ball.setSpeed(currentSpeed);
            }
        }
    }

    private void handlePaddleCollisions() {
        if (ball.velocityX() < 0 && intersects(ball, leftPaddle)) {
            ball.setPosition(leftPaddle.right(), ball.y());
            ball.invertX();
            afterPaddleHit();
        } else if (configuration.mode() == GameConfiguration.Mode.MULTI_PLAYER && ball.velocityX() > 0 && intersects(ball, rightPaddle)) {
            ball.setPosition(rightPaddle.left() - ball.size(), ball.y());
            ball.invertX();
            afterPaddleHit();
        }
    }

    private void afterPaddleHit() {
        hitsUntilSpeedIncrease--;
        if (hitsUntilSpeedIncrease <= 0) {
            hitsUntilSpeedIncrease = HITS_PER_LEVEL;
            currentSpeed = Math.min(scaledMaxSpeed, currentSpeed + scaledSpeedDelta);
        }
        currentSpeed = Math.max(currentSpeed, Math.abs(ball.velocityX()));
        ball.setSpeed(currentSpeed);
        if (configuration.mode() == GameConfiguration.Mode.SINGLE_PLAYER) {
            int gain = (int) Math.round(SINGLEPLAYER_SCORE_FACTOR * currentSpeed);
            singleScore += Math.max(1, gain);
        }
    }

    private void handleScoring() {
        if (configuration.mode() == GameConfiguration.Mode.SINGLE_PLAYER) {
            if (ball.left() <= 0) {
                handleSinglePlayerMiss();
            }
        } else {
            if (ball.left() <= 0) {
                rightScore++;
                checkMultiplayerEnd();
                resetAfterPoint(false);
            } else if (ball.right() >= width) {
                leftScore++;
                checkMultiplayerEnd();
                resetAfterPoint(true);
            }
        }
    }

    private void handleSinglePlayerMiss() {
        timer.stop();
        running = false;
        recordSinglePlayerRun();
        String message = "Score: " + singleScore + "\nNeustart?";
        int option = JOptionPane.showConfirmDialog(this, message, "Single Player", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            startGame(configuration);
        } else {
            window.returnToMenu();
        }
    }

    private void recordSinglePlayerRun() {
        String name = configuration.playerOneName();
        if (name == null || name.isBlank()) {
            name = "Player";
        }
        Storage.LeaderboardEntry entry = Storage.LeaderboardEntry.of("SP", name, Integer.toString(singleScore), java.time.Instant.now());
        context.addLeaderboardEntry(entry);
        window.notifyLeaderboardChanged();
    }

    private void checkMultiplayerEnd() {
        int target = targetScore();
        if (leftScore >= target || rightScore >= target) {
            timer.stop();
            running = false;
            storeMultiplayerResult();
            String winner = leftScore > rightScore ? configuration.playerOneName() : configuration.playerTwoName();
            if (winner == null || winner.isBlank()) {
                winner = leftScore > rightScore ? "Links" : "Rechts";
            }
            String score = leftScore + ":" + rightScore;
            JOptionPane.showMessageDialog(this,
                    "Spielende! Gewinner: " + winner + "\nScore: " + score,
                    "1v1", JOptionPane.INFORMATION_MESSAGE);
            resetMatch();
        }
    }

    private void storeMultiplayerResult() {
        String p1 = configuration.playerOneName();
        String p2 = configuration.playerTwoName() == null ? "" : configuration.playerTwoName();
        if (p1 == null || p1.isBlank()) {
            p1 = "Spieler 1";
        }
        if (p2.isBlank()) {
            p2 = "Spieler 2";
        }
        String name = p1 + " vs " + p2;
        String score = leftScore + ":" + rightScore;
        Storage.LeaderboardEntry entry = Storage.LeaderboardEntry.of("1v1", name, score, java.time.Instant.now());
        context.addLeaderboardEntry(entry);
        window.notifyLeaderboardChanged();
    }

    private void resetAfterPoint(boolean leftScored) {
        currentSpeed = scaledStartSpeed;
        hitsUntilSpeedIncrease = HITS_PER_LEVEL;
        resetPositions();
        resetBall(leftScored ? -1 : 1);
    }

    private void resetPositions() {
        leftPaddle.setPosition(40, height / 2.0 - scaledPaddleHeight / 2.0);
        rightPaddle.setPosition(width - 40 - scaledPaddleWidth, height / 2.0 - scaledPaddleHeight / 2.0);
        // update paddle sizes in case scale changed
        leftPaddle.setSize(scaledPaddleWidth, scaledPaddleHeight);
        rightPaddle.setSize(scaledPaddleWidth, scaledPaddleHeight);
    }

    private void resetBall(int horizontalDirection) {
        double dirX = horizontalDirection == 0 ? randomHorizontalDirection() : Math.signum(horizontalDirection);
        double dirY = random.nextBoolean() ? 1 : -1;
        ball.setPosition(width / 2.0 - ball.size() / 2.0, height / 2.0 - ball.size() / 2.0);
        ball.setVelocity(dirX * currentSpeed, dirY * currentSpeed);
    }

    private int randomHorizontalDirection() {
        return random.nextBoolean() ? 1 : -1;
    }

    private void clampPaddle(Paddle paddle) {
        double y = paddle.top();
        if (y < 0) {
            paddle.setY(0);
        } else if (paddle.bottom() > height) {
            paddle.setY(height - paddle.height());
        }
    }

    private boolean intersects(Ball ball, Paddle paddle) {
        return ball.right() >= paddle.left() && ball.left() <= paddle.right()
                && ball.bottom() >= paddle.top() && ball.top() <= paddle.bottom();
    }

    private void togglePause() {
        if (!running) {
            return;
        }
        paused = !paused;
    }

    private void resetMatch() {
        if (configuration == null) {
            return;
        }
        currentSpeed = scaledStartSpeed;
        hitsUntilSpeedIncrease = HITS_PER_LEVEL;
        singleScore = 0;
        leftScore = 0;
        rightScore = 0;
        paused = false;
        running = true;
        updateTimerDelay();
        timer.restart();
        resetPositions();
        resetBall(randomHorizontalDirection());
    }

    public void onSettingsChanged() {
        configureKeyBindings();
        updateTimerDelay();
    }

    private int defaultDelay() {
        return 1000 / Math.max(30, context.settings().fps);
    }

    private void updateTimerDelay() {
        int delay = defaultDelay();
        timer.setDelay(delay);
        timer.setInitialDelay(0);
    }

    private int targetScore() {
        return Math.max(1, context.settings().maxPoints1v1);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Skin skin = context.currentSkinOrDefault();
        ColorPalette palette = skin.palette();
    g2.setColor(palette.background());
    g2.fillRect(0, 0, width, height);

        Storage.Settings settings = context.settings();
        if (settings.showMidline) {
            g2.setColor(palette.midline());
            int dashHeight = 20;
            int gap = 15;
            int midX = width / 2 - 2;
            for (int y = 0; y < height; y += dashHeight + gap) {
                g2.fillRect(midX, y, 4, dashHeight);
            }
        }

        g2.setColor(palette.paddleLeft());
        g2.fillRect((int) Math.round(leftPaddle.left()), (int) Math.round(leftPaddle.top()), (int) Math.round(leftPaddle.width()), (int) Math.round(leftPaddle.height()));

        if (configuration.mode() == GameConfiguration.Mode.MULTI_PLAYER) {
            g2.setColor(palette.paddleRight());
            g2.fillRect((int) Math.round(rightPaddle.left()), (int) Math.round(rightPaddle.top()), (int) Math.round(rightPaddle.width()), (int) Math.round(rightPaddle.height()));
        }

        g2.setColor(palette.ball());
        g2.fillOval((int) Math.round(ball.left()), (int) Math.round(ball.top()), (int) Math.round(ball.size()), (int) Math.round(ball.size()));

    g2.setColor(palette.text());
    g2.setFont(g2.getFont().deriveFont(Font.BOLD, (float) (20f * Math.max(1.0, scale))));
        if (configuration.mode() == GameConfiguration.Mode.SINGLE_PLAYER) {
            String hud = configuration.playerOneName() + "  Score: " + singleScore;
            // Level-Berechnung: Start bei 1, steigt mit jeder Geschwindigkeitsstufe (Delta)
            int level = 1 + (int) Math.floor((currentSpeed - scaledStartSpeed) / scaledSpeedDelta);
            hud += "  Level: " + level;
            g2.drawString(hud, 30, 30);
        } else {
            int target = targetScore();
            String leftLabel = configuration.playerOneName() + ": " + leftScore + " / " + target;
            String rightLabel = configuration.playerTwoName() + ": " + rightScore + " / " + target;
            g2.drawString(leftLabel, 30, 30);
            g2.drawString(rightLabel, width - 200, 30);
        }
    g2.setFont(g2.getFont().deriveFont(Font.PLAIN, (float) (14f * Math.max(1.0, scale))));
    String boostInfo = "Boost: " + settings.player1BoostKey + " / " + settings.player2BoostKey;
        g2.drawString("P = Pause  R = Reset  ESC = Menü  " + boostInfo, 30, height - (int) (20 * Math.max(1.0, scale)));

        if (paused) {
            g2.setColor(new Color(0, 0, 0, 120));
            g2.fillRect(0, 0, width, height);
            g2.setColor(palette.text());
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, (float) (36f * Math.max(1.0, scale))));
            g2.drawString("Pause", width / 2 - (int) (60 * Math.max(1.0, scale)), height / 2);
        }

        g2.dispose();
    }

    private static final class KeyAction extends AbstractAction {
        private final Runnable runnable;

        private KeyAction(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            runnable.run();
        }
    }
}
