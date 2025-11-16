package pong;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.Objects;

/**
 * AppWindow
 * Zentrales Fenster mit CardLayout-Navigation zwischen Menüs und Spielpanel.
 * Bietet Fullscreen-Toggle und leitet Benachrichtigungen an die Panels weiter.
 */
public final class AppWindow extends JFrame {
    private static final String CARD_MENU = "menu";
    private static final String CARD_PLAY_MENU = "play";
    private static final String CARD_SETTINGS = "settings";
    private static final String CARD_SKINS = "skins";
    private static final String CARD_SHOP = "shop";
    private static final String CARD_LEADERBOARD = "leaderboard";
    private static final String CARD_GAME = "game";

    private final AppContext context;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    private final MenuPanel menuPanel;
    private final PlayMenuPanel playMenuPanel;
    private final SettingsPanel settingsPanel;
    private final SkinsPanel skinsPanel;
    private final ShopPanel shopPanel;
    private final LeaderboardPanel leaderboardPanel;

    private GamePanel gamePanel;
    private boolean fullscreen = false;
    private java.awt.Rectangle windowedBounds = null;

    public AppWindow(AppContext context) {
        this.context = Objects.requireNonNull(context, "context");
        setTitle("Pong");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(900, 600));
        setResizable(false);

        menuPanel = new MenuPanel(this, context);
        playMenuPanel = new PlayMenuPanel(this, context);
        settingsPanel = new SettingsPanel(this, context);
        skinsPanel = new SkinsPanel(this, context);
        shopPanel = new ShopPanel(this, context);
        leaderboardPanel = new LeaderboardPanel(this, context);

        cardPanel.add(menuPanel, CARD_MENU);
        cardPanel.add(playMenuPanel, CARD_PLAY_MENU);
        cardPanel.add(settingsPanel, CARD_SETTINGS);
        cardPanel.add(skinsPanel, CARD_SKINS);
        cardPanel.add(shopPanel, CARD_SHOP);
        cardPanel.add(leaderboardPanel, CARD_LEADERBOARD);

        add(cardPanel);
        pack();
        setLocationRelativeTo(null);
        showMenu();
    }

    public void toggleFullscreen() {
        fullscreen = !fullscreen;
        // toggling undecorated requires disposing
        java.awt.EventQueue.invokeLater(() -> {
            try {
                if (fullscreen) {
                    windowedBounds = getBounds();
                    java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                    dispose();
                    setUndecorated(true);
                    setBounds(0, 0, screen.width, screen.height);
                    setVisible(true);
                    setExtendedState(JFrame.MAXIMIZED_BOTH);
                } else {
                    dispose();
                    setUndecorated(false);
                    setVisible(true);
                    setExtendedState(JFrame.NORMAL);
                    if (windowedBounds != null) {
                        setBounds(windowedBounds);
                    }
                    pack();
                    setLocationRelativeTo(null);
                }
            } catch (Exception e) {
                // best-effort, ignore
            }
        });
    }

    public AppContext context() {
        return context;
    }

    public void showMenu() {
        menuPanel.refresh();
        cardLayout.show(cardPanel, CARD_MENU);
    }

    public void showPlayMenu() {
        playMenuPanel.refresh();
        cardLayout.show(cardPanel, CARD_PLAY_MENU);
    }

    public void showSettings() {
        settingsPanel.refresh();
        cardLayout.show(cardPanel, CARD_SETTINGS);
    }

    public void showSkins() {
        skinsPanel.refresh();
        cardLayout.show(cardPanel, CARD_SKINS);
    }

    public void showShop() {
        shopPanel.refresh();
        cardLayout.show(cardPanel, CARD_SHOP);
    }

    public void showLeaderboard() {
        leaderboardPanel.refresh();
        cardLayout.show(cardPanel, CARD_LEADERBOARD);
    }

    public void startGame(GameConfiguration configuration) {
        Objects.requireNonNull(configuration, "configuration");
        if (gamePanel != null) {
            cardPanel.remove(gamePanel);
        }
        gamePanel = new GamePanel(this, context);
        cardPanel.add(gamePanel, CARD_GAME);
        cardLayout.show(cardPanel, CARD_GAME);
        cardPanel.revalidate();
        gamePanel.startGame(configuration);
        SwingUtilities.invokeLater(gamePanel::requestFocusInWindow);
    }

    public void returnToMenu() {
        if (gamePanel != null) {
            gamePanel.stop();
        }
        showMenu();
    }

    public void notifySkinInventoryChanged() {
        skinsPanel.refresh();
        shopPanel.refresh();
    }

    public void notifyLeaderboardChanged() {
        leaderboardPanel.refresh();
    }

    public void notifySettingsChanged() {
        if (gamePanel != null) {
            gamePanel.onSettingsChanged();
        }
    }
}
