package eu.foxxbl.x4.gameparser.shady.model;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MapType {
    DEFAULT("X4: Foundations", false, "black"),
    SPLIT("X4: Split Vendetta", true, "red"),
    TERRAN("X4: Cradle of Humanity", true, "blue"),
    PIRATE("X4: Tides of Avarice", true, "goldenrod"),
    BORON("X4: Kingdom End", true,"lighseagreen"),
    TIMELINES("X4: Timelines", true, "darkred"),
    MINI_01("X4: Hyperion Pack", true, "#a85f29"),
    MINI_02("X4: Envoy Pack", true, "#9abf01"),
    UNKNOWN("unknown", false, "black");

    private final String fullName;
    private final boolean isDLC;
    private final String color;

    private static final Map<String, MapType> BY_NAME = Stream.of(values())
        .filter(mt -> mt != DEFAULT && mt != UNKNOWN)
        .collect(Collectors.toMap(Enum::name, Function.identity()));

    /**
     * Maps a JSON extension string to a MapType.
     *
     * @param extension extension name from JSON (e.g., "TERRAN", "PIRATE"), or null for base game
     * @return corresponding MapType, DEFAULT when null, UNKNOWN when unrecognized
     */
    public static MapType fromExtension(String extension) {
      if (extension == null) {
        return DEFAULT;
      }
      return BY_NAME.getOrDefault(extension, UNKNOWN);
    }

}
