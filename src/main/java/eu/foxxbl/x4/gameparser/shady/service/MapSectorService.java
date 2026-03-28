package eu.foxxbl.x4.gameparser.shady.service;

import eu.foxxbl.x4.gameparser.shady.model.json.Faction;
import eu.foxxbl.x4.gameparser.shady.model.json.Sector;
import eu.foxxbl.x4.gameparser.shady.model.parse.ParsedMapSector;
import eu.foxxbl.x4.gameparser.shady.model.ui.MapSector;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapSectorService {

  private final JsonDataService jsonDataService;

  public List<MapSector> populateSectors(List<ParsedMapSector> parsedMapSectorList) {
    return parsedMapSectorList.stream()
        .map(parsedMapSector -> MapSector.createMapSector(
            parsedMapSector,
            getSector(parsedMapSector),
            getFaction(parsedMapSector)))
        .collect(Collectors.toList());
  }

  private Faction getFaction(ParsedMapSector parsedMapSector) {
    Faction faction = jsonDataService.findFactionById(parsedMapSector.sectorOwner());
    if (faction == null) {
      String owner = parsedMapSector.sectorOwner();
      String shortName = owner.toUpperCase().substring(0, Math.min(owner.length(), 3));
      faction = new Faction(owner, owner, shortName, null);
      log.warn("Cannot find translation for owner faction {} - created fallback: {}", owner, faction);
    }
    return faction;
  }

  private Sector getSector(ParsedMapSector parsedMapSector) {
    String sectorMacro = parsedMapSector.sectorMacro();
    Sector sector = jsonDataService.findSectorByMacro(sectorMacro);
    if (sector == null) {
      log.warn("Cannot find translation for sector macro {}", sectorMacro);
      sector = new Sector(sectorMacro, 0, null, sectorMacro);
    }
    return sector;
  }

}
