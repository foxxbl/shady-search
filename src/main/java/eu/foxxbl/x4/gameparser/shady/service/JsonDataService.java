package eu.foxxbl.x4.gameparser.shady.service;

import eu.foxxbl.x4.gameparser.shady.model.json.Faction;
import eu.foxxbl.x4.gameparser.shady.model.json.Sector;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.json.JsonMapper;

/**
 * Loads and caches sector and faction data from bundled JSON resource files.
 * Replaces the previous HSQLDB-backed repositories.
 */
@Service
@Slf4j
public class JsonDataService {

  private static final String SECTORS_RESOURCE = "/data/sectors-en.json";
  private static final String FACTIONS_RESOURCE = "/data/factions-en.json";

  @Getter
  private final List<Sector> sectors;

  @Getter
  private final List<Faction> factions;

  private final Map<String, Sector> sectorsByMacro;
  private final Map<String, Faction> factionsById;

  public JsonDataService() {
    JsonMapper mapper = JsonMapper.builder().build();
    this.sectors = loadResource(mapper, SECTORS_RESOURCE, Sector.class);
    this.factions = loadResource(mapper, FACTIONS_RESOURCE, Faction.class);
    this.sectorsByMacro = sectors.stream()
        .collect(Collectors.toMap(s -> s.sectorMacro().toLowerCase(), Function.identity(), (first, second) -> first));
    this.factionsById = factions.stream()
        .collect(Collectors.toMap(Faction::id, Function.identity(), (first, second) -> first));
    log.info("Loaded {} sectors and {} factions from JSON resources", sectors.size(), factionsById.size());
  }

  /**
   * Finds a sector by its macro (case-insensitive).
   */
  public Sector findSectorByMacro(String macro) {
    return sectorsByMacro.get(macro.toLowerCase());
  }

  /**
   * Finds a faction by its ID.
   */
  public Faction findFactionById(String id) {
    return factionsById.get(id);
  }

  private <T> List<T> loadResource(JsonMapper mapper, String resourcePath, Class<T> elementType) {
    try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
      if (is == null) {
        throw new IllegalStateException("Resource not found: " + resourcePath);
      }
      JavaType listType = mapper.getTypeFactory().constructCollectionType(List.class, elementType);
      return mapper.readValue(is, listType);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to load JSON resource: " + resourcePath, e);
    }
  }

}
