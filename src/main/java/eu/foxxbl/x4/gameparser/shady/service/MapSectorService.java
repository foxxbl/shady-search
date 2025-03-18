package eu.foxxbl.x4.gameparser.shady.service;


import eu.foxxbl.x4.gameparser.shady.model.entity.FactionEntity;
import eu.foxxbl.x4.gameparser.shady.model.entity.MapSectorEntity;
import eu.foxxbl.x4.gameparser.shady.model.entity.MapType;
import eu.foxxbl.x4.gameparser.shady.model.parse.ParsedMapSector;
import eu.foxxbl.x4.gameparser.shady.model.ui.MapSector;
import eu.foxxbl.x4.gameparser.shady.repository.FactionRepository;
import eu.foxxbl.x4.gameparser.shady.repository.MapSectorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapSectorService {

  private final MapSectorRepository mapSectorRepository;

  private final FactionRepository factionRepository;

  public List<MapSector> populateSectors(List<ParsedMapSector> parsedMapSectorList) {
    Map<String, MapSectorEntity> mapSectorMapByMacro = createMapSectorMap();
    Map<String, FactionEntity> factionMapByOwner = createFactionMap();

    return parsedMapSectorList.stream()
        .map(parsedMapSector -> MapSector.createMapSector(parsedMapSector, getMapSectorEntity(parsedMapSector, mapSectorMapByMacro),
            getFactionEntity(parsedMapSector, factionMapByOwner)))
        .collect(Collectors.toList());
  }

  private FactionEntity getFactionEntity(ParsedMapSector parsedMapSector, Map<String, FactionEntity> factionMapByOwner) {
    FactionEntity factionEntity = factionMapByOwner.get(parsedMapSector.sectorOwner());
    if (factionEntity == null) {
      String unknownSectorOwner = parsedMapSector.sectorOwner();
      String unknownSectorOwnerShort = parsedMapSector.sectorOwner().toUpperCase().substring(0,Math.min(unknownSectorOwner.length(), 3));
      factionEntity = FactionEntity.builder().id(unknownSectorOwner).name(unknownSectorOwner).shortName(unknownSectorOwnerShort).mapType(MapType.UNKNOWN).build();
      log.warn("Cannot find translation for owner faction {} - created a new faction entity: {} ",  unknownSectorOwner, factionEntity);
    }
    return factionEntity;
  }

  private MapSectorEntity getMapSectorEntity(ParsedMapSector parsedMapSector, Map<String, MapSectorEntity> mapSectorMapByMacro) {
    String sectorMacro = parsedMapSector.sectorMacro();
    MapSectorEntity mapSectorEntity = mapSectorMapByMacro.get(sectorMacro);
    if (mapSectorEntity == null) {
        log.warn("Cannot find translation for sector macro {}", sectorMacro);
        mapSectorEntity = new MapSectorEntity(sectorMacro, MapType.UNKNOWN, sectorMacro);
        mapSectorMapByMacro.put(sectorMacro, mapSectorEntity);
    }
    return mapSectorEntity;
  }

  private Map<String, MapSectorEntity> createMapSectorMap() {
    return mapSectorRepository.findAll().stream()
        .collect(Collectors.toMap(mapSectorEntity -> mapSectorEntity.getSectorMacro().toLowerCase(), Function.identity()));
  }


  private Map<String, FactionEntity> createFactionMap() {
    return factionRepository.findAll().stream().collect(Collectors.toMap(FactionEntity::getId, Function.identity()));
  }


}
