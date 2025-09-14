package eu.foxxbl.x4.gameparser.shady.model.entity;

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

}
