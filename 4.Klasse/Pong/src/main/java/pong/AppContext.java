package pong;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AppContext
 * Lädt/speichert alle Daten (Profile, Settings, Skins, Käufe, Leaderboard) über Storage
 * und stellt bequeme Lese-/Schreibmethoden bereit. Enthält die aktuellen In-Memory-Stände.
 */
public final class AppContext {
    private final Storage storage;
    private final Map<String, Skin> skins;
    private final Storage.ShopCatalog shopCatalog;

    private Storage.Profile profile;
    private Storage.Settings settings;
    private Storage.SkinsOwned skinsOwned;
    private Storage.Purchases purchases;
    private Storage.Leaderboard leaderboard;

    private AppContext(Storage storage, Map<String, Skin> skins, Storage.ShopCatalog shopCatalog) {
        this.storage = storage;
        this.skins = new LinkedHashMap<>(skins);
        this.shopCatalog = shopCatalog;
    }

    public static AppContext load(Path projectRoot) {
        Storage storage = Storage.createDefault(projectRoot);
        Map<String, Skin> skins = storage.loadSkinsFromResources();
        Storage.ShopCatalog catalog = storage.loadShopCatalog();
        AppContext context = new AppContext(storage, skins, catalog);
        context.refresh();
        return context;
    }

    public void refresh() {
        profile = storage.loadProfile();
        settings = storage.loadSettings();
        skinsOwned = storage.loadSkinsOwned();
        purchases = storage.loadPurchases();
        leaderboard = storage.loadLeaderboard();
        if (!skinsOwned.owned.contains("default")) {
            skinsOwned.owned.add("default");
            storage.saveSkinsOwned(skinsOwned);
        }
        if (purchases.items == null) {
            purchases.items = new java.util.ArrayList<>();
        }
        if (leaderboard.entries == null) {
            leaderboard.entries = new java.util.ArrayList<>();
        }
        boolean settingsChanged = false;
        if (settings.selectedSkinId == null || !skins.containsKey(settings.selectedSkinId)) {
            settings.selectedSkinId = "default";
            settingsChanged = true;
        }
        if (settings.fps <= 0) {
            settings.fps = 60;
            settingsChanged = true;
        }
        if (settings.maxPoints1v1 <= 0) {
            settings.maxPoints1v1 = 10;
            settingsChanged = true;
        }
        if (settings.player1UpKey == null || settings.player1UpKey.isBlank()) {
            settings.player1UpKey = "W";
            settingsChanged = true;
        }
        if (settings.player1DownKey == null || settings.player1DownKey.isBlank()) {
            settings.player1DownKey = "S";
            settingsChanged = true;
        }
        if (settings.player1BoostKey == null || settings.player1BoostKey.isBlank()) {
            settings.player1BoostKey = "X";
            settingsChanged = true;
        }
        if (settings.player2UpKey == null || settings.player2UpKey.isBlank()) {
            settings.player2UpKey = "O";
            settingsChanged = true;
        }
        if (settings.player2DownKey == null || settings.player2DownKey.isBlank()) {
            settings.player2DownKey = "L";
            settingsChanged = true;
        }
        if (settings.player2BoostKey == null || settings.player2BoostKey.isBlank()) {
            settings.player2BoostKey = ",";
            settingsChanged = true;
        }
        if (settingsChanged) {
            storage.saveSettings(settings);
        }
    }

    public Storage.Profile profile() {
        return profile;
    }

    public Storage.Settings settings() {
        return settings;
    }

    public Storage.SkinsOwned skinsOwned() {
        return skinsOwned;
    }

    public Storage.Purchases purchases() {
        return purchases;
    }

    public Storage.Leaderboard leaderboard() {
        return leaderboard;
    }

    public Map<String, Skin> skins() {
        return Collections.unmodifiableMap(skins);
    }

    public Storage.ShopCatalog shopCatalog() {
        return shopCatalog;
    }

    public Optional<Skin> currentSkin() {
        return Optional.ofNullable(skins.get(settings.selectedSkinId));
    }

    public Skin currentSkinOrDefault() {
        return currentSkin().orElseGet(() -> skins.getOrDefault("default", defaultSkin()));
    }

    private Skin defaultSkin() {
        return skins.values().stream().findFirst().orElse(new Skin(
                "fallback", "Fallback",
                new ColorPalette(ColorPalette.fromHex("#000000"),
                        ColorPalette.fromHex("#444444"),
                        ColorPalette.fromHex("#FFFFFF"),
                        ColorPalette.fromHex("#FFFFFF"),
                        ColorPalette.fromHex("#FFFFFF"),
                        ColorPalette.fromHex("#FFFFFF"))
        ));
    }

    public void updateProfile(java.util.function.Consumer<Storage.Profile> updater) {
        Objects.requireNonNull(updater, "updater").accept(profile);
        storage.saveProfile(profile);
    }

    public void updateSettings(java.util.function.Consumer<Storage.Settings> updater) {
        Objects.requireNonNull(updater, "updater").accept(settings);
        storage.saveSettings(settings);
    }

    public void addOwnedSkin(String skinId) {
        if (skinId == null || skinId.isBlank()) {
            return;
        }
        if (skinsOwned.owned.add(skinId)) {
            storage.saveSkinsOwned(skinsOwned);
        }
    }

    public boolean isSkinOwned(String skinId) {
        return skinsOwned.owned.contains(skinId);
    }

    public void recordPurchase(String skinId) {
        if (skinId == null || skinId.isBlank()) {
            return;
        }
        boolean exists = purchases.items.stream().anyMatch(entry -> skinId.equals(entry.id));
        if (!exists) {
            purchases.items.add(Storage.PurchaseEntry.of(skinId, Instant.now()));
            storage.savePurchases(purchases);
        }
    }

    public void addLeaderboardEntry(Storage.LeaderboardEntry entry) {
        leaderboard.addEntry(entry);
        storage.saveLeaderboard(leaderboard);
    }

    public java.util.List<Storage.LeaderboardEntry> leaderboardForMode(String mode) {
        if (mode == null) {
            return java.util.List.of();
        }
        if ("SP".equals(mode)) {
            return leaderboard.entries.stream()
                    .filter(e -> Objects.equals(e.mode, mode))
                    .sorted((a, b) -> Integer.compare(parseScore(b.score), parseScore(a.score)))
                    .collect(Collectors.toList());
        }
        if ("1v1".equals(mode)) {
            return leaderboard.entries.stream()
                    .filter(e -> Objects.equals(e.mode, mode))
                    .sorted((a, b) -> b.date.compareTo(a.date))
                    .collect(Collectors.toList());
        }
        return leaderboard.entries.stream()
                .filter(e -> Objects.equals(e.mode, mode))
                .collect(Collectors.toList());
    }

    private int parseScore(String score) {
        try {
            return Integer.parseInt(score);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
