package eu.foxxbl.x4.gameparser.shady.model.gamesave;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConnectionType {

  STATIONS("stations", false), ADJACENTREGIONS("adjacentregions", true), BUILDSTORAGES("buildstorages", true), SHIPS("ships", true), VISIBLEZONES("visiblezones", true), ENTITIES(
      "entities", false), SIGNALLEAKS("signalleaks", false), OTHER(
      "other", false);


  private static final Map<String, ConnectionType> lookup
      = EnumSet.allOf(ConnectionType.class).stream().collect(Collectors.toMap(ConnectionType::getName, Function.identity()));

  private final String name;
  private final boolean ignore;


  public static ConnectionType fromString(String name) {
    return lookup.getOrDefault(name, OTHER);

  }
}
