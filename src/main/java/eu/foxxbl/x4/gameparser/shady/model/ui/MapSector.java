package eu.foxxbl.x4.gameparser.shady.model.ui;

import eu.foxxbl.x4.gameparser.shady.model.entity.MapSectorEntity;
import eu.foxxbl.x4.gameparser.shady.model.entity.MapType;
import eu.foxxbl.x4.gameparser.shady.model.parse.BlackMarketeers;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;


/**
 * Combination of the parsed data from sector and map data from the db (MapType and translated sector name)
 *
 * @param sectorMacro             - sector macro
 * @param mapType                 - {@link MapType} mapType of the sector
 * @param sectorName              - translated sector name
 * @param stationTotal            - nr of stations
 * @param blackMarketeersTotal    - total nr of black marketeers in sector
 * @param blackMarketeersUnlocked - nr of unlocked black marketeers in sector
 */
@Builder
@Slf4j
public record MapSector(String sectorMacro, MapType mapType, String sectorName, int stationTotal, int blackMarketeersTotal, int blackMarketeersUnlocked) {

  public static MapSector createMapSector(BlackMarketeers blackMarketeers, MapSectorEntity mapSectorEntity) {

    MapSector mapSector;
    if (mapSectorEntity == null) {
      log.error("No translation data found for the sector macro: {}", blackMarketeers.sectorMacro());
      mapSector = MapSector.builder().sectorName("No translation found").sectorMacro(blackMarketeers.sectorMacro()).mapType(MapType.DEFAULT)
          .stationTotal(blackMarketeers.stationTotal()).blackMarketeersTotal(blackMarketeers.blackMarketeersTotal())
          .blackMarketeersUnlocked(blackMarketeers.blackMarketeersUnlocked()).build();
    } else {
      mapSector = MapSector.builder().sectorName(mapSectorEntity.getSectorName()).sectorMacro(blackMarketeers.sectorMacro()).mapType(mapSectorEntity.getMapType())
          .stationTotal(blackMarketeers.stationTotal()).blackMarketeersTotal(blackMarketeers.blackMarketeersTotal())
          .blackMarketeersUnlocked(blackMarketeers.blackMarketeersUnlocked()).build();
    }
    return mapSector;


  }
}
