package pong;

import java.util.Objects;

/**
 * GameConfiguration
 * Enthält Modus (Single/Multi) und Spieler-Namen für eine Spielsession.
 */
public final class GameConfiguration {
    public enum Mode {
        SINGLE_PLAYER,
        MULTI_PLAYER
    }

    private final Mode mode;
    private final String playerOneName;
    private final String playerTwoName;

    public GameConfiguration(Mode mode, String playerOneName, String playerTwoName) {
        this.mode = Objects.requireNonNull(mode, "mode");
        this.playerOneName = Objects.requireNonNull(playerOneName, "playerOneName");
        this.playerTwoName = playerTwoName;
    }

    public Mode mode() {
        return mode;
    }

    public String playerOneName() {
        return playerOneName;
    }

    public String playerTwoName() {
        return playerTwoName;
    }
}
