package eu.foxxbl.x4.gameparser.shady.parse.xml;

import eu.foxxbl.x4.gameparser.shady.model.gamesave.Component;
import eu.foxxbl.x4.gameparser.shady.model.gamesave.ComponentClass;
import eu.foxxbl.x4.gameparser.shady.model.gamesave.Connection;
import eu.foxxbl.x4.gameparser.shady.model.gamesave.ConnectionType;
import eu.foxxbl.x4.gameparser.shady.model.gamesave.Post;
import eu.foxxbl.x4.gameparser.shady.model.gamesave.Source;
import eu.foxxbl.x4.gameparser.shady.model.gamesave.Traits;
import eu.foxxbl.x4.gameparser.shady.model.parse.BlackMarketeer;
import eu.foxxbl.x4.gameparser.shady.model.parse.ParsedMapSector;
import eu.foxxbl.x4.gameparser.shady.model.parse.ShadyGuyStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class ShadyGuyParser {

  private static final String PLAYER_OWNER = "player";

  private static final String TRADES_VISIBLE_TRAIT = "tradesvisible";
  private static final String N_A = "N/A";

  public ParsedMapSector findShadyGuys(Component component, String sectorMacro, String sectorOwner) {
    List<BlackMarketeer> blackMarketeerList = new ArrayList<>();
    if (component.getConnections() == null) {
      log.warn("Sector: {} - no connections!", sectorMacro);
      return ParsedMapSector.builder().blackMarketeerList(blackMarketeerList).build();
    }
    //Parsing sector
    for (Connection connectionZones : component.getConnections().getConnection()) {
      for (Component probableZoneComponent : connectionZones.getComponent()) {
        if (probableZoneComponent != null && probableZoneComponent.getClazz() == ComponentClass.ZONE) {
          // Parsing zone
          blackMarketeerList.addAll(parseZone(probableZoneComponent));
        }
      }
    }
    int shadyGuysTotal = Math.toIntExact(blackMarketeerList.stream().filter(shadyGuy -> shadyGuy.getStatus() != ShadyGuyStatus.NONE).count());
    int shadyGuysUnlocked = Math.toIntExact(blackMarketeerList.stream().filter(shadyGuy -> shadyGuy.getStatus() == ShadyGuyStatus.ACTIVE).count());
    int nrOfStations = blackMarketeerList.size();
    log.info("Total number of shady guys in sector {} is {} (unlocked: {})  from total number of stations: {}", sectorMacro, shadyGuysTotal, shadyGuysUnlocked, nrOfStations);
    return ParsedMapSector.builder().sectorMacro(sectorMacro).sectorOwner(sectorOwner).blackMarketeerList(blackMarketeerList).stationTotal(nrOfStations).blackMarketeersTotal(shadyGuysTotal)
        .blackMarketeersUnlocked(shadyGuysUnlocked).build();
  }

  private List<BlackMarketeer> parseZone(Component zoneComponent) {
    List<BlackMarketeer> blackMarketeerList = new ArrayList<>();
    if (zoneComponent.getConnections() != null) {
      for (Connection connection : zoneComponent.getConnections().getConnection()) {
        if (ConnectionType.fromString(connection.getConnectionName()) == ConnectionType.STATIONS) {

          for (Component probableStationComponent : connection.getComponent()) {
            if (probableStationComponent.getClazz() == ComponentClass.STATION && !probableStationComponent.getOwner().equals(PLAYER_OWNER)) {
              blackMarketeerList.add(parseStation(probableStationComponent));
            }
          }
        }
      }
    }
    return blackMarketeerList;
  }

  /**
   * Parses a station and searches for the black marketeer / shady guy.
   * Combines NPC search and voice leak search into a single traversal when the shady guy is inactive.
   */
  private BlackMarketeer parseStation(Component stationComponent) {
    String stationName = getStationName(stationComponent);

    BlackMarketeer blackMarketeer = new BlackMarketeer(stationComponent.getCode(), stationName);
    blackMarketeer.setName(N_A);
    blackMarketeer.setStatus(ShadyGuyStatus.NONE);

    if (stationComponent.getControl() != null) {
      for (Post post : stationComponent.getControl().getPost()) {
        //postComponent is not null -> exists 100%
        if (post.isShadyGuy() && StringUtils.hasText(post.getPostComponent())) {
          blackMarketeer.setComponentId(post.getPostComponent());
          break;
        }
      }
    }

    // Single-pass search: find NPC data and count voice leaks in one traversal
    if (StringUtils.hasText(blackMarketeer.getComponentId())) {
      int voiceLeaks = findShadyGuyAndLeaks(stationComponent, stationComponent.getCode(), blackMarketeer);
      if (blackMarketeer.getStatus() == ShadyGuyStatus.INACTIVE) {
        blackMarketeer.setVoiceLeaks(voiceLeaks);
      }
    }

    return blackMarketeer;
  }

  /**
   * This is TODO - not sure how to get a station name as in-game
   * For now - based  on the station macro or if station has source entry value -> taking that as the name
   */
  private static String getStationName(Component stationComponent) {
    String stationName = Optional.ofNullable(stationComponent.getSource()).orElse(new Source("")).getEntry();
    return  (stationName == null) ? stationComponent.getMacro() : stationName;
  }

  /**
   * Combined single-pass traversal that finds the shady guy NPC and counts voice leaks.
   * Skips connections flagged as ignored (ships, buildstorages, adjacentregions, visiblezones).
   *
   * @return number of voice leaks found in the station's component tree
   */
  private static int findShadyGuyAndLeaks(Component component, String stationCode, BlackMarketeer blackMarketeer) {
    // Check if this component is the target NPC
    if (component.getClazz() == ComponentClass.NPC && component.getId().equals(blackMarketeer.getComponentId())) {
      blackMarketeer.setStatus(isShadyGuyActive(component) ? ShadyGuyStatus.ACTIVE : ShadyGuyStatus.INACTIVE);
      blackMarketeer.setName(component.getName());
      log.info("->Station {} Found Black Marketeer: {} traits: {}", stationCode, component.getName(), component.getTraits());
      return 0;
    }

    // Check if this component is a voice leak
    int voiceLeaks = 0;
    if (component.getClazz() == ComponentClass.SIGNALLEAK && "voice".equals(component.getType())) {
      log.info("->Station {} Found voice leak: {}", stationCode, component.getMacro());
      voiceLeaks++;
    }

    // Recurse into child connections, skipping ignored types
    if (component.getConnections() != null) {
      for (Connection connection : component.getConnections().getConnection()) {
        ConnectionType connectionType = ConnectionType.fromString(connection.getConnectionName());
        if (connectionType.isIgnore()) {
          continue;
        }
        for (Component child : connection.getComponent()) {
          voiceLeaks += findShadyGuyAndLeaks(child, stationCode, blackMarketeer);
        }
      }
    }

    return voiceLeaks;
  }

  private static boolean isShadyGuyActive(Component component) {
    return Optional.ofNullable(component.getTraits()).orElse(new Traits("")).getFlags().contains(TRADES_VISIBLE_TRAIT);
  }

}
