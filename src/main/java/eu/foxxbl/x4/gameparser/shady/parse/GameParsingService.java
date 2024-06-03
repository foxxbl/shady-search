package eu.foxxbl.x4.gameparser.shady.parse;

import eu.foxxbl.x4.gameparser.shady.config.ShadySearchConfig;
import eu.foxxbl.x4.gameparser.shady.model.map.Sector;
import eu.foxxbl.x4.gameparser.shady.model.result.ShadyGuy;
import eu.foxxbl.x4.gameparser.shady.parse.xml.IXmlStreamParser;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class GameParsingService {

  private final IXmlStreamParser xmlStreamParser;
  private final ShadySearchConfig shadySearchConfig;

  /**
   * Parses a gamesave for black marketeers and returns result  as the list of the Black Marketeers
   *
   * @param gameSaveFileToParse - full path to the game save
   * @param mapSector           - mapSectorObject
   * @return List<BlackMarketeers>
   */
  public List<ShadyGuy> parseGameForBlackMarketeers(File gameSaveFileToParse, Sector mapSector) {
    try {
      if (!gameSaveFileToParse.exists()) {
        throw new IllegalArgumentException("File " + gameSaveFileToParse.getName() + " doesn't exist!");
      }

      log.info("Requested sector: {}, filteredSearch?: {}", mapSector, shadySearchConfig.filteredStreamSearchEnabled());
      Instant start = Instant.now();
      List<ShadyGuy> shadyGuyList = xmlStreamParser.parseFileStream(gameSaveFileToParse, mapSector);
      logParsing(xmlStreamParser.getClass().getName(), mapSector.sectorMacro(), gameSaveFileToParse, start, Instant.now());
      return shadyGuyList;
    } catch (Exception e) {
      log.error("Exception {} happened during game parsing! GameSaveFile: {}", e.getLocalizedMessage(), gameSaveFileToParse);
      throw e;
    }

  }

  private void logParsing(String className, String sector, File fileToParse, Instant start, Instant end) {
    Duration duration = Duration.between(start, end);
    log.info("Finished parsing {} file:{} for sector:{}, time: {} sec {} ms", className, fileToParse.getAbsoluteFile(), sector, duration.toSeconds(), duration.toMillisPart());
  }
}
