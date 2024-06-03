package eu.foxxbl.x4.gameparser.shady.model.map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MapType {
  DEFAULT("X4: Foundations", false), SPLIT("X4: Split Vendetta", true), TERRAN("X4: Cradle of Humanity", true), PIRATE("X4: Tides of Avarice", true), BORON("X4: Kingdom End",
      true);
  private final String fullName;
  private final boolean isDLC;

  public static MapType fromFilePath(String filePath) {
    for (MapType mapType : MapType.values()) {
      if (mapType != DEFAULT && filePath.contains(mapType.name().toLowerCase())) {
        return mapType;
      }
    }
    return DEFAULT;
  }
}

