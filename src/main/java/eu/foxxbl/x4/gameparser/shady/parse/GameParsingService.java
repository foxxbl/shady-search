package eu.foxxbl.x4.gameparser.shady.parse;

import eu.foxxbl.x4.gameparser.shady.config.ShadySearchConfig;
import eu.foxxbl.x4.gameparser.shady.model.entity.MapSectorEntity;
import eu.foxxbl.x4.gameparser.shady.model.result.BlackMarketeer;
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
   * @return List<ShadyGuy>
   */
  public List<BlackMarketeer> parseGameForBlackMarketeers(File gameSaveFileToParse, MapSectorEntity mapSector) {
    try {
      if (!gameSaveFileToParse.exists()) {
        throw new IllegalArgumentException("File " + gameSaveFileToParse.getName() + " doesn't exist!");
      }

      log.info("Requested sector: {}, filteredSearch?: {}", mapSector, shadySearchConfig.filteredStreamSearchEnabled());
      Instant start = Instant.now();
      List<BlackMarketeer> blackMarketeerList = xmlStreamParser.parseFileStream(gameSaveFileToParse, mapSector);
      Duration duration = Duration.between(start, Instant.now());
      log.info("Finished parsing {} file:{} for sector:{}, stations: {},  time: {} sec {} ms", xmlStreamParser.getClass().getName(), gameSaveFileToParse.getAbsoluteFile(),
          mapSector.getSectorMacro(), blackMarketeerList.size(), duration.toSeconds(), duration.toMillisPart());
      return blackMarketeerList;
    } catch (Exception e) {
      log.error("Exception {} happened during game parsing! GameSaveFile: {}", e.getLocalizedMessage(), gameSaveFileToParse);
      throw e;
    }

  }

}
