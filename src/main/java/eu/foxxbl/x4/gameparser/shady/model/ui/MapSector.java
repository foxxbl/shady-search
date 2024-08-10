package eu.foxxbl.x4.gameparser.shady.model.ui;

import eu.foxxbl.x4.gameparser.shady.model.entity.FactionEntity;
import eu.foxxbl.x4.gameparser.shady.model.entity.MapSectorEntity;
import eu.foxxbl.x4.gameparser.shady.model.entity.MapType;
import eu.foxxbl.x4.gameparser.shady.model.parse.BlackMarketeer;
import eu.foxxbl.x4.gameparser.shady.model.parse.ParsedMapSector;
import java.util.List;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;


/**
 * Model class for UI representation - combination of the parsed data from sector and map and faction data from the db (MapType and translated sector name)
 *
 * @param sectorName              name of the sector
 * @param mapType                 type of map (DLC or a base game)
 * @param sectorOwnerName         name of the sector owner faction
 * @param blackMarketeerList      list of {@link BlackMarketeer}s
 * @param stationTotal            total nr of stations
 * @param blackMarketeersTotal    total nr of black marketeers in sector
 * @param blackMarketeersUnlocked nr of unlocked black marketeers in sector
 */
@Builder
@Slf4j
public record MapSector(String sectorName, MapType mapType, String sectorOwnerName, List<BlackMarketeer> blackMarketeerList, int stationTotal, int blackMarketeersTotal,
                        int blackMarketeersUnlocked) {

  public static MapSector createMapSector(ParsedMapSector parsedMapSector, MapSectorEntity mapSectorEntity, FactionEntity factionEntity) {

    return MapSector.builder().sectorName(mapSectorEntity.getSectorName()).mapType(mapSectorEntity.getMapType()).sectorOwnerName(factionEntity.getName())
        .blackMarketeerList(parsedMapSector.blackMarketeerList()).stationTotal(parsedMapSector.stationTotal()).blackMarketeersTotal(parsedMapSector.blackMarketeersTotal())
        .blackMarketeersUnlocked(parsedMapSector.blackMarketeersUnlocked()).build();
  }

  public String getMapTypeFullName() {
    return mapType.getFullName();
  }

}
