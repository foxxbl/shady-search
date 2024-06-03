package eu.foxxbl.x4.gameparser.shady.parse.xml;


import eu.foxxbl.x4.gameparser.shady.model.gamesave.Component;
import eu.foxxbl.x4.gameparser.shady.model.gamesave.ComponentClass;
import eu.foxxbl.x4.gameparser.shady.model.gamesave.Connection;
import eu.foxxbl.x4.gameparser.shady.model.gamesave.ConnectionType;
import eu.foxxbl.x4.gameparser.shady.model.gamesave.Post;
import eu.foxxbl.x4.gameparser.shady.model.gamesave.Source;
import eu.foxxbl.x4.gameparser.shady.model.gamesave.Traits;
import eu.foxxbl.x4.gameparser.shady.model.result.ShadyGuy;
import eu.foxxbl.x4.gameparser.shady.model.result.ShadyGuyStatus;
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

  public List<ShadyGuy> findShadyGuys(Component component, String sectorName) {
    List<ShadyGuy> shadyGuyList = new ArrayList<>();
    //Parsing sector
    for (Connection connectionZones : component.getConnections().getConnection()) {
      for (Component probableZoneComponent : connectionZones.getComponent()) {
        if (probableZoneComponent != null && probableZoneComponent.getClazz() == ComponentClass.ZONE) {
          // Parsing zone
          shadyGuyList.addAll(parseZone(probableZoneComponent));
        }

      }
    }
    long shadyGuysTotal = shadyGuyList.stream().filter(shadyGuy -> shadyGuy.getStatus() != ShadyGuyStatus.NONE).count();
    log.info("Total number of shady guys in sector {} is {} from total number of stations: {}", sectorName, shadyGuysTotal, shadyGuyList.size());
    return shadyGuyList;
  }

  private List<ShadyGuy> parseZone(Component zoneComponent) {
    List<ShadyGuy> shadyGuyList = new ArrayList<>();
    if (zoneComponent.getConnections() != null) {
      for (Connection connection : zoneComponent.getConnections().getConnection()) {
        if (ConnectionType.fromString(connection.getConnectionName()) == ConnectionType.STATIONS) {

          for (Component probableStationComponent : connection.getComponent()) {
            if (probableStationComponent.getClazz() == ComponentClass.STATION && !probableStationComponent.getOwner().equals(PLAYER_OWNER)) {
              shadyGuyList.add(parseStation(probableStationComponent));
            }
          }
        }
      }
    }
    return shadyGuyList;
  }

  /**
   * Parses a station and searches for the black marketeer / shady guy
   */
  private ShadyGuy parseStation(Component stationComponent) {
    String stationName = getStationName(stationComponent);

    ShadyGuy shadyGuy = new ShadyGuy(stationComponent.getCode(), stationName);
    shadyGuy.setName(N_A);
    shadyGuy.setStatus(ShadyGuyStatus.NONE);

    if (stationComponent.getControl() != null) {
      for (Post post : stationComponent.getControl().getPost()) {
        //postComponent is not null -> exists 100%
        if (post.isShadyGuy() && StringUtils.hasText(post.getPostComponent())) {
          shadyGuy.setComponentId(post.getPostComponent());
          break;
        }
      }
    }

    //parse if shady guy/black marketeer is already active for player
    if (StringUtils.hasText(shadyGuy.getComponentId())) {
      parseShadyGuyData(stationComponent, stationComponent.getCode(), shadyGuy);

      //parse voice leaks in case of potential shady guy inactive
      if (shadyGuy.getStatus() == ShadyGuyStatus.INACTIVE) {
        List<String> voiceLeaks = new ArrayList<>();
        parseRecursivelyForLeaks(stationComponent, stationComponent.getCode(), voiceLeaks);
        shadyGuy.setVoiceLeaks(voiceLeaks.size());
      }
    }

    return shadyGuy;
  }

  /**
   * This is TODO - not sure how to get a station name as ingame
   */
  private static String getStationName(Component stationComponent) {
    String stationName = Optional.ofNullable(stationComponent.getSource()).orElse(new Source("")).getEntry();
    return  (stationName == null) ? stationComponent.getMacro() : stationName;
  }

  /**
   * Parses the shady guy/black marketeer data - name and if it is active for player
   */
  private static void parseShadyGuyData(Component component, String stationCode, ShadyGuy shadyGuy) {
    if (component.getClazz() == ComponentClass.NPC && component.getId().equals(shadyGuy.getComponentId())) {
      shadyGuy.setStatus(isShadyGuyActive(component) ? ShadyGuyStatus.ACTIVE : ShadyGuyStatus.INACTIVE);
      shadyGuy.setName(component.getName());
      log.info("->Station {} Found Shady guy: {} traits: {}", stationCode, component.getName(), component.getTraits());
    } else {
      if (component.getConnections() != null) {
        for (Connection connection : component.getConnections().getConnection()) {
          for (Component childConnection : connection.getComponent()) {
            parseShadyGuyData(childConnection, stationCode, shadyGuy);
          }
        }
      }
    }
  }

  private static boolean isShadyGuyActive(Component component) {
    return Optional.ofNullable(component.getTraits()).orElse(new Traits("")).getFlags().contains(TRADES_VISIBLE_TRAIT);
  }

  private void parseRecursivelyForLeaks(Component component, String stationCode, List<String> voiceLeaks) {
    if (component.getClazz() == ComponentClass.SIGNALLEAK && "voice".equals(component.getType())) {
      log.info("->Station {} Found voice leak: {}", stationCode, component.getMacro());
      voiceLeaks.add(component.getMacro());
    } else {

      if (component.getConnections() != null) {
        for (Connection connection : component.getConnections().getConnection()) {
          for (Component childConnection : connection.getComponent()) {
            parseRecursivelyForLeaks(childConnection, stationCode, voiceLeaks);
          }
        }

      }
    }
  }

}
