package pong;

import java.awt.Color;
import java.util.Objects;

public final class ColorPalette {
    private final Color background;
    private final Color midline;
    private final Color paddleLeft;
    private final Color paddleRight;
    private final Color ball;
    private final Color text;

    public ColorPalette(Color background,
                        Color midline,
                        Color paddleLeft,
                        Color paddleRight,
                        Color ball,
                        Color text) {
        this.background = Objects.requireNonNull(background, "background");
        this.midline = Objects.requireNonNull(midline, "midline");
        this.paddleLeft = Objects.requireNonNull(paddleLeft, "paddleLeft");
        this.paddleRight = Objects.requireNonNull(paddleRight, "paddleRight");
        this.ball = Objects.requireNonNull(ball, "ball");
        this.text = Objects.requireNonNull(text, "text");
    }

    public Color background() {
        return background;
    }

    public Color midline() {
        return midline;
    }

    public Color paddleLeft() {
        return paddleLeft;
    }

    public Color paddleRight() {
        return paddleRight;
    }

    public Color ball() {
        return ball;
    }

    public Color text() {
        return text;
    }

    public static Color fromHex(String hex) {
        String normalized = Objects.requireNonNull(hex, "hex").trim();
        if (normalized.startsWith("#")) {
            normalized = normalized.substring(1);
        }
        if (normalized.length() != 6) {
            throw new IllegalArgumentException("Color hex must be 6 characters: " + hex);
        }
        return new Color(Integer.parseInt(normalized, 16));
    }
}
