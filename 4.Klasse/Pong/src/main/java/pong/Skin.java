package pong;

import java.util.Objects;

/**
 * Skin
 * Beschreibt einen Skin (id, name) und dessen Farbpalette. Factory-Methode erzeugt ihn aus JSON-Descriptoren.
 */
public final class Skin {
    private final String id;
    private final String name;
    private final ColorPalette palette;

    public Skin(String id, String name, ColorPalette palette) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.palette = Objects.requireNonNull(palette, "palette");
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public ColorPalette palette() {
        return palette;
    }

    public static Skin fromDescriptor(Descriptor descriptor) {
        Objects.requireNonNull(descriptor, "descriptor");
        if (descriptor.id == null || descriptor.id.isBlank()) {
            throw new IllegalArgumentException("Skin id must not be blank");
        }
        if (descriptor.name == null || descriptor.name.isBlank()) {
            throw new IllegalArgumentException("Skin name must not be blank");
        }
        if (descriptor.palette == null) {
            throw new IllegalArgumentException("Skin palette must be provided");
        }
        ColorPalette palette = new ColorPalette(
                ColorPalette.fromHex(descriptor.palette.bg),
                ColorPalette.fromHex(descriptor.palette.midline),
                ColorPalette.fromHex(descriptor.palette.paddleLeft),
                ColorPalette.fromHex(descriptor.palette.paddleRight != null ? descriptor.palette.paddleRight : descriptor.palette.paddleLeft),
                ColorPalette.fromHex(descriptor.palette.ball),
                ColorPalette.fromHex(descriptor.palette.text)
        );
        return new Skin(descriptor.id, descriptor.name, palette);
    }

    public static final class Descriptor {
        public String id;
        public String name;
        public Palette palette;
    }

    public static final class Palette {
        public String bg;
        public String midline;
        public String paddleLeft;
        public String paddleRight;
        public String ball;
        public String text;
    }
}
