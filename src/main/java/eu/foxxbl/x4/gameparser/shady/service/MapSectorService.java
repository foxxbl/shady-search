package eu.foxxbl.x4.gameparser.shady.service;


import eu.foxxbl.x4.gameparser.shady.model.entity.FactionEntity;
import eu.foxxbl.x4.gameparser.shady.model.entity.MapSectorEntity;
import eu.foxxbl.x4.gameparser.shady.model.parse.ParsedMapSector;
import eu.foxxbl.x4.gameparser.shady.model.ui.MapSector;
import eu.foxxbl.x4.gameparser.shady.repository.FactionRepository;
import eu.foxxbl.x4.gameparser.shady.repository.MapSectorRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
      throw new RuntimeException("Cannot find translation for owner faction " + parsedMapSector.sectorOwner());
    }
    return factionEntity;
  }

  private MapSectorEntity getMapSectorEntity(ParsedMapSector parsedMapSector, Map<String, MapSectorEntity> mapSectorMapByMacro) {
    MapSectorEntity mapSectorEntity = mapSectorMapByMacro.get(parsedMapSector.sectorMacro());
    if (mapSectorEntity == null) {
      throw new RuntimeException("Cannot find translation for sector macro " + parsedMapSector.sectorMacro());
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
