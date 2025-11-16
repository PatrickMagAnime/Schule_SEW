package pong;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Storage
 * Verantwortlich für das Laden/Speichern aller JSON-Dateien (Profile, Settings, SkinsOwned,
 * Purchases, Leaderboard) sowie Laden von Ressourcen (Skins, Shop-Katalog). Nutzt Gson.
 */
public final class Storage {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final String PROFILE_FILE = "profile.json";
    private static final String SETTINGS_FILE = "settings.json";
    private static final String SKINS_OWNED_FILE = "skins_owned.json";
    private static final String PURCHASES_FILE = "purchases.json";
    private static final String LEADERBOARD_FILE = "leaderboard.json";

    private final Path dataDirectory;

    private Storage(Path dataDirectory) {
        this.dataDirectory = Objects.requireNonNull(dataDirectory, "dataDirectory");
    }

    public static Storage createDefault(Path projectRoot) {
        Objects.requireNonNull(projectRoot, "projectRoot");
        Path dataDir = projectRoot.resolve("data");
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create data directory", e);
        }
        return new Storage(dataDir);
    }

    public Profile loadProfile() {
        return load(PROFILE_FILE, Profile.class, Profile::new);
    }

    public void saveProfile(Profile profile) {
        save(PROFILE_FILE, profile);
    }

    public Settings loadSettings() {
        return load(SETTINGS_FILE, Settings.class, Settings::new);
    }

    public void saveSettings(Settings settings) {
        save(SETTINGS_FILE, settings);
    }

    public SkinsOwned loadSkinsOwned() {
        return load(SKINS_OWNED_FILE, SkinsOwned.class, SkinsOwned::new);
    }

    public void saveSkinsOwned(SkinsOwned skinsOwned) {
        save(SKINS_OWNED_FILE, skinsOwned);
    }

    public Purchases loadPurchases() {
        return load(PURCHASES_FILE, Purchases.class, Purchases::new);
    }

    public void savePurchases(Purchases purchases) {
        save(PURCHASES_FILE, purchases);
    }

    public Leaderboard loadLeaderboard() {
        return load(LEADERBOARD_FILE, Leaderboard.class, Leaderboard::new);
    }

    public void saveLeaderboard(Leaderboard leaderboard) {
        save(LEADERBOARD_FILE, leaderboard);
    }

    public ShopCatalog loadShopCatalog() {
        ClassLoader loader = Storage.class.getClassLoader();
        try (InputStream stream = loader.getResourceAsStream("shop/catalog.json")) {
            if (stream == null) {
                throw new IllegalStateException("Missing shop catalog resource");
            }
            try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                ShopCatalog catalog = GSON.fromJson(reader, ShopCatalog.class);
                if (catalog == null) {
                    throw new IllegalStateException("Parsed null catalog");
                }
                if (catalog.items == null) {
                    catalog.items = new ArrayList<>();
                }
                return catalog;
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read shop catalog", e);
        } catch (JsonParseException e) {
            throw new IllegalStateException("Invalid shop catalog JSON", e);
        }
    }

    public Map<String, Skin> loadSkinsFromResources() {
        ClassLoader loader = Storage.class.getClassLoader();
        try {
            return listResourceFiles("skins").stream()
                    .filter(name -> name.endsWith(".json"))
                    .map(resource -> readSkin(loader, resource))
                    .collect(Collectors.toMap(Skin::id, skin -> skin, (a, b) -> a, LinkedHashMap::new));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load skin resources", e);
        }
    }

    private List<String> listResourceFiles(String resourceDir) throws IOException {
        List<String> resources = new ArrayList<>();
        Path dirOnDisk = Path.of("src", "main", "resources", resourceDir);
        if (Files.isDirectory(dirOnDisk)) {
            try (var stream = Files.walk(dirOnDisk)) {
                stream.filter(Files::isRegularFile)
                        .forEach(path -> {
                            Path relative = dirOnDisk.relativize(path);
                            resources.add(resourceDir + "/" + relative.toString().replace("\\", "/"));
                        });
            }
        } else {
            InputStream listingStream = Storage.class.getClassLoader().getResourceAsStream(resourceDir);
            if (listingStream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(listingStream, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.isBlank()) {
                            resources.add(resourceDir + "/" + line.trim());
                        }
                    }
                }
            }
        }
        return resources;
    }

    private Skin readSkin(ClassLoader loader, String resourcePath) {
        try (InputStream stream = loader.getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new IllegalStateException("Missing skin resource: " + resourcePath);
            }
            try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                Skin.Descriptor descriptor = GSON.fromJson(reader, Skin.Descriptor.class);
                return Skin.fromDescriptor(descriptor);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read skin resource: " + resourcePath, e);
        } catch (JsonParseException e) {
            throw new IllegalStateException("Invalid skin JSON: " + resourcePath, e);
        }
    }

    private <T> T load(String fileName, Class<T> type, Supplier<T> defaultSupplier) {
        Path file = dataDirectory.resolve(fileName);
        if (!Files.isRegularFile(file)) {
            T defaults = defaultSupplier.get();
            save(fileName, defaults);
            return defaults;
        }
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            T value = GSON.fromJson(reader, type);
            if (value == null) {
                throw new IllegalStateException("Parsed null for " + fileName);
            }
            return value;
        } catch (IOException e) {
            T defaults = defaultSupplier.get();
            save(fileName, defaults);
            return defaults;
        } catch (JsonParseException e) {
            T defaults = defaultSupplier.get();
            save(fileName, defaults);
            return defaults;
        }
    }

    private void save(String fileName, Object value) {
        Path file = dataDirectory.resolve(fileName);
        try {
            Files.createDirectories(file.getParent());
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create directories for " + file, e);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
                StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            GSON.toJson(value, writer);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write file " + file, e);
        }
    }

    public static final class Profile {
        public String currentName = "";
        public String player2Name = "";
    }

    public static final class Settings {
        public double volume = 0.8;
        public String selectedSkinId = "default";
        public int fps = 60;
        public int maxPoints1v1 = 10;
        public String player1UpKey = "W";
        public String player1DownKey = "S";
        public String player1BoostKey = "X";
        public String player2UpKey = "O";
        public String player2DownKey = "L";
        public String player2BoostKey = ",";
        public boolean showMidline = true;
    }

    public static final class SkinsOwned {
        public Set<String> owned = new TreeSet<>();

        public SkinsOwned() {
            owned.add("default");
        }
    }

    public static final class Purchases {
        public List<PurchaseEntry> items = new ArrayList<>();
    }

    public static final class PurchaseEntry {
        public String id;
        public String date; // ISO string

        public static PurchaseEntry of(String id, Instant instant) {
            PurchaseEntry entry = new PurchaseEntry();
            entry.id = id;
            entry.date = instant.toString();
            return entry;
        }
    }

    public static final class Leaderboard {
        public List<LeaderboardEntry> entries = new ArrayList<>();

        public void addEntry(LeaderboardEntry entry) {
            entries.add(entry);
        }

        public List<LeaderboardEntry> entriesByMode(String mode) {
            return entries.stream()
                    .filter(e -> Objects.equals(mode, e.mode))
                    .collect(Collectors.toList());
        }
    }

    public static final class LeaderboardEntry {
        public String mode;
        public String name;
        public String score;
        public String date;

        public static LeaderboardEntry of(String mode, String name, String score, Instant instant) {
            LeaderboardEntry entry = new LeaderboardEntry();
            entry.mode = mode;
            entry.name = name;
            entry.score = score;
            entry.date = instant.toString();
            return entry;
        }
    }

    public static final class ShopCatalog {
        public List<ShopItem> items = new ArrayList<>();
    }

    public static final class ShopItem {
        public String id;
        public String name;
        public double price;
        public String currency;
    }
}
